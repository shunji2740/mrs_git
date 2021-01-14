package mrs.app.sendMail;

import java.io.Serializable;

public class ContactForm implements Serializable {
	private String name = "shunji";
	private String email;
	private String message;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
