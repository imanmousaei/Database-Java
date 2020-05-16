package Engine;

public class InvalidJsonException extends IllegalArgumentException {
    public InvalidJsonException() {
        super();
    }

    public InvalidJsonException(String s) {
        super(s);
    }

    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJsonException(Throwable cause) {
        super(cause);
    }
}
