package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.PlaylistDto;
import bg.sofia.uni.fmi.melodify.mapper.PlaylistMapper;
import bg.sofia.uni.fmi.melodify.model.Playlist;
import bg.sofia.uni.fmi.melodify.service.PlaylistModifySongsFacadeService;
import bg.sofia.uni.fmi.melodify.service.PlaylistCreateFacadeService;
import bg.sofia.uni.fmi.melodify.service.PlaylistService;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static bg.sofia.uni.fmi.melodify.security.RequestManager.getUserByRequest;
import static bg.sofia.uni.fmi.melodify.security.RequestManager.isAdminByRequest;

@RestController
@RequestMapping(path = "api/playlists")
@Validated
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistCreateFacadeService playlistCreateFacadeService;
    private final PlaylistModifySongsFacadeService playlistModifySongsFacadeService;
    private final TokenManagerService tokenManagerService;
    private final UserService userService;
    private final PlaylistMapper playlistMapper;

    @Autowired
    public PlaylistController(PlaylistService playlistService, PlaylistCreateFacadeService playlistCreateFacadeService,
                              PlaylistModifySongsFacadeService playlistModifySongsFacadeService,
                              TokenManagerService tokenManagerService, UserService userService,
                              PlaylistMapper playlistMapper) {
        this.playlistService = playlistService;
        this.playlistCreateFacadeService = playlistCreateFacadeService;
        this.playlistModifySongsFacadeService = playlistModifySongsFacadeService;
        this.tokenManagerService = tokenManagerService;
        this.userService = userService;
        this.playlistMapper = playlistMapper;
    }

    @GetMapping
    public List<PlaylistDto> getPlaylists(@RequestParam Map<String, String> filters){
        return this.playlistMapper.toDtoCollection(this.playlistService.getPlaylists(filters));
    }

    @GetMapping(value = "/{id}")
    public PlaylistDto getPlaylistById(@PathVariable
                                    @NotNull(message = "The provided playlist id cannot be null")
                                    @Positive(message = "The provided playlist id must be positive")
                                    Long id){
        Optional<Playlist> potentialPlaylistToReturn = this.playlistService.getPlaylistById(id);
        if(potentialPlaylistToReturn.isPresent()){
            return this.playlistMapper.toDto(potentialPlaylistToReturn.get());
        }

        throw new ResourceNotFoundException("There is no playlist with such id");
    }

    @PostMapping
    public Long addPlaylist(@NotNull(message = "The provided playlist description in the body cannot be null")
                            @RequestBody PlaylistDto playlistDto,
                            HttpServletRequest request){
        Playlist potentialPlaylistToCreate=  this.playlistCreateFacadeService
            .createPlaylistWithOwner(this.playlistMapper.toEntity(playlistDto),
                getUserByRequest(request, tokenManagerService, userService).getId());

        if (potentialPlaylistToCreate == null){
            throw new ApiBadRequest("The was a problem in creating a playlist");
        }

        return potentialPlaylistToCreate.getId();
    }

    @DeleteMapping(params = {"playlist_id"})
    public PlaylistDto deletePlaylistById(@RequestParam("playlist_id")
                                          @NotNull(message = "The provided playlist id cannot be null")
                                          @Positive(message = "The provided playlist id must be positive")
                                          Long id,
                                          HttpServletRequest request) {
        return this.playlistMapper.toDto(this.playlistService.deletePlaylist(id,
            getUserByRequest(request, tokenManagerService, userService),
            isAdminByRequest(request, tokenManagerService)));
    }

    @PutMapping(value = "/{id}")
    public boolean setPlaylistById(@PathVariable
                                   @NotNull(message = "The provided playlist id cannot be null")
                                   @Positive(message = "The provided playlist id must be positive")
                                   Long id,
                                   @RequestBody
                                   @NotNull(message = "The provided playlist dto in the body cannot be null")
                                   PlaylistDto playlistToUpdate,
                                   HttpServletRequest request){
        return this.playlistService.setPlaylistById(playlistMapper.toEntity(playlistToUpdate), id,
            getUserByRequest(request, tokenManagerService, userService),
            isAdminByRequest(request, tokenManagerService));
    }

    @PatchMapping(value = "/{id}/add")
    public boolean addSongToPlaylist(@PathVariable
                                         @NotNull(message = "The provided playlist id cannot be null")
                                         @Positive(message = "The provided playlist id must be positive")
                                         Long id,
                                     @RequestParam("song_id")
                                     @NotNull(message = "The provided song id cannot be null")
                                     @Positive(message = "The provided song id must be positive")
                                     Long songId,
                                     HttpServletRequest request) {
        return playlistModifySongsFacadeService.addSongToPlaylist(id, songId,
            getUserByRequest(request, tokenManagerService, userService),
            isAdminByRequest(request, tokenManagerService));
    }

    @PatchMapping(value = "/{id}/remove")
    public boolean removeSongFromPlaylist(@PathVariable
                                     @NotNull(message = "The provided playlist id cannot be null")
                                     @Positive(message = "The provided playlist id must be positive")
                                     Long id,
                                     @RequestParam("song_id")
                                     @NotNull(message = "The provided song id cannot be null")
                                     @Positive(message = "The provided song id must be positive")
                                     Long songId,
                                     HttpServletRequest request) {
        return playlistModifySongsFacadeService.removeSongFromPlaylist(id, songId,
            getUserByRequest(request, tokenManagerService, userService),
            isAdminByRequest(request, tokenManagerService));
    }
}