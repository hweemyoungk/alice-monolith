package cards.alice.monolith.common.web.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class DtoProcessingException extends RuntimeException {
    private final Set<String> violationMessages;

    public DtoProcessingException(String message, Set<String> violationMessages) {
        super(message);
        this.violationMessages = violationMessages;
    }

    public DtoProcessingException(String message) {
        super(message);
        this.violationMessages = new HashSet<>();
    }

    public DtoProcessingException() {
        super();
        this.violationMessages = new HashSet<>();
    }

    @Override
    public String toString() {
        return "DtoProcessingException{" + getMessage() +
                "violationMessages=" + violationMessages +
                '}';
    }
}
