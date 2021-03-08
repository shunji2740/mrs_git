package mrs.app.pageTransition;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mrs.app.user.User;
import mrs.domain.model.GroupOrder;
import mrs.domain.model.ReservableRoom;
import mrs.domain.repository.user.UserRepository;
import mrs.domain.service.registration.RegistrationService;
import mrs.domain.service.room.RoomService;
import mrs.domain.service.user.ReservationUserDetails;
import mrs.domain.service.user.UserService;

//ヘッダー部分のログアウトボタン以外のページ遷移専用コントローラ
@Controller
public class pageTransitionController {

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@Autowired
	RoomService roomService;

	@Autowired
	UserService userService;

	@Autowired
	private MailSender mailSender;

	@GetMapping("/coronaMeasure")
	public String transitToCorona(Model model) {
		return "coronaMeasure/coronaMeasure";
	}

	@GetMapping("/introductionOfRooms")
	public String transitToIntroduction(Model model) {
		return "introductionRooms/introductionRooms";
	}

	@GetMapping("/priceList")
	public String transitToPrice(Model model) {
		return "priceList/priceList";
	}

	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@GetMapping("/registration")
	public String getRegistration(@ModelAttribute User user, Model model) {

		//最初に遷移されたときはsessionには何も入っていないのでnullになるので
		//そのまま登録画面に遷移する
		if (Objects.isNull(session.getAttribute("bl"))) {
			return "registration/registration";
		}

		//エラーがありリダイレクトされた場合はエラーメッセージをセットし、遷移する
		if ((Boolean) session.getAttribute("bl") == false) {
			String es = "このユーザーIDは既に使用されています";
			model.addAttribute("message", es);

			session.removeAttribute("bl");
		}
		//registration.htmlに画面遷移
		return "registration/registration";
	}

	@PostMapping("/registration")
	public String save(@ModelAttribute @Validated(GroupOrder.class) User user, BindingResult bindingResult,
			Model model) {

		Boolean blFalse = false;

		//既にuserIdが登録されていないか確認
		int resultCountUserId = userRepository.countByUserId(user.getUserId());

		//bindingResultがエラー、もしくはuserIdが既に登録されていた場合はTRUE(最終的にこの条件式で落ち着いた)
		if (bindingResult.hasErrors() || resultCountUserId > 0) {
			if (resultCountUserId != 0) {
				session.setAttribute("bl", blFalse);
			}
			//入力フォームにもどす
			return getRegistration(user, model);
		}

		//エラーが無ければユーザーを登録する
		registrationService.save(user);

		//login.htmlに画面遷移
		return "login/loginForm";
	}

	//ログイン後にlistRooms画面に遷移するメソッド
	@GetMapping("/rooms")
	String listRooms(Model model) {

		/* Flash Scopeから値の取り出し(sendContactMail()からリダイレクトされていない場合値はnull)
		 * モーダルウィンドウに表示するメッセージとTRUE値をmodelにセットする
		 */

		Boolean booleanResult = (Boolean) model.getAttribute("booleanResult");
		model.addAttribute("booleanResult", booleanResult);
		String message = (String) model.getAttribute("message");
		model.addAttribute("message", message);

		LocalDate today = LocalDate.now();

		//当日の予約可能会議室を取得
		List<ReservableRoom> rooms = roomService.findReservableRooms(today);

		// resetoreEachRoomsToModel()で処理されたmodelを取得
		model = restoreEachRoomToModel(rooms, model);
		model.addAttribute("date", today);
		//model.addAttribute("rooms", rooms);

		return "room/listRooms";
	}

	//ログイン以外でlistRooms画面に遷移するメソッド
	@GetMapping("/rooms/{date}")
	String listRooms(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date, Model model) {

		List<ReservableRoom> rooms = roomService.findReservableRooms(date);

		//各会議室をmodelに登録する
		model = restoreEachRoomToModel(rooms, model);
		model.addAttribute("rooms", rooms);

		return "room/listRooms";
	}

	//アカウント登録情報の更新処理を行うメソッド
	@GetMapping("/user")
	String getEditForm(@ModelAttribute User user, @AuthenticationPrincipal ReservationUserDetails userDetails,
			Model model) {

		/*
		 * userDetailsで取得したuserインスタンスをそのままmodelにaddした場合
		 * エラーメッセージが表示されなかった為、すべての要素をuserにセットしていく
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

	@PostMapping("/user")
	String listRooms(@AuthenticationPrincipal ReservationUserDetails userDetails,
			@ModelAttribute @Validated(GroupOrder.class) User user, BindingResult bindingResult, Model model) {

		//validationのチェック
		if (bindingResult.hasErrors()) {
			//エラーなら入力フォームにもどす
			return getEditForm(user, userDetails, model);
		}

		//セッションからuserIdを取得
		String userId = (String) session.getAttribute("userId");

		//userの登録更新
		userService.saveOrUpdate(user, userId);

		return "login/loginForm";
	}

	//お問い合わせフォームをメール送信するメソッド
	@PostMapping("/rooms")
	public String sendContactMail(RedirectAttributes redirectAttributes,
			@ModelAttribute @Validated(GroupOrder.class) ContactForm contactForm,
			BindingResult bindingResult,
			Model model) {

		//validationのチェック
		if (bindingResult.hasErrors()) {
			//エラーなら入力フォームにもどす
			return listRooms(model);
		}

		String body = "お名前: " + contactForm.getName() + "\n" +
				"メールアドレス: " + contactForm.getEmail() + "\n" +
				"メッセージ: \n" + contactForm.getMsgContents();

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(contactForm.getEmail());
		msg.setTo("shunjimunemoto@gmail.com");
		msg.setText("お問い合わせは下記の通りです。\n\n------------------------------------------\n" + body
				+ "\n------------------------------------------");
		mailSender.send(msg);

		//リダイレクト先に値を渡す
		redirectAttributes.addFlashAttribute("message", "送信完了しました");
		redirectAttributes.addFlashAttribute("booleanResult", true);

		return "redirect:/rooms";
	}

	//各roomオブジェクトに名前を付け、modelに登録し返す処理メソッド
	public Model restoreEachRoomToModel(List<ReservableRoom> rooms, Model model) {

		for (ReservableRoom room : rooms) {
			if (room.getMeetingRoom().getRoomId() == 1) {
				model.addAttribute("room_shinkiba", room);
			}
			if (room.getMeetingRoom().getRoomId() == 2) {
				model.addAttribute("room_tatumi", room);
			}
			if (room.getMeetingRoom().getRoomId() == 3) {
				model.addAttribute("room_toyosu", room);
			}
			if (room.getMeetingRoom().getRoomId() == 4) {
				model.addAttribute("room_tukisima", room);
			}
			if (room.getMeetingRoom().getRoomId() == 5) {
				model.addAttribute("room_sintomimati", room);
			}
			if (room.getMeetingRoom().getRoomId() == 6) {
				model.addAttribute("room_ginnzaicchoume", room);
			} else {
				model.addAttribute("room_yuurakuchou", room);
			}
		}
		return model;
	}

	//ContactFormオブジェクトをmodelに登録
	@ModelAttribute
	ContactForm setUpForm() {
		ContactForm form = new ContactForm();

		return form;
	}

}
