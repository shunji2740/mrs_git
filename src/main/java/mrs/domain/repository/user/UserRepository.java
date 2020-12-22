package mrs.domain.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
=======
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
>>>>>>> refs/remotes/origin/main

import mrs.domain.model.User;

<<<<<<< HEAD
public interface UserRepository extends JpaRepository<User, String>{
=======
@Repository
>>>>>>> refs/remotes/origin/main

<<<<<<< HEAD
=======
@Transactional
public interface UserRepository extends JpaRepository<User, String> {
>>>>>>> refs/remotes/origin/main
}
