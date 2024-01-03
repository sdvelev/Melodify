package bg.sofia.uni.fmi.melodify.security;

import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Optional;

public class RequestManager {

    private RequestManager() {}

    public static User getUserByRequest(HttpServletRequest request, TokenManagerService tokenManagerService,
                                        UserService userService) {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String token = authorizationHeader.replace("Bearer ", "");
            String userId = tokenManagerService.getUserIdFromToken(token);

            BigDecimal userIdAsBigDecimal = new BigDecimal(userId);
            Optional<User> userToSetChanges = userService.getUserById(userIdAsBigDecimal
                .setScale(0, BigDecimal.ROUND_DOWN).longValue());

            if (userToSetChanges.isEmpty()) {
                throw new ApiBadRequest("There is an error with authentication");
            }

            return userToSetChanges.get();
        }
        throw new ApiBadRequest("There is an error with authentication");
    }

    public static boolean isAdminByRequest(HttpServletRequest request, TokenManagerService tokenManagerService) {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String token = authorizationHeader.replace("Bearer ", "");
            String isAdminFromToken = tokenManagerService.getIsAdminFromToken(token);

            return Boolean.parseBoolean(isAdminFromToken);
        }
        throw new ApiBadRequest("There is an error with authentication");
    }
}