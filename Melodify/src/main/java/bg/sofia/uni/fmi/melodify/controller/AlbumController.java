package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.AlbumDto;
import bg.sofia.uni.fmi.melodify.mapper.AlbumMapper;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.service.AlbumService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/albums")
@Validated
public class AlbumController {
    private final AlbumService albumService;
    private final AlbumMapper albumMapper;

    public AlbumController(AlbumService albumService, AlbumMapper albumMapper) {
        this.albumService = albumService;
        this.albumMapper = albumMapper;
    }

    @GetMapping
    public List<AlbumDto> getAllAlbums() {
        return albumMapper.toDtoCollection(albumService.getAllAlbums());
    }

    @GetMapping(value = "/{id}")
    public AlbumDto getAlbumById(@PathVariable
                                 @NotNull(message = "The provided genre id cannot be null")
                                 @Positive(message = "The provided album id must be positive")
                                 Long id) {
        Optional<Album> potentialAlbumToReturn = albumService.getAlbumById(id);
        if (potentialAlbumToReturn.isPresent()) {
            return albumMapper.toDto(potentialAlbumToReturn.get());
        }

        throw new ResourceNotFoundException("The is no such album with the provided id");
    }

    @PostMapping
    public Long addAlbum(@NotNull(message = "The provided album description in the body cannot be null")
                         @RequestBody
                         AlbumDto albumDto) {
        Album potentialAlbumToCreate = albumService.createAlbum(albumMapper.toEntity(albumDto));

        if (potentialAlbumToCreate == null) {
            throw new ApiBadRequest("There was a problem in creating the album");
        }

        return potentialAlbumToCreate.getId();
    }

    @DeleteMapping(params = {"album_id"})
    public AlbumDto deleteAlbumById(@RequestParam("album_id")
                                    @NotNull(message = "The provided album id cannot be null")
                                    @Positive(message = "The provided album id must be positive")
                                    Long albumId) {
        return albumMapper.toDto(albumService.deleteAlbum(albumId));
    }

    @PutMapping(value = "/{id}")
    public boolean setAlbumById(@PathVariable
                                @NotNull(message = "The provided album id cannot be null")
                                @Positive(message = "The provided album id must be positive")
                                Long id,
                                @RequestBody
                                @NotNull(message = "The provided album dto in the body cannot be null")
                                AlbumDto albumToUpdate) {
        return albumService.setAlbumById(albumToUpdate, id);
    }
}
