package bg.sofia.uni.fmi.melodify.repository;

import bg.sofia.uni.fmi.melodify.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
