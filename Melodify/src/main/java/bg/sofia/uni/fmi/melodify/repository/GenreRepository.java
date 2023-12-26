package bg.sofia.uni.fmi.melodify.repository;

import bg.sofia.uni.fmi.melodify.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
