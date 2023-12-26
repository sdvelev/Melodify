package bg.sofia.uni.fmi.melodify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "album")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "album")
    private List<Song> songs;

    @ManyToMany(mappedBy = "albums")
    private List<User> users;

    @ManyToMany
    @JoinTable(
        name = "album_artists",
        joinColumns = @JoinColumn(name = "album_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> artists;

    @Column(name = "uri")
    private String uri;
}
