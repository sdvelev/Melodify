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
public class SongSetFacadeService {
    private final SongService songService;
    private final GenreService genreService;
    private final ArtistService artistService;
    private final AlbumService albumService;

    @Autowired
    public SongSetFacadeService(SongService songService, GenreService genreService, ArtistService artistService,
                                   AlbumService albumService) {
        this.songService = songService;
        this.genreService = genreService;
        this.artistService = artistService;
        this.albumService = albumService;
    }

    @Transactional
    public boolean setSongWithGenreAndArtistsAndAlbumIfProvided(
        @NotNull(message = "The provided album id cannot be null")
        @Positive(message = "The provided album id must be positive")
        Long songId,
        @NotNull(message = "The provided album cannot be null")
        Song songToSave,
        Long genreId,
        Long albumId,
        List<Long> artistIdsList) {

        if (genreId != null) {
            Optional<Genre> potentialGenreToAssociate = genreService.getGenreById(genreId);

            if (potentialGenreToAssociate.isPresent()) {
                songToSave.setGenre(potentialGenreToAssociate.get());
            }
        }

        if (albumId != null) {
            Optional<Album> potentialAlbumToAssociate = albumService.getAlbumById(albumId);

            if (potentialAlbumToAssociate.isPresent()) {
                songToSave.setAlbum(potentialAlbumToAssociate.get());
            }
        }

        if (artistIdsList != null) {
            List<Artist> artistsToAssociateList = new ArrayList<>();
            for (Long currentArtistId : artistIdsList) {
                Optional<Artist> potentialArtistToAssociate = artistService.getArtistById(currentArtistId);

                if (potentialArtistToAssociate.isPresent()) {
                    artistsToAssociateList.add(potentialArtistToAssociate.get());
                    potentialArtistToAssociate.get().getSongs().add(songToSave);
                }
            }
            songToSave.setArtists(artistsToAssociateList);
        }

        return songService.setSongById(songToSave, songId);
    }
}