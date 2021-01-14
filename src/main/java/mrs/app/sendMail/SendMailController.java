package mrs.app.sendMail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("sendmail")
public class SendMailController {
	@Autowired
	private MailSender mailSender;

	@RequestMapping(method = RequestMethod.POST)
	public String sendmail(ContactForm form){

		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
		String body = "お名前: " + form.getName() + "\n" +
					  "メールアドレス: " + form.getEmail() + "\n" +
					  "メッセージ: \n" + form.getMessage();

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(form.getEmail());
		msg.setTo("shunjimunemoto@gmail.com");
		msg.setText("お問い合わせは下記の通りです。\n\n------------------------------------------\n" + body + "\n------------------------------------------");
		mailSender.send(msg);

		return "redirect:/rooms";
	}
}
