package bg.sofia.uni.fmi.melodify.repository;

import bg.sofia.uni.fmi.melodify.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
