package mrs.domain.repository.registration;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import mrs.app.user.User;

@Repository
public interface RegistrationRepository extends JpaRepository<User, String>{

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	User findOneForUpdateByUserId(String userId);

}

