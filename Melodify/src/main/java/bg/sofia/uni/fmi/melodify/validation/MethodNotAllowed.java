package bg.sofia.uni.fmi.melodify.validation;

public class MethodNotAllowed extends RuntimeException {
    public MethodNotAllowed(String message) {
        super(message);
    }

    public MethodNotAllowed(String message, Throwable cause) {
        super(message, cause);
    }
}
