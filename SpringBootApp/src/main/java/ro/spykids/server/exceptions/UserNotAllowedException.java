package ro.spykids.server.exceptions;

public class UserNotAllowedException extends RuntimeException  {
    public UserNotAllowedException(String s) {
        super(s);
    }
}
