package Engine;

import java.util.NoSuchElementException;

public class NoSuchRowException extends NoSuchElementException {
    public NoSuchRowException() {
        super();
    }

    public NoSuchRowException(String s) {
        super(s);
    }

    public NoSuchRowException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchRowException(Throwable cause) {
        super(cause);
    }
}
