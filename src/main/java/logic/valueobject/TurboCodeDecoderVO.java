package logic.valueobject;


import java.util.List;

public class TurboCodeDecoderVO {
    private List<Integer> softInputVector;
    private List<MinElementsVO> minValues;
    private List<Integer> hardDecisionVector; //inputVector
    private List<Integer> sindromVector;
    private List<Integer> softOutputVector;
    private Integer halfIteration;
    private Integer rowNumber;
    public List<TestVectorVO> testVectorsList;
    public Boolean isAllElementsFromSindromZeroFlag;

    public Boolean isAllElementsFromSindromZero() {
        return isAllElementsFromSindromZeroFlag;
    }

    public void setAllElementsFromSindromZero(Boolean allElementsFromSindromZero) {
        isAllElementsFromSindromZeroFlag = allElementsFromSindromZero;
    }

    public TurboCodeDecoderVO(List<Integer> softInputVector) {
        this.softInputVector = softInputVector;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public List<Integer> getSoftInputVector() {
        return softInputVector;
    }

    public void setSoftInputVector(List<Integer> softInputVector) {
        this.softInputVector = softInputVector;
    }

    public List<MinElementsVO> getMinValues() {
        return minValues;
    }

    public void setMinValues(List<MinElementsVO> minValues) {
        this.minValues = minValues;
    }

    public List<Integer> getHardDecisionVector() {
        return hardDecisionVector;
    }

    public void setHardDecisionVector(List<Integer> hardDecisionVector) {
        this.hardDecisionVector = hardDecisionVector;
    }

    public List<Integer> getSindromVector() {
        return sindromVector;
    }

    public void setSindromVector(List<Integer> sindromVector) {
        this.sindromVector = sindromVector;
    }

    public List<Integer> getSoftOutputVector() {
        return softOutputVector;
    }

    public void setSoftOutputVector(List<Integer> softOutputVector) {
        this.softOutputVector = softOutputVector;
    }

    public Integer getHalfIteration() {
        return halfIteration;
    }

    public void setHalfIteration(Integer halfIteration) {
        this.halfIteration = halfIteration;
    }

    public List<TestVectorVO> getTestVectorsList() {
        return testVectorsList;
    }

    public void setTestVectorsList(List<TestVectorVO> testVectorsList) {
        this.testVectorsList = testVectorsList;
    }

    public static class TestVectorVO {
        private final List<Integer> value;
        private final List<Integer> sindromVector;
        private final Boolean isVectorZero;
        private Integer sindromPosition;

        public Integer getSindromPosition() {
            return sindromPosition;
        }

        public void setSindromPosition(Integer sindromPosition) {
            this.sindromPosition = sindromPosition;
        }

        public TestVectorVO(List<Integer> value, List<Integer> sindromVector, Boolean isVectorZero) {
            this.value = value;
            this.sindromVector = sindromVector;
            this.isVectorZero = isVectorZero;
        }

        public List<Integer> getValue() {
            return value;
        }

        public List<Integer> getSindromVector() {
            return sindromVector;
        }

        public Boolean getVectorZero() {
            return isVectorZero;
        }
    }

    public static class MinElementsVO {
        private final Integer maxValue;
        private final Integer maxValueIndex;

        public Integer getMaxValue() {
            return maxValue;
        }

        public Integer getMaxValueIndex() {
            return maxValueIndex;
        }

        public MinElementsVO(Integer maxValue, Integer maxValueIndex) {
            this.maxValue = maxValue;
            this.maxValueIndex = maxValueIndex;
        }
    }
}
