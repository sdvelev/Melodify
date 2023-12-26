package bg.sofia.uni.fmi.melodify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "user")
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

    @OneToMany
    @JoinColumn(name = "id")
    private List<Playlist> playlists;

    @ManyToMany
    @JoinColumn(name = "id")
    private List<Playlist> albums;

    @Column(name="uri")
    private String uri;
}
