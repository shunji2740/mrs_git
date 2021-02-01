package mrs.app.room;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mrs.app.sendContactMail.ContactForm;
import mrs.domain.model.GroupOrder;
import mrs.domain.model.ReservableRoom;
import mrs.domain.repository.user.UserRepository;
import mrs.domain.service.room.RoomService;

@Controller
@RequestMapping("rooms")
public class RoomsController {

	@Autowired
	RoomService roomService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private MailSender mailSender;

	@RequestMapping(method = RequestMethod.GET)
	String listRooms(Model model) {

		// Flash Scopeから値の取り出し
		Boolean booleanResult = (Boolean) model.getAttribute("booleanResult");
		model.addAttribute("booleanResult", booleanResult);
		String message = (String) model.getAttribute("message");
		model.addAttribute("message", message);

		LocalDate today = LocalDate.now();
		List<ReservableRoom> rooms = roomService.findReservableRooms(today);

		//各会議室をmodelに登録する
		model = restoreEachRoomsToModel(rooms, model);
		model.addAttribute("date", today);
		model.addAttribute("rooms", rooms);

		return "room/listRooms";
	}

	@RequestMapping(method = RequestMethod.GET, path = "{date}")
	String listRooms(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date, Model model) {

		List<ReservableRoom> rooms = roomService.findReservableRooms(date);

		//各会議室をmodelに登録する
		model = restoreEachRoomsToModel(rooms, model);
		model.addAttribute("rooms", rooms);

		return "room/listRooms";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String sendmail(RedirectAttributes redirectAttributes,
			@ModelAttribute @Validated(GroupOrder.class) ContactForm contactForm,
			BindingResult bindingResult,
			Model model) {

		//validationのチェック
		if (bindingResult.hasErrors()) {
			System.out.println("★★★★★★★");
			System.out.println("お問い合わせフォームエラー");
			System.out.println("★★★★★★★");
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

	//各会議室をmodelに登録するメソッド
	public Model restoreEachRoomsToModel(List<ReservableRoom> rooms, Model model) {

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

	@ModelAttribute
	ContactForm setUpForm() {
		ContactForm form = new ContactForm();

		return form;
	}
}
