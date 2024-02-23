package cards.alice.monolith.common.web.exceptions;

public class JobClassNotFoundException extends RuntimeException {
    public JobClassNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
