package mrs.domain.repository.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import mrs.domain.model.User;

@Repository
public class EditUserRepositoryJdbc {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	PasswordEncoder passwordEncoder;

	//Userテーブルを1件更新
	public int update(User user, String userId) throws DataAccessException {

		//パスワード暗号化
		String password = passwordEncoder.encode(user.getPassword());
		//1件更新
		int rowNumber = jdbc.update("UPDATE usr"
				+ " SET"
				+ " user_id = ?,"
				+ "first_name = ?,"
				+ "last_name = ?,"
				+ "password = ?,"
				+ "phone_number = ?,"
				+ "zip_code = ?,"
				+ "address = ?,"
				+ "role_name = ? "
				+ "WHERE user_id = ?", user.getUserId(), user.getFirstName(), user.getLastName(), password,
				user.getPhoneNumber(), user.getZipCode(), user.getAddress(), user.getRoleName(), userId);

		return rowNumber;
	}

}
