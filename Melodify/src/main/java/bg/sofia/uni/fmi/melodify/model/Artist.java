package bg.sofia.uni.fmi.melodify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "artist")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "uri")
    private String uri;

    @ManyToMany(mappedBy = "artists")
    private List<Song> songs;

    @ManyToMany(mappedBy = "artists")
    private List<Album> albums;
}