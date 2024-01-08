package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class AlbumDeleteFacadeService {
    private final AlbumService albumService;
    private final SongService songService;

    @Autowired
    public AlbumDeleteFacadeService(AlbumService albumService, SongService songService) {
        this.albumService = albumService;
        this.songService = songService;
    }

    @Transactional
    public Album deleteAlbumWithSongs(
        @NotNull(message = "The provided album id cannot be null")
        @Positive(message = "The provided album id must be positive")
        Long albumId) {
        Optional<Album> potentialAlbumToDelete = albumService.getAlbumById(albumId);

        if (potentialAlbumToDelete.isEmpty()) {
            throw new ResourceNotFoundException("There is no album with such id");
        }

        List<Song> songsToDeleteList = potentialAlbumToDelete.get().getSongs();
        for (Song currentSong : songsToDeleteList) {
            this.songService.deleteSong(currentSong.getId());
        }

        return this.albumService.deleteAlbum(albumId);
    }
}