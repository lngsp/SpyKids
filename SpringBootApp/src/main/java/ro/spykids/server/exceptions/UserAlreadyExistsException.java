package ro.spykids.server.exceptions;

public class UserAlreadyExistsException extends RuntimeException  {
    public UserAlreadyExistsException(String s) {
        super(s);
    }
}
