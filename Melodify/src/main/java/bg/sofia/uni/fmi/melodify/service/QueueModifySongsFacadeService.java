package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class QueueModifySongsFacadeService {
    private final QueueService queueService;
    private final SongService songService;

    @Autowired
    public QueueModifySongsFacadeService(QueueService queueService, SongService songService) {
        this.queueService = queueService;
        this.songService = songService;
    }

    public boolean addSongToQueue(@NotNull(message = "The provided queue id cannot be null")
                                  @Positive(message = "The provided queue id must be positive")
                                  Long queueId,
                                  @NotNull(message = "The provided song id cannot be null")
                                  @Positive(message = "The provided song id must be positive")
                                  Long songId) {
        Optional<Queue> potentialQueueToAddTo = queueService.getQueueById(queueId);
        if (potentialQueueToAddTo.isEmpty()) {
            throw new ResourceNotFoundException("There is not a queue with such an id");
        }

        Optional<Song> potentialSongToAdd = songService.getSongById(songId);
        if (potentialSongToAdd.isEmpty()) {
            throw new ResourceNotFoundException("There is not a sing with such an id");
        }

        potentialQueueToAddTo.get().getSongs().add(potentialSongToAdd.get());
        queueService.createQueue(potentialQueueToAddTo.get());
        return true;
    }

    public boolean removeSongFromQueue(@NotNull(message = "The provided queue id cannot be null")
                                  @Positive(message = "The provided queue id must be positive")
                                  Long queueId,
                                  @NotNull(message = "The provided song id cannot be null")
                                  @Positive(message = "The provided song id must be positive")
                                  Long songId) {
        Optional<Queue> potentialQueueToAddTo = queueService.getQueueById(queueId);
        if (potentialQueueToAddTo.isEmpty()) {
            throw new ResourceNotFoundException("There is not a queue with such an id");
        }

        Optional<Song> potentialSongToAdd = songService.getSongById(songId);
        if (potentialSongToAdd.isEmpty()) {
            throw new ResourceNotFoundException("There is not a sing with such an id");
        }

        potentialQueueToAddTo.get().getSongs().remove(potentialSongToAdd.get());
        queueService.createQueue(potentialQueueToAddTo.get());
        return true;
    }

    public boolean playFromSpecificSongInQueue(Long songId, User userToPlay) {
        Optional<Song> potentialSongToPlay = songService.getSongById(songId);
        if (potentialSongToPlay.isEmpty()) {
            throw new ResourceNotFoundException("There is not a song with such an id");
        }

        Optional<Queue> potentialQueue = queueService.getQueueById(userToPlay.getQueue().getId());
        if (potentialQueue.isEmpty()) {
            throw new ResourceNotFoundException("There is not a queue associated");
        }

        Queue originalQueue = potentialQueue.get();
        Song originalSong = potentialSongToPlay.get();

        if (!originalQueue.getSongs().contains(originalSong)) {
            throw new ResourceNotFoundException("There is not such song in the queue");
        }

        int currentSongIndex = originalQueue.getSongs().indexOf(originalSong);
        if (currentSongIndex <= originalQueue.getSongs().size() - 1) {
            originalQueue.setCurrentSongIndex((long) currentSongIndex + 1);
            queueService.createQueue(originalQueue);
        } /*else if (currentSongIndex == originalQueue.getSongs().size() - 1) {
            originalQueue.setCurrentSongIndex((long) currentSongIndex);
            queueService.createQueue(originalQueue);
        }*/

        return true;
    }

    public boolean removeSpecificSongFromQueue(Long songId, User userToPlay) {
        Optional<Song> potentialSongToRemove = songService.getSongById(songId);
        if (potentialSongToRemove.isEmpty()) {
            throw new ResourceNotFoundException("There is not a song with such an id");
        }

        Optional<Queue> potentialQueue = queueService.getQueueById(userToPlay.getQueue().getId());
        if (potentialQueue.isEmpty()) {
            throw new ResourceNotFoundException("There is not a queue associated");
        }

        Queue originalQueue = potentialQueue.get();
        Song originalSong = potentialSongToRemove.get();

        if (!originalQueue.getSongs().contains(originalSong)) {
            throw new ResourceNotFoundException("There is not such song in the queue");
        }

        int currentSongIndex = originalQueue.getSongs().indexOf(originalSong);

        if (currentSongIndex >= 1 && originalQueue.getCurrentSongIndex() >= currentSongIndex) {
            originalQueue.setCurrentSongIndex((long) currentSongIndex - 1);
        }

        originalQueue.getSongs().remove(originalSong);
        queueService.createQueue(originalQueue);
        return true;
    }
}