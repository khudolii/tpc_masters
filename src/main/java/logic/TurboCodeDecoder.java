package logic;


import logic.exceptions.DecoderException;
import logic.valueobject.TurboCodeDecoderVO;
import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * This class implements the turbo product decoding algorithm.
 * Class fields:
 * @param ENCODED_DATA - matrix to decode
 * @param DECODED_DATA - the matrix into which the decoding result is written
 * @param iterationHistory - list where the iteration history is stored
 */
public class TurboCodeDecoder implements Decoder {

    private static final Logger log = Logger.getLogger(TurboCodeDecoder.class);

    private final SimpleMatrix ENCODED_DATA;
    private SimpleMatrix DECODED_DATA;
    private List<IterationHistory> iterationsHistory;
    private Integer halfIterationNum = 1;

    public TurboCodeDecoder(SimpleMatrix encoded_data) {
        ENCODED_DATA = encoded_data;
    }


    /* Method starts decoding algorithm */
    public TurboCodeDecoder toDecode() throws DecoderException {
        iterationsHistory = new ArrayList<>();
        DECODED_DATA = new SimpleMatrix(ENCODED_DATA);
        log.info("-----------------START DECODING-----------------");
        boolean flag = false;
        try {
            List<TurboCodeDecoderVO> iterationDecHistory = new ArrayList<>();
            while (!flag) {
                int lastIteration = 0;
                for (int i = 0; i < DECODED_DATA.numRows(); i++) {
                    List<Integer> inputToDecode = new ArrayList<>();
                    for (int j = 0; j < DECODED_DATA.numCols(); j++) {
                        inputToDecode.add((int) DECODED_DATA.get(i, j));
                    }
                    log.info("Start decoding row# " + i + "; row = " + Arrays.toString(inputToDecode.toArray()));
                    TurboCodeDecoderVO decodedRowVO = new TurboCodeDecoderVO(inputToDecode);

                    //1. Generate hard decision vector for soft input vector
                    List<Integer> hardDecisionVector = generateHardDecisionVector(decodedRowVO.getSoftInputVector());
                    log.info("Found hard decision vector for row # " + i);

                    //2. Generate sindrom vector for hard decision vector
                    List<Integer> sindromForHardDecisionVector = DecodeUtil.generateSindromVector(hardDecisionVector);

                    //3. Сheck if all elements of the syndrome are equal to zero
                    boolean isAllElementsFromSindromZero = DecodeUtil.isAllElementsInListZero(sindromForHardDecisionVector, Integer.class);
                    log.info("isAllElementsFromSindromZero = " + isAllElementsFromSindromZero);

                    decodedRowVO.setRowNumber(i);
                    decodedRowVO.setHardDecisionVector(hardDecisionVector);
                    decodedRowVO.setSindromVector(sindromForHardDecisionVector);
                    decodedRowVO.setAllElementsFromSindromZero(isAllElementsFromSindromZero);
                    iterationDecHistory.add(decodedRowVO);
                    lastIteration = i;
                }

                for (TurboCodeDecoderVO vector : iterationDecHistory) {
                    List<Integer> softOutputVector = null;
                    if (vector.isAllElementsFromSindromZero()) {
                        // 4.1 If all elements of the syndrome are equal to zero, then we replace the input vector by +-2
                        softOutputVector = returnSoftOutputVector(vector.getSoftInputVector(), 2, null);
                        vector.setSoftOutputVector(softOutputVector);
                    } else {
                        // 4.2 If all elements of the syndrome are equal to zero, then additional decoding operations are performed
                        decodingRow(vector);
                        softOutputVector = vector.getSoftOutputVector();
                    }
                    DecodeUtil.setChangesForMatrix(DECODED_DATA, softOutputVector, vector.getRowNumber());
                }

                if (lastIteration == DECODED_DATA.numRows() - 1) {
                    if (halfIterationNum < 50) {
                        if (halfIterationNum % 2 == 0) {
                            IterationHistory iterationHistory = new IterationHistory(iterationDecHistory, halfIterationNum / 2, new SimpleMatrix(DECODED_DATA));
                            iterationsHistory.add(iterationHistory);
                            iterationDecHistory = new ArrayList<>();
                            boolean allElementsIsCorrect = DecodeUtil.checkMatrixForCorrectValues(DECODED_DATA);
                            if (allElementsIsCorrect) {
                                flag = true;
                            }
                        }
                        DECODED_DATA = DECODED_DATA.transpose();
                        halfIterationNum++;
                    } else {
                        flag = true;
                    }
                }
            }
        } catch (DecoderException e) {
            log.error(e);
            throw new DecoderException("toDecode: " + e.getMessage());
        }
        return this;
    }

    /*
     * The method implements decoding of the input vector if the syndrome of the input vector is not equal to zero.
     * @param vo - value object with vectors fields during decoding
     */
    private TurboCodeDecoderVO decodingRow(TurboCodeDecoderVO vo) throws DecoderException {

        List<Integer> softOutputVector = null;

        List<Integer> softInputVector = vo.getSoftInputVector();
        List<Integer> hardDecisionVector = vo.getHardDecisionVector();
        List<Integer> sindromForHardDecisionVector = vo.getSindromVector();

        // 5. Search for the 3 minimum values of the input vector
        List<TurboCodeDecoderVO.MinElementsVO> minElementsInSoftInputVector = getMinElementsValue(softInputVector);

        // 6. Calculate the syndrome for a hard decision vector
        softOutputVector = checkSindromOfHardDecisionVector(sindromForHardDecisionVector, softInputVector);

        if (softOutputVector == null) {
            vo.setHaveSindromPositionToHardVector(false);

            // 7. If the output vector was not calculated in checkSindromOfHardDecisionVector()
            // then we perform operations with test vectors. Test vectors need to be generated first.
            List<TurboCodeDecoderVO.TestVectorVO> testVectorVOList = generateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput(hardDecisionVector, minElementsInSoftInputVector);
            vo.setTestVectorsList(testVectorVOList);

            Integer zeroVectorIndex = testVectorVOList.stream()
                    .filter(TurboCodeDecoderVO.TestVectorVO::getVectorZero)
                    .findFirst()
                    .map(testVectorVOList::indexOf)
                    .orElse(null);

            if (zeroVectorIndex != null) {
                List<Integer> changedSoftInputVectorByTestVector = changeSignOfSoftInputVectorByTestVector(softInputVector, testVectorVOList.get(zeroVectorIndex));
                softOutputVector = returnSoftOutputVector(changedSoftInputVectorByTestVector, 1, null);
                vo.setChangesVectorByTest(changedSoftInputVectorByTestVector);
            } else {
                softOutputVector = checkSindromForTestVectors(softInputVector, testVectorVOList);
            }
        } else {
            vo.setHaveSindromPositionToHardVector(true);
        }

        vo.setSoftOutputVector(softOutputVector);
        vo.setMinValues(minElementsInSoftInputVector);

        return vo;
    }

    /*
     * The method generates a vector of difficult decisions, if the value is less than 0,
     * then the value of the vector is set to 1, if it is greater than 0, then 0 is set.
     */
    private List<Integer> generateHardDecisionVector(List<Integer> softInputVector) {
        return softInputVector.stream().map(_e -> _e < 0 ? 1 : 0).collect(Collectors.toList());
    }

    /*
     * The method searches for the minimum values of the input vector by casting all elements modulo.
     */
    private List<TurboCodeDecoderVO.MinElementsVO> getMinElementsValue(List<Integer> softInputVector) throws DecoderException {
        try {
            log.info("Start to find 3 min elements in softInputVector.");
            List<Integer> mathAbsElements = softInputVector.stream().map(Math::abs).collect(Collectors.toList());
            log.debug("** Make all elements from vector positive: " + Arrays.toString(mathAbsElements.toArray()));

            Integer maxValue = mathAbsElements
                    .stream()
                    .mapToInt(_e -> _e)
                    .max()
                    .orElseThrow(NoSuchElementException::new);
            Integer maxValueIndex = mathAbsElements.indexOf(maxValue);
            log.debug("** Found max value for this vector: maxValue = " + maxValue + ", maxValueIndex = " + maxValueIndex);

            List<TurboCodeDecoderVO.MinElementsVO> minElementsList = new ArrayList<>();
            minElementsList.add(new TurboCodeDecoderVO.MinElementsVO(maxValue, maxValueIndex));
            minElementsList.add(new TurboCodeDecoderVO.MinElementsVO(maxValue, maxValueIndex));
            minElementsList.add(new TurboCodeDecoderVO.MinElementsVO(maxValue, maxValueIndex));

            for (int i = 0; i < mathAbsElements.size(); i++) {
                if (mathAbsElements.get(i) < minElementsList.get(2).getMaxValue()) {
                    minElementsList.set(0, minElementsList.get(1));
                    minElementsList.set(1, minElementsList.get(2));
                    minElementsList.set(2, new TurboCodeDecoderVO.MinElementsVO(mathAbsElements.get(i), i));
                } else if (mathAbsElements.get(i) < minElementsList.get(1).getMaxValue()) {
                    minElementsList.set(0, minElementsList.get(1));
                    minElementsList.set(1, new TurboCodeDecoderVO.MinElementsVO(mathAbsElements.get(i), i));
                } else if (mathAbsElements.get(i) < minElementsList.get(0).getMaxValue()) {
                    minElementsList.set(0, new TurboCodeDecoderVO.MinElementsVO(mathAbsElements.get(i), i));
                }
            }

            log.info("** Found min elements: " + minElementsList.stream().map(TurboCodeDecoderVO.MinElementsVO::toString));
            return minElementsList;

        } catch (Exception e) {
            log.error("getMinElementsValue: " + e.getMessage());
            throw new DecoderException("getMinElementsValue: " + e.getMessage());
        }
    }

    /*
     * The method generates output vector by changing the incoming vector by +-count.
     * Then it makes a correction to the maximum correct value.
     */
    private List<Integer> returnSoftOutputVector(List<Integer> resultVector, Integer count, Integer exceptionPosition) throws DecoderException {
        List<Integer> transformedList = new ArrayList<>();
        log.info("Start to generate soft output vector:");
        log.debug("Exception position : " + exceptionPosition + ", change result vector on +-" + count);
        try {
            IntStream.range(0, resultVector.size()).forEach(i -> {
                if (exceptionPosition != null && exceptionPosition == i) {
                    transformedList.add(resultVector.get(i));
                } else if (resultVector.get(i) >= 0) {
                    transformedList.add(resultVector.get(i) + count);
                } else {
                    transformedList.add(resultVector.get(i) - count);
                }
            });

            List<Integer> softOutputVector = new ArrayList<>();
            Integer value = DecodeUtil.CV;
            IntStream.range(0, transformedList.size()).forEach(i -> {
                Integer element = transformedList.get(i);
                if (element > 0) {
                    softOutputVector.add(element >= value ? value : element);
                } else if (element < 0) {
                    softOutputVector.add(element <= -value ? -value : element);
                }
            });
            log.info("Generated soft output vector: " + Arrays.toString(softOutputVector.toArray()));
            return softOutputVector;
        } catch (Exception e) {
            log.error("returnSoftOutputVector: " + e);
            throw new DecoderException("returnSoftOutputVector: " + e.getMessage());
        }
    }


    /*
        This method searches for the position of the syndrome, if the position is found,
        then the input vector is corrected at this position, and the output vector is returned.
        If the position is not found, then null is returned.
        Params:
        @param sindromForHardDecisionVector - this list included sindrom for hard decision vector
        @param softInputVector - this list included soft input vector
     */
    private List<Integer> checkSindromOfHardDecisionVector(List<Integer> sindromForHardDecisionVector, List<Integer> softInputVector) throws DecoderException {
        log.info("Start to check sindrom of hard decision vector");
        Integer sindromPosition = DecodeUtil.findSindromPosition(sindromForHardDecisionVector);
        log.debug("sindromPosition: " + sindromPosition);
        if (sindromPosition != null) {
            List<Integer> fixedSoftInputVector = toFixSoftInputVectorBySindromPosition(softInputVector, sindromPosition);
            return returnSoftOutputVector(fixedSoftInputVector, 1, sindromPosition);
        } else {
            return null;
        }
    }

    /*
     * The method generates an output vector by searching for the syndrome position for each test vector,
     * after which it searches for the first syndrome position from all vectors.
     * If the position of the syndrome is found, the signs of the incoming vector are changed by the value
     * of the test vector and the output vector is announced. If there is no syndrome position, the output vector is returned.
     */
    private List<Integer> checkSindromForTestVectors(List<Integer> softInputVector,
                                                     List<TurboCodeDecoderVO.TestVectorVO> testVectorsVOList) throws DecoderException {
        List<Integer> softOutputVector = null;
        try {
            log.info("Start to check sindrom for test vectors");
            testVectorsVOList.forEach(testVectorVO -> {
                Integer position = DecodeUtil.findSindromPosition(testVectorVO.getSindromVector());
                testVectorVO.setSindromPosition(position);
            });

            log.info("To find first sindrom position not null");
            Integer sindromPosition = testVectorsVOList
                    .stream()
                    .filter(_t -> _t.getSindromPosition() != null)
                    .findFirst()
                    .map(TurboCodeDecoderVO.TestVectorVO::getSindromPosition)
                    .orElse(null);
            log.debug("sindromPosition: " + sindromPosition);

            if (sindromPosition != null) {
                List<Integer> correctedVector = DecodeUtil.changeSignByVector(softInputVector, testVectorsVOList.get(0).getValue());
                softOutputVector = returnSoftOutputVector(correctedVector, 1, sindromPosition);
            } else {
                softOutputVector = returnSoftOutputVector(softInputVector, -1, null);
            }

            return softOutputVector;
        } catch (Exception e) {
            log.error("checkSindromForTestVectors:" + e);
            throw new DecoderException("checkSindromForTestVectors: " + e);
        }
    }

    /*
     * The method changes the sign of the incoming vector based on the position of the syndrome
     */
    private List<Integer> toFixSoftInputVectorBySindromPosition(List<Integer> softInputVector, Integer sindromPosition) throws DecoderException {
        log.info("Start to fix soft input vector by sindrom position.");
        try {
            for (int i = 0; i < softInputVector.size(); i++) {
                if (i == sindromPosition) {
                    Integer element = softInputVector.get(i);
                    softInputVector.set(i, element == 0 ? -1 : element * -1);
                }
            }
            log.info("Generated fix vector: " + Arrays.toString(softInputVector.toArray()));
            return softInputVector;
        } catch (Exception e) {
            log.error("toFixSoftInputVectorBySindromPosition: " + e);
            throw new DecoderException("toFixSoftInputVectorBySindromPosition: " + e);
        }
    }

    /*
     * The method generates test vectors using hardDecisionVector and the minimum elements of the input elements.
     * Get the value vector from the index of the minimum elements and all variants of the test vectors.
     * A syndrome is generated for each test vector.
     */
    private List<TurboCodeDecoderVO.TestVectorVO> generateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput(List<Integer> hardDecisionVector,
                                                                                                                   List<TurboCodeDecoderVO.MinElementsVO> minElementsVOList) throws DecoderException {
        log.info("Start generate test vectors by hard decision vector:");
        List<TurboCodeDecoderVO.TestVectorVO> testVectorsVO = new ArrayList<>();
        try {
            for (int i = 0; i < DecodeUtil.VARIANTS_TO_TEST_VECTORS.length; i++) {
                List<Integer> valueVector = new ArrayList<>(hardDecisionVector);
                valueVector.set(minElementsVOList.get(0).getMaxValueIndex(), DecodeUtil.VARIANTS_TO_TEST_VECTORS[i][0]);
                valueVector.set(minElementsVOList.get(1).getMaxValueIndex(), DecodeUtil.VARIANTS_TO_TEST_VECTORS[i][1]);
                valueVector.set(minElementsVOList.get(2).getMaxValueIndex(), DecodeUtil.VARIANTS_TO_TEST_VECTORS[i][2]);
                log.info("** Generated value vector: " + Arrays.toString(valueVector.toArray()));

                List<Integer> sindromVector = DecodeUtil.generateSindromVector(valueVector);
                log.info("** Generated sindrom by value vector: " + Arrays.toString(sindromVector.toArray()));

                boolean isAllElementsSindromZero = DecodeUtil.isAllElementsInListZero(sindromVector, Integer.class);
                log.info("** isAllElementsSindromZero: " + isAllElementsSindromZero);

                TurboCodeDecoderVO.TestVectorVO testVectorVO = new TurboCodeDecoderVO.TestVectorVO(valueVector, sindromVector, isAllElementsSindromZero);
                testVectorsVO.add(testVectorVO);
            }
            if (testVectorsVO.size() > 0) {
                log.info("End generate test vectors: generated : " + testVectorsVO.size() + " vectors");
            } else {
                log.error("End generate test vectors: vectors not found");
            }
            return testVectorsVO;
        } catch (Exception e) {
            log.error("generateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput: " + e);
            throw new DecoderException("generateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput: " + e.getMessage());
        }
    }

    /*
     * The method changes the sign of the incoming vector based on the value vector of the test vector
     */
    private List<Integer> changeSignOfSoftInputVectorByTestVector(List<Integer> softInputVector, TurboCodeDecoderVO.TestVectorVO testVectorVO) throws DecoderException {
        List<Integer> changedDecodingRow = new ArrayList<>(softInputVector);
        log.info("Start to change sign of soft input vector by test vector.");
        try {
            for (int i = 0; i < changedDecodingRow.size(); i++) {
                List<Integer> testVectorValue = testVectorVO.getValue();
                if (testVectorValue.get(i) == 1 && Math.abs(changedDecodingRow.get(i)) == 0) {
                    log.debug("testVectorValue=1; changedDecodingRow=0 -> set -1 to index:" + i);
                    changedDecodingRow.set(i, -1);
                } else if (testVectorValue.get(i) == 1) {
                    log.debug("testVectorValue=1 -> set -" + changedDecodingRow.get(i) + " to index:" + i);
                    changedDecodingRow.set(i, Math.abs(changedDecodingRow.get(i)) * -1);
                } else if (testVectorValue.get(i) == 0) {
                    log.debug("testVectorValue=0 -> set " + Math.abs(changedDecodingRow.get(i)) + " to index:" + i);
                    changedDecodingRow.set(i, Math.abs(changedDecodingRow.get(i)));
                }
            }
            log.info("Generated changed vector: " + Arrays.toString(changedDecodingRow.toArray()));
            return changedDecodingRow;
        } catch (Exception e) {
            log.error("changeSignOfSoftInputVectorByTestVector: " + e.getMessage());
            throw new DecoderException("changeSignOfSoftInputVectorByTestVector: " + e.getMessage());
        }
    }

    public static class IterationHistory {
        private final List<TurboCodeDecoderVO> halfIterations;
        private final Integer iterationNum;
        private final SimpleMatrix matrixAfterIteration;

        public IterationHistory(List<TurboCodeDecoderVO> halfIterations, Integer iterationNum, SimpleMatrix matrixAfterIteration) {
            this.halfIterations = halfIterations;
            this.iterationNum = iterationNum;
            this.matrixAfterIteration = matrixAfterIteration;
        }

        public List<TurboCodeDecoderVO> getHalfIterations() {
            return halfIterations;
        }

        public Integer getIterationNum() {
            return iterationNum;
        }

        public SimpleMatrix getMatrixAfterIteration() {
            return matrixAfterIteration;
        }
    }

    public SimpleMatrix getDECODED_DATA() {
        return DECODED_DATA;
    }

    public List<IterationHistory> getIterationsHistory() {
        return iterationsHistory;
    }

}
