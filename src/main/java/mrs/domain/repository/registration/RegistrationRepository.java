package mrs.domain.repository.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mrs.domain.model.User;

@Repository
public interface RegistrationRepository extends JpaRepository<User, Integer>{

}

