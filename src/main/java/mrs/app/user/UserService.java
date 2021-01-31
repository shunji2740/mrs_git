package mrs.app.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mrs.domain.repository.user.EditUserRepositoryJdbc;
import mrs.domain.repository.user.UserRepository;

@Service
@Transactional
@Component
public class UserService {

	@Autowired
	EditUserRepositoryJdbc userRepositoryJdbc;

	@Autowired
	UserRepository userRepository;

	public void saveOrUpdate(User user, String userId) throws DataAccessException {

		System.out.println("beforeFix: " + userId);
		System.out.println("fixed: " + user.getUserId());

		userRepositoryJdbc.update(user, userId);
	}

	public boolean checkDuplicate(String userId) {

		if (true) {
			return false;
		}

		return false;
	}

}
