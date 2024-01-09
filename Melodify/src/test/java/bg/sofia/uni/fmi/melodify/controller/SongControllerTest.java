package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.*;
import bg.sofia.uni.fmi.melodify.mapper.SongMapper;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Genre;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.security.RequestManager;
import bg.sofia.uni.fmi.melodify.service.SongCreateFacadeService;
import bg.sofia.uni.fmi.melodify.service.SongService;
import bg.sofia.uni.fmi.melodify.service.SongSetFacadeService;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SongControllerTest {
    @MockBean
    private SongService songService;
    @MockBean
    private SongSetFacadeService songSetFacadeService;
    @MockBean
    private SongCreateFacadeService songCreateFacadeService;
    @MockBean
    private SongMapper songMapper;
    @MockBean
    private TokenManagerService tokenManagerService;

    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    @Autowired
    private SongController songController;

    private Song song1;
    private Song song2;
    private List<Song> songs;
    private SongDto songDto1;
    private SongDto songDto2;
    private List<SongDto> songDtos;
    @MockBean
    private AuthenticationDto authenticationDto;
    @MockBean
    private ChangePasswordDto changePasswordDto;
    @MockBean
    private Genre genre;
    @MockBean
    private GenreDto genreDto;
    @MockBean
    private Album album;
    @MockBean
    private AlbumDto albumDto;

    @BeforeEach
    public void setup() {
        this.song1 = new Song(1L, "Name", 100, 0, this.genre, this.album, Collections.emptyList(), "song1.com");
        this.song2 = new Song(2L, "Name", 100, 0, this.genre, this.album, Collections.emptyList(), "song2.com");
        this.songs = List.of(song1, song2);
        this.songDto1 = new SongDto(1L, "Name", 100, 0, this.genreDto, "1", "album", "image", Collections.emptyList(), "song1.com");
        this.songDto2 = new SongDto(2L, "Name", 100, 0, this.genreDto, "1", "album", "image", Collections.emptyList(), "song2.com");
        this.songDtos = List.of(songDto1, songDto2);

        MockitoAnnotations.openMocks(this);

        String stringToReturn = "Bearer ";
        when(this.request.getHeader("Authorization")).thenReturn(stringToReturn);

        when(this.songMapper.toDto(this.song1)).thenReturn(this.songDto1);
        when(this.songMapper.toDto(this.song2)).thenReturn(this.songDto2);
        when(this.songMapper.toDtoCollection(this.songs)).thenReturn(this.songDtos);
        when(this.songMapper.toDtoCollection(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(this.songMapper.toEntity(this.songDto1)).thenReturn(this.song1);
        when(this.songMapper.toEntity(this.songDto2)).thenReturn(this.song2);
        when(this.songMapper.toEntityCollection(this.songDtos)).thenReturn(this.songs);
        when(this.songMapper.toEntityCollection(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(this.authenticationDto.getEmail()).thenReturn("email");
        when(this.authenticationDto.getPassword()).thenReturn("password");

        when(this.changePasswordDto.getEmail()).thenReturn("email");
        when(this.changePasswordDto.getOldPassword()).thenReturn("oldPassword");
        when(this.changePasswordDto.getNewPassword()).thenReturn("newPassword");
    }

    @Test
    public void testGetAllSongs() {
        when(this.songService.getSongs(new HashMap<>())).thenReturn(this.songs);
        List<SongDto> providedSongDtos = this.songController.getAllSongs(new HashMap<>());

        assertEquals(this.songDtos, providedSongDtos);
        verify(this.songService, times(1)).getSongs(anyMap());
    }

    @Test
    public void testGetAllSongsWithFilters() {
        Map<String, String> filters = new HashMap<>();
        filters.put("key", "value");
        when(this.songService.getSongs(new HashMap<>())).thenReturn(this.songs);
        when(this.songService.getSongs(filters)).thenReturn(Collections.emptyList());

        List<SongDto> providedSongDtos = this.songController.getAllSongs(filters);

        assertEquals(Collections.emptyList(), providedSongDtos);
        verify(this.songService, times(1)).getSongs(anyMap());
    }

    @Test
    public void testGetSongById() {
        when(this.songService.getSongById(1L)).thenReturn(Optional.ofNullable(this.song1));
        assertEquals(this.songDto1, this.songController.getSongById(1L));
        verify(this.songService, times(1)).getSongById(1L);
    }

    @Test
    public void testGetSongByIdNonExistent() {
        when(this.songService.getSongById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> this.songController.getSongById(1L));
    }

    @Test
    public void testGetSongByIdNull() {
        assertThrows(ConstraintViolationException.class, () -> this.songController.getSongById(null));
    }

    @Test
    public void testAddSong() {
        try(MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)){
            mockedRequestManager.when(()->RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);
            when(this.songCreateFacadeService.createSongWithGenreAndArtistsAndAlbum(this.song1, 1L, 1L, Collections.emptyList())).thenReturn(this.song1);

            Long providedSongId = this.songController.addSong(this.songDto1, 1L, 1L, Collections.emptyList(), this.request);

            assertEquals(this.song1.getId(), providedSongId,
                    "When addSong() is called successfully, it should return the correct id of the object");
            verify(this.songCreateFacadeService, times(1)).createSongWithGenreAndArtistsAndAlbum(this.song1, 1L, 1L, Collections.emptyList());
        }
    }

    @Test
    public void testAddSongFail() {
        try(MockedStatic<RequestManager> mockedRequestManager = Mockito.mockStatic(RequestManager.class)){
            mockedRequestManager.when(()->RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);
            when(this.songCreateFacadeService.createSongWithGenreAndArtistsAndAlbum(this.song1, 1L, 1L, Collections.emptyList())).thenReturn(null);

            assertThrows(ApiBadRequest.class, () -> this.songController.addSong(this.songDto1, 1L, 1L, Collections.emptyList(),this.request));
        }
    }

    @Test
    public void testAddSongNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.songController.addSong(null, 1L, 1L, Collections.emptyList(), this.request));
        assertThrows(ConstraintViolationException.class, () -> this.songController.addSong(this.songDto1, null, 1L, Collections.emptyList(), this.request));
        assertThrows(ConstraintViolationException.class, () -> this.songController.addSong(this.songDto1, 1L, null, Collections.emptyList(), this.request));
        assertThrows(ConstraintViolationException.class, () -> this.songController.addSong(this.songDto1, 1L, 1L, null, this.request));
    }

    @Test
    public void testSetSongById() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);
            when(this.songSetFacadeService.setSongWithGenreAndArtistsAndAlbumIfProvided(song1.getId(), song1, 1L, 1L, Collections.emptyList())).thenReturn(true);

            boolean providedResult = this.songController.setSongById(this.song1.getId(), this.songDto1, 1L, 1L, Collections.emptyList(), this.request);

            assertTrue(providedResult);
            verify(this.songSetFacadeService, times(1)).setSongWithGenreAndArtistsAndAlbumIfProvided(song1.getId(), song1, 1L, 1L, Collections.emptyList());
        }
    }

    @Test
    public void testSetSongByIdNotAdmin() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(false);

            assertThrows(MethodNotAllowed.class, ()-> this.songController.setSongById(this.song1.getId(), this.songDto1, 1L, 1L, Collections.emptyList(), this.request));
        }
    }

    @Test
    public void testSetSongByIdNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.songController.setSongById(null, this.songDto1, 1L, 1L, Collections.emptyList(), this.request));
        assertThrows(ConstraintViolationException.class, () -> this.songController.setSongById(this.song1.getId(), null, 1L, 1L, Collections.emptyList(), this.request));

    }

    @Test
    public void testDeleteSongById() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(true);
            when(this.songService.deleteSong(this.song1.getId())).thenReturn(this.song1);

            assertEquals(this.songDto1, this.songController.deleteSongById(this.song1.getId(), this.request));
            verify(this.songService, times(1)).deleteSong(anyLong());
        }
    }
    @Test
    public void testDeleteSongByIdNotAdmin() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(false);

            assertThrows(MethodNotAllowed.class, ()-> this.songController.deleteSongById(this.song1.getId(), this.request));
        }
    }

    @Test
    public void testDeleteSongByIdNullParams() {
        try (MockedStatic<RequestManager> mocked = Mockito.mockStatic(RequestManager.class)) {
            mocked.when(() -> RequestManager.isAdminByRequest(this.request, this.tokenManagerService)).thenReturn(false);

            assertThrows(ConstraintViolationException.class, ()-> this.songController.deleteSongById(null, this.request));
        }
    }
}