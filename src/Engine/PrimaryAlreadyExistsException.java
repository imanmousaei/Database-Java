package Engine;

public class PrimaryAlreadyExistsException extends IllegalArgumentException {
    public PrimaryAlreadyExistsException() {
        super();
    }

    public PrimaryAlreadyExistsException(String s) {
        super(s);
    }

    public PrimaryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrimaryAlreadyExistsException(Throwable cause) {
        super(cause);
    }

}
