package bg.sofia.uni.fmi.melodify.controller;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private RequestManager requestManager;

    @Mock
    private HttpServletRequest request;

    @Autowired
    @InjectMocks
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    private User firstUserToReturn;
    private User secondUserToReturn;

    private UserDto firstUserDto;
    private UserDto secondUserDto;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        firstUserToReturn = new User(1L, "Ivan", "Ivanov", "ivan@ivanov.com",
            "123456", "/images/users/1.png", new ArrayList<>(), new Queue(), "/uri");
        secondUserToReturn = new User(2L, "Georgi", "Georgiev", "georgi@georgiev.com",
            "123456", "/images/users/2.png", new ArrayList<>(), new Queue(), "/uri");

        firstUserDto = new UserDto(1L, "Ivan", "Ivanov", "ivan@ivanov.com",
            "123456", "/images/users/1.png", new ArrayList<>(), new QueueDto(), "/uri");
        secondUserDto = new UserDto(2L, "Georgi", "Georgiev", "georgi@georgiev.com",
            "123456", "/images/users/2.png", new ArrayList<>(), new QueueDto(), "/uri");
    }

    @Test
    public void testGetUsersEmptyList() {
        String stringToReturn = "Bearer ";
        when(request.getHeader("Authorization")).thenReturn(stringToReturn);
        when(this.tokenManagerService.getUserIdFromToken(any())).thenReturn("1");
        when(userService.getUserById(1L)).thenReturn(Optional.of(firstUserToReturn));
        when(this.tokenManagerService.getIsAdminFromToken(any())).thenReturn("true");

        when(userService.getUsers(new HashMap<>(), firstUserToReturn, true)).thenReturn(Collections.emptyList());
        when(userMapper.toDtoCollection(any())).thenReturn(Collections.emptyList());

        List<UserDto> actualReturnedList = userController.getUsers(new HashMap<>(), request);

        assertTrue(actualReturnedList.isEmpty(), "Expected empty list");
        verify(userService, times(1)).getUsers(new HashMap<>(), firstUserToReturn, true);
        verify(userMapper, times(1)).toDtoCollection(Collections.emptyList());
    }

    @Test
    public void testGetUsersAdminRequest() {
        String headerToReturn = "Bearer ";
        when(request.getHeader("Authorization")).thenReturn(headerToReturn);
        when(this.tokenManagerService.getUserIdFromToken(any())).thenReturn("1");
        when(userService.getUserById(1L)).thenReturn(Optional.of(firstUserToReturn));
        when(this.tokenManagerService.getIsAdminFromToken(any())).thenReturn("true");

        when(userService.getUsers(new HashMap<>(), firstUserToReturn, true))
            .thenReturn(List.of(firstUserToReturn, secondUserToReturn));

        List<UserDto> expectedUsersDtoList = List.of(firstUserDto, secondUserDto);

        when(userMapper.toDtoCollection(any())).thenReturn(expectedUsersDtoList);

        List<UserDto> actualReturnedList = userController.getUsers(new HashMap<>(), request);

        assertIterableEquals(expectedUsersDtoList, actualReturnedList,
            "The returned dto list invoked from administrator is not the same as expected");

        verify(userService, times(1)).getUsers(new HashMap<>(), firstUserToReturn, true);
        verify(userMapper, times(1)).toDtoCollection(any());
    }

    @Test
    public void testAddUserSuccessfully() throws Exception {
        when(userMapper.toEntity(secondUserDto)).thenReturn(secondUserToReturn);
        when(userCreateWithPlaylistAndQueueFacadeService.createUserWithPlaylistAndQueue(secondUserToReturn))
            .thenReturn(secondUserToReturn);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(secondUserDto)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(String.valueOf(secondUserToReturn.getId())));
    }

//    @Test
//    public void testDeleteUserSuccessfully() throws Exception {
//        AuthenticationDto credentials = new AuthenticationDto("ivan@ivanov.com", "123456");
//
//        when(userDeleteFacadeService.deleteUserWithPlaylistsAssociatedToIt(credentials.getEmail(),
//            credentials.getPassword())).thenReturn(true);
//        when(userService.getUserByEmailAndPassword(credentials.getEmail(), credentials.getPassword()))
//            .thenReturn(firstUserToReturn);
//        when(userService.deleteUser(credentials.getEmail(), credentials.getPassword()))
//            .thenReturn(firstUserToReturn);
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(credentials)))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andExpect(MockMvcResultMatchers.content().string("true"));
//    }
}
