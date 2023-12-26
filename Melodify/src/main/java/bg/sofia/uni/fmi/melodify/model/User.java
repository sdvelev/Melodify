package bg.sofia.uni.fmi.melodify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "registered_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "image")
    private String image;

    @Column(name = "current_song")
    private Integer currentSong;

    @Column(name="is_playing")
    private Boolean isPlaying;

    @OneToMany(mappedBy = "user")
    private List<Playlist> playlists;

    @ManyToMany
    @JoinTable(
        name = "user_albums",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "album_id"))
    private List<Album> albums;

    @Column(name="uri")
    private String uri;
}
