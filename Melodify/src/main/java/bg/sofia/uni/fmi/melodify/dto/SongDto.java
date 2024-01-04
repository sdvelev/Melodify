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
public class SongDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("number_of_plays")
    private long numberOfPlays;

    @JsonProperty("genre")
    private GenreDto genreDto;

//    @JsonProperty("album")
//    private AlbumDto albumDto;

    @JsonProperty("album_image")
    private String albumImage;

    @JsonProperty("artists")
    private List<ArtistDto> artistDtos;

    @JsonProperty("uri")
    private String uri;
}
