package ro.spykids.server.exceptions;

public class LocationAlreadyExists extends RuntimeException {
    public LocationAlreadyExists(String s) {
        super(s);
    }
}
