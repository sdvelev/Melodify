package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.model.Song;
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
}