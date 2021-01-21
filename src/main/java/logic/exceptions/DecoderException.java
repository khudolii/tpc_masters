package logic.exceptions;

public class DecoderException extends Exception{
    public DecoderException(String s) {
        super(s);
    }

    public DecoderException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
