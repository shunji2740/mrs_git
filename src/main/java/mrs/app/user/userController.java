package mrs.app.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import mrs.domain.model.User;
import mrs.domain.service.user.ReservationUserDetails;

@Controller
@RequestMapping("user")
public class userController {

	@Autowired
	HttpSession session;

	@RequestMapping(method = RequestMethod.GET)
	String listRooms(Model model, @AuthenticationPrincipal ReservationUserDetails userDetails) {

		User user = userDetails.getUser();
		user.setPassword("");
		model.addAttribute("user",user);
		session.setAttribute("userId", user.getUserId());


		return "userdetail/userdetail";
	}

	@RequestMapping(method = RequestMethod.POST)
	String listRooms(Model model, User user) {

		//セッションからreservationエンティティを取得
		String userId = (String) session.getAttribute("userId");


		System.out.println("★★★★★★★★★★★★★★★★");
		System.out.println(userId);
		return "login/loginForm";
	}
}
