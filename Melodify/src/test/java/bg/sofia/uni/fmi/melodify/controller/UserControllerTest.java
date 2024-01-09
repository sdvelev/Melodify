package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.AuthenticationDto;
import bg.sofia.uni.fmi.melodify.dto.ChangePasswordDto;
import bg.sofia.uni.fmi.melodify.dto.QueueDto;
import bg.sofia.uni.fmi.melodify.dto.UserDto;
import bg.sofia.uni.fmi.melodify.mapper.UserMapper;
import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.security.RequestManager;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserCreateWithPlaylistAndQueueFacadeService;
import bg.sofia.uni.fmi.melodify.service.UserDeleteFacadeService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static bg.sofia.uni.fmi.melodify.security.RequestManager.getUserByRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private UserDeleteFacadeService userDeleteFacadeService;
    @MockBean
    private UserCreateWithPlaylistAndQueueFacadeService userCreateWithPlaylistAndQueueFacadeService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private TokenManagerService tokenManagerService;
    @MockBean
    private RequestManager requestManager;

    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    @Autowired
    private UserController userController;

    private User user1;
    private User user2;
    private List<User> users;
    private UserDto userDto1;
    private UserDto userDto2;
    private List<UserDto> userDtos;
    @MockBean
    private AuthenticationDto authenticationDto;
    @MockBean
    private ChangePasswordDto changePasswordDto;
    @MockBean
    private Queue queue;
    @MockBean
    private QueueDto queueDto;

    @BeforeEach
    public void setup() {
        this.user1 = new User(1L, "Name", "Surname", "email", "password", "user1.png", Collections.emptyList(), this.queue, "user1.com");
        this.user2 = new User(2L, "Name", "Surname", "email", "password", "user2.png", Collections.emptyList(), this.queue, "user1.com");
        this.users = List.of(user1, user2);
        this.userDto1 = new UserDto(1L, "Name", "Surname", "email", "password", "user1.png", Collections.emptyList(), this.queueDto, "user1.com");
        this.userDto2 = new UserDto(2L, "Name", "Surname", "email", "password", "user2.png", Collections.emptyList(), this.queueDto, "user1.com");
        this.userDtos = List.of(userDto1, userDto2);

        MockitoAnnotations.openMocks(this);

        String stringToReturn = "Bearer ";
        when(this.request.getHeader("Authorization")).thenReturn(stringToReturn);

        when(this.userMapper.toDto(this.user1)).thenReturn(this.userDto1);
        when(this.userMapper.toDto(this.user2)).thenReturn(this.userDto2);
        when(this.userMapper.toDtoCollection(this.users)).thenReturn(this.userDtos);
        when(this.userMapper.toDtoCollection(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(this.userMapper.toEntity(this.userDto1)).thenReturn(this.user1);
        when(this.userMapper.toEntity(this.userDto2)).thenReturn(this.user2);
        when(this.userMapper.toEntityCollection(this.userDtos)).thenReturn(this.users);
        when(this.userMapper.toEntityCollection(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(this.authenticationDto.getEmail()).thenReturn("email");
        when(this.authenticationDto.getPassword()).thenReturn("password");

        when(this.changePasswordDto.getEmail()).thenReturn("email");
        when(this.changePasswordDto.getOldPassword()).thenReturn("oldPassword");
        when(this.changePasswordDto.getNewPassword()).thenReturn("newPassword");
    }

    @Test
    public void testGetUsersAdmin() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);
            mocked.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);

            when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
            when(userService.getUsers(new HashMap<>(), this.user1, true)).thenReturn(this.users);

            List<UserDto> providedUserDtos = this.userController.getUsers(new HashMap<>(), this.request);

            assertEquals(this.userDtos, providedUserDtos);
            verify(this.userService, times(1)).getUsers(anyMap(), any(User.class), anyBoolean());

        }
        ;
    }

    @Test
    public void testGetUsersAdminWithFilters() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            mocked.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);
            Map<String, String> filters = new HashMap<>();
            filters.put("key", "value");
            when(userService.getUsers(new HashMap<>(), this.user1, true)).thenReturn(this.users);
            when(userService.getUsers(filters, this.user1, true)).thenReturn(Collections.emptyList());


            List<UserDto> providedUserDtos = this.userController.getUsers(filters, this.request);

            assertEquals(Collections.emptyList(), providedUserDtos);
            verify(this.userService, times(1)).getUsers(anyMap(), any(User.class), anyBoolean());
        }
    }

    @Test
    public void testGetAllUsersUserNotAdmins() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(false);
            mocked.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(userService.getUsers(new HashMap<>(), this.user1, false)).thenReturn(List.of(this.user1));

            List<UserDto> providedUserDtos = this.userController.getUsers(new HashMap<>(), this.request);

            assertEquals(Collections.emptyList(), providedUserDtos);
            verify(this.userService, times(1)).getUsers(anyMap(), any(User.class), anyBoolean());
        }
    }

    @Test
    public void testAddUser() {
        when(this.userCreateWithPlaylistAndQueueFacadeService.createUserWithPlaylistAndQueue(this.user1)).thenReturn(this.user1);

        Long providedUserId = this.userController.addUser(this.userDto1);

        assertEquals(this.user1.getId(), providedUserId,
                "When addUser() is called successfully, it should return the correct id of the object");
        verify(this.userCreateWithPlaylistAndQueueFacadeService, times(1)).createUserWithPlaylistAndQueue(this.user1);
    }

    @Test
    public void testAddUserFail() {
        when(this.userCreateWithPlaylistAndQueueFacadeService.createUserWithPlaylistAndQueue(this.user1)).thenReturn(null);

        assertThrows(ApiBadRequest.class, () -> this.userController.addUser(this.userDto1));
    }

    @Test
    public void testAddUserNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.userController.addUser(null),
                "When addUser() is called with a null user dto, it should throw an exception");
    }

    @Test
    public void testRemoveUser() {
        when(this.userDeleteFacadeService.deleteUserWithPlaylistsAssociatedToIt(anyString(), anyString())).thenReturn(true);
        assertTrue(this.userController.removeUser(this.authenticationDto));
    }

    @Test
    public void testRemoveUserNull() {
        assertThrows(ConstraintViolationException.class, () -> this.userController.removeUser(null));
    }

    @Test
    public void testSearchUserByEmailAndPassword() {
        when(this.userService.getUserByEmailAndPassword(anyString(), anyString())).thenReturn(this.user1);
        assertEquals(ResponseEntity.ok(this.userDto1), this.userController.searchUserByEmailAndPassword(this.authenticationDto));
        verify(this.userService, times(1)).getUserByEmailAndPassword(anyString(), anyString());
    }

    @Test
    public void testSearchUserByEmailAndPasswordNull() {
        assertThrows(ConstraintViolationException.class, () -> this.userController.searchUserByEmailAndPassword(null));
    }

    @Test
    public void testSearchUserById() {
        when(this.userService.getUserById(1L)).thenReturn(Optional.ofNullable(this.user1));
        assertEquals(ResponseEntity.ok(this.userDto1), this.userController.searchUserById(1L));
        verify(this.userService, times(1)).getUserById(1L);
    }

    @Test
    public void testSearchUserByIdNonExistent() {
        when(this.userService.getUserById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> this.userController.searchUserById(1L));
    }

    @Test
    public void testSearchUserByIdNull() {
        assertThrows(ConstraintViolationException.class, () -> this.userController.searchUserById(null));
    }

    @Test
    public void testSearchUserByEmail() {
        when(this.userService.getUserByEmail(anyString())).thenReturn(Optional.ofNullable(this.user1));
        assertEquals(ResponseEntity.ok(this.userDto1), this.userController.searchUserByEmail("someEmail"));
        verify(this.userService, times(1)).getUserByEmail(anyString());
    }

    @Test
    public void testSearchUserByIdEmailExistent() {
        when(this.userService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> this.userController.searchUserByEmail("someEmail"));
    }

    @Test
    public void testSearchUserByEmailNull() {
        assertThrows(ConstraintViolationException.class, () -> this.userController.searchUserByEmail(null));
    }

    @Test
    public void testSetUserById() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.userService.setUserById(this.userDto1, 1L)).thenReturn(true);

            boolean providedResult = this.userController.setUserByUserId(this.userDto1, this.request);

            assertTrue(providedResult);
            verify(this.userService, times(1)).setUserById(this.userDto1, 1L);
        }
    }

    @Test
    public void testSetUserByIdNotSameUser() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.userService.setUserById(this.userDto2, 1L)).thenReturn(false);

            boolean providedResult = this.userController.setUserByUserId(this.userDto1, this.request);

            assertFalse(providedResult);
            verify(this.userService, times(1)).setUserById(this.userDto1, 1L);
        }
    }

    @Test
    public void testSetUserByIdNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.userController.setUserByUserId(null, this.request));
    }

    @Test
    public void testSetPasswordByProvidingEmailAndOldPassword() {
        when(this.userService.setPasswordByProvidingEmailAndOldPassword(anyString(), anyString(), anyString())).thenReturn(true);

        boolean providedResult = this.userController.setPasswordByProvidingEmailAndOldPassword(this.changePasswordDto);

        assertTrue(providedResult);
        verify(this.userService, times(1)).setPasswordByProvidingEmailAndOldPassword(anyString(), anyString(), anyString());
    }

    @Test
    public void testSetPasswordByProvidingEmailAndOldPasswordFail() {
        when(this.userService.setPasswordByProvidingEmailAndOldPassword(anyString(), anyString(), anyString())).thenReturn(false);

        boolean providedResult = this.userController.setPasswordByProvidingEmailAndOldPassword(this.changePasswordDto);

        assertFalse(providedResult);
        verify(this.userService, times(1)).setPasswordByProvidingEmailAndOldPassword(anyString(), anyString(), anyString());
    }

    @Test
    public void testSetPasswordByProvidingEmailAndOldPasswordNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.userController.setPasswordByProvidingEmailAndOldPassword(null));
    }
}