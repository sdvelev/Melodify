package bg.sofia.uni.fmi.melodify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @JsonProperty("genre")
    private GenreDto genreDto;

    @JsonProperty("image")
    private String image;

    @JsonProperty("songs")
    private List<SongDto> songDtos;

    @JsonProperty("artists")
    private List<ArtistDto> artistDtos;

    @JsonProperty("uri")
    private String uri;
}