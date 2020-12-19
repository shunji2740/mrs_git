package mrs.domain.repository.user;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import mrs.domain.model.User;

public interface UserRepository extends JpaRepository<User, String>{

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	User findOneForUpdateByUserId(String username);

}
