package mrs.app.registration;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import mrs.app.user.User;
import mrs.domain.model.GroupOrder;
import mrs.domain.repository.user.UserRepository;
import mrs.domain.service.registration.RegistrationService;

@Controller
public class RegistrationController {

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@GetMapping("/registration")
	public String getRegistration(@ModelAttribute User user, Model model) {

		//もう少しいい条件式を考える
		if ((Boolean) session.getAttribute("bl") != null) {
			String es = "このユーザーIDは既に使用されています";
			model.addAttribute("message", es);
			//セッションを開放する
			session.removeAttribute("bl");
		}
		//registration.htmlに画面遷移
		return "registration/registration";
	}

	@PostMapping("/registration")
	public String saveOrUpdateUser(@ModelAttribute @Validated(GroupOrder.class) User user, BindingResult bindingResult,
			Model model) {

		int resultCountUserId = userRepository.countByUserId(user.getUserId());

		//もう少しいい条件式を考える
		if (bindingResult.hasErrors() || resultCountUserId != 0) {
			if (resultCountUserId != 0) {
				Boolean bl = true;
				session.setAttribute("bl", bl);
			}
			//エラーなら入力フォームにもどす
			return getRegistration(user, model);
		}
		registrationService.save(user);

		//login.htmlに画面遷移
		return "login/loginForm";
	}

}
