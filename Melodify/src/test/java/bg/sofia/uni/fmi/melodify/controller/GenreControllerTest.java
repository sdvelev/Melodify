package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.GenreDto;
import bg.sofia.uni.fmi.melodify.mapper.GenreMapper;
import bg.sofia.uni.fmi.melodify.model.Genre;
import bg.sofia.uni.fmi.melodify.service.GenreDeleteFacadeService;
import bg.sofia.uni.fmi.melodify.service.GenreService;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GenreControllerTest {
    @MockBean
    private GenreService genreService;
    @MockBean
    private GenreDeleteFacadeService genreDeleteFacadeService;
    @MockBean
    private TokenManagerService tokenManagerService;
    @MockBean
    private GenreMapper genreMapper;


    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    @Autowired
    private GenreController genreController;

    private Genre genre1;
    private Genre genre2;
    private List<Genre> genres;
    private GenreDto genreDto1;
    private GenreDto genreDto2;
    private List<GenreDto> genreDtos;

    @BeforeEach
    public void setup() {
        this.genre1 = new Genre(1L, "Genre1");
        this.genre2 = new Genre(2L, "Genre2");
        this.genres = List.of(genre1, genre2);
        this.genreDto1 = new GenreDto(1L, "Genre1");
        this.genreDto2 = new GenreDto(2L, "Genre2");
        this.genreDtos = List.of(genreDto1, genreDto2);

        MockitoAnnotations.openMocks(this);

        String stringToReturn = "Bearer ";
        when(this.request.getHeader("Authorization")).thenReturn(stringToReturn);

        when(this.genreMapper.toDto(this.genre1)).thenReturn(this.genreDto1);
        when(this.genreMapper.toDto(this.genre2)).thenReturn(this.genreDto2);
        when(this.genreMapper.toDtoCollection(this.genres)).thenReturn(this.genreDtos);
        when(this.genreMapper.toDtoCollection(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(this.genreMapper.toEntity(this.genreDto1)).thenReturn(this.genre1);
        when(this.genreMapper.toEntity(this.genreDto2)).thenReturn(this.genre2);
        when(this.genreMapper.toEntityCollection(this.genreDtos)).thenReturn(this.genres);
        when(this.genreMapper.toEntityCollection(Collections.emptyList())).thenReturn(Collections.emptyList());
    }

    @Test
    public void testGetAllGenres() {
        when(genreService.getAllGenres(any())).thenReturn(this.genres);

        List<GenreDto> providedGenreDtos = this.genreController.getGenres(new HashMap<>());

        assertEquals(this.genreDtos, providedGenreDtos);
        verify(this.genreService, times(1)).getAllGenres(any());
        verify(this.genreMapper, times(1)).toDtoCollection(any());
    }

    @Test
    public void testGetAllGenresWithFilters() {
        Map<String, String> filters = new HashMap<>();
        filters.put("key", "value");
        when(genreService.getAllGenres(any())).thenReturn(this.genres);
        when(genreService.getAllGenres(filters)).thenReturn(Collections.emptyList());

        List<GenreDto> providedGenreDtos = this.genreController.getGenres(filters);

        assertEquals(Collections.emptyList(), providedGenreDtos);
        verify(this.genreService, times(1)).getAllGenres(any());
        verify(this.genreMapper, times(1)).toDtoCollection(any());
    }

    @Test
    public void testGetGenreById() {
        when(this.genreService.getGenreById(1L)).thenReturn(Optional.of(this.genre1));
        GenreDto providedGenreDto = genreController.getGenreById(1L);

        assertEquals(genreDto1, providedGenreDto);
        verify(this.genreService, times(1)).getGenreById(1L);
        verify(this.genreMapper, times(1)).toDto(this.genre1);
    }

    @Test
    public void testGetGenreByIdNonExistent() {
        when(this.genreService.getGenreById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> this.genreController.getGenreById(1L),
                "getGenreById should throw a ResourceNotFound exception, when there is no genre with the provided id");
        verify(this.genreService, times(1)).getGenreById(1L);
    }

    @Test
    public void testGetGenreByIdNull() {
        assertThrows(ConstraintViolationException.class, () -> this.genreController.getGenreById(null),
                "getGenreById should throw an exception, when there the provided id is null");
    }

    @Test
    public void testAddGenre() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.genreService.createGenre(this.genre1)).thenReturn(this.genre1);

        Long providedGenreId = this.genreController.addGenre(this.genreDto1, this.request);

        assertEquals(this.genre1.getId(), providedGenreId,
                "When addGenre() is called successfully, it should return the correct id of the object");
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.genreService, times(1)).createGenre(this.genre1);
    }

    @Test
    public void testAddGenreFail() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.genreService.createGenre(this.genre1)).thenReturn(null);

        assertThrows(ApiBadRequest.class, () -> this.genreController.addGenre(this.genreDto1, this.request),
                "When addGenre() is called but object fails to create, it should throw a Method Not allowed exception");
    }

    @Test
    public void testAddGenreNotAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");

        assertThrows(MethodNotAllowed.class, () -> this.genreController.addGenre(this.genreDto1, this.request),
                "When addGenre() is called without admin privileges, it should throw a Method Not allowed exception");
    }

    @Test
    public void testAddGenreNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.genreController.addGenre(null, this.request),
                "When addGenre() is called with a null genre dto, it should throw an exception");
    }

    @Test
    public void testSetGenreById() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.genreService.setGenreById(this.genreDto1, 1L)).thenReturn(true);

        boolean providedResult = this.genreController.setGenreById(1L, this.genreDto1, this.request);

        assertTrue(providedResult);
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.genreService, times(1)).setGenreById(this.genreDto1, 1L);
    }

    @Test
    public void testSetGenreByIdNotAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");

        assertThrows(MethodNotAllowed.class, () -> this.genreController.setGenreById(1L, this.genreDto1, this.request));
    }

    @Test
    public void testSetGenreByIdNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.genreController.setGenreById(null, this.genreDto1, this.request));
        assertThrows(ConstraintViolationException.class, () -> this.genreController.setGenreById(1L, null, this.request));
    }

    @Test
    public void testDeleteGenreById() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("true");
        when(this.genreDeleteFacadeService.deleteGenreWithNullReferences(1L)).thenReturn(this.genre1);

        GenreDto deletedGenre = genreController.deleteGenreById(1L, this.request);

        assertEquals(this.genreDto1, deletedGenre);
        verify(this.tokenManagerService, times(1)).getIsAdminFromToken(anyString());
        verify(this.genreDeleteFacadeService, times(1)).deleteGenreWithNullReferences(1L);
    }

    @Test
    public void testDeleteGenreByIdNotAdmin() {
        when(this.tokenManagerService.getIsAdminFromToken(anyString())).thenReturn("false");

        assertThrows(MethodNotAllowed.class, () -> this.genreController.deleteGenreById(1L, this.request));
    }

    @Test
    public void testDeleteGenreByIdNullParams() {
        assertThrows(ConstraintViolationException.class, () -> this.genreController.deleteGenreById(null, this.request));
    }
}
