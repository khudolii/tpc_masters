package logic.delegate;

import logic.DecodeUtil;
import logic.TurboCodeDecoder;
import logic.beans.DecoderBean;
import logic.exceptions.DecoderException;
import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransportDelegate {
    private static final Logger log = Logger.getLogger(TransportDelegate.class);

    public DecoderBean startDecodingProcess(DecoderBean decoderBean) throws DecoderException {
        SimpleMatrix encodedMatrix = new SimpleMatrix(decoderBean.getINPUT_DATA());
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
}
