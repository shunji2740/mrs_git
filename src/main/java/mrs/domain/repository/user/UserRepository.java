package mrs.domain.repository.user;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import mrs.domain.model.User;



@Transactional
public interface UserRepository extends JpaRepository<User, String>{

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	User findOneForUpdateByUserId(String username);

}
