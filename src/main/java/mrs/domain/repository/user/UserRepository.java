package mrs.domain.repository.user;

import javax.persistence.LockModeType;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import mrs.domain.model.User;

@Repository
@Configurable
public interface UserRepository extends JpaRepository<User, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	User findOneForUpdateByUserId(String userId);
	long countByUserId(String userId);

}
