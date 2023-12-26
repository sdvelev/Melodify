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
public class UserDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("email")
    private String email;

    @JsonProperty("image")
    private String image;

    @JsonProperty("current_song")
    private Integer currentSong;

    @JsonProperty("is_playing")
    private Boolean isPlaying;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("playlists")
    private List<PlaylistDto> playlistDtos;

    @JsonProperty("albums")
    private List<AlbumDto> albumDtos;
}
