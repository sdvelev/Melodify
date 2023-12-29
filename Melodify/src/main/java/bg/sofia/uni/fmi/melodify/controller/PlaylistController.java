package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.PlaylistDto;
import bg.sofia.uni.fmi.melodify.mapper.PlaylistMapper;
import bg.sofia.uni.fmi.melodify.model.Playlist;
import bg.sofia.uni.fmi.melodify.service.PlaylistModifySongsFacadeService;
import bg.sofia.uni.fmi.melodify.service.PlaylistCreateFacadeService;
import bg.sofia.uni.fmi.melodify.service.PlaylistService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/playlists")
@Validated
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistCreateFacadeService playlistCreateFacadeService;
    private final PlaylistModifySongsFacadeService playlistModifySongsFacadeService;
    private final PlaylistMapper playlistMapper;

    @Autowired
    public PlaylistController(PlaylistService playlistService, PlaylistCreateFacadeService playlistCreateFacadeService,
                              PlaylistModifySongsFacadeService playlistModifySongsFacadeService,
                              PlaylistMapper playlistMapper) {
        this.playlistService = playlistService;
        this.playlistCreateFacadeService = playlistCreateFacadeService;
        this.playlistModifySongsFacadeService = playlistModifySongsFacadeService;
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
                            @RequestParam("owner_id")
                            @NotNull(message = "The provided owner id cannot be null")
                            @Positive(message = "The provided owner id must be positive")
                            Long userId){
        Playlist potentialPlaylistToCreate=  this.playlistCreateFacadeService
            .createPlaylistWithOwner(this.playlistMapper.toEntity(playlistDto), userId);

        if (potentialPlaylistToCreate == null){
            throw new ApiBadRequest("The was a problem in creating a playlist");
        }

        return potentialPlaylistToCreate.getId();
    }

    @DeleteMapping(params = {"playlist_id"})
    public PlaylistDto deletePlaylistById(@RequestParam("playlist_id")
                                          @NotNull(message = "The provided playlist id cannot be null")
                                          @Positive(message = "The provided playlist id must be positive")
                                          Long id) {
        return this.playlistMapper.toDto(this.playlistService.deletePlaylist(id));
    }

    @PutMapping(value = "/{id}")
    public boolean setPlaylistById(@PathVariable
                                   @NotNull(message = "The provided playlist id cannot be null")
                                   @Positive(message = "The provided playlist id must be positive")
                                   Long id,
                                   @RequestBody
                                   @NotNull(message = "The provided playlist dto in the body cannot be null")
                                   PlaylistDto playlistToUpdate){
        return this.playlistService.setPlaylistById(playlistMapper.toEntity(playlistToUpdate), id);
    }

    @PatchMapping(value = "/{id}/add")
    public boolean addSongToPlaylist(@PathVariable
                                         @NotNull(message = "The provided playlist id cannot be null")
                                         @Positive(message = "The provided playlist id must be positive")
                                         Long id,
                                     @RequestParam("song_id")
                                     @NotNull(message = "The provided song id cannot be null")
                                     @Positive(message = "The provided song id must be positive")
                                     Long songId) {
        return playlistModifySongsFacadeService.addSongToPlaylist(id, songId);
    }

    @PatchMapping(value = "/{id}/remove")
    public boolean removeSongFromPlaylist(@PathVariable
                                     @NotNull(message = "The provided playlist id cannot be null")
                                     @Positive(message = "The provided playlist id must be positive")
                                     Long id,
                                     @RequestParam("song_id")
                                     @NotNull(message = "The provided song id cannot be null")
                                     @Positive(message = "The provided song id must be positive")
                                     Long songId) {
        return playlistModifySongsFacadeService.removeSongFromPlaylist(id, songId);
    }
}