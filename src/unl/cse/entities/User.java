package unl.cse.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.gson.Gson;

@Entity
@Table(name="User")
public class User {

	private static final Gson gson = new Gson();
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id", nullable=false)
	private Integer userId;
	@Column(name="first_name", nullable=false)
	private String firstName;
	@Column(name="last_name", nullable=false)
	private String lastName;
	@Column(name="nuid", nullable=false)
	private String nuid;
	@Column(name="my_unl_login", nullable=false)
	private String myUnlLogin;
	public Set<UserPlan> getPlans() {
		return plans;
	}

	public String getMyUnlLogin() {
		return myUnlLogin;
	}

	@Column(name="cse_login", nullable=false)
	private String cseLogin;
	@Column(name="is_admin", nullable=false)
	private Boolean isAdmin;

	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	protected Set<UserPlan> plans;
	
	@SuppressWarnings("unused")
	private User() {} 
	
	public User(Integer userId, String nuid, String myUnlLogin, String cseLogin,
			String lastName, String firstname, boolean isAdmin) {
		super();
		this.userId = userId;
		this.nuid = nuid;
		this.myUnlLogin = myUnlLogin;
		this.cseLogin = cseLogin;
		this.lastName = lastName;
		this.firstName = firstname;
		this.isAdmin = isAdmin;
	}

	public User(String myUnlLogin) {
		this(null, null, myUnlLogin, null, null, null, false);
	}

	public String getName() {
		if(this.lastName == null) {
			return null;
		} else {
			return this.lastName + ", " + this.firstName;
		}
	}
	
	public boolean isAdmin() {
		return this.isAdmin;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getNuid() {
		return nuid;
	}

	public String getCseLogin() {
		return cseLogin;
	}

	@Override
	public String toString() {
		return "User [nuid=" + nuid + ", myUnlLogin=" + myUnlLogin
				+ ", cseLogin=" + cseLogin + ", lastName=" + lastName
				+ ", firstname=" + firstName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cseLogin == null) ? 0 : cseLogin.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((myUnlLogin == null) ? 0 : myUnlLogin.hashCode());
		result = prime * result + ((nuid == null) ? 0 : nuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (cseLogin == null) {
			if (other.cseLogin != null)
				return false;
		} else if (!cseLogin.equals(other.cseLogin))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (myUnlLogin == null) {
			if (other.myUnlLogin != null)
				return false;
		} else if (!myUnlLogin.equals(other.myUnlLogin))
			return false;
		if (nuid == null) {
			if (other.nuid != null)
				return false;
		} else if (!nuid.equals(other.nuid))
			return false;
		return true;
	}

	public static User fromJson(String jsonUser) {
		return gson.fromJson(jsonUser, User.class);
	}
	
}
