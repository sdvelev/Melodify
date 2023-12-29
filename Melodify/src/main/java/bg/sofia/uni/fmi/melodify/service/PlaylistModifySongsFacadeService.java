package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Playlist;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class PlaylistModifySongsFacadeService {
    private final PlaylistService playlistService;
    private final SongService songService;

    @Autowired
    public PlaylistModifySongsFacadeService(PlaylistService playlistService, SongService songService) {
        this.playlistService = playlistService;
        this.songService = songService;
    }

    public boolean addSongToPlaylist(@NotNull(message = "The provided playlist id cannot be null")
                                     @Positive(message = "The provided playlist id must be positive")
                                     Long playlistId,
                                     @NotNull(message = "The provided song id cannot be null")
                                     @Positive(message = "The provided song id must be positive")
                                     Long songId) {

        Optional<Playlist> potentialPlaylist = playlistService.getPlaylistById(playlistId);

        if (potentialPlaylist.isEmpty()) {
            throw new ResourceNotFoundException("There is not a playlist with such an id");
        }

        Optional<Song> potentialSongToAdd = songService.getSongById(songId);

        if (potentialSongToAdd.isEmpty()) {
            throw new ResourceNotFoundException("There is not a song with such an id");
        }

        potentialPlaylist.get().getSongs().add(potentialSongToAdd.get());

        playlistService.createPlaylist(potentialPlaylist.get());

        return true;
    }

    public boolean removeSongFromPlaylist(@NotNull(message = "The provided playlist id cannot be null")
                                     @Positive(message = "The provided playlist id must be positive")
                                     Long playlistId,
                                     @NotNull(message = "The provided song id cannot be null")
                                     @Positive(message = "The provided song id must be positive")
                                     Long songId) {

        Optional<Playlist> potentialPlaylist = playlistService.getPlaylistById(playlistId);

        if (potentialPlaylist.isEmpty()) {
            throw new ResourceNotFoundException("There is not a playlist with such an id");
        }

        Optional<Song> potentialSongToAdd = songService.getSongById(songId);

        if (potentialSongToAdd.isEmpty()) {
            throw new ResourceNotFoundException("There is not a song with such an id");
        }

        if (!potentialPlaylist.get().getSongs().contains(potentialSongToAdd.get())) {
            throw new ResourceNotFoundException("There is not such a song in the provided playlist");
        }

        potentialPlaylist.get().getSongs().remove(potentialSongToAdd.get());

        playlistService.createPlaylist(potentialPlaylist.get());

        return true;
    }
}