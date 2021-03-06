package mrs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import mrs.domain.service.user.ReservationUserDetailsService;

@Configuration

//springsecurityのweb連帯機能(CSRF対策など)を有効にする
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	ReservationUserDetailsService userDetailsService;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/webjars/**").permitAll()
				.antMatchers("/js/**", "/css/**", "/images/**").permitAll()
				.antMatchers("/registration/**").permitAll()
				.antMatchers("/reservation/**").permitAll()
				.antMatchers("/**").authenticated()
				.and()
				.formLogin()
				.loginPage("/loginForm")
				.loginProcessingUrl("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.defaultSuccessUrl("/rooms", true)//認証に成功した場合、rooms.htmlに画面遷移
				.failureUrl("/loginForm?error=true").permitAll();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//指定のUserDetailsServiceとPasswordEncoderを使用して認証を行う
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
}
