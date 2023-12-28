package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.dto.ArtistDto;
import bg.sofia.uni.fmi.melodify.model.Artist;
import bg.sofia.uni.fmi.melodify.repository.ArtistRepository;
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
public class ArtistService {
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository){
        this.artistRepository = artistRepository;
    }

    public Artist createArtist(@NotNull(message = "The provided artist cannot be null") Artist artistToSave){
        return artistRepository.save(artistToSave);
    }

    public List<Artist> getAllArtists(){
        return this.artistRepository.findAll();
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

    public boolean setArtistById(
            @NotNull(message = "The provided artist description cannot be null")
            ArtistDto artistToChange,
            @NotNull(message = "The provided artist id cannot be null")
            @Positive(message = "The provided artist id must be positive")
            Long artistId){
        Optional<Artist> optionalArtistToUpdate = artistRepository.findById(artistId);

        if(optionalArtistToUpdate.isPresent()){
            Artist artistToUpdate = optionalArtistToUpdate.get();
            artistToUpdate.setName(artistToChange.getName());
            artistToUpdate.setImage(artistToChange.getImage());
            artistToUpdate.setUri(artistToChange.getUri());
//            SongMapper songMapper = SongMapper.INSTANCE;
//            artistToUpdate.setSongs(songMapper.toEntityCollection(artistToChange.getSongs()));
//            AlbumMapper albumMapper = AlbumMapper.INSTANCE;
//            artistToUpdate.setAlbums(albumMapper.toEntityCollection(artistToChange.getAlbums()));
        }

        throw new ResourceNotFoundException("There is no artist with such id");
    }

    public Artist deleteArtist(
            @NotNull(message = "The provided artist id cannot be null")
            @Positive(message = "The provided artist id must be positive")
            Long id){
        Optional<Artist> optionalArtistToDelete = artistRepository.findById(id);

        if(optionalArtistToDelete.isPresent()){
            Artist artistToDelete = optionalArtistToDelete.get();
            artistRepository.delete(artistToDelete);
            return artistToDelete;
        }

        throw new ResourceNotFoundException("There is no artist with such id");
    }
}
