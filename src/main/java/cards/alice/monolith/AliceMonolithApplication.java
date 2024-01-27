package cards.alice.monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class AliceMonolithApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliceMonolithApplication.class, args);
    }

}
