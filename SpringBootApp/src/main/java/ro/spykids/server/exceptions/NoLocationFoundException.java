package ro.spykids.server.exceptions;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public class NoLocationFoundException extends RuntimeException {
    public NoLocationFoundException(String s) {
        super(s);
    }
}
