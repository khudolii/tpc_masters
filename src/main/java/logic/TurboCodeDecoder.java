package logic;


import logic.valueobject.TurboCodeDecoderVO;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TurboCodeDecoder implements Decoder {

    private final SimpleMatrix ENCODED_DATA;
    private SimpleMatrix DECODED_DATA;
    private List<IterationHistory> iterationsHistory;
    private Integer halfIterationNum = 1;

    public TurboCodeDecoder(SimpleMatrix encoded_data) {
        ENCODED_DATA = encoded_data;
    }

    public String toDecode() {
        iterationsHistory = new ArrayList<>();
        DECODED_DATA = new SimpleMatrix(ENCODED_DATA);

        boolean flag = false;

        while (!flag) {
            int lastIteration = 0;

            List<TurboCodeDecoderVO> iterationDecHistory = new ArrayList<>();

            for (int i = 0; i < DECODED_DATA.numRows(); i++) {

                List<Integer> inputToDecode = new ArrayList<>();
                for (int j = 0; j < DECODED_DATA.numCols(); j++) {
                    inputToDecode.add((int) DECODED_DATA.get(i, j));
                }

                TurboCodeDecoderVO decodedRowVO = new TurboCodeDecoderVO(inputToDecode);

                List<Integer> hardDecisionVector = generateHardDecisionVector(decodedRowVO.getSoftInputVector());
                List<Integer> sindromForHardDecisionVector = DecodeUtil.generateSindromVector(hardDecisionVector);
                boolean isAllElementsFromSindromZero = DecodeUtil.isAllElementsInListZero(sindromForHardDecisionVector, Integer.class);

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
                    softOutputVector = returnSoftOutputVector(vector.getSoftInputVector(), 2, null);
                    vector.setSoftOutputVector(softOutputVector);
                } else {
                    TurboCodeDecoderVO rowToDecode = decodingRow(vector);
                    softOutputVector = rowToDecode.getSoftOutputVector();
                    iterationDecHistory.set(vector.getRowNumber(), rowToDecode);
                }
                DecodeUtil.setChangesForMatrix(DECODED_DATA, softOutputVector, vector.getRowNumber());
            }

            if (lastIteration == DECODED_DATA.numRows() - 1) {
                if (halfIterationNum < 12) {
                    if (halfIterationNum % 2 == 0) {
                        System.out.println("halfIterationNum = " + halfIterationNum/2);
                        System.out.println(DECODED_DATA);
                        IterationHistory iterationHistory = new IterationHistory(iterationDecHistory, halfIterationNum / 2, new SimpleMatrix(DECODED_DATA));
                        iterationsHistory.add(iterationHistory);
                        boolean allElementsIsCorrect = DecodeUtil.checkMatrixForCorrectValues(DECODED_DATA);
                        if (allElementsIsCorrect){
                            System.out.println("ALL TRUE");
                            flag = true;
                        }
                    }
                    DECODED_DATA = DECODED_DATA.transpose();
                    halfIterationNum++;
                } else {
                    System.out.println("Done");
                    flag = true;
                }
            }
        }

        return null;
    }

    private TurboCodeDecoderVO decodingRow(TurboCodeDecoderVO vo) {
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

    private List<TurboCodeDecoderVO.MinElementsVO> getMinElementsValue(List<Integer> softInputVector) {
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
    }

    private List<Integer> returnSoftOutputVector(List<Integer> resultVector, Integer count, Integer exceptionPosition) {
        List<Integer> transformedList = new ArrayList<>();

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
        Integer value = DecodeUtil.CORRECT_VALUE;
        IntStream.range(0, transformedList.size()).forEach(i -> {
            Integer element = transformedList.get(i);
            if (element > 0) {
                softOutputVector.add(element >= value ? value : element);
            } else if (element < 0) {
                softOutputVector.add(element <= -value ? -value : element);
            }
        });

        return softOutputVector;
    }

    private List<Integer> checkSindromOfHardDecisionVector(List<Integer> sindromForHardDecisionVector, List<Integer> softInputVector) {
        Integer sindromPosition = DecodeUtil.findSindromPosition(sindromForHardDecisionVector);
        if (sindromPosition != null) {
            List<Integer> fixedSoftInputVector = toFixSoftInputVectorBySindromPosition(softInputVector, sindromPosition);
            return returnSoftOutputVector(fixedSoftInputVector, 1, sindromPosition);
        } else {
            return null;
        }
    }

    private List<Integer> checkSindromForTestVectors(List<Integer> softInputVector, List<TurboCodeDecoderVO.TestVectorVO> testVectorsVOList) {
        List<Integer> softOutputVector = null;

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
    }

    private List<Integer> toFixSoftInputVectorBySindromPosition(List<Integer> softInputVector, Integer sindromPosition) {
        for (int i = 0; i < softInputVector.size(); i++) {
            if (i == sindromPosition) {
                Integer element = softInputVector.get(i);
                softInputVector.set(i, element == 0 ? -1 : element * -1);
            }
        }
        return softInputVector;
    }

    private List<TurboCodeDecoderVO.TestVectorVO> generateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput(List<Integer> hardDecisionVector,
                                                                                                                   List<TurboCodeDecoderVO.MinElementsVO> minElementsVOList) {

        List<TurboCodeDecoderVO.TestVectorVO> testVectorsVO = new ArrayList<>();
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
    }

    private List<Integer> changeSignOfSoftInputVectorByTestVector(List<Integer> softInputVector, TurboCodeDecoderVO.TestVectorVO testVectorVO) {
        List<Integer> changedDecodingRow = new ArrayList<>(softInputVector);
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
    }

    private static class IterationHistory {
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
}
