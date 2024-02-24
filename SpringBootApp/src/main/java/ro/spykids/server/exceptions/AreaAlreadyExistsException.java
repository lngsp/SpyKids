package ro.spykids.server.exceptions;

public class AreaAlreadyExistsException extends RuntimeException  {
    public AreaAlreadyExistsException(String s) {
        super(s);
    }
}