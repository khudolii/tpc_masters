package logic;

import logic.exceptions.DecoderException;
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

    TurboCodeDecoder decoder = null;

    @BeforeClass
    private void createDecoderObject() {
        decoder = new TurboCodeDecoder(new SimpleMatrix(DecodeUtil.INPUT_DATA));
    }

    @DataProvider(name = "softOutput")
    private Object[][] dataProviderForSoftOutputTests() {
        return new Object[][]{
                {Arrays.asList(7, 8, 10, 10, -11, -5, 1, 3, 10, 10, -10, 5, 6, 7, 10, 8),
                        Arrays.asList(9, 10, 10, 10, -10, -7, 3, 5, 10, 10, -10, 7, 8, 9, 10, 10),
                        2,
                        null
                },
                {Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0),
                        Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 10, 10, 10, 10, 2),
                        2,
                        null
                },
                {Arrays.asList(-1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -13, -14, -15, 0),
                        Arrays.asList(-3, -4, -5, -6, -7, -8, -9, -10, -10, -10, -10, -10, -10, -10, -10, 2),
                        2,
                        null
                },
                {Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                        Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                        2,
                        null
                },
                {Arrays.asList(-10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10),
                        Arrays.asList(-10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10),
                        2,
                        null
                },
                {Arrays.asList(1, 6, 7, 9, 2, -5, 1, 4, 0, 10, 10, -10, 2, 1, 10, 9),
                        Arrays.asList(2, 7, 8, 10, 3, -6, 2, 5, 1, 10, 10, -10, 3, 2, 10, 10),
                        1,
                        null
                },
                {Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                        Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10),
                        1,
                        null
                },
                {Arrays.asList(2, 6, 7, 9, 2, -5, 1, 4, 0, 10, 10, -10, 2, 1, 10, 9),
                        Arrays.asList(1, 5, 6, 8, 1, -4, 0, 3, -1, 9, 9, -9 ,1, 0, 9, 8),
                        -1,
                        null
                },
                {Arrays.asList(2, 6, 7, 9, 2, -5, 1, 4, 0, 10, 10, -10, 2, 1, 10, 9),
                        Arrays.asList(1, 5, 6, 8, 1, -4, 0, 3, -1, 9, 9, -9 ,1, 0, 9, 8),
                        1,
                        5
                },
        };
    }

    @Test (dataProvider = "softOutput")
    private void softOutputTests (List<Integer> testData, List<Integer> correctData, int count, Integer syndromePosition ) {
      /*  List<Integer> testVector = new ArrayList<>(testData);
        List<Integer> correctVector = new ArrayList<>(correctData);
        try {
            List<Integer> resultVector = decoder.returnSoftOutputVector(testVector, count, syndromePosition);
            assertEquals(resultVector, correctVector);
        } catch (DecoderException e) {
            fail("softOutputTests");
            e.printStackTrace();
        }*/
    }

}
