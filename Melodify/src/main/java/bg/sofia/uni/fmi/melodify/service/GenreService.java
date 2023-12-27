package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.dto.GenreDto;
import bg.sofia.uni.fmi.melodify.model.Genre;
import bg.sofia.uni.fmi.melodify.repository.GenreRepository;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre createGenre(@NotNull(message = "The provided genre cannot be null") Genre genreToSave) {
        return genreRepository.save(genreToSave);
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Optional<Genre> getGenreById(
        @NotNull(message = "The provided id cannot be null")
        @Positive(message = "The provided id must be positive")
        Long id) {
        Optional<Genre> potentialGenre = genreRepository.findById(id);

        if (potentialGenre.isPresent()) {
            return potentialGenre;
        }

        throw new ResourceNotFoundException("There is not a genre with such id");
    }

    public boolean setGenreById(
        @NotNull(message = "The provided genre description cannot be null")
        GenreDto genreDtoToChange,
        @NotNull(message = "The provided genre id cannot be null")
        @Positive(message = "The provided genre id must be positive")
        Long genreId) {

        Optional<Genre> optionalGenreToUpdate = genreRepository.findById(genreId);

        if (optionalGenreToUpdate.isPresent()) {
            Genre genreToUpdate = optionalGenreToUpdate.get();
            genreToUpdate.setGenre(genreDtoToChange.getGenre());
            genreRepository.save(genreToUpdate);
            return true;
        }

        throw new ResourceNotFoundException("There is not a genre with such an id");
    }

    public Genre deleteGenre(
        @NotNull(message = "The provided genre id cannot be null")
        @Positive(message = "The provided genre id must be positive")
        Long genreToDeleteId) {

        Optional<Genre> optionalGenreToDelete = genreRepository.findById(genreToDeleteId);

        if (optionalGenreToDelete.isPresent()) {
            Genre genreToDelete = optionalGenreToDelete.get();
            genreRepository.delete(genreToDelete);
            return genreToDelete;
        }

        throw new ResourceNotFoundException("There is not a genre with such an id");
    }
}