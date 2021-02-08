package logic;

import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

public class DecodeUtilTest {
    private static final Logger log = Logger.getLogger(DecodeUtilTest.class);

    @DataProvider(name = "allElementsZero")
    private Object[][] dataProviderForTestIsAllElementsInListZero() {
        return new Object[][]{
                {
                        Arrays.asList(7, 8, 10, 10, -11, -5),
                        false,
                        Integer.class
                },
                {
                        Arrays.asList(0, 0, 0, 0, 0, 0),
                        true,
                        Integer.class
                },
                {
                        Arrays.asList(0, 0, 0, 0, 0, 0),
                        false,
                        String.class
                },
        };
    }

    @Test(dataProvider = "allElementsZero")
    public void testIsAllElementsInListZero(List<Integer> testData, Boolean expectedResult, Class clazz) {
        log.info("----- Start test - testIsAllElementsInListZero ----");
        log.info("Test value: " + Arrays.toString(testData.toArray()));
        log.info("Expected value: " + expectedResult);

        Boolean actualResult = DecodeUtil.isAllElementsInListZero(testData, clazz);
        log.info("Actual value: " + actualResult);
        assertEquals(actualResult, expectedResult);
    }

    @DataProvider(name = "parity")
    private Object[][] dataProviderForTestCalculateParity() {
        return new Object[][]{
                {0, 0, 0, 0},
                {0, 0, 1, 1},
                {0, 1, 0, 1},
                {0, 1, 1, 0},
                {1, 0, 0, 1},
                {1, 0, 1, 0},
                {1, 1, 0, 0},
                {1, 1, 1, 1},
        };
    }

    @Test(dataProvider = "parity")
    public void testCalculateParity(int parity, int hardDecElement, double checkMatrixElement, int expectedResult) {
        log.info("----- Start test - testCalculateParity ----");
        log.info("Test value: parity = " + parity + ", hardDecElement = " + hardDecElement + ", checkMatrixElement = " + checkMatrixElement);
        log.info("Expected value: " + expectedResult);

        int actualResult = DecodeUtil.calculateParity(parity, hardDecElement, checkMatrixElement);
        log.info("Actual value: " + actualResult);
        assertEquals(actualResult, expectedResult);
    }

    @DataProvider(name = "sindromPosition")
    private Object[][] dataProviderForTestFindSindromPosition() {
        return new Object[][]{
                { new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1)), 0 },
                { new ArrayList<>(Arrays.asList(1, 0, 0, 0, 1)), 1 },
                { new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0)), null },
                { new ArrayList<>(Arrays.asList(0, 0, 0, 1, 1)), 4 },
                { new ArrayList<>(Arrays.asList(1, 0, 0, 1, 1)), 15 },
        };
    }

    @Test(dataProvider = "sindromPosition")
    public void testFindSindromPosition(List<Integer> testData, Integer expectedResult) {
        log.info("----- Start test - testFindSindromPosition ----");
        log.info("Test value: " + Arrays.toString(testData.toArray()));
        log.info("Expected value: " + expectedResult);

        Integer actualResult = DecodeUtil.findSindromPosition(testData);
        log.info("Actual value: " + actualResult);
        assertEquals(actualResult, expectedResult);
    }

    @DataProvider(name = "changeSignByVector")
    private Object[][] dataProviderForTestChangeSignByVector() {
        return new Object[][]{
                {
                    new ArrayList<>(Arrays.asList(7, 8, 10, 10, -11, -5, 1, 3, 10, 10, -10, 5, 6, 7, 10, 8)),
                    new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0)),
                    new ArrayList<>(Arrays.asList(7, 8, 10, 10, -11, 5, 1, 3, 10, 10, 10, 5, -6, 7, 10, 8)),
                },
                {
                    new ArrayList<>(Arrays.asList(-2, -8, -5, -4, -2, -5, -1, -3, -2, -5, -10, -5, -6, -7, -1, -8)),
                    new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)),
                    new ArrayList<>(Arrays.asList(2, 8, 5, 4, 2, 5, 1, 3, 2, 5, 10, 5, 6, 7, 1, 8)),
                },
                {
                    new ArrayList<>(Arrays.asList(-2, -8, -5, -4, -2, -5, -1, -3, -2, -5, -10, -5, -6, -7, -1, -8)),
                    new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)),
                    new ArrayList<>(Arrays.asList(-2, -8, -5, -4, -2, -5, -1, -3, -2, -5, -10, -5, -6, -7, -1, -8)),
                },
                {
                    new ArrayList<>(Arrays.asList(2, 8, 5, 4, 2, 5, 1, 3, 2, 5, 10, 5, 6, 7, 1, 8)),
                    new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)),
                    new ArrayList<>(Arrays.asList(-2, -8, -5, -4, -2, -5, -1, -3, -2, -5, -10, -5, -6, -7, -1, -8)),
                }
        };
    }
    @Test (dataProvider = "changeSignByVector")
    public void testChangeSignByVector(List<Integer> vectorToChange, List<Integer> valuesForChanges, List<Integer> expectedResult) {
        log.info("----- Start test - testChangeSignByVector ----");
        log.info("Test value: vectorToChange = " + Arrays.toString(vectorToChange.toArray()) + ", valuesForChanges = "  + Arrays.toString(valuesForChanges.toArray()));
        log.info("Expected value: " + expectedResult);

        List<Integer> actualResult = DecodeUtil.changeSignByVector(vectorToChange, valuesForChanges);
        log.info("Actual value: " + actualResult);
        assertEquals(actualResult, expectedResult);
    }

    @DataProvider(name = "sindromVector")
    private Object[][] dataProviderForTestGenerateSindromVector() {
        return new Object[][]{
                {
                    new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1)),
                    new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0))
                },
                {
                    new ArrayList<>(Arrays.asList(0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0)),
                    new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1))
                },
                {
                    new ArrayList<>(Arrays.asList(1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1)),
                    new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1))
                },
                {
                    new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)),
                    new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0))
                },
        };
    }
    @Test (dataProvider = "sindromVector")
    public void testGenerateSindromVector(List<Integer> testData, List<Integer> expectedResult) {
        log.info("----- Start test - testGenerateSindromVector ----");
        log.info("Test value: " + Arrays.toString(testData.toArray()));
        log.info("Expected value: " + expectedResult);

        List<Integer> actualResult = DecodeUtil.generateSindromVector(testData);
        log.info("Actual value: " + actualResult);
        assertEquals(actualResult, expectedResult);
    }
}