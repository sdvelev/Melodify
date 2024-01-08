package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.dto.PlaylistDto;
import bg.sofia.uni.fmi.melodify.mapper.SongMapper;
import bg.sofia.uni.fmi.melodify.mapper.UserMapper;
import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Playlist;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.repository.PlaylistRepository;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Validated
public class PlaylistService {
    private final PlaylistRepository playlistRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository){
        this.playlistRepository = playlistRepository;
    }

    public Playlist createPlaylist(@NotNull(message = "The provided playlist cannot be null") Playlist playlistToSave){
        playlistToSave.setCreationDate(LocalDateTime.now());
        Playlist savedPlaylist = this.playlistRepository.save(playlistToSave);
        savedPlaylist.setUri("/frontend/user/playlist.html?id=" + savedPlaylist.getId());
        return this.playlistRepository.save(savedPlaylist);
    }
    public List<Playlist> getPlaylists(Map<String, String> filters){
        String name = filters.get("name");
        // owner
        String creationDate = filters.get("creationDate");
        String image = filters.get("image");
        // songs
        String uri = filters.get("uri");

        Specification<Playlist> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (creationDate != null && !creationDate.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("creationDate")), "%" + creationDate.toLowerCase() + "%"));
        }

        if (image != null && !image.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("image")), "%" + image.toLowerCase() + "%"));
        }

        if (uri != null && !uri.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("uri")), "%" + uri.toLowerCase() + "%"));
        }

        return  this.playlistRepository.findAll(spec);
    }

    public Optional<Playlist> getPlaylistById(
            @NotNull(message = "The provided playlist id cannot be null")
            @Positive(message = "The provided playlist id must be positive")
            Long id){
        Optional<Playlist> potentialPlaylist = this.playlistRepository.findById(id);

        if(potentialPlaylist.isPresent()){
            return potentialPlaylist;
        }

        throw new ResourceNotFoundException("There is no playlist with such id");
    }

//    public boolean setPlaylistById(
//            @NotNull(message = "The provided playlist description cannot be null")
//            PlaylistDto playlistDtoToChange,
//            @NotNull(message = "The provided playlist id cannot be null")
//            @Positive(message = "The provided playlist id must be positive")
//            Long id){
//        Optional<Playlist> optionalPlaylistToUpdate = this.playlistRepository.findById(id);
//
//        if(optionalPlaylistToUpdate.isPresent()){
//            Playlist playlistToUpdate = optionalPlaylistToUpdate.get();
//            playlistToUpdate.setName(playlistDtoToChange.getName());
////            playlistToUpdate.setOwner(userMapper.toEntity(playlistDtoToChange.getOwnerDto()));
//            playlistToUpdate.setCreationDate(playlistDtoToChange.getCreationDate());
//            playlistToUpdate.setImage(playlistDtoToChange.getImage());
////            playlistToUpdate.setSongs(songMapper.toEntityCollection(playlistDtoToChange.getSongDtos()));
//            playlistToUpdate.setUri(playlistDtoToChange.getUri());
//
//            this.playlistRepository.save(playlistToUpdate);
//            return true;
//        }
//        throw new ResourceNotFoundException("There is no a playlist with such id");
//    }

    public boolean setPlaylistById(
        @NotNull(message = "The provided playlist dto cannot be null")
        Playlist playlistFieldsToChange,
        @NotNull(message = "The provided playlist id cannot be null")
        @Positive(message = "The provided playlist id must be positive")
        Long playlistId,
        User userToSet,
        boolean isAdmin) {

        Optional<Playlist> optionalPlaylistToUpdate = playlistRepository.findById(playlistId);
        if (optionalPlaylistToUpdate.isPresent()) {
            if (!optionalPlaylistToUpdate.get().getOwner().getId().equals(userToSet.getId()) && !isAdmin) {
                throw new MethodNotAllowed("There is a problem in authorization");
            }

            Playlist playlistToUpdate = setPlaylistNonNullFields(playlistFieldsToChange, optionalPlaylistToUpdate.get());
            playlistRepository.save(playlistToUpdate);
            return true;
        }

        throw new ResourceNotFoundException("There is not a playlist with such an id");
    }

    private Playlist setPlaylistNonNullFields(
        @NotNull(message = "The provided playlist dto cannot be null")
        Playlist playlistFieldsToChange,
        @NotNull(message = "The provided playlist cannot be null")
        Playlist playlistToUpdate) {

        if (playlistFieldsToChange.getName() != null) {
            playlistToUpdate.setName(playlistFieldsToChange.getName());
        }

        if (playlistFieldsToChange.getImage() != null) {
            playlistToUpdate.setImage(playlistFieldsToChange.getImage());
        }

        if (playlistFieldsToChange.getUri() != null) {
            playlistToUpdate.setUri(playlistFieldsToChange.getUri());
        }

        return playlistToUpdate;
    }

    public Playlist deletePlaylist(
            @NotNull(message = "The provided playlist id cannot be null")
            @Positive(message = "The provided playlist id must be positive")
            Long id,
            User userToDelete,
            boolean isAdmin){

        Optional<Playlist> optionalPlaylistToDelete = this.playlistRepository.findById(id);

        if (optionalPlaylistToDelete.isPresent()){
            Playlist playlistToDelete = optionalPlaylistToDelete.get();

            if (!playlistToDelete.getOwner().getId().equals(userToDelete.getId()) && !isAdmin) {
                throw new MethodNotAllowed("There is a problem in authorization");
            }

            this.playlistRepository.delete(playlistToDelete);
            return playlistToDelete;
        }

        throw new ResourceNotFoundException("There is no playlist with such id");
    }
}
