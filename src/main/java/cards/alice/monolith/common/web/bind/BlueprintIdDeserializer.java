package cards.alice.monolith.common.web.bind;

import cards.alice.monolith.common.models.BlueprintDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BlueprintIdDeserializer extends StdDeserializer<BlueprintDto> {
    private static EntityManager entityManager;

    public BlueprintIdDeserializer(Class<?> vc) {
        super(vc);
    }

    public BlueprintIdDeserializer() {
        this((Class<?>) null);
    }

    @Autowired
    public BlueprintIdDeserializer(EntityManager entityManager) {
        this((Class<?>) null);
        BlueprintIdDeserializer.entityManager = entityManager;
    }

    @Override
    public BlueprintDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final Long blueprintId = p.getLongValue();
        // return entityManager.getReference(Blueprint.class, blueprintId);
        return BlueprintDto.builder().id(blueprintId).build();
    }
}
