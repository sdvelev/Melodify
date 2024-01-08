package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.PlaylistDto;
import bg.sofia.uni.fmi.melodify.mapper.PlaylistMapper;
import bg.sofia.uni.fmi.melodify.model.Playlist;
import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.service.*;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaylistControllerTest {
    @MockBean
    private PlaylistService playlistService;
    @MockBean
    private PlaylistCreateFacadeService playlistCreateFacadeService;
    @MockBean
    private PlaylistModifySongsFacadeService playlistModifySongsFacadeService;
    @MockBean
    private TokenManagerService tokenManagerService;
    @MockBean
    private UserService userService;
    @MockBean
    private PlaylistMapper playlistMapper;

    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    @Autowired
    private PlaylistController playlistController;

    private Playlist playlist1;
    private Playlist playlist2;
    private List<Playlist> playlists;
    private PlaylistDto playlistDto1;
    private PlaylistDto playlistDto2;
    private List<PlaylistDto> playlistDtos;

    @MockBean
    private Queue queue;
    private User user = new User(1L, "Name", "surname", "email", "password", "user.png", Collections.emptyList(), this.queue,"user.com");
    @BeforeEach
    public void setup(){
        this.playlist1= new Playlist(1L, "Playlist1", user, LocalDateTime.of(2001, 9, 22, 12, 0), "playlist1.png", Collections.emptyList(), "playlist1.com");
        this.playlist2= new Playlist(2L, "Playlist2", user, LocalDateTime.of(2001, 9, 22, 12, 0), "playlist2.png", Collections.emptyList(), "playlist2.com");
        this.playlists = List.of(playlist1, playlist2);
        this.playlistDto1 = new PlaylistDto(1L, "Playlist1", LocalDateTime.of(2001, 9, 22, 12, 0), "playlist1.png", "playlist1.com", Collections.emptyList());
        this.playlistDto2 = new PlaylistDto(2L, "Playlist2", LocalDateTime.of(2001, 9, 22, 12, 0), "playlist2.png", "playlist2.com", Collections.emptyList());
        this.playlistDtos = List.of(playlistDto1, playlistDto2);

        MockitoAnnotations.openMocks(this);

        String stringToReturn = "Bearer ";
        when(this.request.getHeader("Authorization")).thenReturn(stringToReturn);

        when(this.playlistMapper.toDto(this.playlist1)).thenReturn(this.playlistDto1);
        when(this.playlistMapper.toDto(this.playlist2)).thenReturn(this.playlistDto2);
        when(this.playlistMapper.toDtoCollection(this.playlists)).thenReturn(this.playlistDtos);
        when(this.playlistMapper.toDtoCollection(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(this.playlistMapper.toEntity(this.playlistDto1)).thenReturn(this.playlist1);
        when(this.playlistMapper.toEntity(this.playlistDto2)).thenReturn(this.playlist2);
        when(this.playlistMapper.toEntityCollection(this.playlistDtos)).thenReturn(this.playlists);
        when(this.playlistMapper.toEntityCollection(Collections.emptyList())).thenReturn(Collections.emptyList());
    }

    @Test
    public void testGetAllPlaylists() {
        when(playlistService.getPlaylists(any())).thenReturn(this.playlists);

        List<PlaylistDto> providedPlaylistDtos = this.playlistController.getPlaylists(new HashMap<>());

        assertEquals(this.playlistDtos, providedPlaylistDtos);
        verify(this.playlistService, times(1)).getPlaylists(any());
        verify(this.playlistMapper, times(1)).toDtoCollection(any());
    }

    @Test
    public void testGetAllPlaylistsWithFilters() {
        Map<String, String> filters = new HashMap<>();
        filters.put("key", "value");
        when(playlistService.getPlaylists(any())).thenReturn(this.playlists);
        when(playlistService.getPlaylists(filters)).thenReturn(Collections.emptyList());

        List<PlaylistDto> providedPlaylistDtos = this.playlistController.getPlaylists(filters);

        assertEquals(Collections.emptyList(), providedPlaylistDtos);
        verify(this.playlistService, times(1)).getPlaylists(any());
        verify(this.playlistMapper, times(1)).toDtoCollection(any());
    }

    @Test
    public void testGetPlaylistById() {
        when(this.playlistService.getPlaylistById(1L)).thenReturn(Optional.of(this.playlist1));
        PlaylistDto providedPlaylistDto  = playlistController.getPlaylistById(1L);

        assertEquals(playlistDto1, providedPlaylistDto);
        verify(this.playlistService, times(1)).getPlaylistById(1L);
        verify(this.playlistMapper, times(1)).toDto(this.playlist1);
    }

    @Test
    public void testGetPlaylistByIdNonExistent() {
        when(this.playlistService.getPlaylistById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()-> this.playlistController.getPlaylistById(1L),
                "getPlaylistById should throw a ResourceNotFound exception, when there is no playlist with the provided id");
        verify(this.playlistService, times(1)).getPlaylistById(1L);
    }

    @Test
    public void testGetPlaylistByIdNull() {
        assertThrows(ConstraintViolationException.class,()-> this.playlistController.getPlaylistById(null),
                "getPlaylistById should throw an exception, when there the provided id is null");
    }

    @Test
    public void testAddPlaylist() {
        when(this.tokenManagerService.getUserIdFromToken(anyString())).thenReturn("1");
        when(this.userService.getUserById(1L)).thenReturn(Optional.of(this.user));
        when(this.playlistCreateFacadeService.createPlaylistWithOwner(this.playlist1, this.user.getId())).thenReturn(this.playlist1);

        Long providedPlaylistId = this.playlistController.addPlaylist(this.playlistDto1, this.request);

        assertEquals(this.playlist1.getId(), providedPlaylistId,
                "When addPlaylist() is called successfully, it should return the correct id of the object");
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.userService, times(1)).getUserById(1L);
        verify(this.playlistCreateFacadeService, times(1)).createPlaylistWithOwner(this.playlist1, this.user.getId());
    }

    @Test
    public void testAddPlaylistFail() {
        when(this.tokenManagerService.getUserIdFromToken(anyString())).thenReturn("1");
        when(this.userService.getUserById(1L)).thenReturn(Optional.of(this.user));
        when(this.playlistCreateFacadeService.createPlaylistWithOwner(this.playlist1, this.user.getId())).thenReturn(null);

        assertThrows(ApiBadRequest.class, ()->this.playlistController.addPlaylist(this.playlistDto1, this.request),
                "When addPlaylist() is called but object fails to create, it should throw a Method Not allowed exception");
    }

    @Test
    public void testAddPlaylistNullParams() {
        assertThrows(ConstraintViolationException.class, ()-> this.playlistController.addPlaylist(null, this.request),
                "When addPlaylist() is called with a null playlist dto, it should throw an exception");
    }

    @Test
    public void testSetPlaylistById() {
        when(this.tokenManagerService.getUserIdFromToken(anyString())).thenReturn("1");
        when(this.userService.getUserById(1L)).thenReturn(Optional.of(this.user));

        boolean providedResult = this.playlistController.setPlaylistById(1L, this.playlistDto1, this.request);

        assertTrue(providedResult);
        verify(this.tokenManagerService, times(1)).getUserIdFromToken(anyString());
        verify(this.userService, times(1)).getUserById(1L);
    }

    @Test
    public void testSetPlaylistByIdAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.userService.getUserById(anyLong())).thenReturn(Optional.empty());
        boolean providedResult = this.playlistController.setPlaylistById(1L, this.playlistDto1, this.request);

        assertTrue(providedResult);
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
    }

    @Test
    public void testSetPlaylistByIdNotAdminNorOwner() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");
        when(this.tokenManagerService.getUserIdFromToken(anyString())).thenReturn("2");
        when(this.userService.getUserById(2L)).thenReturn(Optional.empty());

        assertThrows(MethodNotAllowed.class, ()-> this.playlistController.setPlaylistById(1L, this.playlistDto1, this.request));
    }

    @Test
    public void testSetPlaylistByIdNullParams() {
        assertThrows(ConstraintViolationException.class, ()-> this.playlistController.setPlaylistById(null, this.playlistDto1, this.request));
        assertThrows(ConstraintViolationException.class, ()-> this.playlistController.setPlaylistById(1L, null, this.request));
    }

    @Test
    public void testDeletePlaylistByIdAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.userService.getUserById(1L)).thenReturn(Optional.empty());
        when(this.playlistService.deletePlaylist(1L, this.user, true)).thenReturn(this.playlist1);

        PlaylistDto deletedPlaylist = playlistController.deletePlaylistById(1L, this.request);

        assertEquals(this.playlistDto1, deletedPlaylist);
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.playlistService, times(1)).deletePlaylist(1L, this.user, false);
    }
    @Test
    public void testDeletePlaylistByIdUser() {
        when(this.tokenManagerService.getUserIdFromToken(anyString())).thenReturn("1");
        when(this.userService.getUserById(1L)).thenReturn(Optional.ofNullable(this.user));
        when(this.playlistService.deletePlaylist(1L, this.user, false)).thenReturn(this.playlist1);

        PlaylistDto deletedPlaylist = playlistController.deletePlaylistById(1L, this.request);

        assertEquals(this.playlistDto1, deletedPlaylist);
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.tokenManagerService, times(1)).getUserIdFromToken(anyString());
        verify(this.playlistService, times(1)).deletePlaylist(1L, this.user, false);
    }

    @Test
    public void testDeletePlaylistByIdNotAdminNorUser() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");
        when(this.tokenManagerService.getUserIdFromToken(anyString())).thenReturn("1");
        when(this.userService.getUserById(1L)).thenReturn(Optional.empty());

        assertThrows(MethodNotAllowed.class, ()-> this.playlistController.deletePlaylistById(1L, this.request));
    }

    @Test
    public void testDeletePlaylistByIdNullParams() {
        assertThrows(ConstraintViolationException.class,()-> this.playlistController.deletePlaylistById(null, this.request));
    }

    @Test
    public void testAddSongToPlaylist(){

    }
}
