package unl.cse.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

@Entity
@Table(name="UserPlan")
public class UserPlan {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_plan_id", nullable=false)
	private Integer userPlanId;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;

	@Column(name="json_plan", nullable=false)
	private String jsonPlan;

	@Column(name="create_date", nullable=false)
	private String createDate;
	@Column(name="update_date", nullable=false)
	private String updateDate;

	@SuppressWarnings("unused")
	private UserPlan() {}

	public UserPlan(Integer userPlanId, User user, String jsonPlan, String createDate,
			String updateDate) {
		this.userPlanId = userPlanId;
		this.user = user;
		this.jsonPlan = jsonPlan;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public UserPlan(User user, String jsonPlan, DateTime createDate,
			DateTime updateDate) {
		this.user = user;
		this.jsonPlan = jsonPlan;
		this.createDate = createDate.toString();
		this.updateDate = updateDate.toString();
	}
	
	public User getUser() {
		return this.user;
	}
	
	public String getJsonPlan() {
		return jsonPlan;
	}

	public void setJsonPlan(String jsonPlan) {
		this.jsonPlan = jsonPlan;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getUserPlanId() {
		return userPlanId;
	}

	
}
