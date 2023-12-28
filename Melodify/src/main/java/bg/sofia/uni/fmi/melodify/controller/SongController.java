package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.SongDto;
import bg.sofia.uni.fmi.melodify.mapper.SongMapper;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.service.SongService;
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
@RequestMapping(path = "api/songs")
@Validated
public class SongController {
    private final SongService songService;
    private final SongMapper songMapper;
    @Autowired
    public SongController(SongService songService, SongMapper songMapper){
        this.songService = songService;
        this.songMapper = songMapper;
    }

    @GetMapping
    public List<SongDto> getAllArtists(@RequestParam Map<String, String> filters){
        return this.songMapper.toDtoCollection(this.songService.getSongs(filters));
    }

    @GetMapping(value = "/{id}")
    public SongDto getSongById(@PathVariable
                               @NotNull(message = "The provided song id cannot be null")
                               @Positive(message = "The provided song id must be positive")
                               Long id) {
        Optional<Song> potentialSongToReturn = this.songService.getSongById(id);

        if(potentialSongToReturn.isPresent()){
            return this.songMapper.toDto(potentialSongToReturn.get());
        }

        throw new ResourceNotFoundException("There is no song with such an id");
    }

    @PostMapping
    public Long addSong(@NotNull(message = "The provided song description in the body cannot be null")
                          @RequestBody SongDto songDto) {
        Song potentialSongToCreate = songService.createSong(this.songMapper.toEntity(songDto));

        if (potentialSongToCreate == null) {
            throw new ApiBadRequest("There was a problem in creating a song");
        }

        return potentialSongToCreate.getId();
    }

    @PutMapping(value = "/{id}")
    public boolean setSongById(@PathVariable
                                 @NotNull(message = "The provided song id cannot be null")
                                 @Positive(message = "The provided song id must be positive")
                                 Long id,
                               @RequestBody
                                 @NotNull(message = "The provided song dto in the body cannot be null")
                                 SongDto songDto) {
        return this.songService.setSongById(songDto, id);
    }

    @DeleteMapping(params ={"songId"})
    public SongDto deleteSongById(@RequestParam("songId")
                                      @NotNull(message = "The provided song id cannot be null")
                                      @Positive(message = "THe provided song id must be positive")
                                      Long id) {
        return this.songMapper.toDto(this.songService.deleteSong(id));
    }
}
