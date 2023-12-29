package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Playlist;
import bg.sofia.uni.fmi.melodify.model.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class PlaylistCreateFacadeService {
    private final PlaylistService playlistService;
    private final UserService userService;

    @Autowired
    public PlaylistCreateFacadeService(PlaylistService playlistService, UserService userService) {
        this.playlistService = playlistService;
        this.userService = userService;
    }

    @Transactional
    public Playlist createPlaylistWithOwner(
        @NotNull(message = "The provided playlist cannot be null")
        Playlist playlistToSave,
        @NotNull(message = "The provided associated owner id cannot be null")
        @Positive(message = "The provided associated owner id must be positive")
        Long userIdToAssociate) {

        Optional<User> potentialUserToAssociate = userService.getUserById(userIdToAssociate);

        if (potentialUserToAssociate.isPresent()) {
            playlistToSave.setOwner(potentialUserToAssociate.get());
            potentialUserToAssociate.get().getPlaylists().add(playlistToSave);
        }

        return playlistService.createPlaylist(playlistToSave);
    }
}