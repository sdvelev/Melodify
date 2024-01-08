package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.AlbumDto;
import bg.sofia.uni.fmi.melodify.dto.ArtistDto;
import bg.sofia.uni.fmi.melodify.dto.GenreDto;
import bg.sofia.uni.fmi.melodify.mapper.AlbumMapper;
import bg.sofia.uni.fmi.melodify.mapper.ArtistMapper;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Artist;
import bg.sofia.uni.fmi.melodify.model.Genre;
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

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ArtistControllerTest {
    @MockBean
    private ArtistService artistService;
    @MockBean
    private TokenManagerService tokenManagerService;
    @MockBean
    private ArtistMapper artistMapper;
    @MockBean
    private AlbumMapper albumMapper;

    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    @Autowired
    private ArtistController artistController;

    private Artist artist1;
    private Artist artist2;
    private List<Artist> artists;
    private ArtistDto artistDto1;
    private ArtistDto artistDto2;
    private List<ArtistDto> artistDtos;
    @BeforeEach
    public void setup(){
        this.artist1= new Artist(1L, "Artist1", "artist1.png", "artist1.com", Collections.emptyList(), Collections.emptyList());
        this.artist2= new Artist(2L, "Artist2", "artist2.png", "artist2.com", Collections.emptyList(), Collections.emptyList());
        this.artists = List.of(artist1, artist2);
        this.artistDto1 = new ArtistDto(1L, "Artist1", "artist1.png", "artist1.com");
        this.artistDto2 = new ArtistDto(2L, "Artist2", "artist2.png", "artist2.com");
        this.artistDtos = List.of(artistDto1, artistDto2);

        MockitoAnnotations.openMocks(this);

        String stringToReturn = "Bearer ";
        when(this.request.getHeader("Authorization")).thenReturn(stringToReturn);

        when(this.artistMapper.toDto(this.artist1)).thenReturn(this.artistDto1);
        when(this.artistMapper.toDto(this.artist2)).thenReturn(this.artistDto2);
        when(this.artistMapper.toDtoCollection(this.artists)).thenReturn(this.artistDtos);
        when(this.artistMapper.toDtoCollection(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(this.artistMapper.toEntity(this.artistDto1)).thenReturn(this.artist1);
        when(this.artistMapper.toEntity(this.artistDto2)).thenReturn(this.artist2);
        when(this.artistMapper.toEntityCollection(this.artistDtos)).thenReturn(this.artists);
        when(this.artistMapper.toEntityCollection(Collections.emptyList())).thenReturn(Collections.emptyList());
    }

    @Test
    public void testGetAllArtists() {
        when(artistService.getArtists(any())).thenReturn(this.artists);

        List<ArtistDto> providedArtistDtos = this.artistController.getAllArtists(new HashMap<>());

        assertEquals(this.artistDtos, providedArtistDtos);
        verify(this.artistService, times(1)).getArtists(any());
        verify(this.artistMapper, times(1)).toDtoCollection(any());
    }

    @Test
    public void testGetAllArtistsWithFilters() {
        Map<String, String> filters = new HashMap<>();
        filters.put("key", "value");
        when(artistService.getArtists(any())).thenReturn(this.artists);
        when(artistService.getArtists(filters)).thenReturn(Collections.emptyList());

        List<ArtistDto> providedArtistDtos = this.artistController.getAllArtists(filters);

        assertEquals(Collections.emptyList(), providedArtistDtos);
        verify(this.artistService, times(1)).getArtists(any());
        verify(this.artistMapper, times(1)).toDtoCollection(any());
    }

    @Test
    public void testGetArtistById() {
        when(this.artistService.getArtistById(1L)).thenReturn(Optional.of(this.artist1));
        ArtistDto providedArtistDto  = artistController.getArtistById(1L);

        assertEquals(artistDto1, providedArtistDto);
        verify(this.artistService, times(1)).getArtistById(1L);
        verify(this.artistMapper, times(1)).toDto(this.artist1);
    }

    @Test
    public void testGetArtistByIdNonExistent() {
        when(this.artistService.getArtistById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()-> this.artistController.getArtistById(1L),
                "getArtistById should throw a ResourceNotFound exception, when there is no artist with the provided id");
        verify(this.artistService, times(1)).getArtistById(1L);
    }

    @Test
    public void testGetArtistByIdNull() {
        assertThrows(ConstraintViolationException.class,()-> this.artistController.getArtistById(null),
                "getArtistById should throw an exception, when there the provided id is null");
    }

    @Test
    public void testGetArtistAlbumsById() {
        Album album1 = new Album(1L, "One", LocalDate.of(2001, 9, 22), new Genre(1L, "rock"), "one.png", Collections.emptyList(), Collections.emptyList(), "one.com");
        Album album2 = new Album(2L, "Two", LocalDate.of(2001, 9, 22), new Genre(1L, "rock"), "two.png", Collections.emptyList(), Collections.emptyList(), "two.com");
        List<Album> albums = List.of(album1, album2);

        AlbumDto albumDto1 = new AlbumDto(1L, "One", LocalDate.of(2001, 9, 22), new GenreDto(1L, "rock"), "one.png", Collections.emptyList(), Collections.emptyList(), "one.com");
        AlbumDto albumDto2 = new AlbumDto(2L, "Two", LocalDate.of(2001, 9, 22), new GenreDto(1L, "rock"), "two.png", Collections.emptyList(), Collections.emptyList(), "two.com");
        List<AlbumDto> albumDtos = List.of(albumDto1, albumDto2);

        when(this.artistService.getArtistAlbumsById(1L)).thenReturn(albums);
        when(this.albumMapper.toDtoCollection(albums)).thenReturn(albumDtos);

        List<AlbumDto> providedAlbumDtos = this.artistController.getArtistAlbumsById(1L);

        assertEquals(albumDtos, providedAlbumDtos);
        verify(this.artistService, times(1)).getArtistAlbumsById(1L);
        verify(this.albumMapper, times(1)).toDtoCollection(albums);
    }

    @Test
    public void testGetArtistAlbumsByIdNull() {
        assertThrows(ConstraintViolationException.class, ()->this.artistController.getArtistAlbumsById(null),
                "getArtistAlbumsById should throw an exception, when there the provided id is null");
    }

    @Test
    public void testAddArtist() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.artistService.createArtist(this.artist1)).thenReturn(this.artist1);

        Long providedArtistId = this.artistController.addArtist(this.artistDto1, this.request);

        assertEquals(this.artist1.getId(), providedArtistId,
                "When addArtist() is called successfully, it should return the correct id of the object");
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.artistService, times(1)).createArtist(this.artist1);
    }

    @Test
    public void testAddArtistFail() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.artistService.createArtist(this.artist1)).thenReturn(null);

        assertThrows(ApiBadRequest.class, ()->this.artistController.addArtist(this.artistDto1, this.request),
                "When addArtist() is called but object fails to create, it should throw a Method Not allowed exception");
    }

    @Test
    public void testAddArtistNotAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");

        assertThrows(MethodNotAllowed.class, ()->this.artistController.addArtist(this.artistDto1, this.request),
                "When addArtist() is called without admin privileges, it should throw a Method Not allowed exception");
    }

    @Test
    public void testAddArtistNullParams() {
        assertThrows(ConstraintViolationException.class, ()-> this.artistController.addArtist(null, this.request),
                "When addArtist() is called with a null artist dto, it should throw an exception");
    }

    @Test
    public void testSetArtistById() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.artistService.setArtistById(this.artistDto1, 1L)).thenReturn(true);

        boolean providedResult = this.artistController.setArtistById(1L, this.artistDto1, this.request);

        assertTrue(providedResult);
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.artistService, times(1)).setArtistById(this.artistDto1, 1L);
    }

    @Test
    public void testSetArtistByIdNotAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");

        assertThrows(MethodNotAllowed.class, ()-> this.artistController.setArtistById(1L, this.artistDto1, this.request));
    }

    @Test
    public void testSetArtistByIdNullParams() {
        assertThrows(ConstraintViolationException.class, ()-> this.artistController.setArtistById(null, this.artistDto1, this.request));
        assertThrows(ConstraintViolationException.class, ()-> this.artistController.setArtistById(1L, null, this.request));
    }

    @Test
    public void testDeleteArtistById() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.artistService.deleteArtist(1L)).thenReturn(this.artist1);

        ArtistDto deletedArtist = artistController.deleteArtistById(1L, this.request);

        assertEquals(this.artistDto1, deletedArtist);
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.artistService, times(1)).deleteArtist(1L);
    }

    @Test
    public void testDeleteArtistByIdNotAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");

        assertThrows(MethodNotAllowed.class, ()-> this.artistController.deleteArtistById(1L, this.request));
    }

    @Test
    public void testDeleteArtistByIdNullParams() {

        assertThrows(ConstraintViolationException.class,()-> this.artistController.deleteArtistById(null, this.request));
    }
}