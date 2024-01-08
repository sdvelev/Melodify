package bg.sofia.uni.fmi.melodify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueueDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("currentSongIndex")
    private Long currentSongIndex;

    @JsonProperty("songs")
    private List<SongDto> songDtos;
}