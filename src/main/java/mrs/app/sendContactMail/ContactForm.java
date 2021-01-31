package mrs.app.sendContactMail;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import mrs.domain.model.ValidGroup1;
import mrs.domain.model.ValidGroup2;

public class ContactForm implements Serializable {

	@NotBlank(groups=ValidGroup1.class)
	@Length(min=4, max=100, groups=ValidGroup2.class)
	private String name;

	@NotBlank(groups=ValidGroup1.class)
	@Email(groups=ValidGroup2.class)
	private String email;

	@NotBlank(groups=ValidGroup1.class)
	@Length(min=10, max=500, groups=ValidGroup2.class)
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
