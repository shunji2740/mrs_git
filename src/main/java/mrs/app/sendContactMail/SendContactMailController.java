/*package mrs.app.sendContactMail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mrs.domain.model.GroupOrder;

@Controller
@RequestMapping("sendmail")
public class SendContactMailController {
	@Autowired
	private MailSender mailSender;

 	@RequestMapping(method = RequestMethod.GET)
	public String sendmail(Model model) {
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
			return sendmail(model);
		}

		String body = "お名前: " + contactForm.getName() + "\n" +
				"メールアドレス: " + contactForm.getEmail() + "\n" +
				"メッセージ: \n" + contactForm.getMessage();

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
}
*/