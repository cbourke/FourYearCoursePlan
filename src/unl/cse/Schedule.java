package unl.cse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import unl.cse.schedule.UnlTermIterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Schedule {
	
	private static final org.apache.log4j.Logger log = Logger.getLogger(Schedule.class);
	private static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	public static final Integer TRANSFER_CREDIT_TERM_CODE = 1000;
	
	private Degree degree;
	private SortedMap<Integer, List<Course>> termToCourses;
	private Map<Course, Integer> courseToTerm;
	//private List<Course> transferCourses = new ArrayList<Course>();

	/**
	 * Creates a new empty four year course schedule beginning with the given <code>termCode</code>
	 * @param degree
	 * @param termCode
	 */
	public Schedule(Degree degree, Integer termCode) {
		this.degree = degree;
		this.termToCourses = new TreeMap<Integer, List<Course>>();
		UnlTermIterator iter = new UnlTermIterator(termCode, false);
		for(int i=0; i<8; i++) {
			this.termToCourses.put(iter.next(), new ArrayList<Course>());
		}
		this.courseToTerm = new HashMap<Course, Integer>();
	}

	public Schedule(String jsonSchedule) {

		this.termToCourses = new TreeMap<Integer, List<Course>>();
		this.courseToTerm = new HashMap<Course, Integer>();
		
		JsonParser jp = new JsonParser();
		JsonObject root = jp.parse(jsonSchedule).getAsJsonObject();
		if(root.get("degreeTypeId") != null) {
			this.degree = Degree.getDegreeById(root.get("degreeTypeId").getAsInt());
		}
		if(root.get("transferCourses") != null) {
			this.termToCourses.put(TRANSFER_CREDIT_TERM_CODE, new ArrayList<Course>());
			for(JsonElement je : root.get("transferCourses").getAsJsonArray()) {
				Course c = CourseUtils.getCourse(je.getAsString());
				if(c == null) {
					log.warn("No course with id = " + je.getAsString() + " creating dummy course...");
					String subject = je.getAsString().substring(0, 4);
					String number = je.getAsString().substring(4);
					c = new Course(null, subject, number, "", "", "", null, null, "fall,spring,even,odd", "");
				}
				//this.transferCourses.add(c);
				//is this safe?
				this.courseToTerm.put(c, TRANSFER_CREDIT_TERM_CODE);
				this.termToCourses.get(TRANSFER_CREDIT_TERM_CODE).add(c);
			}
		}
		if(root.get("termCodes") != null) {
			for(JsonElement je : root.get("termCodes").getAsJsonArray()) {
				termToCourses.put(je.getAsInt(), new ArrayList<Course>());
			}
		}
		for(Integer termCode : termToCourses.keySet()) {
			if(root.get(termCode.toString()) != null) {
				for(JsonElement je : root.get(termCode.toString()).getAsJsonArray()) {
					Course c = CourseUtils.getCourse(je.getAsString());
					if(c == null) {
						log.warn("No course with id = " + je.getAsString() + " creating dummy course...");
						String subject = je.getAsString().substring(0, 4);
						String number = je.getAsString().substring(4);
						c = new Course(null, subject, number, "", "", "", null, null, "fall,spring,even,odd", "");
					}
					this.termToCourses.get(termCode).add(c);
					this.courseToTerm.put(c, termCode);
				}
			}
		}
	}
	
	public String toJson() {
		//now build the json:
		JsonObject o = new JsonObject();
		o.addProperty("degreeTypeId", this.degree.getId());
		if(this.termToCourses.get(TRANSFER_CREDIT_TERM_CODE) != null && !this.termToCourses.get(TRANSFER_CREDIT_TERM_CODE).isEmpty()) {
			ArrayList<String> courses = new ArrayList<String>();
			for(Course c : this.termToCourses.get(TRANSFER_CREDIT_TERM_CODE)) {
				courses.add(c.getCourseId());
			}
			o.add("transferCourses", GSON.toJsonTree(courses));
		}
		JsonArray ja = new JsonArray();
		for(Integer term : this.termToCourses.keySet()) {
			ArrayList<String> courses = new ArrayList<String>();
			for(Course c : termToCourses.get(term)) {
				courses.add(c.getCourseId());
			}
			o.add(term.toString(), GSON.toJsonTree(courses));
			ja.add(GSON.toJsonTree(term.toString()));
		}
		o.add("termCodes", ja);
		return o.toString();
	}
	
	public void addCourses(List<Course> courses, Integer termCode) {
		for(Course c : courses) {
			this.addCourse(c, termCode);
		}
	}
	
	public void addCourse(Course c, Integer termCode) {
		if(this.courseToTerm.keySet().contains(c)) {
			log.warn("Attempted to add " +  c.getCourseId() +" to term " + termCode + " but Schedule already contains it in " + this.courseToTerm.get(c));
			return;
		}
		if(this.termToCourses.get(termCode) == null) {
			this.termToCourses.put(termCode, new ArrayList<Course>());
		}
		this.termToCourses.get(termCode).add(c);
		this.courseToTerm.put(c, termCode);
	}
	
//	public List<Course> getTransferCourses() {
//		return this.transferCourses;
//	}
	
	public void removeCourse(Course c) {
		if(!this.courseToTerm.keySet().contains(c)) {
			log.warn("Attempted to remove course " + c.getCourseId() + " but it was not already scheduled");
			return;
		}
		int term = this.courseToTerm.get(c);
		this.termToCourses.get(term).remove(c);
		this.courseToTerm.remove(c);
		return;
	}
	
	public void reSchedule(Course c, Integer newTermCode) {
		if(!this.courseToTerm.keySet().contains(c)) {
			log.warn("Attempted to reschedule " + c.getCourseId() + " but it was not already scheduled");
			return;
		}
		int oldTerm = this.courseToTerm.get(c);
		this.termToCourses.get(oldTerm).remove(c);
		this.termToCourses.get(newTermCode).add(c);
		this.courseToTerm.put(c, newTermCode);
	}

	public Map<Course, Integer> getCourseToTermMap() {
		return courseToTerm;
	}

	public Map<Integer, List<Course>> getTermToCoursesMap() {
		return this.termToCourses;
	}

	public Degree getDegree() {
		return this.degree;
	}
	
}
