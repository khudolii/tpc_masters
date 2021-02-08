package logic;

import logic.exceptions.DecoderException;
import logic.valueobject.TurboCodeDecoderVO;
import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class TurboCodeDecoderTest {

    private static final Logger log = Logger.getLogger(TurboCodeDecoderTest.class);
    private TurboCodeDecoder decoder = null;

    @BeforeClass
    private void createDecoderObject() {
        decoder = new TurboCodeDecoder(new SimpleMatrix(DecodeUtil.INPUT_DATA));
    }

    @DataProvider(name = "softOutput")
    private Object[][] dataProviderForSoftOutputTests() {
        return new Object[][]{
                {
                        Arrays.asList(7, 8, 10, 10, -11, -5, 1, 3, 10, 10, -10, 5, 6, 7, 10, 8),
                        Arrays.asList(9, 10, 10, 10, -10, -7, 3, 5, 10, 10, -10, 7, 8, 9, 10, 10),
                        2,
                        null
                },
                {
                        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0),
                        Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 10, 10, 10, 10, 2),
                        2,
                        null
                },
                {
                        Arrays.asList(-1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -13, -14, -15, 0),
                        Arrays.asList(-3, -4, -5, -6, -7, -8, -9, -10, -10, -10, -10, -10, -10, -10, -10, 2),
                        2,
                        null
                },
                {
                        Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                        Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                        2,
                        null
                },
                {
                        Arrays.asList(-10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10),
                        Arrays.asList(-10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10),
                        2,
                        null
                },
                {
                        Arrays.asList(1, 6, 7, 9, 2, -5, 1, 4, 0, 10, 10, -10, 2, 1, 10, 9),
                        Arrays.asList(2, 7, 8, 10, 3, -6, 2, 5, 1, 10, 10, -10, 3, 2, 10, 10),
                        1,
                        null
                },
                {
                        Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                        Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                        1,
                        null
                },
                {
                        Arrays.asList(2, 6, 7, 9, 2, -5, 1, 4, 0, 10, 10, -10, 2, 1, 10, 9),
                        Arrays.asList(1, 5, 6, 8, 1, -4, 0, 3, -1, 9, 9, -9, 1, 0, 9, 8),
                        -1,
                        null
                },
                {
                        Arrays.asList(2, 6, 7, 9, 2, -5, 1, 4, 0, 10, 10, -10, 2, 1, 10, 9),
                        Arrays.asList(3, 7, 8, 10, 3, -5, 2, 5, 1, 10, 10, -10, 3, 2, 10, 10),
                        1,
                        5
                },
        };
    }

    @Test(dataProvider = "softOutput")
    private void softOutputTests(List<Integer> testData, List<Integer> correctData, int count, Integer syndromePosition) {
        log.info("----- Start test - softOutput ----");
        log.info("Test value: " + Arrays.toString(testData.toArray()));
        log.info("Expected value: " + Arrays.toString(correctData.toArray()));

        try {
            List<Integer> resultVector = decoder.returnSoftOutputVector(testData, count, syndromePosition);
            log.info("Actual value: " + Arrays.toString(resultVector.toArray()));
            assertEquals(resultVector, correctData);
        } catch (DecoderException e) {
            log.error("softOutputTests" + e);
            fail("softOutputTests");
        }
    }

    @DataProvider(name = "hardDecision")
    private Object[][] dataProviderForGenerateHardDecisionTests() {
        return new Object[][]{
                {
                        Arrays.asList(7, 8, 10, 10, -11, -5, 1, 3, 10, 10, -10, 5, 6, 7, 10, 8),
                        Arrays.asList(0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
                },
                {
                        Arrays.asList(5, 6, 8, 10, 11, 5, 1, 3, 10, 10, 10, 8, 1, 4, 9, 8),
                        Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                },
                {
                        Arrays.asList(-7, -8, -10, -10, -11, -5, -1, -3, -10, -10, -10, -5, -6, -7, -10, -8),
                        Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                },
                {
                        Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                        Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                },
        };
    }

    @Test(dataProvider = "hardDecision")
    private void generateHardDecisionTests(List<Integer> testData, List<Integer> correctData) {
        log.info("----- Start test - hardDecision ----");
        log.info("Test value: " + Arrays.toString(testData.toArray()));
        log.info("Expected value: " + Arrays.toString(correctData.toArray()));

        List<Integer> resultVector = decoder.generateHardDecisionVector(testData);
        log.info("Actual value: " + Arrays.toString(resultVector.toArray()));
        assertEquals(resultVector, correctData);
    }

    @DataProvider(name = "minValues")
    private Object[][] dataProviderForGetMinElementsValueTests() {
        return new Object[][]{
                {
                        Arrays.asList(7, 8, 10, 10, -11, -5, 1, 3, 10, 10, -10, 5, 6, 7, 10, 8),
                        Arrays.asList(
                                new TurboCodeDecoderVO.MinElementsVO(5, 5),
                                new TurboCodeDecoderVO.MinElementsVO(3, 7),
                                new TurboCodeDecoderVO.MinElementsVO(1, 6)
                        ),
                },
                {
                        Arrays.asList(-7, -8, -10, -10, -11, -5, -1, -3, -10, -10, -10, -5, -6, -7, -10, -8),
                        Arrays.asList(
                                new TurboCodeDecoderVO.MinElementsVO(5, 5),
                                new TurboCodeDecoderVO.MinElementsVO(3, 7),
                                new TurboCodeDecoderVO.MinElementsVO(1, 6)
                        ),
                },
                {
                        Arrays.asList(5, 6, 8, 10, 11, 5, 1, 3, 10, 10, 10, 8, 1, 4, 9, 8),
                        Arrays.asList(
                                new TurboCodeDecoderVO.MinElementsVO(3, 7),
                                new TurboCodeDecoderVO.MinElementsVO(1, 12),
                                new TurboCodeDecoderVO.MinElementsVO(1, 6)
                        ),
                },
                {
                        Arrays.asList(-1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -13, -14, -15, 0),
                        Arrays.asList(
                                new TurboCodeDecoderVO.MinElementsVO(2, 1),
                                new TurboCodeDecoderVO.MinElementsVO(1, 0),
                                new TurboCodeDecoderVO.MinElementsVO(0, 15)
                        ),
                },
        };
    }

    @Test(dataProvider = "minValues")
    private void getMinElementsValueTests(List<Integer> testData, List<TurboCodeDecoderVO.MinElementsVO> correctData) {
        log.info("----- Start test - minValues ----");
        log.info("Test value: " + Arrays.toString(testData.toArray()));
        log.info("Expected value: " + Arrays.toString(correctData.toArray()));

        try {
            List<TurboCodeDecoderVO.MinElementsVO> resultVector = decoder.getMinElementsValue(testData);
            log.info("Actual value: " + Arrays.toString(resultVector.toArray()));
            for (int i = 0; i < resultVector.size(); i++) {
                assertEquals(resultVector.get(i).getMaxValue(), correctData.get(i).getMaxValue());
                assertEquals(resultVector.get(i).getMaxValueIndex(), correctData.get(i).getMaxValueIndex());
            }
        } catch (DecoderException e) {
            log.error("getMinElementsValueTests" + e);
            fail("getMinElementsValueTests");
        }
    }

    @DataProvider(name = "testVectors")
    private Object[][] dataProviderForTestGenerateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput() {
        return new Object[][]{
                {
                        Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0),
                        Arrays.asList(
                                new TurboCodeDecoderVO.MinElementsVO(5, 5),
                                new TurboCodeDecoderVO.MinElementsVO(3, 7),
                                new TurboCodeDecoderVO.MinElementsVO(1, 6)
                        ),
                        Arrays.asList(
                                new TurboCodeDecoderVO.TestVectorVO(
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0)),
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0)),
                                        true),
                                new TurboCodeDecoderVO.TestVectorVO(
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0)),
                                        new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1)),
                                        false),
                                new TurboCodeDecoderVO.TestVectorVO(
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0)),
                                        new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1)),
                                        false),
                                new TurboCodeDecoderVO.TestVectorVO(
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0)),
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0)),
                                        true),
                                new TurboCodeDecoderVO.TestVectorVO(
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0)),
                                        new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1)),
                                        false),
                                new TurboCodeDecoderVO.TestVectorVO(
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0)),
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0)),
                                        true),
                                new TurboCodeDecoderVO.TestVectorVO(
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0)),
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0)),
                                        true),
                                new TurboCodeDecoderVO.TestVectorVO(
                                        new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0)),
                                        new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1)),
                                        false)
                        ),
                },
        };
    }

    @Test(dataProvider = "testVectors")
    public void testGenerateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput(List<Integer> hardDecisionVector,
                                                                                     List<TurboCodeDecoderVO.MinElementsVO> minElementsVOList,
                                                                                     List<TurboCodeDecoderVO.TestVectorVO> expectedResult) {
        log.info("----- Start test - testGenerateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput ----");
        log.info("Test value: hardDecisionVector = " + Arrays.toString(hardDecisionVector.toArray()) + ", minElementsVOList = " + Arrays.toString(minElementsVOList.toArray()));
        log.info("Expected value: " + Arrays.toString(expectedResult.toArray()));

        try {
            List<TurboCodeDecoderVO.TestVectorVO> resultVector = decoder.generateTestVectorsByHardDecisionVectorAndMinElementsInSoftInput(hardDecisionVector, minElementsVOList);
            log.info("Actual value: " + Arrays.toString(resultVector.toArray()));
            for (int i = 0; i < resultVector.size(); i++) {
                assertEquals(resultVector.get(i).getValue(), expectedResult.get(i).getValue());
                assertEquals(resultVector.get(i).getSindromVector(), expectedResult.get(i).getSindromVector());
            }
        } catch (DecoderException e) {
            log.error("getMinElementsValueTests" + e);
            fail("getMinElementsValueTests");
        }
    }

}
