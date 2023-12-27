package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.dto.UserDto;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.repository.UserRepository;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForYear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(@NotNull(message = "The provided user cannot be null")
                           User userToSave) {

        Optional<User> usersWithThatEmailList = userRepository.findByEmail(userToSave.getEmail());
        if (usersWithThatEmailList.isPresent()) {
            throw new ApiBadRequest("There is already a user associated with that credentials");
        }

        userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
        return userRepository.save(userToSave);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(@NotNull(message = "The provided user id cannot be null")
                            @Positive(message = "The provided user id must be positive")
                            Long id) {
        Optional<User> potentialUserToReturn = userRepository.findById(id);

        if (potentialUserToReturn.isPresent()) {
            return potentialUserToReturn;
        }

        throw new ResourceNotFoundException("User with such an id cannot be found");
    }

    public User getUserByEmail(@NotNull(message = "THe provided email cannot be null")
                               @NotBlank(message = "The provided email cannot be empty or blank")
                               String email) {
        Optional<User> potentialUserToReturn = userRepository.findByEmail(email);

        if (potentialUserToReturn.isPresent()) {
            return potentialUserToReturn.get();
        }

        throw new ResourceNotFoundException("User with such an email cannot be found");
    }

    public User getUserByEmailAndPassword(@NotNull(message = "The provided email cannot be null")
                                             @NotBlank(message = "The provided email cannot be empty or blank")
                                             String email,
                                             @NotNull(message = "The provided password cannot be null")
                                             @NotBlank(message = "The provided password cannot be empty or blank")
                                             String password) {

        Optional<User> potentialUserToReturn = userRepository.findByEmail(email);
        if (potentialUserToReturn.isPresent() && passwordEncoder.matches(password, potentialUserToReturn.get().getPassword())) {
            return potentialUserToReturn.get();
        }

        throw new ResourceNotFoundException("User with such a username and password cannot be found");
    }

    public boolean setPasswordByProvidingEmailAndOldPassword(@NotNull(message = "The provided new password cannot be null")
                                                                @NotBlank(message = "The provided new password cannot be empty or blank")
                                                                String newPassword,
                                                                @NotNull(message = "The provided email cannot be null")
                                                                @NotBlank(message = "The provided email cannot be empty or blank")
                                                                String email,
                                                                @NotNull(message = "The provided old password cannot be null")
                                                                @NotBlank(message = "The provided old password cannot be empty or blank")
                                                                String oldPassword) {

        Optional<User> optionalUsersToChange = userRepository.findByEmail(email);
        if (optionalUsersToChange.isPresent() && passwordEncoder
            .matches(oldPassword, optionalUsersToChange.get().getPassword())) {
            User userToChangePassword = optionalUsersToChange.get();
            userToChangePassword.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userToChangePassword);
            return true;
        }

        throw new ResourceNotFoundException("User with such an email and password cannot be found");
    }


    private User setUserNonNullFields(
        @NotNull(message = "The provided user dto cannot be null")
        UserDto userFieldsToChange,
        @NotNull(message = "The provided user cannot be null")
        User userToUpdate) {

        if (userFieldsToChange.getName() != null) {
            userToUpdate.setName(userFieldsToChange.getName());
        }

        if (userFieldsToChange.getSurname() != null) {
            userToUpdate.setSurname(userFieldsToChange.getSurname());
        }

        if (userFieldsToChange.getEmail() != null) {
            userToUpdate.setEmail(userFieldsToChange.getEmail());
        }

        if (userFieldsToChange.getImage() != null) {
            userToUpdate.setImage(userFieldsToChange.getImage());
        }

        return userToUpdate;
    }

    public boolean setUserById(
        @NotNull(message = "The provided user dto cannot be null")
        UserDto userFieldsToChange,
        @NotNull(message = "The provided user id cannot be null")
        @Positive(message = "The provided user id must be positive")
        Long userId) {

        Optional<User> optionalUserToUpdate = userRepository.findById(userId);
        if (optionalUserToUpdate.isPresent()) {
            User userToUpdate = setUserNonNullFields(userFieldsToChange, optionalUserToUpdate.get());;
            userRepository.save(userToUpdate);
            return true;
        }

        throw new ResourceNotFoundException("There is not a user with such an id");
    }

    public User deleteUser(
        @NotNull(message = "The provided email cannot be null")
        @NotBlank(message = "The provided email cannot be empty or blank")
        String email,
        @NotNull(message = "The provided password cannot be null")
        @NotBlank(message = "The provided password cannot be empty or blank")
        String password) {

        Optional<User> optionalUsersToDelete = userRepository.findByEmail(email);
        if (optionalUsersToDelete.isPresent() && passwordEncoder.matches(password, optionalUsersToDelete.get().getPassword())) {
            User userToDelete = optionalUsersToDelete.get();
            userRepository.delete(userToDelete);
            return userToDelete;
        }

        throw new ResourceNotFoundException("User with such an email and password cannot be found");
    }
}