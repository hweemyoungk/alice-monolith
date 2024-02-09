package cards.alice.monolith;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"default", "dev", "common", "h2", "monolith", "oauth2_https"})
class AliceMonolithApplicationTests {

    @Test
    void contextLoads() {
    }

}
