package mrs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

@SpringBootApplication
public class MrsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MrsApplication.class, args);

//		try (ConfigurableApplicationContext ctx = SpringApplication.run(MrsApplication.class, args)) {
//            ctx.getBean(MrsApplication.class).sendMail();
//        }

	}

	@Autowired
    private MailSender sender;

    public void sendMail() {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom("test@mail.com");
        msg.setTo("shunjimunemoto@gmail.com");
        msg.setSubject("これはテストメールです"); //タイトルの設定
        msg.setText("Spring Boot より本文送信"); //本文の設定

        this.sender.send(msg);
    }
}
