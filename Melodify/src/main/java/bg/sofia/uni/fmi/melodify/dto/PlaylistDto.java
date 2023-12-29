package bg.sofia.uni.fmi.melodify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

//    @JsonProperty("owner")
//    private UserDto ownerDto;

    @JsonProperty("creation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonProperty("image")
    private String image;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("songs")
    private List<SongDto> songDtos;
}
