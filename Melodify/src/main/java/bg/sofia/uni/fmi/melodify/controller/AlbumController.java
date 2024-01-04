package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.AlbumDto;
import bg.sofia.uni.fmi.melodify.mapper.AlbumMapper;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.service.AlbumCreateFacadeService;
import bg.sofia.uni.fmi.melodify.service.AlbumDeleteFacadeService;
import bg.sofia.uni.fmi.melodify.service.AlbumService;
import bg.sofia.uni.fmi.melodify.service.AlbumSetFacadeService;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
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

import static bg.sofia.uni.fmi.melodify.security.RequestManager.isAdminByRequest;

@RestController
@RequestMapping(path = "api/albums")
@Validated
public class AlbumController {
    private final AlbumService albumService;
    private final AlbumCreateFacadeService albumCreateFacadeService;
    private final AlbumSetFacadeService albumSetFacadeService;
    private final AlbumDeleteFacadeService albumDeleteFacadeService;
    private final TokenManagerService tokenManagerService;
    private final AlbumMapper albumMapper;

    @Autowired
    public AlbumController(AlbumService albumService, AlbumCreateFacadeService albumCreateFacadeService,
                           AlbumSetFacadeService albumSetFacadeService,
                           AlbumDeleteFacadeService albumDeleteFacadeService,
                           TokenManagerService tokenManagerService, AlbumMapper albumMapper) {
        this.albumService = albumService;
        this.albumCreateFacadeService = albumCreateFacadeService;
        this.albumSetFacadeService = albumSetFacadeService;
        this.albumDeleteFacadeService = albumDeleteFacadeService;
        this.tokenManagerService = tokenManagerService;
        this.albumMapper = albumMapper;
    }

    @GetMapping
    public List<AlbumDto> getAlbums(@RequestParam Map<String, String> filters) {
        return albumMapper.toDtoCollection(albumService.getAlbums(filters));
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
                         AlbumDto albumDto,
                         @RequestParam("genre_id")
                         @NotNull(message = "The provided genre id cannot be null")
                         @Positive(message = "The provided genre id must be positive")
                         Long genreId,
                         @RequestParam("artist_ids")
                         @NotNull(message = "The provided artist ids cannot be null")
                         List<Long> artistIdsList,
                         HttpServletRequest request) {
        if (!isAdminByRequest(request, tokenManagerService)) {
            throw new MethodNotAllowed("There was a problem in authorization");
        }

        Album potentialAlbumToCreate = albumCreateFacadeService
            .createAlbumWithGenreAndArtists(albumMapper.toEntity(albumDto), genreId, artistIdsList);

        if (potentialAlbumToCreate == null) {
            throw new ApiBadRequest("There was a problem in creating the album");
        }

        return potentialAlbumToCreate.getId();
    }

    @DeleteMapping(params = {"album_id"})
    public AlbumDto deleteAlbumById(@RequestParam("album_id")
                                    @NotNull(message = "The provided album id cannot be null")
                                    @Positive(message = "The provided album id must be positive")
                                    Long albumId,
                                    HttpServletRequest request) {
        if (!isAdminByRequest(request, tokenManagerService)) {
            throw new MethodNotAllowed("There was a problem in authorization");
        }

        return albumMapper.toDto(albumDeleteFacadeService.deleteAlbumWithSongs(albumId));
    }

    @PutMapping(value = "/{id}")
    public boolean setAlbumById(@PathVariable
                                @NotNull(message = "The provided album id cannot be null")
                                @Positive(message = "The provided album id must be positive")
                                Long id,
                                @RequestBody
                                @NotNull(message = "The provided album dto in the body cannot be null")
                                AlbumDto albumToUpdate,
                                @RequestParam(name = "genre_id", required = false)
                                Long genreId,
                                @RequestParam(name = "artist_ids", required = false)
                                List<Long> artistIdsList,
                                HttpServletRequest request) {
        if (!isAdminByRequest(request, tokenManagerService)) {
            throw new MethodNotAllowed("There was a problem in authorization");
        }

        return albumSetFacadeService.setAlbumWithGenreAndArtistsIfProvided(id, albumMapper.toEntity(albumToUpdate),
            genreId, artistIdsList);
    }
}