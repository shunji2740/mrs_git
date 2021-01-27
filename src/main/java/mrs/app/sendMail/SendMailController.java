package mrs.app.sendMail;

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
public class SendMailController {
	@Autowired
	private MailSender mailSender;

	@RequestMapping(method = RequestMethod.GET)
	public String sendmail(Model model) {

		return "";

	}

	@RequestMapping(method = RequestMethod.POST)
	public String sendmail(RedirectAttributes redirectAttributes,
			@ModelAttribute @Validated(GroupOrder.class) ContactForm form,
			BindingResult bindingResult,
			Model model) {

		//validationのチェック
		if (bindingResult.hasErrors()) {
			//エラーなら入力フォームにもどす
			return sendmail(model);
		}

		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
		System.out.println(form.getName());
		System.out.println(form.getMessage());

		String body = "お名前: " + form.getName() + "\n" +
				"メールアドレス: " + form.getEmail() + "\n" +
				"メッセージ: \n" + form.getMessage();

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(form.getEmail());
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
