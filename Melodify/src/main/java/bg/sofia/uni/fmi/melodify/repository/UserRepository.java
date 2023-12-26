package bg.sofia.uni.fmi.melodify.repository;

import bg.sofia.uni.fmi.melodify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
