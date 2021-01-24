package mrs.domain.repository.user;

import org.springframework.dao.DataAccessException;

public abstract class UserRepository2 implements UserRepository{

	public int updateOne(int userId) throws DataAccessException {



		return userId;
	}

}
