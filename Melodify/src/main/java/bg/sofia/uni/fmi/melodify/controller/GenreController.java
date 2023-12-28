package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.GenreDto;
import bg.sofia.uni.fmi.melodify.mapper.GenreMapper;
import bg.sofia.uni.fmi.melodify.model.Genre;
import bg.sofia.uni.fmi.melodify.service.GenreService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController
@RequestMapping(path = "api/genres")
@Validated
public class GenreController {

    private final GenreService genreService;
    private final GenreMapper genreMapper;

    @Autowired
    public GenreController(GenreService genreService, GenreMapper genreMapper) {
        this.genreService = genreService;
        this.genreMapper = genreMapper;
    }

    @GetMapping()
    public List<GenreDto> getGenres(@RequestParam Map<String, String> filters){
        return this.genreMapper.toDtoCollection(this.genreService.getAllGenres(filters));
    }

    @GetMapping(value = "/{id}")
    public GenreDto getGenreById(@PathVariable
                                           @NotNull(message = "The provided genre id cannot be null")
                                           @Positive(message = "The provided genre id must be positive")
                                     Long id) {
        Optional<Genre> potentialGenreToReturn = genreService.getGenreById(id);
        if (potentialGenreToReturn.isPresent()) {
            return genreMapper.toDto(potentialGenreToReturn.get());
        }

        throw new ResourceNotFoundException("There is not a genre with such an id");
    }

    @PostMapping
    public Long addGenre(@NotNull(message = "The provided genre description in the body cannot be null")
                         @RequestBody
                             GenreDto genreDto) {
        Genre potentialGenreToCreate = genreService.createGenre(genreMapper.toEntity(genreDto));

        if (potentialGenreToCreate == null) {
            throw new ApiBadRequest("There was a problem in creating a genre");
        }

        return potentialGenreToCreate.getId();
    }
    @PutMapping(value = "/{id}")
    public boolean setGenreById(@PathVariable
                                @NotNull(message = "The provided genre id cannot be null")
                                @Positive(message = "The provided genre id must be positive")
                                Long id,
                                @RequestBody
                                @NotNull(message = "The provided genre dto in the body of the query cannot be null")
                                GenreDto genreToUpdate) {
        return genreService.setGenreById(genreToUpdate, id);
    }

    @DeleteMapping(params = {"genre_id"})
    public GenreDto deleteGenreById(@RequestParam("genre_id")
                               @NotNull(message = "The provided genre id cannot be null")
                               @Positive(message = "The provided genre id must be positive")
                               Long genreId) {
        return genreMapper.toDto(genreService.deleteGenre(genreId));
    }
}