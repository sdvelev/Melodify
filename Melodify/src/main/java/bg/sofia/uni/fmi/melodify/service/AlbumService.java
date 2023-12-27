package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.dto.AlbumDto;
import bg.sofia.uni.fmi.melodify.mapper.ArtistMapper;
import bg.sofia.uni.fmi.melodify.mapper.GenreMapper;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.repository.AlbumRepository;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
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

    public List<Album> getAllAlbums(){
        return albumRepository.findAll();
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

    public boolean setAlbumById(
            @NotNull(message = "The provided album description cannot be null")
            AlbumDto albumDtoToChange,
            @NotNull(message = "The provided album id cannot be null")
            @Positive(message = "The provided album id must be positive")
            Long albumId) {
        Optional<Album> optionalAlbumToUpdate = albumRepository.findById(albumId);
        if(optionalAlbumToUpdate.isPresent()){
            Album albumToUpdate = optionalAlbumToUpdate.get();
            ArtistMapper artistMapper = ArtistMapper.INSTANCE;
            albumToUpdate.setArtists(artistMapper.toEntityCollection(albumDtoToChange.getArtistDtos()));
            GenreMapper genreMapper = GenreMapper.INSTANCE;
            albumToUpdate.setGenre(genreMapper.toEntity(albumDtoToChange.getGenre()));
            albumToUpdate.setName(albumDtoToChange.getName());
            albumToUpdate.setImage(albumDtoToChange.getImage());
//            SongMapper songMapper = SongMapper.INSTANCE;
//            albumToUpdate.setSongs(songMapper.toEntityCollection(albumDtoToChange.getSongs()));
            albumToUpdate.setUri(albumDtoToChange.getUri());
//            UserMapper userMapper = UserMapper.INSTANCE;
//            albumToUpdate.setUsers(userMapper.toEntityCollection(albumDtoToChange.getUsers()));
            albumToUpdate.setReleaseDate(albumDtoToChange.getReleaseDate());
            albumRepository.save(albumToUpdate);
            return true;
        }
        throw new ResourceNotFoundException("There is no album with such id");
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
