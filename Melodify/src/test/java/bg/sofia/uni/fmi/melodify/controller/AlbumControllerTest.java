package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.AlbumDto;
import bg.sofia.uni.fmi.melodify.mapper.AlbumMapper;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Artist;
import bg.sofia.uni.fmi.melodify.model.Genre;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.service.*;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AlbumControllerTest {

    @MockBean
    private AlbumService albumService;
    @MockBean
    private AlbumCreateFacadeService albumCreateFacadeService;
    @MockBean
    private AlbumSetFacadeService albumSetFacadeService;
    @MockBean
    private AlbumDeleteFacadeService albumDeleteFacadeService;
    @MockBean
    private TokenManagerService tokenManagerService;
    @MockBean
    private AlbumMapper albumMapper;

    private List<Artist> artists;
    private List<Song> songs;

    private Genre genre;
    private Album album1;
    private Album album2;
    private List<Album> albums;

    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    @Autowired
    private AlbumController albumController;

    @BeforeEach
    public void setup() {
        artists = new ArrayList<>();
        songs = new ArrayList<>();

        genre = new Genre(1L, "rock");
        album1 = new Album(1L, "One", LocalDate.of(2001, 9, 22), genre, "one.png", songs, artists, "one.com");
        album2 = new Album(2L, "Two", LocalDate.of(2001, 9, 22), genre, "two.png", songs, artists, "two.com");
        albums = List.of(album1, album2);

        MockitoAnnotations.openMocks(this);

        String stringToReturn = "Bearer ";
        when(request.getHeader("Authorization")).thenReturn(stringToReturn);
    }

    @Test
    public void testGetAlbums() {
        List<AlbumDto> expectedAlbumDtos = this.albumMapper.toDtoCollection(this.albums);
        when(this.albumService.getAlbums(any())).thenReturn(this.albums);

        List<AlbumDto> providedAlbumDtos = this.albumController.getAlbums(new HashMap<>());

        assertEquals(expectedAlbumDtos, providedAlbumDtos);
        verify(albumService, times(1)).getAlbums(any());
        verify(albumMapper, times(2)).toDtoCollection(any());
    }

    @Test
    public void testGetWithFilters() {
        Map<String, String> filters = new HashMap<>();
        filters.put("key", "value");
        when(this.albumService.getAlbums(any())).thenReturn(this.albums);
        when(this.albumService.getAlbums(filters)).thenReturn(Collections.emptyList());

        List<AlbumDto> expectedAlbumDtos = Collections.emptyList();
        List<AlbumDto> providedAlbumDtos = this.albumController.getAlbums(filters);

        assertEquals(expectedAlbumDtos, providedAlbumDtos, "Provided list of album dtos was not as expected");
        verify(this.albumService, times(1)).getAlbums(any());
        verify(this.albumMapper, times(1)).toDtoCollection(any());
    }

    @Test
    public void testGetAlbumById() {
        when(this.albumService.getAlbumById(1L)).thenReturn(Optional.ofNullable(this.album1));
        when(this.albumService.getAlbumById(2L)).thenReturn(Optional.ofNullable(this.album2));

        AlbumDto expectedAlbumDto = this.albumMapper.toDto(this.album1);
        AlbumDto providedAlbumDto = this.albumController.getAlbumById(1L);
        assertEquals(expectedAlbumDto, providedAlbumDto, "Provided AlbumDto should was not as expected");
        verify(this.albumService, times(1)).getAlbumById(1L);
        verify(this.albumMapper, times(2)).toDto(this.album1);
    }

    @Test
    public void testGetAlbumByIdWithNullId() {
        assertThrows(ConstraintViolationException.class, () -> this.albumController.getAlbumById(null),
                "Calling getAlbumById() with an id of a non-existent album should have thrown an exceptions");
    }

    @Test
    public void testGetAlbumByIdWithNonExistentId() {
        when(this.albumService.getAlbumById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> this.albumController.getAlbumById(1L),
                "Calling getAlbumById() with an id of a non-existent album should have thrown an exceptions");
        verify(this.albumService, times(1)).getAlbumById(1L);
    }

    @Test
    public void testAddAlbumNullParameters() {
        assertThrows(ConstraintViolationException.class, () -> this.albumController.addAlbum(null, 1L, Collections.emptyList(), request),
                "Calling addAlbum() with a null request body parameter should throw an exception");
        assertThrows(ConstraintViolationException.class, () -> this.albumController.addAlbum(new AlbumDto(), null, Collections.emptyList(), request),
                "Calling addAlbum() with a null id parameter should throw an exception");
        assertThrows(ConstraintViolationException.class, () -> this.albumController.addAlbum(new AlbumDto(), 1L, null, request),
                "Calling addAlbum() with a null artist list parameter should throw an exception");
    }

    @Test
    public void testAddAlbumNoAdminRequest() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");
        assertThrows(MethodNotAllowed.class, () -> this.albumController.addAlbum(new AlbumDto(), 1L, Collections.emptyList(), request),
                "Calling addAlbum() without admin privileges token should throw an exception");

    }

    @Test
    public void testAddAlbumNull() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.albumCreateFacadeService.createAlbumWithGenreAndArtists(this.album1, 1L, Collections.emptyList())).thenReturn(null);
        assertThrows(ApiBadRequest.class, () -> this.albumController.addAlbum(new AlbumDto(), 1L, Collections.emptyList(), request),
                "The addAlbum() method throw an exception, when the created album is null");
        verify(this.albumCreateFacadeService, times(1)).createAlbumWithGenreAndArtists(any(), any(), any());
    }

    @Test
    public void testAddAlbumSuccess() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.albumMapper.toEntity(any(AlbumDto.class))).thenReturn(this.album1);
        when(this.albumCreateFacadeService.createAlbumWithGenreAndArtists(this.album1, 1L, Collections.emptyList())).thenReturn(this.album1);
        assertEquals(1L, this.albumController.addAlbum(new AlbumDto(), 1L, Collections.emptyList(), request),
                "The addAlbum() method should return an id if the request is successful");
        verify(this.albumCreateFacadeService, times(1)).createAlbumWithGenreAndArtists(any(), any(), any());
    }

    @Test
    public void testDeleteAlbumByIdNull() {
        assertThrows(ConstraintViolationException.class, () -> this.albumController.deleteAlbumById(null, request),
                "Calling addAlbum() with a null id parameter should throw an exception");
    }

    @Test
    public void testDeleteNotAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString()
        )).thenReturn("false");
        when(this.albumMapper.toDto(this.album1)).thenReturn(new AlbumDto());
        when(this.albumDeleteFacadeService.deleteAlbumWithSongs(1L)).thenReturn(this.album1);
        assertThrows(MethodNotAllowed.class, () -> this.albumController.deleteAlbumById(1L, request),
                "Calling deleteAlbum() without admin privileges token should throw an exception");
    }

    @Test
    public void testDeleteSuccess() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.albumDeleteFacadeService.deleteAlbumWithSongs(1L)).thenReturn(this.album1);
        when(this.albumMapper.toDto(this.album1)).thenReturn(new AlbumDto());

        AlbumDto expectedAlbumDto = new AlbumDto();
        AlbumDto providedAlbumDto = this.albumController.deleteAlbumById(1L, request);
        assertEquals(expectedAlbumDto, providedAlbumDto, "A successful delete should return the proper album dto");
        verify(this.albumDeleteFacadeService, times(1)).deleteAlbumWithSongs(1L);
        verify(this.albumMapper, times(1)).toDto(this.album1);
    }

    @Test
    public void testSetAlbumByIdNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.albumController.setAlbumById(null, new AlbumDto(), 1L, Collections.emptyList(), request),
                "Calling addAlbum() with a null id parameter should throw an exception");
        assertThrows(ConstraintViolationException.class, () -> this.albumController.setAlbumById(1L, null, 1L, Collections.emptyList(), request),
                "Calling addAlbum() with a null albumDto parameter should throw an exception");
    }

    @Test
    public void setAlbumByIdNoAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");
        assertThrows(MethodNotAllowed.class, () -> this.albumController.setAlbumById(1L, new AlbumDto(), 1L, Collections.emptyList(), request),
                "When setAlbumById() is called without admin privileges token, it should throw a MethodNotAllowed exception");
    }

    @Test
    public void setAlbumByIdInvalidId() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.albumMapper.toEntity(any())).thenReturn(this.album1);
        when(this.albumSetFacadeService.setAlbumWithGenreAndArtistsIfProvided(1L, this.album1, 1L, Collections.emptyList())).thenReturn(true);
        assertTrue(this.albumController.setAlbumById(1L, new AlbumDto(), 1L, Collections.emptyList(), request));
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.albumMapper, times(1)).toEntity(any());
        verify(this.albumSetFacadeService, times(1)).setAlbumWithGenreAndArtistsIfProvided(any(), any(), any(), any());
    }
}