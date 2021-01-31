package mrs.app.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import mrs.domain.model.GroupOrder;
import mrs.domain.service.user.ReservationUserDetails;

@Controller
@RequestMapping("user")
public class userController {

	@Autowired
	HttpSession session;

	@Autowired
	UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	String getEditForm(@ModelAttribute User user, @AuthenticationPrincipal ReservationUserDetails userDetails,
			Model model) {

		/*userDetailsで取得したuserインスタンスをそのままmodelにaddした場合
		エラーメッセージが表示されなかった為、すべての要素をuserにセットしていく
		*/
		User authenticatedUser = userDetails.getUser();

		user.setUserId(authenticatedUser.getUserId());
		user.setFirstName(authenticatedUser.getFirstName());
		user.setLastName(authenticatedUser.getLastName());
		user.setAddress(authenticatedUser.getAddress());
		user.setZipCode(authenticatedUser.getZipCode());
		user.setPassword("");
		user.setPhoneNumber(authenticatedUser.getPhoneNumber());

		model.addAttribute("user", user);
		session.setAttribute("userId", user.getUserId());

		return "userdetail/userdetail";
	}

	@RequestMapping(method = RequestMethod.POST)
	String listRooms(@AuthenticationPrincipal ReservationUserDetails userDetails,
			@ModelAttribute @Validated(GroupOrder.class) User user, BindingResult bindingResult, Model model) {

		//validationのチェック
		if (bindingResult.hasErrors()) {
			//エラーなら入力フォームにもどす
			return getEditForm(user, userDetails, model);
		}

		//セッションからreservationエンティティを取得
		String userId = (String) session.getAttribute("userId");
		userService.saveOrUpdate(user, userId);

		System.out.println("ユーザー登録更新完了。");
		return "login/loginForm";
	}

}
