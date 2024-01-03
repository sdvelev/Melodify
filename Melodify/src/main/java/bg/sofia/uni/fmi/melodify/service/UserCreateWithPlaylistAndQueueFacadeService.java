package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Playlist;
import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UserCreateWithPlaylistAndQueueFacadeService {
    private final static String DEFAULT_LIKED_SONGS_PLAYLIST_NAME = "Liked songs";
    private final static String DEFAULT_LIKED_SONGS_PLAYLIST_IMAGE = "https://i1.sndcdn.com/artworks-y6qitUuZoS6y8LQo-5s2pPA-t500x500.jpg";

    private final UserService userService;
    private final PlaylistService playlistService;
    private final QueueService queueService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserCreateWithPlaylistAndQueueFacadeService(UserService userService, PlaylistService playlistService,
                                                       QueueService queueService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.playlistService = playlistService;
        this.queueService = queueService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUserWithPlaylistAndQueue(@NotNull(message = "The provided user cannot be null")
                                               User userToSave) {

        Optional<User> usersWithThatEmailList = userService.getUserByEmail(userToSave.getEmail());
        if (usersWithThatEmailList.isPresent()) {
            throw new ApiBadRequest("There is already a user associated with that credentials");
        }

        userService.createUser(userToSave);

        Playlist playlistToCreate = new Playlist();
        playlistToCreate.setCreationDate(LocalDateTime.now());
        playlistToCreate.setOwner(userToSave);
        playlistToCreate.setName(DEFAULT_LIKED_SONGS_PLAYLIST_NAME);
        playlistToCreate.setImage(DEFAULT_LIKED_SONGS_PLAYLIST_IMAGE);

        playlistService.createPlaylist(playlistToCreate);

        Queue queueToCreate = new Queue();
        queueToCreate.setOwner(userToSave);

        List<Playlist> playListListToAdd = new ArrayList<>();
        playListListToAdd.add(playlistToCreate);

        userToSave.setPlaylists(playListListToAdd);
        userToSave.setQueue(queueToCreate);

        userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));

        return userService.createUser(userToSave);
    }
}