package bg.sofia.uni.fmi.melodify.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "song")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "duration")
    private int duration;

    @Column(name = "number_of_plays")
    private long numberOfPlays;

    @Column(name = "genre")
    @ManyToOne
    @JoinColumn(name = "id")
    private Genre genre;

    @Column(name = "album")
    @ManyToOne
    @JoinColumn(name = "id")
    private Album album;

    @ManyToMany
    @JoinColumn(name = "id")
    private List<Artist> artists;

    @Column(name = "uri")
    private String uri;
}
