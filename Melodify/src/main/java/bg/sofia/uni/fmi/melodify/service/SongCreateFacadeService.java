package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Artist;
import bg.sofia.uni.fmi.melodify.model.Genre;
import bg.sofia.uni.fmi.melodify.model.Song;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class SongCreateFacadeService {
    private final SongService songService;
    private final GenreService genreService;
    private final ArtistService artistService;
    private final AlbumService albumService;

    @Autowired
    public SongCreateFacadeService(SongService songService, GenreService genreService, ArtistService artistService,
                                   AlbumService albumService) {
        this.songService = songService;
        this.genreService = genreService;
        this.artistService = artistService;
        this.albumService = albumService;
    }

    @Transactional
    public Song createSongWithGenreAndArtistsAndAlbum(
        @NotNull(message = "The provided album cannot be null")
        Song songToSave,
        @NotNull(message = "The provided genre id cannot be null")
        @Positive(message = "The provided genre id must be positive")
        Long genreId,
        @NotNull(message = "The provided album id cannot be null")
        @Positive(message = "The provided album id must be positive")
        Long albumId,
        @NotNull(message = "The provided artist ids cannot be null")
        List<Long> artistIdsList) {

        Optional<Genre> potentialGenreToAssociate = genreService.getGenreById(genreId);

        if (potentialGenreToAssociate.isPresent()) {
            songToSave.setGenre(potentialGenreToAssociate.get());
        }

        Optional<Album> potentialAlbumToAssociate = albumService.getAlbumById(albumId);

        if (potentialAlbumToAssociate.isPresent()) {
            songToSave.setAlbum(potentialAlbumToAssociate.get());
        }

        List<Artist> artistsToAssociateList = new ArrayList<>();
        for (Long currentArtistId : artistIdsList) {
            Optional<Artist> potentialArtistToAssociate = artistService.getArtistById(currentArtistId);

            if (potentialArtistToAssociate.isPresent()) {
                artistsToAssociateList.add(potentialArtistToAssociate.get());
                potentialArtistToAssociate.get().getSongs().add(songToSave);
            }
        }

        songToSave.setArtists(artistsToAssociateList);

        return songService.createSong(songToSave);
    }
}