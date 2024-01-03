package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Artist;
import bg.sofia.uni.fmi.melodify.model.Genre;
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
public class AlbumCreateFacadeService {
    private final AlbumService albumService;
    private final GenreService genreService;
    private final ArtistService artistService;

    @Autowired
    public AlbumCreateFacadeService(AlbumService albumService, GenreService genreService, ArtistService artistService) {
        this.albumService = albumService;
        this.genreService = genreService;
        this.artistService = artistService;
    }

    @Transactional
    public Album createAlbumWithGenreAndArtists(
        @NotNull(message = "The provided album cannot be null")
        Album albumToSave,
        @NotNull(message = "The provided associated genre id cannot be null")
        @Positive(message = "The provided associated genre id must be positive")
        Long genreIdToAssociate,
        @NotNull(message = "The provided associated event id cannot be null")
        List<Long> artistsIdsToAssociateList) {

        Optional<Genre> potentialGenreToAssociate = genreService.getGenreById(genreIdToAssociate);

        if (potentialGenreToAssociate.isPresent()) {
            albumToSave.setGenre(potentialGenreToAssociate.get());
        }

        List<Artist> artistsToAssociateList = new ArrayList<>();
        for (Long currentArtistId : artistsIdsToAssociateList) {
            Optional<Artist> potentialArtistToAssociate = artistService.getArtistById(currentArtistId);

            if (potentialArtistToAssociate.isPresent()) {
                artistsToAssociateList.add(potentialArtistToAssociate.get());
                potentialArtistToAssociate.get().getAlbums().add(albumToSave);
            }
        }

        albumToSave.setArtists(artistsToAssociateList);

        return albumService.createAlbum(albumToSave);
    }
}