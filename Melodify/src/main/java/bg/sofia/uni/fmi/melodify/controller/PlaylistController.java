package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.PlaylistDto;
import bg.sofia.uni.fmi.melodify.mapper.PlaylistMapper;
import bg.sofia.uni.fmi.melodify.model.Playlist;
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
@RequestMapping(path = "api/playlist")
@Validated
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistMapper playlistMapper;

    @Autowired
    public PlaylistController(PlaylistService playlistService, PlaylistMapper playlistMapper){
        this.playlistService = playlistService;
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
                            @RequestBody PlaylistDto playlistDto){
        Playlist potentialPlaylistToCreate=  this.playlistService.createPlaylist(this.playlistMapper.toEntity(playlistDto));

        if(potentialPlaylistToCreate == null){
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
        return this.playlistService.SetPlaylistById(playlistToUpdate, id);
    }
}
