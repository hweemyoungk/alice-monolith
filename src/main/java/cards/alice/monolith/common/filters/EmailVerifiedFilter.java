package cards.alice.monolith.common.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class EmailVerifiedFilter extends OncePerRequestFilter {
    private final AuthenticationEntryPoint authenticationEntryPoint = new BearerTokenAuthenticationEntryPoint();

    private final AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(
            (request, response, exception) -> this.authenticationEntryPoint.commence(request, response, exception));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken bearer) {
            final Object emailVerified = bearer.getToken().getClaims().get("email_verified");
            if (emailVerified instanceof Boolean verified && verified) {
                filterChain.doFilter(request, response);
            } else {
                authenticationFailureHandler.onAuthenticationFailure(request, response,
                        new OAuth2AuthenticationException(new OAuth2Error(BearerTokenErrorCodes.INSUFFICIENT_SCOPE), "Email must be verified"));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
