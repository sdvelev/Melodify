package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.repository.SongRepository;
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
public class SongService {
    private final SongRepository songRepository;

    @Autowired
    public SongService(SongRepository songRepository){
        this.songRepository = songRepository;
    }

    public List<Song> getSongs(Map<String, String> filters) {
        String name = filters.get("name");
        String duration = filters.get("duration");
        String numberOfPlays = filters.get("numberOfPlays");
        String uri = filters.get("uri");

        Specification<Song> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (duration != null && !duration.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("duration")), "%" + duration.toLowerCase() + "%"));
        }

        if (numberOfPlays != null && !numberOfPlays.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("numberOfPlays")), "%" + numberOfPlays.toLowerCase() + "%"));
        }

        if (uri != null && !uri.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("uri")), "%" + uri.toLowerCase() + "%"));
        }

        return songRepository.findAll(spec);
    }

    public Optional<Song> getSongById(
            @NotNull(message = "The provided id cannot be null")
            @Positive(message = "The provided id must be valid")
            Long id){
        Optional<Song> potentialSong = songRepository.findById(id);

        if(potentialSong.isPresent()){
            return potentialSong;
        }

        throw new ResourceNotFoundException("There is no song with such id");
    }
    public Song createSong(@NotNull(message = "The provided song cannot be null") Song songToSave){
        return songRepository.save(songToSave);
    }

    public boolean setSongById(
        @NotNull(message = "The provided song dto cannot be null")
        Song songFieldsToChange,
        @NotNull(message = "The provided song id cannot be null")
        @Positive(message = "The provided song id must be positive")
        Long songId) {

        Optional<Song> optionalSongToUpdate = songRepository.findById(songId);
        if (optionalSongToUpdate.isPresent()) {
            Song songToUpdate = setSongNonNullFields(songFieldsToChange, optionalSongToUpdate.get());
            songRepository.save(songToUpdate);
            return true;
        }

        throw new ResourceNotFoundException("There is not a song with such an id");
    }

    private Song setSongNonNullFields(
        @NotNull(message = "The provided song dto cannot be null")
        Song songFieldsToChange,
        @NotNull(message = "The provided song cannot be null")
        Song songToUpdate) {

        if (songFieldsToChange.getName() != null) {
            songToUpdate.setName(songFieldsToChange.getName());
        }

        if (songFieldsToChange.getDuration() != null) {
            songToUpdate.setDuration(songFieldsToChange.getDuration());
        }

        if (songFieldsToChange.getGenre() != null) {
            songToUpdate.setGenre(songFieldsToChange.getGenre());
        }

        if (songFieldsToChange.getArtists() != null) {
            songToUpdate.setArtists(songFieldsToChange.getArtists());
        }

        if (songFieldsToChange.getAlbum() != null) {
            songToUpdate.setAlbum(songFieldsToChange.getAlbum());
        }

        if (songFieldsToChange.getUri() != null) {
            songToUpdate.setUri(songFieldsToChange.getUri());
        }

        return songToUpdate;
    }

    public Song deleteSong(
            @NotNull(message = "The provided song id cannot be null")
            @Positive(message = "The provided song id must be positive")
            Long id){
        Optional<Song> optionalSongToDelete = songRepository.findById(id);

        if (optionalSongToDelete.isPresent()){
            Song songToDelete = optionalSongToDelete.get();
            songRepository.delete(songToDelete);
            return songToDelete;
        }

        throw new ResourceNotFoundException("There is no song with such id");
    }
}