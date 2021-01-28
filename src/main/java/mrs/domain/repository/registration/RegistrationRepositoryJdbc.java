/*package mrs.domain.repository.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import mrs.domain.model.User;

@Repository
public class RegistrationRepositoryJdbc implements RegistrationRepository {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	PasswordEncoder passwordEncoder;

	//Userテーブルにデータを1件insert
	@Override
	public int save(User user) throws DataAccessException {

		//パスワード暗号化
		String password = passwordEncoder.encode(user.getPassword());

		//1件登録
		int rowNumber = jdbc.update("INSERT INTO usr("
				+ "user_id,"
				+ "first_name,"
				+ "last_name,"
				+ "password,"
				+ "phone_number,"
				+ "zip_code,"
				+ "address,"
				+ "role_name)"
				+ "VALUES(?,?,?,?,?,?,?,?)", user.getUserId(), user.getFirstName(), user.getLastName(),
				password, user.getPhoneNumber(), user.getZipCode(), user.getAddress(), user.getRoleName());

		return rowNumber;
	}

	//Userテーブルを1件更新
	@Override
	public int update(User user, String userId) throws DataAccessException {

		//パスワード暗号化
		String password = passwordEncoder.encode(user.getPassword());
		//1件更新
		int rowNumber = jdbc.update("UPDATE usr"
				+ " SET"
				+ "user_id = ?,"
				+ "first_name = ?,"
				+ "last_name = ?,"
				+ "password = ?,"
				+ "phone_number = ?,"
				+ "zip_code = ?,"
				+ "address = ?"
				+ "roleName = ?)"
				+ "WHERE user_id = ?", user.getUserId(), user.getFirstName(), user.getLastName(), password,
				user.getPhoneNumber(), user.getZipCode(), user.getAddress(), user.getRoleName());

		return rowNumber;
	}

}
*/