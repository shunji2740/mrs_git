package mrs.domain.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;


//会議室予約システムを利用するユーザー情報を格納するテーブル
//Userエンティティー
@Entity
@Table(name = "usr")
public class User implements Serializable {

	@Id
	@NotBlank(groups=ValidGroup1.class)
	private String userId;

	@NotBlank(groups=ValidGroup1.class)
	@Length(min=4, max=100, groups=ValidGroup2.class)
	private String password;

	@NotBlank(groups=ValidGroup1.class)
	private String firstName;

	@NotBlank(groups=ValidGroup1.class)
	private String lastName;

	@Enumerated(EnumType.STRING)
	private RoleName roleName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public RoleName getRoleName() {
		return roleName;
	}

	public void setRoleName(RoleName roleName) {
		this.roleName = roleName;
	}

}
