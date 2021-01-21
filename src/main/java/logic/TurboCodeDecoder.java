package logic;


import logic.delegate.TransportDelegate;
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

public class TurboCodeDecoder implements Decoder {
    private static final Logger log = Logger.getLogger(TurboCodeDecoder.class);

    private final SimpleMatrix ENCODED_DATA;
    private SimpleMatrix DECODED_DATA;
    private List<IterationHistory> iterationsHistory;
    private Integer halfIterationNum = 1;

    public TurboCodeDecoder(SimpleMatrix encoded_data) {
        ENCODED_DATA = encoded_data;
        log.info("Create TurboCodeDecor object - > " + encoded_data.toString());
    }

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

                    List<Integer> hardDecisionVector = generateHardDecisionVector(decodedRowVO.getSoftInputVector());
                    log.info("Found hard decision vector for row # " + i);

                    List<Integer> sindromForHardDecisionVector = DecodeUtil.generateSindromVector(hardDecisionVector);
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
                    System.out.println("SoftInputOld" + Arrays.toString(vector.getSoftInputVector().toArray()) + "; numRow" + vector.getRowNumber());
                    List<Integer> softOutputVector = null;
                    if (vector.isAllElementsFromSindromZero()) {
                        softOutputVector = returnSoftOutputVector(vector.getSoftInputVector(), 2, null);
                        vector.setSoftOutputVector(softOutputVector);
                    } else {
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
            e.printStackTrace();
            throw new DecoderException("toDecode: " + e.getMessage());
        }
        return this;
    }

    private TurboCodeDecoderVO decodingRow(TurboCodeDecoderVO vo) throws DecoderException {
        List<Integer> softOutputVector = null;

        List<Integer> softInputVector = vo.getSoftInputVector();
        List<Integer> hardDecisionVector = vo.getHardDecisionVector();
        List<TurboCodeDecoderVO.MinElementsVO> minElementsInSoftInputVector = getMinElementsValue(softInputVector);
        List<Integer> sindromForHardDecisionVector = vo.getSindromVector();

        softOutputVector = checkSindromOfHardDecisionVector(sindromForHardDecisionVector, softInputVector); //checkSindromOfInputVectorInMATRIX
        if (softOutputVector == null) {
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
        }

        vo.setSoftOutputVector(softOutputVector);
        vo.setMinValues(minElementsInSoftInputVector);

        return vo;
    }

    private List<Integer> generateHardDecisionVector(List<Integer> softInputVector) {
        return softInputVector.stream().map(_e -> _e < 0 ? 1 : 0).collect(Collectors.toList());
    }

    private List<TurboCodeDecoderVO.MinElementsVO> getMinElementsValue(List<Integer> softInputVector) throws DecoderException {
        try {
            List<Integer> mathAbsElements = softInputVector.stream().map(Math::abs).collect(Collectors.toList());

            Integer maxValue = mathAbsElements
                    .stream()
                    .mapToInt(_e -> _e)
                    .max()
                    .orElseThrow(NoSuchElementException::new);
            Integer maxValueIndex = mathAbsElements.indexOf(maxValue);

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
            return minElementsList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new DecoderException("getMinElementsValue: " + e.getMessage());
        }
    }

    private List<Integer> returnSoftOutputVector(List<Integer> resultVector, Integer count, Integer exceptionPosition) throws DecoderException {
        List<Integer> transformedList = new ArrayList<>();
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
            return softOutputVector;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DecoderException("returnSoftOutputVector: " + e.getMessage());
        }
    }

    private List<Integer> checkSindromOfHardDecisionVector(List<Integer> sindromForHardDecisionVector, List<Integer> softInputVector) throws DecoderException {
        Integer sindromPosition = DecodeUtil.findSindromPosition(sindromForHardDecisionVector);
        if (sindromPosition != null) {
            List<Integer> fixedSoftInputVector = toFixSoftInputVectorBySindromPosition(softInputVector, sindromPosition);
            return returnSoftOutputVector(fixedSoftInputVector, 1, sindromPosition);
        } else {
            return null;
        }
    }

    private List<Integer> checkSindromForTestVectors(List<Integer> softInputVector, List<TurboCodeDecoderVO.TestVectorVO> testVectorsVOList) throws DecoderException {
        List<Integer> softOutputVector = null;
        try {
            testVectorsVOList.forEach(testVectorVO -> {
                Integer position = DecodeUtil.findSindromPosition(testVectorVO.getSindromVector());
                testVectorVO.setSindromPosition(position);
            });

            Integer sindromPosition = testVectorsVOList
                    .stream()
                    .filter(_t -> _t.getSindromPosition() != null)
                    .findFirst()
                    .map(TurboCodeDecoderVO.TestVectorVO::getSindromPosition)
                    .orElse(null);

            if (sindromPosition != null) {
                List<Integer> correctedVector = DecodeUtil.changeSignByVector(softInputVector, testVectorsVOList.get(0).getValue());
                softOutputVector = returnSoftOutputVector(correctedVector, 1, sindromPosition);
            } else {
                softOutputVector = returnSoftOutputVector(softInputVector, -1, null);
            }
            return softOutputVector;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DecoderException("checkSindromForTestVectors: " + e.getMessage());
        }
    }

    private List<Integer> toFixSoftInputVectorBySindromPosition(List<Integer> softInputVector, Integer sindromPosition) throws DecoderException {
        try {
            for (int i = 0; i < softInputVector.size(); i++) {
                if (i == sindromPosition) {
                    Integer element = softInputVector.get(i);
                    softInputVector.set(i, element == 0 ? -1 : element * -1);
                }
            }
            return softInputVector;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DecoderException("toFixSoftInputVectorBySindromPosition: " + e.getMessage());
        }
    }

    private List<TurboCodeDecoderVO.TestVectorVO> generateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput(List<Integer> hardDecisionVector,
                                                                                                                   List<TurboCodeDecoderVO.MinElementsVO> minElementsVOList) throws DecoderException {

        List<TurboCodeDecoderVO.TestVectorVO> testVectorsVO = new ArrayList<>();
        try {
            for (int i = 0; i < DecodeUtil.VARIANTS_TO_TEST_VECTORS.length; i++) {
                List<Integer> valueVector = new ArrayList<>(hardDecisionVector);
                valueVector.set(minElementsVOList.get(0).getMaxValueIndex(), DecodeUtil.VARIANTS_TO_TEST_VECTORS[i][0]);
                valueVector.set(minElementsVOList.get(1).getMaxValueIndex(), DecodeUtil.VARIANTS_TO_TEST_VECTORS[i][1]);
                valueVector.set(minElementsVOList.get(2).getMaxValueIndex(), DecodeUtil.VARIANTS_TO_TEST_VECTORS[i][2]);

                List<Integer> sindromVector = DecodeUtil.generateSindromVector(valueVector);
                boolean isAllElementsSindromZero = DecodeUtil.isAllElementsInListZero(sindromVector, Integer.class);

                TurboCodeDecoderVO.TestVectorVO testVectorVO = new TurboCodeDecoderVO.TestVectorVO(valueVector, sindromVector, isAllElementsSindromZero);
                testVectorsVO.add(testVectorVO);
            }
            return testVectorsVO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DecoderException("generateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput: " + e.getMessage());
        }
    }

    private List<Integer> changeSignOfSoftInputVectorByTestVector(List<Integer> softInputVector, TurboCodeDecoderVO.TestVectorVO testVectorVO) throws DecoderException {
        List<Integer> changedDecodingRow = new ArrayList<>(softInputVector);
        try {
            for (int i = 0; i < changedDecodingRow.size(); i++) {
                List<Integer> testVectorValue = testVectorVO.getValue();
                if (testVectorValue.get(i) == 1 && Math.abs(changedDecodingRow.get(i)) == 0) {
                    changedDecodingRow.set(i, -1);
                } else if (testVectorValue.get(i) == 1) {
                    changedDecodingRow.set(i, Math.abs(changedDecodingRow.get(i)) * -1);
                } else if (testVectorValue.get(i) == 0) {
                    changedDecodingRow.set(i, Math.abs(changedDecodingRow.get(i)));
                }
            }
            return changedDecodingRow;
        } catch (Exception e) {
            e.printStackTrace();
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
