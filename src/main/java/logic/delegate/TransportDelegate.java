package logic.delegate;

import logic.DecodeUtil;
import logic.TurboCodeDecoder;
import logic.beans.DecoderBean;
import logic.exceptions.DecoderException;
import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import java.text.SimpleDateFormat;
import java.util.*;

public class TransportDelegate {
    private static final Logger log = Logger.getLogger(TransportDelegate.class);

    public DecoderBean startDecodingProcess(DecoderBean decoderBean) throws DecoderException {
        SimpleMatrix encodedMatrix = makeNoiseForMatrix(new SimpleMatrix(DecodeUtil.INPUT_DATA), decoderBean.getProbability());
        decoderBean.setINPUT_DATA(encodedMatrix);
        TurboCodeDecoder turboCodeDecoder = new TurboCodeDecoder(encodedMatrix);
        Date dateOfDecoding = new Date();
        String stringDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateOfDecoding);
        turboCodeDecoder.toDecode();
        decoderBean.setOUTPUT_DATA(turboCodeDecoder.getDECODED_DATA());
        decoderBean.setIterationsHistory(turboCodeDecoder.getIterationsHistory());
        decoderBean.setNumOfIterations(turboCodeDecoder.getIterationsHistory().size());
        decoderBean.setDateOfDecoding(stringDate);
        return decoderBean;
    }

    private SimpleMatrix makeNoiseForMatrix (SimpleMatrix matrix, double probability) {
        Random rand = new Random();
        int inversionsCount = (int) Math.round(matrix.numRows() * matrix.numCols() * probability);
        int maxElement = (matrix.numRows() * matrix.numCols()) - 1;
        int addPosition = 0;
        List<Integer> inversionPositions = new ArrayList<>(Collections.nCopies(inversionsCount, 0));
        for (int i = 0; i < inversionsCount; i++) {
            boolean found = false;
            if (i == inversionsCount - 5)  break;
            while (!found){
                System.out.println("------------- i = " + i);
                int randomValue = rand.nextInt(maxElement);
                System.out.println("randomValue = " + randomValue);;
                if (!inversionPositions.contains(randomValue)){
                    System.out.println("Add new random value");
                    inversionPositions.set(addPosition, randomValue);
                    addPosition++;
                    found = true;
                }
            }
        }
        for (int i = 0; i < inversionsCount; i++) {
            int col = (inversionPositions.get(i) % matrix.numCols()) + 1;
            int row = (int) Math.floor((inversionPositions.get(i) / matrix.numRows())) + 1;
            col = col >= matrix.numCols() ? col - 1 : col;
            row = row >= matrix.numRows() ? row - 1 : row;

            int element = (int) matrix.get(row, col);
            int value = (-1) * DecodeUtil.sign(element) * (Math.abs(element));
            int random = rand.nextInt(10);
            value = value > 0 ? value - random : value + random;
            matrix.set(row, col, value);
        }
        return matrix;
    }


}
