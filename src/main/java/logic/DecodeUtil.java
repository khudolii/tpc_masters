package logic;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecodeUtil {

    public static Integer CORRECT_VALUE = 7;
    public static Integer NUM_OF_ITERATIONS = 6;
    private static final double[][] CHECK_MATRIX_VALUE = {
            {0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1},
            {0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0},
            {0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    public static final int[][] VARIANTS_TO_TEST_VECTORS = {
            {0, 0, 0},
            {0, 0, 1},
            {0, 1, 0},
            {0, 1, 1},
            {1, 0, 0},
            {1, 0, 1},
            {1, 1, 0},
            {1, 1, 1}
    };

/*    private static final  double[][] INPUT_DATA = {
            {-7, 7, 7, -7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, -1, 7},
            {7, -7, 7, 7, 7, -2, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
            //{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
            {7, 7, -7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
            {1, 7, 7, -7, 7, 7, -4, 7, 7, 7, -1, 7, 7, 7, 7, 7},
            {7, 7, 7, 7, -7, 7, 2, -3, 7, 7, 7, 7, 7, 7, 7, 7},
            {7, 7, 7, 7, 7, -7, 7, 7, 7, 7, 7, 7, -6, 7, 7, 7},
            {7, -1, 7, 7, 7, 7, -7, 7, 7, 7, 7, 7, 7, 4, 7, 7},
            {7, 7, 7, -1, 7, 7, 3, -7, 7, 7, -2, 7, 7, -4, 7, 5},
            {7, 7, 7, 7, 7, 7, 7, 7, -7, 7, 7, 7, 7, 7, 7, 7},
            {7, 7, 7, 7, -7, 7, -4, 7, 7, -7, 7, 7, 7, 7, 7, 7},
            {7, 7, 7, 7, 7, 7, 7, 7, 7, 7, -7, 7, 7, 7, -3, 7},
            {7, 7, 7, -6, 7, 7, 7, -7, 7, -1, 7, -7, 7, 7, 7, 7},
            {7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, -7, 7, 7, -2},
            {7, 7, 7, 7, 7, 7, -3, 7, 7, 7, 7, 7, 7, -7, 7, 7},
            {7, 7, 7, 7, 7, 7, 7, 7, 7, -2, 7, 7, 7, 7, -7, 7},
            {7, 7, -2, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, -7}
    };*/

    private static final double[][] INPUT_DATA = {
            {8, 2, 1, -6, 2, 1, 3, 4, 8, 1, 2, 3, 7, 2, -1, 6},
            {7, -7, 7, 7, 7, -2, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
            //{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
            {7, 7, -7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
            {1, 7, 7, -7, 7, 7, -4, 7, 7, 7, -1, 7, 7, 7, 7, 7},
            {7, 7, 7, 7, -7, 7, 2, -3, 7, 4, 7, 7, 7, 2, 7, 7},
            {7, 7, 7, 7, 7, -7, 7, 7, 7, 7, 7, 7, -6, 7, 7, 7},
            {7, -1, 7, 7, 7, 7, -7, 7, 7, 7, 7, 7, 7, 4, 7, 7},
            {7, 7, 7, -1, 7, 7, 3, -7, 7, 7, -2, 7, 7, -4, 7, 5},
            {7, 7, 7, 7, 7, 7, 7, 7, -7, 7, 7, 7, 7, 7, 7, 7},
            {7, 7, 7, 7, -7, 7, -4, 7, 7, -7, 7, 7, 7, 7, 7, 7},
            {7, 7, 7, 7, 7, 7, 7, 7, 7, 7, -7, 7, 7, 7, -3, 7},
            {7, 7, 7, -6, 7, 7, 3, -7, 7, -1, 7, -7, 7, 7, 7, 7},
            {7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, -7, 7, 7, -2},
            {7, 7, 7, 7, 7, 7, -3, 7, 7, 7, 7, 7, 7, -7, 7, 7},
            {7, 7, 7, 7, 7, 7, 7, 7, 7, -2, 7, 7, 7, 7, -7, 7},
            {7, 7, -2, 7, 7, 7, 7, 5, 7, 7, 7, 7, 5, 7, 7, -7}
    };

    public static final SimpleMatrix CHECK_MATRIX = new SimpleMatrix(CHECK_MATRIX_VALUE);
    public static final SimpleMatrix TRANSPOSE_CHECK_MATRIX = CHECK_MATRIX.transpose();
    public static final SimpleMatrix INPUT_DATA_MATRIX = new SimpleMatrix(INPUT_DATA);

    public static boolean isAllElementsInListZero(List<?> list, Class typeOfList) {
        if (typeOfList != String.class) {
            return list.stream().allMatch(_e -> _e.equals(0));
        } else {
            return false;
        }
    }

    static Integer calculateParity(Integer parity, Integer hardDecElement, Double checkMatrixElement) {
        Integer compareHardAndCheckElement = hardDecElement.equals(1) && checkMatrixElement.equals(1.) ? 1 : 0;
        parity = parity ^ compareHardAndCheckElement;
        return parity;
    }

    static Integer findSindromPosition(List<Integer> sindromVector) {
        Integer sindromPosition = null;
        for (int i = 0; i < DecodeUtil.TRANSPOSE_CHECK_MATRIX.numRows(); i++) {
            int counter = 0;
            for (int j = 0; j < DecodeUtil.TRANSPOSE_CHECK_MATRIX.numCols(); j++) {
                if (sindromVector.get(j) == (int) DecodeUtil.TRANSPOSE_CHECK_MATRIX.get(i, j)) {
                    counter++;
                    if (counter == DecodeUtil.TRANSPOSE_CHECK_MATRIX.numCols()) {
                        sindromPosition = i;
                        break;
                    }
                }
            }
        }
        return sindromPosition;
    }

    static List<Integer> changeSignByVector(List<Integer> vectorToChange, List<Integer> valuesForChanges) {
        List<Integer> correctedRowToDecodeWithSign = new ArrayList<>(vectorToChange);
        for (int i = 0; i < vectorToChange.size(); i++) {
            if (valuesForChanges.get(i) == 0) {
                correctedRowToDecodeWithSign.set(i, Math.abs(correctedRowToDecodeWithSign.get(i)));
            } else if (valuesForChanges.get(i) == 1) {
                correctedRowToDecodeWithSign.set(i, Math.abs(correctedRowToDecodeWithSign.get(i)) * -1);
            }
        }
        return correctedRowToDecodeWithSign;
    }

    static List<Integer> generateSindromVector(List<Integer> vectorForSindrom) {
        List<Integer> sindromVector = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));
        for (int i = 0; i < DecodeUtil.CHECK_MATRIX.numRows(); i++) {
            Integer parity = 0;
            for (int j = 0; j < DecodeUtil.CHECK_MATRIX.numCols(); j++) {
                parity = calculateParity(parity, vectorForSindrom.get(j), DecodeUtil.CHECK_MATRIX.get(i, j));
            }
            sindromVector.set(i, parity);
        }
        return sindromVector;
    }

    public static SimpleMatrix setChangesForMatrix(SimpleMatrix matrix, List<Integer> changesValue, Integer numOfRow) {
        for (int i = 0; i < matrix.numRows(); i++) {
            if (i == numOfRow) {
                for (int j = 0; j < matrix.numCols(); j++) {
                    double value = changesValue.get(j);
                    matrix.set(i, j, value);
                }
            }
        }
        return matrix;
    }

    public static boolean checkMatrixForCorrectValues(SimpleMatrix matrix) {
        boolean isAllMatrixValueCorrect = true;
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                if (matrix.get(i, j) != DecodeUtil.CORRECT_VALUE) {
                    isAllMatrixValueCorrect = false;
                }
            }
        }
        return isAllMatrixValueCorrect;
    }

    public static void main(String[] args) {
        System.out.println("DecodeUtil.INPUT_DATA_MATRIX.getNumElements() = " + DecodeUtil.INPUT_DATA_MATRIX.getNumElements());
    }
}
