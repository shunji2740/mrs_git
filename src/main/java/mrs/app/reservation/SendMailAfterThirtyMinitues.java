package mrs.app.reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import mrs.domain.model.Reservation;


@Component
public class SendMailAfterThirtyMinitues {

	 @Autowired
	 private MailSender mailSenderAfterThirtyMinitues;

	public void sendMailAfterThirtyMinitues(Reservation reservation) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		Timer timer = new Timer(false);
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				SimpleMailMessage msg = new SimpleMailMessage();
				msg.setFrom("shunjimunemoto@gmail.com");
				msg.setTo("shunjimunemoto@gmail.com");
				msg.setText("ご予約時間の30分前となりました。"
						+ "\n\n------------------------------------------\n" +
						  "\n------------------------------------------");

				mailSenderAfterThirtyMinitues.send(msg);

				timer.cancel();
			}
		};

//		LocalDate reservedDate = reservation.getReservableRoom().getReservableRoomId().getReservedDate();
//
//		String formattedDate = reservedDate.toString();
//
//		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
//		System.out.println(formattedDate);
//		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

		timer.schedule(task,sdf.parse("2021/01/15 15:24"));

	}
}
