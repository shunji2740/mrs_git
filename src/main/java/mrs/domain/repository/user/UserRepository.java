package mrs.domain.repository.user;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import mrs.app.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	User findOneForUpdateByUserId(String userId);
	Integer countByUserId(String userId);
	Integer countByPassword(String password);

}
