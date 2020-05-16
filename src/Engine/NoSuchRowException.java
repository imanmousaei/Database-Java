package Engine;

import java.util.NoSuchElementException;

public class NoSuchRowException extends NoSuchElementException {
    public NoSuchRowException() {
        super();
    }

    public NoSuchRowException(String s) {
        super(s);
    }
}
