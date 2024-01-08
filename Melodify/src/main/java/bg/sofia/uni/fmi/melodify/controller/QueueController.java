package bg.sofia.uni.fmi.melodify.controller;

import bg.sofia.uni.fmi.melodify.dto.QueueDto;
import bg.sofia.uni.fmi.melodify.mapper.QueueMapper;
import bg.sofia.uni.fmi.melodify.model.Queue;
import bg.sofia.uni.fmi.melodify.service.QueueModifySongsFacadeService;
import bg.sofia.uni.fmi.melodify.service.QueueService;
import bg.sofia.uni.fmi.melodify.service.TokenManagerService;
import bg.sofia.uni.fmi.melodify.service.UserService;
import bg.sofia.uni.fmi.melodify.validation.ApiBadRequest;
import bg.sofia.uni.fmi.melodify.validation.MethodNotAllowed;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static bg.sofia.uni.fmi.melodify.security.RequestManager.getUserByRequest;
import static bg.sofia.uni.fmi.melodify.security.RequestManager.isAdminByRequest;

@RestController
@RequestMapping(path = "api/queues")
@Validated
public class QueueController {
    private final QueueService queueService;
    private final QueueModifySongsFacadeService queueModifySongsFacadeService;
    private final TokenManagerService tokenManagerService;
    private final UserService userService;
    private final QueueMapper queueMapper;
    private final ResourceLoader resourceLoader;

    @Autowired
    public QueueController(QueueService queueService, QueueModifySongsFacadeService queueModifySongsFacadeService,
                           TokenManagerService tokenManagerService, UserService userService, QueueMapper queueMapper,
                           ResourceLoader resourceLoader) {
        this.queueService = queueService;
        this.queueModifySongsFacadeService = queueModifySongsFacadeService;
        this.tokenManagerService = tokenManagerService;
        this.userService = userService;
        this.queueMapper = queueMapper;
        this.resourceLoader = resourceLoader;
    }

    @GetMapping
    public List<QueueDto> getQueues(@RequestParam Map<String, String> filters, HttpServletRequest request) {
        return queueMapper.toDtoCollection(queueService.getQueues(filters,
            getUserByRequest(request, tokenManagerService, userService),
            isAdminByRequest(request, tokenManagerService)));
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
                         QueueDto queueDto,
                         HttpServletRequest request) {
        if (!isAdminByRequest(request, tokenManagerService)) {
            throw new MethodNotAllowed("There was a problem in authorization");
        }

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
                                    Long queueId,
                                    HttpServletRequest request) {
        if (!isAdminByRequest(request, tokenManagerService)) {
            throw new MethodNotAllowed("There was a problem in authorization");
        }

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

    @PatchMapping("/add")
    public boolean addSongToQueue(@RequestParam("song_ids")
                                  @NotNull(message = "The provided song ids cannot be null")
                                  List<Long> songIds,
                                  HttpServletRequest request) {
        return queueModifySongsFacadeService
            .addSongToQueue(getUserByRequest(request, tokenManagerService, userService).getId(), songIds);
    }

    @PatchMapping("/clear")
    public boolean clearSongsFromQueue(HttpServletRequest request) {
        return queueService.clearSongsFromQueue(getUserByRequest(request, tokenManagerService, userService));
    }

//    @PatchMapping("/remove")
//    public boolean removeSongFromQueue(@RequestParam("song_id")
//                                  @NotNull(message = "The provided song id cannot be null")
//                                  @Positive(message = "The provided song id must be positive")
//                                  Long songId,
//                                  HttpServletRequest request) {
//        return queueModifySongsFacadeService
//            .removeSongFromQueue(getUserByRequest(request, tokenManagerService, userService).getId(), songId);
//    }

    @GetMapping("/play")
    public ResponseEntity<Resource> playSongFromQueue(
        @RequestParam(name = "song_id", required = false)
        @Positive(message = "") Long songId,
        HttpServletRequest request) {
        try {
            Long songToPlayId = (songId != null) ? songId :
                queueService.playSongFromQueue(getUserByRequest(request, tokenManagerService, userService).getId());

            boolean toPlay;
            if (songId != null) {
                toPlay = queueModifySongsFacadeService
                    .playFromSpecificSongInQueue(songToPlayId,
                        getUserByRequest(request, tokenManagerService, userService));
            } else {
                toPlay = true;
            }

            Resource resource = resourceLoader.getResource("classpath:/tracks/" + songToPlayId + ".mp3");

            if (toPlay && resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("inline", songToPlayId + ".mp3");
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/previous")
    public ResponseEntity<Resource> playPreviousSongFromQueue(HttpServletRequest request) {
        try {
            Long songToPlayId = queueService
                .playPreviousSongFromQueue(getUserByRequest(request, tokenManagerService, userService).getId());

            Resource resource = resourceLoader.getResource("classpath:/tracks/" + songToPlayId + ".mp3");

            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("inline", songToPlayId + ".mp3");
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/next")
    public ResponseEntity<Resource> playNextSongFromQueue(HttpServletRequest request) {
        try {
            Long songToPlayId = queueService
                .playNextSongFromQueue(getUserByRequest(request, tokenManagerService, userService).getId());

            Resource resource = resourceLoader.getResource("classpath:/tracks/" + songToPlayId + ".mp3");

            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("inline", songToPlayId + ".mp3");
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/remove")
    public boolean removeSongFromQueue(
        @RequestParam(name = "song_id", required = false)
        @Positive(message = "") Long songId,
        HttpServletRequest request) {

        boolean toRemove;
            if (songId != null) {
                toRemove = queueModifySongsFacadeService
                    .removeSpecificSongFromQueue(songId, getUserByRequest(request, tokenManagerService, userService));
            } else {
            toRemove = queueService.removeSongFromQueue(getUserByRequest(request, tokenManagerService, userService));
        }

         return toRemove;
    }
}