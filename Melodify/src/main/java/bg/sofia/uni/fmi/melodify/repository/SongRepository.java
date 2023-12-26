package bg.sofia.uni.fmi.melodify.repository;

import bg.sofia.uni.fmi.melodify.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {

}
