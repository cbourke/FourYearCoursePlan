package unl.cse;

public class Course {
	
	private Integer dbCourseId;
	private String courseId;
	private String subject;
	private String number;
	private String title;
	private String description;
	private String prerequisiteText;
	private String adviserComment;
	private Integer creditHours;
	private String schedule;
	private String uiGroup;
	
	public Course(Integer dbCourseId, String subject, String number,
			String title, String description, String prerequisiteText,
			String adviserComment, Integer creditHours, String schedule, String uiGroup) {
		super();
		this.dbCourseId = dbCourseId;
		this.courseId = subject + number;
		this.subject = subject;
		this.number = number;
		this.title = title;
		this.description = description;
		this.prerequisiteText = prerequisiteText;
		this.adviserComment = adviserComment;
		this.creditHours = creditHours;
		this.schedule = schedule;
		this.uiGroup = uiGroup;
	}
	/**
	 * @return the courseId
	 */
	public String getCourseId() {
		return courseId;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the prerequisiteText
	 */
	public String getPrerequisiteText() {
		return prerequisiteText;
	}
	/**
	 * @return the creditHours
	 */
	public Integer getCreditHours() {
		return creditHours;
	}
	/**
	 * @return the schedule
	 */
	public String getSchedule() {
		return schedule;
	}
	/**
	 * @return the uiGroup
	 */
	public String getUiGroup() {
		return uiGroup;
	}

	

	@Override
	public String toString() {
		return "Course [dbCourseId=" + dbCourseId + ", courseId=" + courseId
				+ ", subject=" + subject + ", number=" + number + ", title="
				+ title + ", description=" + description
				+ ", prerequisiteText=" + prerequisiteText + ", creditHours="
				+ creditHours + ", schedule=" + schedule + ", uiGroup="
				+ uiGroup + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((courseId == null) ? 0 : courseId.hashCode());
		result = prime * result
				+ ((creditHours == null) ? 0 : creditHours.hashCode());
		result = prime * result
				+ ((dbCourseId == null) ? 0 : dbCourseId.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime
				* result
				+ ((prerequisiteText == null) ? 0 : prerequisiteText.hashCode());
		result = prime * result
				+ ((schedule == null) ? 0 : schedule.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((uiGroup == null) ? 0 : uiGroup.hashCode());
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
		Course other = (Course) obj;
		if (courseId == null) {
			if (other.courseId != null)
				return false;
		} else if (!courseId.equals(other.courseId))
			return false;
		if (creditHours == null) {
			if (other.creditHours != null)
				return false;
		} else if (!creditHours.equals(other.creditHours))
			return false;
		if (dbCourseId == null) {
			if (other.dbCourseId != null)
				return false;
		} else if (!dbCourseId.equals(other.dbCourseId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (prerequisiteText == null) {
			if (other.prerequisiteText != null)
				return false;
		} else if (!prerequisiteText.equals(other.prerequisiteText))
			return false;
		if (schedule == null) {
			if (other.schedule != null)
				return false;
		} else if (!schedule.equals(other.schedule))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (uiGroup == null) {
			if (other.uiGroup != null)
				return false;
		} else if (!uiGroup.equals(other.uiGroup))
			return false;
		return true;
	}
	
	public String getAdviserComment() {
		return adviserComment;
	}

}
