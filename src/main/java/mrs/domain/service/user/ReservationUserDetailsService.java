package mrs.domain.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import mrs.domain.model.User;
import mrs.domain.repository.user.UserRepository;

@Service
public class ReservationUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		//ユーザー取得処理はUserRepositoryに委譲
		User user = userRepository.findOneForUpdateByUserId(username);

		if (user == null) {

//			User user_test = new User();
//			user_test.setUserId("aaaa");
//			user_test.setPassword("demo");
//			user_test.setFirstName("Aaa");
//			user_test.setLastName("Aaa");
//			user_test.setRoleName(RoleName.USER);
//
//			System.out.println("セット完了");
//			return new ReservationUserDetails(user_test);

			throw new UsernameNotFoundException(username + " is not found");
		}



		//ユーザーの認証・認可処理はReservationUserDetailsに委譲
		return new ReservationUserDetails(user);
	}
}
