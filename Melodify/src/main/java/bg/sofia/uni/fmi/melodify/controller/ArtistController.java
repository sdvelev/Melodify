package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.ArtistDto;
import bg.sofia.uni.fmi.melodify.mapper.ArtistMapper;
import bg.sofia.uni.fmi.melodify.model.Artist;
import bg.sofia.uni.fmi.melodify.service.ArtistService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/genres")
@Validated
public class ArtistController {
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    @Autowired
    public ArtistController(ArtistService artistService, ArtistMapper artistMapper) {
        this.artistService = artistService;
        this.artistMapper = artistMapper;
    }

    @GetMapping
    public List<ArtistDto> getAllArtists() {
        return this.artistMapper.toDtoCollection(this.artistService.getAllArtists());
    }

    @GetMapping(value = "/{id}")
    public ArtistDto getArtistById(@PathVariable
                                   @NotNull(message = "The provided artist id cannot be null")
                                   @Positive(message = "The provided artist id must be positive")
                                   Long id) {
        Optional<Artist> potentialArtistToReturn = this.artistService.getArtistById(id);

        if (potentialArtistToReturn.isPresent()) {
            return this.artistMapper.toDto(potentialArtistToReturn.get());
        }

        throw new ResourceNotFoundException("There is no artist with such an id");
    }

    @PostMapping
    public Long addArtist(@NotNull(message = "The provided artist description in the body cannot be null")
                          @RequestBody ArtistDto artistDto) {
        Artist potentialArtistToCreate = artistService.createArtist(this.artistMapper.toEntity(artistDto));

        if (potentialArtistToCreate == null) {
            throw new ApiBadRequest("There was a problem in creating an author");
        }

        return potentialArtistToCreate.getId();
    }

    @DeleteMapping(params ={"artist_id"})
    public ArtistDto deleteArtistById(@RequestParam("artist_id")
                                      @NotNull(message = "The provided artist id cannot be null")
                                      @Positive(message = "THe provided artist id must be positive")
                                      Long id) {
        return this.artistMapper.toDto(this.artistService.deleteArtist(id));
    }

    @PutMapping(value = "/{id}")
    public boolean setArtistById(@PathVariable
                                 @NotNull(message = "The provided artist id cannot be null")
                                 @Positive(message = "The provided artist id must be positive")
                                 Long id,
                                 @RequestBody
                                 @NotNull(message = "The provided artist dto in the body cannot be null")
                                 ArtistDto artistToUpdate) {
        return this.artistService.setArtistById(artistToUpdate, id);
    }
}
