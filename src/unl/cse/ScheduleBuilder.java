package unl.cse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

public class ScheduleBuilder {

	private static List<Course> langSequence;
	private static List<List<Course>> csMathSequence;
	private static List<List<Course>> ceMathSequence;
	private static List<List<Course>> csSequence;
	private static List<List<Course>> ceSequence;
	private static List<Course> ceSciSequence;
	private static List<Course> csSciSequence;
	private static List<Course> freeElectiveSequence;
	
	static {
		initialize();
	}
	
	public static Schedule getSchedule(Degree d, Integer termCode, Integer numLangYears, String firstMath) {
		
		Schedule s = new Schedule(d, termCode);
		
		Integer term = termCode;
		ArrayList<Integer> terms = new ArrayList<Integer>();
		for(int i=0; i<8; i++) {
			terms.add(term);
			term += (term % 10 == 1) ? 7 : 3;
		}

		if(d == Degree.COMPUTER_ENGINEERING) {
			
			for(int i=0; i<ceSequence.size(); i++) {
				s.addCourses(ceSequence.get(i), terms.get(i));
			}
			//if spring semester, advance the following courses by one semester as they are offered in the opposite semester sequence
			if(terms.get(0) % 10 == 1) {
				List<String> courseShift = Arrays.asList("CSCE10", "ENGR20", "CSCE236", "CSCE351", "CSCE462");
				for(String str : courseShift) {
					Course c = CourseUtils.getCourse(str);
					Integer oldTerm = s.getCourseToTermMap().get(c);
					int i = terms.indexOf(oldTerm);
					s.reSchedule(c, terms.get(i+1));
				}
			}
			
			Course c = CourseUtils.getCourse(firstMath);
			//find the index containing c...
			int index = -1;
			for(int i=0; i<ceMathSequence.size(); i++) {
				if(ceMathSequence.get(i).contains(c)) {
					index = i;
				}
			}
			
			for(int i=index; i<ceMathSequence.size(); i++) {
				s.addCourses(ceMathSequence.get(i), terms.get(i-index));
			}
			
			if(firstMath.equals("MATH103")) {
				index = 2;
			} else {
				index = 0;
			}
			for(int i=0; i<ceSciSequence.size(); i++) {
				s.addCourse(ceSciSequence.get((index+i)%3), terms.get(i));
			}
			
			if(numLangYears < 2) {
				index = 0;
			} else {
				index = 4;
			}
			for(int i=index; i<langSequence.size(); i++) {
				s.addCourse(langSequence.get(i), terms.get(i-index));
			}

			
		} else if(d == Degree.COMPUTER_SCIENCE) {

			for(int i=0; i<csSequence.size(); i++) {
				s.addCourses(csSequence.get(i), terms.get(i));
			}

			//if spring semester, advance the following courses by one semester as they are offered in the opposite semester sequence
			if(terms.get(0) % 10 == 1) {
				//remove CSCE10?
				Course c = CourseUtils.getCourse("CSCE10");
				s.removeCourse(c);
				
				//reschedule 322
				c = CourseUtils.getCourse("CSCE322");
				Integer oldTerm = s.getCourseToTermMap().get(c);
				int i = terms.indexOf(oldTerm);
				s.reSchedule(c, terms.get(i+1));
				
				//exchange 428 with 423
				c = CourseUtils.getCourse("CSCE428");
				int tCode = s.getCourseToTermMap().get(c);
				s.removeCourse(c);
				c = CourseUtils.getCourse("CSCE423");
				s.addCourse(c, tCode);
				
				//exchange 451 with 351
				c = CourseUtils.getCourse("CSCE451");
				tCode = s.getCourseToTermMap().get(c);
				s.removeCourse(c);
				c = CourseUtils.getCourse("CSCE351");
				s.addCourse(c, tCode);
			}

			Course c = CourseUtils.getCourse(firstMath);
			//find the index containing c...
			int index = -1;
			for(int i=0; i<csMathSequence.size(); i++) {
				if(csMathSequence.get(i).contains(c)) {
					index = i;
				}
			}
			for(int i=index; i<csMathSequence.size(); i++) {
				s.addCourses(csMathSequence.get(i), terms.get(i-index));
			}

			if(numLangYears < 2) {
				index = 0;
				//only 1 free elective is needed, put it in the last semester?
				s.addCourse(freeElectiveSequence.get(0), terms.get(terms.size()-1));
			} else if(numLangYears < 4) {
				index = 2;
				s.addCourse(freeElectiveSequence.get(0), terms.get(terms.size()-2));
				s.addCourse(freeElectiveSequence.get(1), terms.get(terms.size()-2));
				s.addCourse(freeElectiveSequence.get(2), terms.get(terms.size()-1));
				s.addCourse(freeElectiveSequence.get(3), terms.get(terms.size()-1));
			} else {
				index = 4;
				s.addCourse(freeElectiveSequence.get(0), terms.get(3));
				s.addCourse(freeElectiveSequence.get(1), terms.get(4));
				s.addCourse(freeElectiveSequence.get(2), terms.get(6));
				s.addCourse(freeElectiveSequence.get(3), terms.get(6));
				s.addCourse(freeElectiveSequence.get(4), terms.get(7));
				s.addCourse(freeElectiveSequence.get(5), terms.get(7));
			}
			int termIndex = 0;
			for(int i=index; i<langSequence.size(); i++) {
				s.addCourse(langSequence.get(i), terms.get(termIndex));
				termIndex++;
			}
			//after they are done with languages, natural sciences
			for(int i=0; i<csSciSequence.size(); i++) {
				s.addCourse(csSciSequence.get(i), terms.get(termIndex));
				termIndex++;
			}

						
		}
		
		return s;
	}
	
	public static void initialize() {
		
		freeElectiveSequence = new ArrayList<Course>();
		freeElectiveSequence.add(CourseUtils.getCourse("FREEELC1"));
		freeElectiveSequence.add(CourseUtils.getCourse("FREEELC2"));
		freeElectiveSequence.add(CourseUtils.getCourse("FREEELC3"));
		freeElectiveSequence.add(CourseUtils.getCourse("FREEELC4"));
		freeElectiveSequence.add(CourseUtils.getCourse("FREEELC5"));
		freeElectiveSequence.add(CourseUtils.getCourse("FREEELC6"));
		
		ceSciSequence = new ArrayList<Course>();
		ceSciSequence.add(CourseUtils.getCourse("PHYS211"));
		ceSciSequence.add(CourseUtils.getCourse("PHYS212"));
		ceSciSequence.add(CourseUtils.getCourse("CHEM109"));
		
		csSciSequence = new ArrayList<Course>();
		csSciSequence.add(CourseUtils.getCourse("NSCI001"));
		csSciSequence.add(CourseUtils.getCourse("NSCI002"));
		csSciSequence.add(CourseUtils.getCourse("NSCI003"));

		ceMathSequence = new ArrayList<List<Course>>(8);
		ceMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH103")));
		ceMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH106")));
		ceMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH107")));
		ceMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH208")));
		ceMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH221")));
		ceMathSequence.add(Collections.<Course> emptyList());
		ceMathSequence.add(Arrays.asList(
				CourseUtils.getCourse("MATH314"),
				CourseUtils.getCourse("ELEC305")));
		ceMathSequence.add(Collections.<Course> emptyList());

		csMathSequence = new ArrayList<List<Course>>(8);
		csMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH103")));
		csMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH106")));
		csMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH107")));
		csMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH208")));
		csMathSequence.add(Arrays.asList(CourseUtils.getCourse("MATH314")));
		csMathSequence.add(Arrays.asList(CourseUtils.getCourse("STAT380")));
		csMathSequence.add(Collections.<Course> emptyList());
		csMathSequence.add(Collections.<Course> emptyList());
		
		langSequence = new ArrayList<Course>();
		langSequence.add(CourseUtils.getCourse("LANG101"));
		langSequence.add(CourseUtils.getCourse("LANG102"));
		langSequence.add(CourseUtils.getCourse("LANG201"));
		langSequence.add(CourseUtils.getCourse("LANG202"));

		csSequence = new ArrayList<List<Course>>(8);
		csSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE10"),
				CourseUtils.getCourse("CSCE155A"),
				CourseUtils.getCourse("ACE1")));
		csSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE156"),
				CourseUtils.getCourse("CSCE235"),
				CourseUtils.getCourse("CSCE251")));
		csSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE310"),
				CourseUtils.getCourse("ACE2"),
				CourseUtils.getCourse("CDRA")));
		csSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE230"),
				CourseUtils.getCourse("CSCE361")));
		csSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE322"),
				CourseUtils.getCourse("ACE5"),
				CourseUtils.getCourse("ACE6")));
		csSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCEELC1"),
				CourseUtils.getCourse("CSCEELC2"),
				CourseUtils.getCourse("CDRC"),
				CourseUtils.getCourse("CDRD"),
				CourseUtils.getCourse("ACE7")));
		csSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE428"),
				CourseUtils.getCourse("CSCEELC3"),
				CourseUtils.getCourse("CSCE486")));
		csSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE451"),
				CourseUtils.getCourse("CSCE487"),
				CourseUtils.getCourse("ACE9")));

		ceSequence = new ArrayList<List<Course>>(8);
		ceSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE10"),
				CourseUtils.getCourse("CSCE155E"),
				CourseUtils.getCourse("ACE5")));
		ceSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE156"),
				CourseUtils.getCourse("CSCE235"),
				CourseUtils.getCourse("CSCE251")));
		ceSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE230"),
				CourseUtils.getCourse("ELEC215"),
				CourseUtils.getCourse("ELEC235"),
				CourseUtils.getCourse("ENGR20")
				));
		ceSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE236"),
				CourseUtils.getCourse("ELEC216"),
				CourseUtils.getCourse("ELEC236"),
				CourseUtils.getCourse("CSCE310"),
				CourseUtils.getCourse("JGEN200")));
		ceSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE351"),
				CourseUtils.getCourse("CSCE361"),
				CourseUtils.getCourse("ELEC304"),
				CourseUtils.getCourse("ELEC316"),
				CourseUtils.getCourse("ACE6")));
		ceSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE335"),
				CourseUtils.getCourse("CSCE462"),
				CourseUtils.getCourse("ELEC305"),
				CourseUtils.getCourse("CSCEELC1")));
		ceSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE440"),
				CourseUtils.getCourse("CSCEELC2"),
				CourseUtils.getCourse("CSCE488"),
				CourseUtils.getCourse("JGEN300"),
				CourseUtils.getCourse("FREEELC1"),
				CourseUtils.getCourse("ACE7")));
		ceSequence.add(Arrays.asList(
				CourseUtils.getCourse("CSCE489"),
				CourseUtils.getCourse("CSCEELC3"),
				CourseUtils.getCourse("CSCEELC4"),
				CourseUtils.getCourse("CSCEELC5"),
				CourseUtils.getCourse("ACE9")
				));

	}
	
	public static void main(String args[]) {
		DateTime dt = new DateTime();
		System.out.println(dt);
		Schedule s = getSchedule(Degree.COMPUTER_ENGINEERING, 1158, 4, "MATH106");
		System.out.println(s.toJson());
	}
}
