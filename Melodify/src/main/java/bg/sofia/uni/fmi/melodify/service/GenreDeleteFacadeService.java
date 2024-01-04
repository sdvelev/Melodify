package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.controller.GenreController;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Genre;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class GenreDeleteFacadeService {
    private final GenreService genreService;
    private final AlbumService albumService;
    private final SongService songService;

    @Autowired
    public GenreDeleteFacadeService(GenreService genreService, AlbumService albumService, SongService songService) {
        this.genreService = genreService;
        this.albumService = albumService;
        this.songService = songService;
    }

    @Transactional
    public Genre deleteGenreWithNullReferences(
        @NotNull(message = "The provided genre id cannot be null")
        @Positive(message = "The provided genre id must be positive")
        Long genreId) {
        Optional<Genre> potentialGenreToDelete = genreService.getGenreById(genreId);

        if (potentialGenreToDelete.isEmpty()) {
            throw new ResourceNotFoundException("There is not a genre with such an id");
        }

        List<Album> allAlbumsList = albumService.getAlbums(new HashMap<>());
        for (Album currentAlbum : allAlbumsList) {
            if (currentAlbum.getGenre().getGenre().equals(potentialGenreToDelete.get().getGenre())) {
                currentAlbum.setGenre(null);
                albumService.createAlbum(currentAlbum);
            }
        }

        List<Song> allSongsList = songService.getSongs(new HashMap<>());
        for (Song currentSong : allSongsList) {
            if (currentSong.getGenre().getGenre().equals(potentialGenreToDelete.get().getGenre())) {
                currentSong.setGenre(null);
                songService.createSong(currentSong);
            }
        }

        return genreService.deleteGenre(genreId);
    }
}
