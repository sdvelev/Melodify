package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.UserDto;
import bg.sofia.uni.fmi.melodify.mapper.UserMapper;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.service.UserCreateWithPlaylistAndQueueFacadeService;
import bg.sofia.uni.fmi.melodify.service.UserDeleteFacadeService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/users")
@Validated
public class UserController {
    private final UserService userService;
    private final UserDeleteFacadeService userDeleteFacadeService;
    private final UserCreateWithPlaylistAndQueueFacadeService userCreateWithPlaylistAndQueueFacadeService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserDeleteFacadeService userDeleteFacadeService,
                          UserCreateWithPlaylistAndQueueFacadeService userCreateWithPlaylistAndQueueFacadeService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.userDeleteFacadeService = userDeleteFacadeService;
        this.userCreateWithPlaylistAndQueueFacadeService = userCreateWithPlaylistAndQueueFacadeService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam Map<String, String> filters) {
        return userMapper.toDtoCollection(userService.getUsers(filters));
    }

    @PostMapping
    public Long addUser(@NotNull(message = "The provided user dto as body of the query cannot be null")
                        @RequestBody UserDto userDto) {
        User potentialUserToCreate = userCreateWithPlaylistAndQueueFacadeService
            .createUserWithPlaylistAndQueue(userMapper.toEntity(userDto));

        if (potentialUserToCreate != null) {
            return potentialUserToCreate.getId();
        }

        throw new ApiBadRequest("There was a problem in creating a user");
    }

    @DeleteMapping(params = {"email", "password"})
    public boolean removeUser(
        @NotNull(message = "The provided email cannot be null")
        @NotBlank(message = "The provided email cannot be blank")
        @RequestParam("email")
        String email,
        @NotNull(message = "The provided password cannot be null")
        @NotBlank(message = "The provided password cannot be blank")
        @RequestParam("password")
        String password) {

        return userDeleteFacadeService.deleteUserWithPlaylistsAssociatedToIt(email, password);
    }

    @GetMapping(params = {"email", "password"})
    public ResponseEntity<UserDto> searchUserByUsernameAndPassword(
        @NotNull(message = "The provided email cannot be null")
        @NotBlank(message = "The provided email cannot be blank")
        @RequestParam("email") String email,
        @NotNull(message = "The provided password cannot be null")
        @NotBlank(message = "The provided password cannot be blank")
        @RequestParam("password") String password) {
        User userToReturn = userService.getUserByEmailAndPassword(email, password);

        return ResponseEntity.ok(userMapper.toDto(userToReturn));
    }

    @GetMapping(value = "/{id}", params = {"id"})
    public ResponseEntity<UserDto> searchUserById(
        @NotNull(message = "The provided user id cannot be null")
        @Positive(message = "The provided user id must be positive")
        @RequestParam("id") Long id) {
        Optional<User> optionalUserToReturn = userService.getUserById(id);

        if (optionalUserToReturn.isPresent()) {
            return ResponseEntity.ok(userMapper.toDto(optionalUserToReturn.get()));
        }

        throw new ResourceNotFoundException("User with such an email and password cannot be found");
    }

    @GetMapping(params = {"email"})
    public ResponseEntity<UserDto> searchUserByEmail(
        @NotNull(message = "The provided email cannot be null")
        @NotBlank(message = "The provided email cannot be blank")
        @RequestParam("email") String email) {

        Optional<User> potentialUserToReturn = userService.getUserByEmail(email);

        if (potentialUserToReturn.isPresent()) {
            return ResponseEntity.ok(userMapper.toDto(potentialUserToReturn.get()));
        }

        throw new ResourceNotFoundException("User with such an email cannot be found");
    }

    @PutMapping(value = "/set", params = {"user_id"})
    public boolean setUserByUserId(@RequestParam("user_id")
                                   @NotNull(message = "The provided user id cannot be null")
                                   @Positive(message = "The provided user id must be positive")
                                   Long userId,
                                   @RequestBody
                                   @NotNull(message = "The provided user dto as body of the query cannot be null")
                                   UserDto userToUpdate) {
        return userService.setUserById(userToUpdate, userId);
    }

    @PatchMapping(value = "/settings", params = {"new_password", "username", "old_password"})
    public boolean setPasswordByProvidingUsernameAndOldPassword(
        @NotNull(message = "The provided new password cannot be null")
        @NotBlank(message = "The provided new password cannot be empty or blank")
        @RequestParam("new_password")
        String newPassword,
        @NotNull(message = "The provided email cannot be null")
        @NotBlank(message = "The provided email cannot be empty or blank")
        @RequestParam("email")
        String email,
        @NotNull(message = "The provided old password cannot be null")
        @NotBlank(message = "The provided old password cannot be empty or blank")
        @RequestParam("old_password")
        String oldPassword) {
        return userService.setPasswordByProvidingEmailAndOldPassword(newPassword, email, oldPassword);
    }
}