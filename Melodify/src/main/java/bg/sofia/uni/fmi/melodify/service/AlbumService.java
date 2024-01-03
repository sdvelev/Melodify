package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.mapper.ArtistMapper;
import bg.sofia.uni.fmi.melodify.mapper.GenreMapper;
import bg.sofia.uni.fmi.melodify.mapper.SongMapper;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.repository.AlbumRepository;
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
public class AlbumService {
    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository){
        this.albumRepository = albumRepository;
    }

    public Album createAlbum(@NotNull(message = "The provided album cannot be null") Album albumToSave) {
        return albumRepository.save(albumToSave);
    }

    public List<Album> getAlbums(Map<String, String> filters){
        String name = filters.get("name");
        String releaseDate = filters.get("releaseDate");
        String image = filters.get("image");
        String uri = filters.get("uri");

        Specification<Album> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (releaseDate != null && !releaseDate.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("releaseDate")), "%" + releaseDate.toLowerCase() + "%"));
        }

        if (image != null && !image.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("image")), "%" + image.toLowerCase() + "%"));
        }

        if (uri != null && !uri.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("uri")), "%" + uri.toLowerCase() + "%"));
        }

        return albumRepository.findAll(spec);
    }

    public Optional<Album> getAlbumById(
            @NotNull(message = "The provided id cannot be null")
            @Positive(message = "The provided id must be positive")
            Long id) {
        Optional<Album> potentialAlbum = albumRepository.findById(id);

        if(potentialAlbum.isPresent()) {
            return potentialAlbum;
        }

        throw new ResourceNotFoundException("There is no album with such id");
    }

//    public boolean setAlbumById(
//            @NotNull(message = "The provided album description cannot be null")
//            AlbumDto albumDtoToChange,
//            @NotNull(message = "The provided album id cannot be null")
//            @Positive(message = "The provided album id must be positive")
//            Long albumId) {
//        Optional<Album> optionalAlbumToUpdate = albumRepository.findById(albumId);
//
//        if(optionalAlbumToUpdate.isPresent()){
//            Album albumToUpdate = optionalAlbumToUpdate.get();
//            albumToUpdate.setName(albumDtoToChange.getName());
//            albumToUpdate.setReleaseDate(albumDtoToChange.getReleaseDate());
////            albumToUpdate.setGenre(genreMapper.toEntity(albumDtoToChange.getGenreDto()));
//            albumToUpdate.setImage(albumDtoToChange.getImage());
////            albumToUpdate.setSongs(songMapper.toEntityCollection(albumDtoToChange.getSongDtos()));
////            albumToUpdate.setArtists(artistMapper.toEntityCollection(albumDtoToChange.getArtistDtos()));
//            albumToUpdate.setUri(albumDtoToChange.getUri());
//
//            albumRepository.save(albumToUpdate);
//            return true;
//        }
//        throw new ResourceNotFoundException("There is no album with such id");
//    }

    public boolean setAlbumById(
        @NotNull(message = "The provided album dto cannot be null")
        Album albumFieldsToChange,
        @NotNull(message = "The provided album id cannot be null")
        @Positive(message = "The provided album id must be positive")
        Long albumId) {

        Optional<Album> optionalAlbumToUpdate = albumRepository.findById(albumId);
        if (optionalAlbumToUpdate.isPresent()) {
            Album albumToUpdate = setAlbumNonNullFields(albumFieldsToChange, optionalAlbumToUpdate.get());
            albumRepository.save(albumToUpdate);
            return true;
        }

        throw new ResourceNotFoundException("There is not an album with such an id");
    }

    private Album setAlbumNonNullFields(
        @NotNull(message = "The provided album dto cannot be null")
        Album albumFieldsToChange,
        @NotNull(message = "The provided album cannot be null")
        Album albumToUpdate) {

        if (albumFieldsToChange.getName() != null) {
            albumToUpdate.setName(albumFieldsToChange.getName());
        }

        if (albumFieldsToChange.getReleaseDate() != null) {
            albumToUpdate.setReleaseDate(albumFieldsToChange.getReleaseDate());
        }

        if (albumFieldsToChange.getImage() != null) {
            albumToUpdate.setImage(albumFieldsToChange.getImage());
        }

        if (albumFieldsToChange.getGenre() != null) {
            albumToUpdate.setGenre(albumFieldsToChange.getGenre());
        }

        if (albumFieldsToChange.getArtists() != null) {
            albumToUpdate.setArtists(albumFieldsToChange.getArtists());
        }

        if (albumFieldsToChange.getUri() != null) {
            albumToUpdate.setUri(albumFieldsToChange.getUri());
        }

        return albumToUpdate;
    }


    public Album deleteAlbum(
            @NotNull(message = "The provided album id cannot be null")
            @Positive(message = "The provided album id must be positive")
            Long id) {
        Optional<Album> optionalAlbumToDelete = albumRepository.findById(id);

        if (optionalAlbumToDelete.isPresent()) {
            Album albumToDelete = optionalAlbumToDelete.get();
            albumRepository.delete(albumToDelete);
            return albumToDelete;
        }

        throw new ResourceNotFoundException("There is not a album with such an id");
    }
}