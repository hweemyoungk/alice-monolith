package cards.alice.monolith.common.config;

import cards.alice.monolith.common.filters.CsrfCookieFilter;
import cards.alice.monolith.common.filters.EmailVerifiedFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class OAuth2SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No session
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("*"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
                .headers(configurer -> {
                    configurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
                })
                .csrf(configurer -> configurer
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        // .ignoringRequestMatchers("/csrfNotRequiredPath/*")) // TODO Configure ignoringRequestMatchers
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Frontend app script should access cookies
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .addFilterBefore(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new EmailVerifiedFilter(), BearerTokenAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/auth/**").authenticated()
                        .requestMatchers("/customer/**").hasRole("customer")
                        .requestMatchers("/owner/**").hasRole("owner")
                )
                /* Auth server should be Alice's own auth server.
                 * 1. Social login redirects user to Alice auth server /auth.
                 * 2. Auth server filter validates social account's oidc token.
                 * 3. Authenticate user with email. If user is new, create a new user detail with default authorities.
                 * 4. Redirect user to landing page with Alice's jwt.
                 * */
                .oauth2ResourceServer(configurer -> {
                    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
                    configurer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter));
                });
        //.oauth2ResourceServer(configurer -> {configurer.jwt(Customizer.withDefaults());});
        return http.build();
    }
}
