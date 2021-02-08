package logic;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecodeUtil {
    private static final Logger log = Logger.getLogger(DecodeUtil.class);

    /*CORRECT VALUE*/
    public static final Integer CV = 10;

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

    public static final double[][] INPUT_DATA = {
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
            {CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV, CV},
    };

    public static final SimpleMatrix CHECK_MATRIX = new SimpleMatrix(CHECK_MATRIX_VALUE);
    public static final SimpleMatrix TRANSPOSE_CHECK_MATRIX = CHECK_MATRIX.transpose();

    public static boolean isAllElementsInListZero(List<?> list, Class typeOfList) {
        log.info("Сhecking the vector for the fact that all elements are equal to zero.");
        if (typeOfList != String.class) {
            return list.stream().allMatch(_e -> _e.equals(0));
        } else {
            return false;
        }
    }

    static Integer calculateParity(Integer parity, Integer hardDecElement, Double checkMatrixElement) {
        log.debug("Start calculate parity");
        log.debug("oldParity:" + parity + "; hardDecElement:" + hardDecElement + " checkMatrixElement:" + checkMatrixElement);
        Integer compareHardAndCheckElement = hardDecElement.equals(1) && checkMatrixElement.equals(1.) ? 1 : 0;
        parity = parity ^ compareHardAndCheckElement;
        log.debug("Calculated parity: " + parity);
        return parity;
    }

    static Integer findSindromPosition(List<Integer> sindromVector) {
        log.info("Start to find sindrom position with sindrom vector = " + Arrays.toString(sindromVector.toArray()));
        Integer sindromPosition = null;
        for (int i = 0; i < DecodeUtil.TRANSPOSE_CHECK_MATRIX.numRows(); i++) {
            int counter = 0;
            for (int j = 0; j < DecodeUtil.TRANSPOSE_CHECK_MATRIX.numCols(); j++) {
                if (sindromVector.get(j) == (int) DecodeUtil.TRANSPOSE_CHECK_MATRIX.get(i, j)) {
                    counter++;
                    log.debug("Element # " + j + " in sindrom == element # (" + i + ", " + j + ") in check matrix. counter: " + counter);
                    if (counter == DecodeUtil.TRANSPOSE_CHECK_MATRIX.numCols()) {
                        sindromPosition = i;
                        log.debug("Counter == chek matrix length -> sindromPosition: " + i);
                        break;
                    }
                }
            }
        }
        log.info("Sindrom Position is " + sindromPosition);
        return sindromPosition;
    }

    static List<Integer> changeSignByVector(List<Integer> vectorToChange, List<Integer> valuesForChanges) {
        log.info("Start to change sign for vector:" + Arrays.toString(vectorToChange.toArray()) + "," +
                " by values: " + Arrays.toString(valuesForChanges.toArray()));

        List<Integer> correctedVectorWithSign = new ArrayList<>(vectorToChange);
        for (int i = 0; i < vectorToChange.size(); i++) {
            if (valuesForChanges.get(i) == 0) {
                log.debug("Value = 0, set to corrected vector " + correctedVectorWithSign.get(i) + " for index = " + i);
                correctedVectorWithSign.set(i, Math.abs(correctedVectorWithSign.get(i)));
            } else if (valuesForChanges.get(i) == 1) {
                log.debug("Value = 1, set to corrected vector -" + correctedVectorWithSign.get(i) + " for index = " + i);
                correctedVectorWithSign.set(i, Math.abs(correctedVectorWithSign.get(i)) * -1);
            }
        }
        log.info("Generated changed vector: " + Arrays.toString(correctedVectorWithSign.toArray()));
        return correctedVectorWithSign;
    }

    static List<Integer> generateSindromVector(List<Integer> vectorForSindrom) {
        log.info("Start generate sindrom for vector: " + Arrays.toString(vectorForSindrom.toArray()));
        List<Integer> sindromVector = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));
        for (int i = 0; i < DecodeUtil.CHECK_MATRIX.numRows(); i++) {
            Integer parity = 0;
            for (int j = 0; j < DecodeUtil.CHECK_MATRIX.numCols(); j++) {
                parity = calculateParity(parity, vectorForSindrom.get(j), DecodeUtil.CHECK_MATRIX.get(i, j));
            }
            sindromVector.set(i, parity);
        }
        log.info("Found sindrom vector: " + Arrays.toString(sindromVector.toArray()));
        return sindromVector;
    }

    public synchronized static SimpleMatrix setChangesForMatrix(SimpleMatrix matrix, List<Integer> changesValue, Integer numOfRow) {
        log.info("Start to set changes to matrix in row#: " + numOfRow + " values: " + Arrays.toString(changesValue.toArray()));
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
                if (matrix.get(i, j) != DecodeUtil.CV) {
                    isAllMatrixValueCorrect = false;
                }
            }
        }
        return isAllMatrixValueCorrect;
    }

    public static int sign(int x) {
        if (x > 0)
            return 1;
        else if (x < 0)
            return -1;
        return 0;
    }
}
