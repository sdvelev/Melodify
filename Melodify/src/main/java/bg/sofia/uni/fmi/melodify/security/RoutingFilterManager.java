package bg.sofia.uni.fmi.melodify.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(3)
public class RoutingFilterManager extends OncePerRequestFilter {

    private static final String LOGIN_ENDPOINT = "/api/login";
    private static final String USERS_ENDPOINT = "/api/users";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_BEARER = "Bearer ";
    private static final Integer POSITION_TOKEN = 7;
    private static final String SLASH_REGEX = "/";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String httpMethod = request.getMethod();

        if (httpMethod.equals(HttpMethod.OPTIONS.toString())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (httpMethod.equals(HttpMethod.GET.toString()) ||
            ((httpMethod.equals(HttpMethod.POST.toString()) || httpMethod.equals(HttpMethod.OPTIONS.toString()))
                && getUriToRequest(request).equals(LOGIN_ENDPOINT)) ||
            ((httpMethod.equals(HttpMethod.POST.toString()) || httpMethod.equals(HttpMethod.OPTIONS.toString())) &&
                getUriToRequest(request).equals(USERS_ENDPOINT))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_BEARER)) {
            String token = authorizationHeader.substring(POSITION_TOKEN);
            if (token.isBlank()) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access");
                return;
            }
        }

        if (authorizationHeader == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getUriToRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.endsWith(SLASH_REGEX)) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }
}