package mrs.domain.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mrs.domain.model.User;
import mrs.domain.repository.user.EditUserRepositoryJdbc;

@Service
public class UserService {

	@Autowired
	EditUserRepositoryJdbc userRepositoryJdbc;

	@Transactional
	public void saveOrUpdate(User user, String userId) throws DataAccessException {

		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
		System.out.println("beforeFix: " + userId);
		System.out.println("fixed: " + user.getUserId());
		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

		userRepositoryJdbc.update(user,userId);
	}

}
