package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.AuthenticationDto;
import bg.sofia.uni.fmi.melodify.dto.ChangePasswordDto;
import bg.sofia.uni.fmi.melodify.dto.UserDto;
import bg.sofia.uni.fmi.melodify.mapper.UserMapper;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserCreateWithPlaylistAndQueueFacadeService;
import bg.sofia.uni.fmi.melodify.service.UserDeleteFacadeService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static bg.sofia.uni.fmi.melodify.security.RequestManager.getUserByRequest;

@RestController
@RequestMapping(path = "api/users")
@Validated
public class UserController {
    private final UserService userService;
    private final UserDeleteFacadeService userDeleteFacadeService;
    private final UserCreateWithPlaylistAndQueueFacadeService userCreateWithPlaylistAndQueueFacadeService;
    private final UserMapper userMapper;
    private final TokenManagerService tokenManagerService;

    @Autowired
    public UserController(UserService userService, UserDeleteFacadeService userDeleteFacadeService,
                          UserCreateWithPlaylistAndQueueFacadeService userCreateWithPlaylistAndQueueFacadeService,
                          UserMapper userMapper, TokenManagerService tokenManagerService) {
        this.userService = userService;
        this.userDeleteFacadeService = userDeleteFacadeService;
        this.userCreateWithPlaylistAndQueueFacadeService = userCreateWithPlaylistAndQueueFacadeService;
        this.userMapper = userMapper;
        this.tokenManagerService = tokenManagerService;
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

    @DeleteMapping
    public boolean removeUser(
        @NotNull(message = "The provided authentication details cannot be null")
        @RequestBody
        AuthenticationDto authenticationDto) {

        return userDeleteFacadeService.deleteUserWithPlaylistsAssociatedToIt(authenticationDto.getEmail(),
            authenticationDto.getPassword());
    }

    @GetMapping(value = "/search")
    public ResponseEntity<UserDto> searchUserByEmailAndPassword(
        @NotNull(message = "The provided authentication details cannot be null")
        @RequestBody
        AuthenticationDto authenticationDto) {
        User userToReturn = userService.getUserByEmailAndPassword(authenticationDto.getEmail(),
            authenticationDto.getPassword());

        return ResponseEntity.ok(userMapper.toDto(userToReturn));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> searchUserById(
        @NotNull(message = "The provided user id cannot be null")
        @Positive(message = "The provided user id must be positive")
        @PathVariable Long id) {
        Optional<User> optionalUserToReturn = userService.getUserById(id);

        if (optionalUserToReturn.isPresent()) {
            return ResponseEntity.ok(userMapper.toDto(optionalUserToReturn.get()));
        }

        throw new ResourceNotFoundException("User with such an id cannot be found");
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

    @PutMapping(value = "/settings")
    public boolean setUserByUserId(@RequestBody
                                   @NotNull(message = "The provided user dto as body of the query cannot be null")
                                   UserDto userToUpdate,
                                   HttpServletRequest request) {
        return userService.setUserById(userToUpdate, getUserByRequest(request, tokenManagerService, userService).getId());
    }

    @PatchMapping(value = "/password")
    public boolean setPasswordByProvidingEmailAndOldPassword(
        @NotNull(message = "The provided change password details cannot be null")
        @RequestBody
        ChangePasswordDto changePasswordDto) {
        return userService.setPasswordByProvidingEmailAndOldPassword(changePasswordDto.getNewPassword(),
            changePasswordDto.getEmail(), changePasswordDto.getOldPassword());
    }
}