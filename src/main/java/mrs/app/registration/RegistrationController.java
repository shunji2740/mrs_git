package mrs.app.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import mrs.domain.model.User;
import mrs.domain.service.registration.RegistrationService;

@Controller
public class RegistrationController {

	@Autowired
	private RegistrationService registrationService;

	@GetMapping("/registration")
	public String getRegistration(@ModelAttribute User user, Model model) {
		System.out.println("helloooooo");
		//registration.htmlに画面遷移
		return "registration/registration";
	}

	@PostMapping("/registration")
	public String postSignUp(Model model, User user) {

		//ここで登録処理を呼び出す
		//registrationService.save(user);


		System.out.println(user.getUserId());
		System.out.println(user.getFirstName());

		/*
		 * あとは登録画面.htmlを作成後に各項目の値を持ったuserインスタンスを
		 * こちらで受け取り登録する。
		 * returnは登録成功後と失敗用の画面を用意する。
		 */
		//login.htmlに画面遷移
        return "login/login";
	}
}
