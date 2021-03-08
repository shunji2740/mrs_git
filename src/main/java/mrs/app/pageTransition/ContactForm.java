package mrs.app.pageTransition;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import mrs.domain.model.ValidGroup1;

/*
 * お問い合わせフォームオブジェクト
 */
public class ContactForm implements Serializable {

	@NotBlank(groups = ValidGroup1.class)
	//@Length(min=4, max=100, groups=ValidGroup2.class)
	private String name;

	@NotBlank(groups = ValidGroup1.class)
	//@Email(groups=ValidGroup2.class)
	private String email;

	@NotBlank(groups = ValidGroup1.class)
	//@Length(min=10, max=500, groups=ValidGroup2.class)
	private String msgContents;

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

	public String getMsgContents() {
		return msgContents;
	}

	public void setMsgContents(String msgContents) {
		this.msgContents = msgContents;
	}

}
