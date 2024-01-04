package bg.sofia.uni.fmi.melodify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

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
    private Integer duration;

    @Column(name = "number_of_plays")
    private long numberOfPlays;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "song_artists",
        joinColumns = @JoinColumn(name = "song_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id"))
    private List<Artist> artists;

    @Column(name = "uri")
    private String uri;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(id, song.id) && Objects.equals(name, song.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
