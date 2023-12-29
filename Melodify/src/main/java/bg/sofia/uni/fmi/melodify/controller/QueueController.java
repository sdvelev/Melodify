package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.QueueDto;
import bg.sofia.uni.fmi.melodify.mapper.QueueMapper;
import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.service.QueueModifySongsFacadeService;
import bg.sofia.uni.fmi.melodify.service.QueueService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/queues")
@Validated
public class QueueController {
    private final QueueService queueService;
    private final QueueModifySongsFacadeService queueModifySongsFacadeService;
    private final QueueMapper queueMapper;

    @Autowired
    public QueueController(QueueService queueService, QueueModifySongsFacadeService queueModifySongsFacadeService,
                           QueueMapper queueMapper) {
        this.queueService = queueService;
        this.queueModifySongsFacadeService = queueModifySongsFacadeService;
        this.queueMapper = queueMapper;
    }

    @GetMapping
    public List<QueueDto> getQueues(@RequestParam Map<String, String> filters) {
        return queueMapper.toDtoCollection(queueService.getQueues(filters));
    }

    @GetMapping(value = "/{id}")
    public QueueDto getQueueById(@PathVariable
                                 @NotNull(message = "The provided queue id cannot be null")
                                 @Positive(message = "The provided queue id must be positive")
                                 Long id) {
        Optional<Queue> potentialQueueToReturn = queueService.getQueueById(id);
        if (potentialQueueToReturn.isPresent()) {
            return queueMapper.toDto(potentialQueueToReturn.get());
        }

        throw new ResourceNotFoundException("The is no such queue with the provided id");
    }

    @PostMapping
    public Long addQueue(@NotNull(message = "The provided queue description in the body cannot be null")
                         @RequestBody
                         QueueDto queueDto) {
        Queue potentialQueueToCreate = queueService.createQueue(queueMapper.toEntity(queueDto));

        if (potentialQueueToCreate == null) {
            throw new ApiBadRequest("There was a problem in creating the album");
        }

        return potentialQueueToCreate.getId();
    }

    @DeleteMapping(params = {"queue_id"})
    public QueueDto deleteQueueById(@RequestParam("queue_id")
                                    @NotNull(message = "The provided queue id cannot be null")
                                    @Positive(message = "The provided queue id must be positive")
                                    Long queueId) {
        return queueMapper.toDto(queueService.deleteQueue(queueId));
    }

//    @PutMapping(value = "/{id}")
//    public boolean setQueueById(@PathVariable
//                                @NotNull(message = "The provided queue id cannot be null")
//                                @Positive(message = "The provided queue id must be positive")
//                                Long id,
//                                @RequestBody
//                                @NotNull(message = "The provided album dto in the body cannot be null")
//                                QueueDto albumToUpdate) {
//        return queueService.setQueueById(albumToUpdate, id);
//    }

    @PatchMapping("/{id}/add")
    public boolean addSongToQueue(@PathVariable
                                      @NotNull(message = "The provided queue id cannot be null")
                                      @Positive(message = "The provided queue id must be positive")
                                      Long id,
                                  @RequestParam("song_id")
                                  @NotNull(message = "The provided song id cannot be null")
                                  @Positive(message = "The provided song id must be positive")
                                  Long songId) {
        return queueModifySongsFacadeService.addSongToQueue(id, songId);
    }

    @PatchMapping("/{id}/remove")
    public boolean removeSongFromQueue(@PathVariable
                                  @NotNull(message = "The provided queue id cannot be null")
                                  @Positive(message = "The provided queue id must be positive")
                                  Long id,
                                  @RequestParam("song_id")
                                  @NotNull(message = "The provided song id cannot be null")
                                  @Positive(message = "The provided song id must be positive")
                                  Long songId) {
        return queueModifySongsFacadeService.removeSongFromQueue(id, songId);
    }



}
