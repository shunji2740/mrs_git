package mrs.app.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import mrs.domain.model.GroupOrder;
import mrs.domain.model.User;
import mrs.domain.service.registration.RegistrationService;

@Controller
public class RegistrationController {

	@Autowired
	private RegistrationService registrationService;

	@GetMapping("/registration")
	public String getRegistration(@ModelAttribute User user, Model model) {

		//registration.htmlに画面遷移
		return "registration/registration";
	}

	@PostMapping("/registration")
	public String saveOrUpdateUser(@ModelAttribute @Validated(GroupOrder.class) User user, BindingResult bindingResult,
			Model model) {

		//validationのチェック
		if (bindingResult.hasErrors()) {
			//エラーなら入力フォームにもどす
			return getRegistration(user, model);
		}

		/*
		 * あとは登録画面.htmlを作成後に各項目の値を持ったuserインスタンスを
		 * こちらで受け取り登録する。
		 * returnは登録成功後と失敗用の画面を用意する。
		 */

		registrationService.save(user);

		//login.htmlに画面遷移
		return "login/loginForm";
	}

}
