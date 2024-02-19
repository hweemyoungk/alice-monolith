package cards.alice.monolith.common.config;

import cards.alice.monolith.common.models.CustomerMembershipDto;
import cards.alice.monolith.common.models.OwnerMembershipDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
public class MembershipsConfig {
    @Value("${cards.alice.app.server.host}:${cards.alice.app.server.port}${cards.alice.app.web.controllers.path.base}")
    private String appServiceUrl;
    @Value("${cards.alice.app.web.controllers.path.public.customer.membership.map}")
    private String customerMembershipMapPath;
    @Value("${cards.alice.app.web.controllers.path.public.owner.membership.map}")
    private String ownerMembershipMapPath;

    @Bean
    public Map<String, CustomerMembershipDto> customerMembershipMap(RestTemplate restTemplate) {
        final String url = appServiceUrl + customerMembershipMapPath;
        final var requestEntity = RequestEntity.get(url).build();
        final var responseType = new ParameterizedTypeReference<Map<String, CustomerMembershipDto>>() {
        };
        return restTemplate.exchange(requestEntity, responseType).getBody();
    }

    @Bean
    public Map<String, OwnerMembershipDto> ownerMembershipMap(RestTemplate restTemplate) {
        final String url = appServiceUrl + ownerMembershipMapPath;
        final var requestEntity = RequestEntity.get(url).build();
        final var responseType = new ParameterizedTypeReference<Map<String, OwnerMembershipDto>>() {
        };
        return restTemplate.exchange(requestEntity, responseType).getBody();
    }
}
