package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.QueueDto;
import bg.sofia.uni.fmi.melodify.mapper.QueueMapper;
import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.security.RequestManager;
import bg.sofia.uni.fmi.melodify.service.QueueModifySongsFacadeService;
import bg.sofia.uni.fmi.melodify.service.QueueService;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class QueueControllerTest {
    @MockBean
    private QueueService queueService;
    @MockBean
    private UserService userService;
    @MockBean
    private QueueModifySongsFacadeService queueModifySongsFacadeService;
    @MockBean
    private TokenManagerService tokenManagerService;
    @MockBean
    private QueueMapper queueMapper;
    @MockBean
    private ResourceLoader resourceLoader;

    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    @Autowired
    private QueueController queueController;

    private Queue queue1;
    private Queue queue2;
    private List<Queue> queues;
    private QueueDto queueDto1;
    private QueueDto queueDto2;
    private List<QueueDto> queueDtos;

    @MockBean
    private Queue queue;
    private User user1 = new User(1L, "Name", "surname", "email", "password", "user.png", Collections.emptyList(), this.queue,"user.com");
    private User user2 = new User(2L, "Name", "surname", "email", "password", "user.png", Collections.emptyList(), this.queue,"user.com");

    @MockBean
    private Resource resource;
    @BeforeEach
    public void setup(){
        this.queue1= new Queue(1L, this.user1, 1L, Collections.emptyList());
        this.queue2= new Queue(2L, this.user2, 1L, Collections.emptyList());
        this.queues = List.of(queue1, queue2);
        this.queueDto1= new QueueDto(1L, 1L, Collections.emptyList());
        this.queueDto2= new QueueDto(2L, 1L, Collections.emptyList());
        this.queueDtos = List.of(queueDto1, queueDto2);

        MockitoAnnotations.openMocks(this);

        String stringToReturn = "Bearer ";
        when(this.request.getHeader("Authorization")).thenReturn(stringToReturn);

        when(this.queueMapper.toDto(this.queue1)).thenReturn(this.queueDto1);
        when(this.queueMapper.toDto(this.queue2)).thenReturn(this.queueDto2);
        when(this.queueMapper.toDtoCollection(this.queues)).thenReturn(this.queueDtos);
        when(this.queueMapper.toDtoCollection(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(this.queueMapper.toEntity(this.queueDto1)).thenReturn(this.queue1);
        when(this.queueMapper.toEntity(this.queueDto2)).thenReturn(this.queue2);
        when(this.queueMapper.toEntityCollection(this.queueDtos)).thenReturn(this.queues);
        when(this.queueMapper.toEntityCollection(Collections.emptyList())).thenReturn(Collections.emptyList());
    }

    @Test
    public void testGetQueuesAdmin() {
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(()-> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            mockedRequestManager.when(()-> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);

            when(this.queueService.getQueues(new HashMap<>(), this.user1, true)).thenReturn(this.queues);

            List<QueueDto> providedQueueDtos = this.queueController.getQueues(new HashMap<>(), this.request);

            assertEquals(this.queueDtos, providedQueueDtos);
            verify(this.queueService, times(1)).getQueues(anyMap(), any(User.class), anyBoolean());
            verify(this.queueMapper, times(1)).toDtoCollection(any());
        }
    }

    @Test
    public void testGetQueuesUser() {
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            Map<String, String> filters = new HashMap<>();
            mockedRequestManager.when(()-> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            mockedRequestManager.when(()-> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(false);

            when(this.queueService.getQueues(filters, this.user1, false)).thenReturn(List.of(this.queue1));
            when(this.queueMapper.toDtoCollection(List.of(this.queue1))).thenReturn(List.of(this.queueDto1));

            List<QueueDto> providedQueueDtos = this.queueController.getQueues(filters, this.request);

            assertEquals(List.of(this.queueDto1), providedQueueDtos);
            verify(this.queueService, times(1)).getQueues(filters, this.user1, false);
            verify(this.queueMapper, times(1)).toDtoCollection(any());
        }
    }

    @Test
    public void testGetQueueById() {
        when(this.queueService.getQueueById(1L)).thenReturn(Optional.of(this.queue1));
        QueueDto providedQueueDto  = queueController.getQueueById(1L);

        assertEquals(queueDto1, providedQueueDto);
        verify(this.queueService, times(1)).getQueueById(1L);
        verify(this.queueMapper, times(1)).toDto(this.queue1);
    }

    @Test
    public void testGetQueueByIdNonExistent() {
        when(this.queueService.getQueueById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()-> this.queueController.getQueueById(1L),
                "getQueueById should throw a ResourceNotFound exception, when there is no queue with the provided id");
        verify(this.queueService, times(1)).getQueueById(1L);
    }

    @Test
    public void testGetQueueByIdNull() {
        assertThrows(ConstraintViolationException.class,()-> this.queueController.getQueueById(null),
                "getQueueById should throw an exception, when there the provided id is null");
    }

    @Test
    public void testAddQueue() {
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(()->RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);
            when(this.queueService.createQueue(this.queue1)).thenReturn(this.queue1);

            Long providedQueueId = this.queueController.addQueue(this.queueDto1, this.request);

            assertEquals(this.queue1.getId(), providedQueueId,
                    "When addQueue() is called successfully, it should return the correct id of the object");
            verify(this.queueService, times(1)).createQueue(this.queue1);
        }
    }

    @Test
    public void testAddQueueFail() {
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(false);

            when(this.queueService.createQueue(this.queue1)).thenReturn(null);

            assertThrows(MethodNotAllowed.class, () -> this.queueController.addQueue(this.queueDto1, this.request));
        }
    }

    @Test
    public void testAddQueueNullParams() {
        assertThrows(ConstraintViolationException.class, ()-> this.queueController.addQueue(null, this.request),
                "When addQueue() is called with a null queue dto, it should throw an exception");
    }

    @Test
    public void testClearSongsFromQueue() {
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.queueService.clearSongsFromQueue(this.user1)).thenReturn(true);
            assertTrue(this.queueController.clearSongsFromQueue(this.request));
            verify(this.queueService, times(1)).clearSongsFromQueue(this.user1);
        }
    }

    @Test
    public void testPlaySongFromQueue(){
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.queueModifySongsFacadeService.playSongFromQueue(this.user1.getId())).thenReturn(1L);
            when(this.queueModifySongsFacadeService.playFromSpecificSongInQueue(1L, this.user1)).thenReturn(true);
            when(this.resource.exists()).thenReturn(true);
            when(this.resource.isReadable()).thenReturn(true);

            ResponseEntity<Resource> response = this.queueController.playSongFromQueue(1L, this.request);
            assertEquals(HttpStatus.OK ,response.getStatusCode());
            verify(this.queueModifySongsFacadeService, times(1)).playFromSpecificSongInQueue(1L, this.user1);
        }
    }

    @Test
    public void testPlaySongFromQueueFail(){
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.queueModifySongsFacadeService.playSongFromQueue(this.user1.getId())).thenReturn(1L);
            when(this.queueModifySongsFacadeService.playFromSpecificSongInQueue(1L, this.user1)).thenReturn(false);
            when(this.resource.exists()).thenReturn(false);
            when(this.resource.isReadable()).thenReturn(true);

            ResponseEntity<Resource> response = this.queueController.playSongFromQueue(1L, this.request);
            assertEquals(HttpStatus.NOT_FOUND ,response.getStatusCode());

            verify(this.queueModifySongsFacadeService, times(1)).playFromSpecificSongInQueue(1L, this.user1);
        }
    }

    @Test
    public void testPlayPreviousSongFromQueue(){
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.queueModifySongsFacadeService.playPreviousSongFromQueue(this.queue1.getId())).thenReturn(1L);
            when(this.queueModifySongsFacadeService.playFromSpecificSongInQueue(1L, this.user1)).thenReturn(true);
            when(this.resource.exists()).thenReturn(true);
            when(this.resource.isReadable()).thenReturn(true);

            ResponseEntity<Resource> response = this.queueController.playPreviousSongFromQueue(this.request);
            assertEquals(HttpStatus.OK ,response.getStatusCode());
        }
    }

    @Test
    public void testPlayPreviousSongFromQueueFail(){
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.queueModifySongsFacadeService.playPreviousSongFromQueue(this.queue1.getId())).thenReturn(1L);
            when(this.queueModifySongsFacadeService.playFromSpecificSongInQueue(1L, this.user1)).thenReturn(false);
            when(this.resource.exists()).thenReturn(false);
            when(this.resource.isReadable()).thenReturn(false);

            ResponseEntity<Resource> response = this.queueController.playPreviousSongFromQueue(this.request);
            assertEquals(HttpStatus.OK ,response.getStatusCode());
        }
    }

    @Test
    public void testPlayNextSongFromQueue(){
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.queueModifySongsFacadeService.playNextSongFromQueue(this.queue1.getId())).thenReturn(1L);
            when(this.queueModifySongsFacadeService.playFromSpecificSongInQueue(1L, this.user1)).thenReturn(true);
            when(this.resource.exists()).thenReturn(true);
            when(this.resource.isReadable()).thenReturn(true);

            ResponseEntity<Resource> response = this.queueController.playNextSongFromQueue(this.request);
            assertEquals(HttpStatus.OK ,response.getStatusCode());
        }
    }

    @Test
    public void testPlayNextSongFromQueueFail(){
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);
            when(this.queueModifySongsFacadeService.playNextSongFromQueue(anyLong())).thenReturn(1L);
            when(this.queueModifySongsFacadeService.playFromSpecificSongInQueue(anyLong(), any(User.class))).thenReturn(false);
            when(this.resource.exists()).thenReturn(false);

            ResponseEntity<Resource> response = this.queueController.playNextSongFromQueue(this.request);
            assertEquals(HttpStatus.OK ,response.getStatusCode());

            when(this.resource.isReadable()).thenReturn(false);

            response = this.queueController.playNextSongFromQueue(this.request);
            assertEquals(HttpStatus.OK ,response.getStatusCode());
        }
    }

    @Test
    public void testDeleteQueueByIdAdmin() {
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);

            when(this.queueService.deleteQueue(1L)).thenReturn(this.queue1);

            QueueDto deletedQueue = queueController.deleteQueueById(1L, this.request);

            assertEquals(this.queueDto1, deletedQueue);
        }
    }

    @Test
    public void testDeleteQueueByIdAdminFail() {
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(false);

            assertThrows(MethodNotAllowed.class, ()->this.queueController.deleteQueueById(1L, this.request));
        }
    }

    @Test
    public void testRemoveSongFromQueue(){
        try (MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)) {
            mockedRequestManager.when(() -> RequestManager.getUserByRequest(this.request, this.tokenManagerService, this.userService)).thenReturn(this.user1);

            when(this.queueModifySongsFacadeService.removeSpecificSongFromQueue(1L,  this.user1)).thenReturn(true);
            when(this.queueService.removeSongFromQueue(this.user1)).thenReturn(true);

            boolean providedResult = this.queueController.removeSongFromQueue(1L, this.request);

            assertTrue(providedResult);
        }
    }
}
