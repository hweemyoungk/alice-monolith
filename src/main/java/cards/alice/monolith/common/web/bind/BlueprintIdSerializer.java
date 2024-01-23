package cards.alice.monolith.common.web.bind;

import cards.alice.monolith.common.domain.Blueprint;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class BlueprintIdSerializer extends StdSerializer<Blueprint> {
    protected BlueprintIdSerializer(Class<Blueprint> t) {
        super(t);
    }

    protected BlueprintIdSerializer(JavaType type) {
        super(type);
    }

    protected BlueprintIdSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected BlueprintIdSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(Blueprint value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getId());
    }
}
