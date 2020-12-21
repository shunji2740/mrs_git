package mrs.domain.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mrs.domain.model.User;

@Repository

@Transactional
public interface UserRepository extends JpaRepository<User, String> {
}
