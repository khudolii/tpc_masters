package logic.delegate;

import logic.DecodeUtil;
import logic.TurboCodeDecoder;

public class TransportDelegate {
    public static void main(String[] args) {
        TurboCodeDecoder turboCodeDecoder = new TurboCodeDecoder(DecodeUtil.INPUT_DATA_MATRIX);
        turboCodeDecoder.toDecode();
    }
}
