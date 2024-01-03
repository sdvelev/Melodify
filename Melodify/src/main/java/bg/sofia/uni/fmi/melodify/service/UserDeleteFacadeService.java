package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Playlist;
import bg.sofia.uni.fmi.melodify.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class UserDeleteFacadeService {
    private final UserService userService;
    private final PlaylistService playlistService;
    private final QueueService queueService;

    @Autowired
    public UserDeleteFacadeService(UserService userService, PlaylistService playlistService,
                                   QueueService queueService) {
        this.userService = userService;
        this.playlistService = playlistService;
        this.queueService = queueService;
    }

    public boolean deleteUserWithPlaylistsAssociatedToIt(
        @NotNull(message = "The provided email cannot be null")
        @NotBlank(message = "The provided email must be positive")
        String email,
        @NotNull(message = "The provided password cannot be null")
        @NotBlank(message = "The provided password must be positive")
        String password) {
        User userToDelete = userService.getUserByEmailAndPassword(email, password);

        List<Playlist> associatedUserPlaylistsList = userToDelete.getPlaylists();

        for (Playlist currentPlaylist : associatedUserPlaylistsList) {
            playlistService.deletePlaylist(currentPlaylist.getId(), userToDelete, false);
        }

        queueService.deleteQueue(userToDelete.getQueue().getId());

        userService.deleteUser(email, password);
        return true;
    }
}