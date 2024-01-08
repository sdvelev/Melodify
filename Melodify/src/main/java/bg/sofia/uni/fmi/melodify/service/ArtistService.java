package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.dto.ArtistDto;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Artist;
import bg.sofia.uni.fmi.melodify.repository.ArtistRepository;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Validated
public class ArtistService {
    private final ArtistRepository artistRepository;
    @Autowired
    public ArtistService(ArtistRepository artistRepository){
        this.artistRepository = artistRepository;
    }
    public List<Artist> getArtists(Map<String, String> filters) {
        String name = filters.get("name");
        String image = filters.get("image");
        String uri = filters.get("uri");

        Specification<Artist> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (image != null && !image.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("image")), "%" + image.toLowerCase() + "%"));
        }

        if (uri != null && !uri.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("uri")), "%" + uri.toLowerCase() + "%"));
        }

        return artistRepository.findAll(spec);
    }

    public Optional<Artist> getArtistById(
            @NotNull(message = "The provided id cannot be null")
            @Positive(message = "The provided id must be valid")
            Long id){
        Optional<Artist> potentialArtist = artistRepository.findById(id);

        if(potentialArtist.isPresent()){
            return potentialArtist;
        }

        throw new ResourceNotFoundException("There is no artist with such id");
    }

    public List<Album> getArtistAlbumsById(
        @NotNull(message = "The provided id cannot be null")
        @Positive(message = "The provided id must be valid")
        Long id){
        Optional<Artist> potentialArtist = artistRepository.findById(id);

        if (potentialArtist.isPresent()){
            return potentialArtist.get().getAlbums();
        }

        throw new ResourceNotFoundException("There is no artist with such id");
    }

    public Artist createArtist(@NotNull(message = "The provided artist cannot be null") Artist artistToSave){
        return artistRepository.save(artistToSave);
    }

  /*  public boolean setArtistById(
            @NotNull(message = "The provided artist description cannot be null")
            ArtistDto artistDto,
            @NotNull(message = "The provided artist id cannot be null")
            @Positive(message = "The provided artist id must be positive")
            Long artistId){
        Optional<Artist> optionalArtistToUpdate = artistRepository.findById(artistId);

        if (optionalArtistToUpdate.isPresent()){
            Artist artistToUpdate = optionalArtistToUpdate.get();
            artistToUpdate.setName(artistDto.getName());
            artistToUpdate.setImage(artistDto.getImage());
            artistToUpdate.setUri(artistDto.getUri());
//            if (artistDto.getAlbumDtos() != null && !artistDto.getAlbumDtos().isEmpty()) {
//                artistToUpdate.getAlbums().clear(); // Remove existing albums
//                AlbumMapper albumMapper = AlbumMapper.INSTANCE;
//                List<Album> albums =  albumMapper.toEntityCollection(artistDto.getAlbumDtos());
//                artistToUpdate.getAlbums().addAll(albums);
//            }
//            if (artistDto.getSongDtos() != null && !artistDto.getSongDtos().isEmpty()) {
//                artistToUpdate.getSongs().clear();
//                SongMapper songMapper = SongMapper.INSTANCE;
//                List<Song> songs = songMapper.toEntityCollection(artistDto.getSongDtos());
//                artistToUpdate.getSongs().addAll(songs);
//            }
        }

        throw new ResourceNotFoundException("There is no artist with such id");
    }*/

    public boolean setArtistById(
        @NotNull(message = "The provided artist dto cannot be null")
        ArtistDto artistFieldsToChange,
        @NotNull(message = "The provided artist id cannot be null")
        @Positive(message = "The provided artist id must be positive")
        Long artistId) {

        Optional<Artist> optionalArtistToUpdate = artistRepository.findById(artistId);
        if (optionalArtistToUpdate.isPresent()) {
            Artist artistToUpdate = setArtistNonNullFields(artistFieldsToChange, optionalArtistToUpdate.get());
            artistRepository.save(artistToUpdate);
            return true;
        }

        throw new ResourceNotFoundException("There is not an artist with such an id");
    }

    private Artist setArtistNonNullFields(
        @NotNull(message = "The provided artist dto cannot be null")
        ArtistDto artistFieldsToChange,
        @NotNull(message = "The provided artist cannot be null")
        Artist artistToUpdate) {

        if (artistFieldsToChange.getName() != null) {
            artistToUpdate.setName(artistFieldsToChange.getName());
        }

        if (artistFieldsToChange.getImage() != null) {
            artistToUpdate.setImage(artistFieldsToChange.getImage());
        }

        return artistToUpdate;
    }

    public Artist deleteArtist(
            @NotNull(message = "The provided artist id cannot be null")
            @Positive(message = "The provided artist id must be positive")
            Long id){
        Optional<Artist> optionalArtistToDelete = artistRepository.findById(id);

        if (optionalArtistToDelete.isPresent()){
            Artist artistToDelete = optionalArtistToDelete.get();
            artistRepository.delete(artistToDelete);
            return artistToDelete;
        }

        throw new ResourceNotFoundException("There is no artist with such id");
    }
}
