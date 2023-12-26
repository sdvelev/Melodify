package bg.sofia.uni.fmi.melodify.dto;

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
public class AlbumDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("release_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime releaseDate;

    @JsonProperty("genre")
    private GenreDto genre;

    @JsonProperty("image")
    private String image;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("artists")
    private List<ArtistDto> artistDtos;
}
