package ro.spykids.server.exceptions;

public class NoBatteryFoundException extends RuntimeException {
    public NoBatteryFoundException(String s) {
        super(s);
    }
}
