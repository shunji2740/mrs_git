package mrs.domain.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import mrs.app.user.User;
import mrs.domain.repository.user.UserRepository;

@Service
public class ReservationUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		//ユーザー取得処理はUserRepositoryに委譲
		User user = userRepository.findById(username).get();

		if (user == null) {
			throw new UsernameNotFoundException(username + " is not found");
		}

		//ユーザーの認証・認可処理はReservationUserDetailsに委譲
		return new ReservationUserDetails(user);
	}
}
