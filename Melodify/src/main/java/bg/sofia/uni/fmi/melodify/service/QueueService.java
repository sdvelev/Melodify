package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.repository.QueueRepository;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Validated
public class QueueService {
    private final QueueRepository queueRepository;

    public QueueService(QueueRepository queueRepository){
        this.queueRepository = queueRepository;
    }
    public List<Queue> getQueues(Map<String, String> filters, User userToGet, boolean isAdmin) {
        if (!isAdmin) {
            Optional<Queue> potentialQueueToReturn = this.getQueueById(userToGet.getQueue().getId());
            if (potentialQueueToReturn.isPresent()) {
                return List.of(potentialQueueToReturn.get());
            } else {
                throw new MethodNotAllowed("There is a problem in authorization");
            }
        }

        return this.queueRepository.findAll();
    }

    public Optional<Queue> getQueueById(@NotNull(message = "The provided id cannot be null")
                                        @Positive(message = "The provided id must be positive")
                                        Long id) {
        Optional<Queue> potentialQueueToReturn = this.queueRepository.findById(id);

        if (potentialQueueToReturn.isPresent()){
            return potentialQueueToReturn;
        }

        throw new ResourceNotFoundException("The is no such queue with the provided id");
    }

    public Queue createQueue(@NotNull(message = "The provided queue cannot be null")
                             Queue queueToCreate) {
        return this.queueRepository.save(queueToCreate);
    }

//    public boolean setQueueById(
//            @NotNull(message = "The provided album description cannot be null")
//            QueueDto queueDto,
//            @NotNull(message = "The provided album id cannot be null")
//            @Positive(message = "The provided album id must be positive")
//            Long id) {
//
//        Optional<Queue> optionalAlbumToUpdate = queueRepository.findById(id);
//
//        if (optionalAlbumToUpdate.isPresent()){
//            Queue queueToUpdate = optionalAlbumToUpdate.get();
//            UserMapper userMapper = UserMapper.INSTANCE;
////            queueToUpdate.setOwner(userMapper.toEntity(queueDto.getOwnerDto()));
////            SongMapper songMapper = SongMapper.INSTANCE;
////            queueToUpdate.setSongs(songMapper.toEntityCollection(queueDto.getSongDtos()));
//            queueToUpdate.setCurrentSongIndex(queueDto.getCurrentSongIndex());
//
//            queueRepository.save(queueToUpdate);
//            return true;
//        }
//        throw new ResourceNotFoundException("There is no album with such id");
//    }

    public Queue deleteQueue(
            @NotNull(message = "The provided queue id cannot be null")
            @Positive(message = "The provided queue id must be positive")
            Long queueId) {

        Optional<Queue> optionalQueueToDelete = queueRepository.findById(queueId);

        if (optionalQueueToDelete.isPresent()) {
            Queue queueToDelete = optionalQueueToDelete.get();
            queueRepository.delete(queueToDelete);
            return queueToDelete;
        }

        throw new ResourceNotFoundException("There is not a queue with such an id");
    }

    public Long playSongFromQueue(
        @NotNull(message = "The provided queue id cannot be null")
        @Positive(message = "The provided queue id must be positive")
        Long queueId) {
        Optional<Queue> potentialQueue = queueRepository.findById(queueId);
        if (potentialQueue.isEmpty() || potentialQueue.get().getSongs().isEmpty()) {
            throw new ResourceNotFoundException("There are not songs in queue");
        }

        Queue queue = potentialQueue.get();

        Long currentSongIndex = queue.getCurrentSongIndex();
        if (currentSongIndex.equals((long) queue.getSongs().size())) {
            throw new ResourceNotFoundException("There are not more songs in queue");
        }
        queue.setCurrentSongIndex(++currentSongIndex);

        queueRepository.save(queue);
        return queue.getSongs().get(queue.getCurrentSongIndex().intValue() - 1).getId();
    }

    public boolean removeSongFromQueue(
        @NotNull(message = "The provided queue id cannot be null")
        User potentialUser) {
        Queue queue = potentialUser.getQueue();
        if (queue == null || queue.getSongs().isEmpty()) {
            throw new ResourceNotFoundException("There are not songs in queue");
        }

        Long currentSongIndex = queue.getCurrentSongIndex();
        if (currentSongIndex.equals((long) queue.getSongs().size())) {
            throw new ResourceNotFoundException("There are not songs in queue");
        }
        if (!currentSongIndex.equals(0L)) {
            queue.setCurrentSongIndex(--currentSongIndex);
        }
        queue.getSongs().remove(0);

        queueRepository.save(queue);
        return true;
    }

    public boolean clearSongsFromQueue(@NotNull(message = "The provided queue id cannot be null")
                                       User potentialUser) {
        Queue queue = potentialUser.getQueue();
        if (queue == null) {
            throw new ResourceNotFoundException("There are not songs in queue");
        }

        queue.setCurrentSongIndex(0L);
        queue.setSongs(new ArrayList<>());

        queueRepository.save(queue);
        return true;
    }
}