package logic.exceptions;

public class ReportException extends Exception{
    public ReportException(String s) {
        super(s);
    }

    public ReportException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
