package cards.alice.monolith.common.web.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceType;
    private final Long resourceId;

    public ResourceNotFoundException(Class<?> resourceType, Long resourceId) {
        this.resourceType = resourceType.getName();
        this.resourceId = resourceId;
    }
}
