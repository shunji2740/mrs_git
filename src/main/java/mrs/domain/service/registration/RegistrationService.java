package mrs.domain.service.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mrs.domain.model.User;
import mrs.domain.repository.registration.RegistrationRepository;

@Service
public class RegistrationService {

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	JdbcTemplate jdbc;

	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Transactional
	public void save(User user) throws DataAccessException {
		//パスワード暗号化
		String password = passwordEncoder().encode(user.getPassword());

		//ハッシュ化したパスワードをuserにセットする
		user.setPassword(password);

		registrationRepository.save(user);
		//registrationRepository.saveAndFlush(user);
	}


}
