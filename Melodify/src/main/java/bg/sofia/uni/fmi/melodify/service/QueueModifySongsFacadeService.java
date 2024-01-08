package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.model.User;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
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

    @Transactional
    public boolean addSongToQueue(@NotNull(message = "The provided queue id cannot be null")
                                  @Positive(message = "The provided queue id must be positive")
                                  Long queueId,
                                  @NotNull(message = "The provided song id cannot be null")
                                  List<Long> songIds) {
        Optional<Queue> potentialQueueToAddTo = queueService.getQueueById(queueId);
        if (potentialQueueToAddTo.isEmpty()) {
            throw new ResourceNotFoundException("There is not a queue with such an id");
        }

        for (Long songId : songIds) {
            Optional<Song> potentialSongToAdd = songService.getSongById(songId);
            if (potentialSongToAdd.isEmpty()) {
                throw new ResourceNotFoundException("Some of the provided song ids are wrong. Operation failed");
            }

            potentialQueueToAddTo.get().getSongs().add(potentialSongToAdd.get());
        }

        queueService.createQueue(potentialQueueToAddTo.get());
        return true;
    }

    public Long playSongFromQueue(
        @NotNull(message = "The provided queue id cannot be null")
        @Positive(message = "The provided queue id must be positive")
        Long queueId) {
        Optional<Queue> potentialQueue = queueService.getQueueById(queueId);
        if (potentialQueue.isEmpty() || potentialQueue.get().getSongs().isEmpty()) {
            throw new ResourceNotFoundException("There are not songs in queue");
        }

        Queue queue = potentialQueue.get();
        Song currentSong = queue.getSongs().get(queue.getCurrentSongIndex().intValue());
        currentSong.setNumberOfPlays(currentSong.getNumberOfPlays() + 1);

        this.songService.createSong(currentSong);

        return queue.getSongs().get(queue.getCurrentSongIndex().intValue() /*- 1*/).getId();
    }

    public Long playPreviousSongFromQueue(
        @NotNull(message = "The provided queue id cannot be null")
        @Positive(message = "The provided queue id must be positive")
        Long queueId) {
        Optional<Queue> potentialQueue = queueService.getQueueById(queueId);
        if (potentialQueue.isEmpty() || potentialQueue.get().getSongs().isEmpty()) {
            throw new ResourceNotFoundException("There are not songs in queue");
        }

        Queue queue = potentialQueue.get();

        Long currentSongIndex = queue.getCurrentSongIndex();
        if (currentSongIndex > 0) {
            queue.setCurrentSongIndex(queue.getCurrentSongIndex() - 1);
            queueService.createQueue(queue);
            Song songToPlay = queue.getSongs().get(queue.getCurrentSongIndex().intValue());
            songToPlay.setNumberOfPlays(queue.getSongs().get(queue.getCurrentSongIndex().intValue()).getNumberOfPlays() + 1);
            this.songService.createSong(songToPlay);
            return queue.getSongs().get(queue.getCurrentSongIndex().intValue()).getId();
        }

        throw new ResourceNotFoundException("There are not previous songs in queue");
    }

    public Long playNextSongFromQueue(
        @NotNull(message = "The provided queue id cannot be null")
        @Positive(message = "The provided queue id must be positive")
        Long queueId) {
        Optional<Queue> potentialQueue = queueService.getQueueById(queueId);
        if (potentialQueue.isEmpty() || potentialQueue.get().getSongs().isEmpty()) {
            throw new ResourceNotFoundException("There are not songs in queue");
        }

        Queue queue = potentialQueue.get();

        Long currentSongIndex = queue.getCurrentSongIndex();
        if (currentSongIndex < queue.getSongs().size() - 1) {
            queue.setCurrentSongIndex(queue.getCurrentSongIndex() + 1);
            queueService.createQueue(queue);
            Song currentSong = queue.getSongs().get(queue.getCurrentSongIndex().intValue());
            currentSong.setNumberOfPlays(currentSong.getNumberOfPlays() + 1);
            this.songService.createSong(currentSong);
            return queue.getSongs().get(queue.getCurrentSongIndex().intValue()).getId();
        }

        throw new ResourceNotFoundException("There are not next songs in queue");
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
            originalQueue.setCurrentSongIndex((long) currentSongIndex/* + 1*/);
            queueService.createQueue(originalQueue);
        }

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

        if (originalQueue.getCurrentSongIndex().equals(0L) && currentSongIndex == 0) {
            originalQueue.setCurrentSongIndex(0L);
        }
        else if (currentSongIndex >= 0 && originalQueue.getCurrentSongIndex() >= currentSongIndex) {
            originalQueue.setCurrentSongIndex(originalQueue.getCurrentSongIndex() - 1);
        }

        originalQueue.getSongs().remove(originalSong);
        queueService.createQueue(originalQueue);
        return true;
    }
}