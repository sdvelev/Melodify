package bg.sofia.uni.fmi.melodify.security;

import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Order(2)
public class AuthorizationFilterManager extends OncePerRequestFilter {

    private static final String HEADER_BEARER = "Bearer ";
    private static final Integer POSITION_TOKEN = 7;
    private static final String LOGGED_USER = "LoggedUser";
    private static final String USER_ID_REGEX = "\\d+\\.0";
    private static final String SPLIT_REGEX = "\\.";

    private TokenManagerService tokenManagerService;
    private UserService userService;

    @Autowired
    public AuthorizationFilterManager(TokenManagerService tokenManagerService, UserService userService) {
        this.tokenManagerService = tokenManagerService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String token = getTokenFromHeader(request);
        if (token != null) {

            try {
                String extractedUserId = tokenManagerService.getUserIdFromToken(token);
                if (extractedUserId == null || !extractedUserId.matches(USER_ID_REGEX)) {
                    throw new MethodNotAllowed("There is an error in authorization" + extractedUserId);
                }

                extractedUserId = extractedUserId.split(SPLIT_REGEX)[0];
                Optional<User> user = userService.getUserById(Long.parseLong(extractedUserId));
                if (user.isEmpty()) {
                    throw new MethodNotAllowed("There is an error in authorization");
                }

                request.setAttribute(LOGGED_USER, user.get());
            } catch(MethodNotAllowed e) {
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                response.getWriter().write(e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(HEADER_BEARER)) {
            return authHeader.substring(POSITION_TOKEN);
        }

        return null;
    }
}