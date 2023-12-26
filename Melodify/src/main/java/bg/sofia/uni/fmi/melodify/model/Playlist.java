package bg.sofia.uni.fmi.melodify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "playlist")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "user")
    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "image")
    private String image;

    @ManyToMany
    @JoinColumn(name = "id")
    private List<Song> songs;

    @Column(name = "uri")
    private String uri;
}
