package ro.spykids.server.exceptions;

public class IncompleteRequestException extends RuntimeException {
    public IncompleteRequestException(String s) {
        super(s);
    }
}
