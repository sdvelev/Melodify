package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.SongDto;
import bg.sofia.uni.fmi.melodify.mapper.SongMapper;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.service.SongCreateFacadeService;
import bg.sofia.uni.fmi.melodify.service.SongService;
import bg.sofia.uni.fmi.melodify.service.SongSetFacadeService;
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
    private final SongCreateFacadeService songCreateFacadeService;
    private final SongSetFacadeService songSetFacadeService;
    private final SongMapper songMapper;

    @Autowired
    public SongController(SongService songService, SongCreateFacadeService songCreateFacadeService,
                          SongSetFacadeService songSetFacadeService, SongMapper songMapper) {
        this.songService = songService;
        this.songCreateFacadeService = songCreateFacadeService;
        this.songSetFacadeService = songSetFacadeService;
        this.songMapper = songMapper;
    }

    @GetMapping
    public List<SongDto> getAllSongs(@RequestParam Map<String, String> filters){
        return this.songMapper.toDtoCollection(this.songService.getSongs(filters));
    }

    @GetMapping(value = "/{id}")
    public SongDto getSongById(@PathVariable
                               @NotNull(message = "The provided song id cannot be null")
                               @Positive(message = "The provided song id must be positive")
                               Long id) {
        Optional<Song> potentialSongToReturn = this.songService.getSongById(id);

        if (potentialSongToReturn.isPresent()){
            return this.songMapper.toDto(potentialSongToReturn.get());
        }

        throw new ResourceNotFoundException("There is no song with such an id");
    }

    @PostMapping
    public Long addSong(@NotNull(message = "The provided song description in the body cannot be null")
                          @RequestBody
                            SongDto songDto,
                        @RequestParam("genre_id")
                        @NotNull(message = "The provided genre id cannot be null")
                        @Positive(message = "The provided genre id must be positive")
                        Long genreId,
                        @RequestParam("album_id")
                        @NotNull(message = "The provided album id cannot be null")
                        @Positive(message = "The provided album id must be positive")
                        Long albumId,
                        @RequestParam("artist_ids")
                        @NotNull(message = "The provided artist ids cannot be null")
                        List<Long> artistIdsList) {
        Song potentialSongToCreate = songCreateFacadeService
            .createSongWithGenreAndArtistsAndAlbum(this.songMapper.toEntity(songDto), genreId, albumId, artistIdsList);

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
                                 SongDto songDto,
                               @RequestParam(name = "genre_id", required = false)
                                   Long genreId,
                               @RequestParam(name = "album_id", required = false)
                                   Long albumId,
                               @RequestParam(name = "artist_ids", required = false)
                                   List<Long> artistIdsList) {
        return this.songSetFacadeService
            .setSongWithGenreAndArtistsAndAlbumIfProvided(id, songMapper.toEntity(songDto), genreId, albumId, artistIdsList);
    }

    @DeleteMapping(params ={"song_id"})
    public SongDto deleteSongById(@RequestParam("song_id")
                                      @NotNull(message = "The provided song id cannot be null")
                                      @Positive(message = "THe provided song id must be positive")
                                      Long id) {
        return this.songMapper.toDto(this.songService.deleteSong(id));
    }
}