package logic;

import logic.exceptions.DecoderException;

public interface Decoder {
    TurboCodeDecoder toDecode() throws DecoderException;
}
