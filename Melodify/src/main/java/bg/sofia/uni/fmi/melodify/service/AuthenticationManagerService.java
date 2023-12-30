package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.dto.AuthenticationDto;
import bg.sofia.uni.fmi.melodify.dto.TokenUserIdDto;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

@Service
public class AuthenticationManagerService {

    private UserService userService;
    private TokenManagerService tokenManagerService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationManagerService(UserService userService,
                                        TokenManagerService tokenManagerService,
                                        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenManagerService = tokenManagerService;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenUserIdDto login(AuthenticationDto authenticationDto) {

        String insertedEmail = authenticationDto.getEmail();
        String insertedPassword = authenticationDto.getPassword();

        validateCredentials(insertedEmail, insertedPassword);

        Optional<User> returnedUser = userService.getUserByEmail(insertedEmail);
        if (returnedUser.isEmpty()) {
            throw new ApiBadRequest("There is not a user with such an email");
        }

        if (!passwordEncoder.matches(insertedPassword, returnedUser.get().getPassword())) {
            throw new ApiBadRequest("Login failed");
        }

        TokenUserIdDto tokenIdDtoToReturn = new TokenUserIdDto();
        tokenIdDtoToReturn.setUserId(returnedUser.get().getId());

        Resource resource = new ClassPathResource("admin_ids.txt");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String currentLine;
            boolean isFound = false;

            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.contains(String.valueOf(returnedUser.get().getId()))) {
                    isFound = true;
                    break;
                }
            }

            tokenIdDtoToReturn.setToken(tokenManagerService.generateTokenByUser(returnedUser.get(), isFound));
            tokenIdDtoToReturn.setIsAdmin(isFound);
        } catch (IOException e) {
            throw new ResourceNotFoundException("There is a problem with admin check");
        }

        return tokenIdDtoToReturn;
    }

    private void validateCredentials(String potentialUsername, String potentialPassword) {

        if (potentialUsername == null || potentialUsername.isBlank() ||
            potentialPassword == null || potentialPassword.isBlank()) {
            throw new ApiBadRequest("You have to provide a username and a password to log in");
        }
    }
}