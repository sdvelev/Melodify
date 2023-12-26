package bg.sofia.uni.fmi.melodify.dto;

import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Artist;
import bg.sofia.uni.fmi.melodify.model.Genre;
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
public class SongDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("release_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime releaseDate;

    @JsonProperty("number_of_plays")
    private long numberOfPlays;

    @JsonProperty("genre")
    private Genre genre;

    @JsonProperty("album")
    private Album album;

    @JsonProperty("artists")
    private List<ArtistDto> artistDtos;

    @JsonProperty("uri")
    private String uri;
}
