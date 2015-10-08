package unl.cse.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import unl.cse.Course;
import unl.cse.CourseUtils;

public class DegreeRequirements {

	public static final List<Requirement> degreeRequirements = new ArrayList<Requirement>();
	public static final List<Requirement> ceRequirements = new ArrayList<Requirement>();
	public static final List<Requirement> csRequirements = new ArrayList<Requirement>();
	
	static {
		ceRequirements.add(new CreditHourRequirement(125));
		csRequirements.add(new CreditHourRequirement(120));
	}

	public static final List<Requirement> requirements = buildRequirements();

	private static List<Requirement> buildRequirements() {
		List<Requirement> requirements = new ArrayList<Requirement>();
		
		requirements.add(new SchedulingValidator());

		//math prereqs
		requirements.add(new Prerequisite(CourseUtils.getCourse("MATH107"), CourseUtils.getCourse("MATH106")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("MATH208"), CourseUtils.getCourse("MATH107")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("MATH314"), CourseUtils.getCourse("MATH107")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("STAT380"), CourseUtils.getCourse("MATH107")));

		List<Course> csce155s = Arrays.asList(CourseUtils.getCourse("CSCE155A"),
				CourseUtils.getCourse("CSCE155E"),
				CourseUtils.getCourse("CSCE155H"),
				CourseUtils.getCourse("CSCE155T"),
				CourseUtils.getCourse("CSCE155N"));
		
		for(String s : Arrays.asList("CSCE156", "CSCE230", "CSCE235", "CSCE251", "CSCE340")) {
			DisjunctiveRequirement r = new DisjunctiveRequirement(s + " requires Computer Science I (CSCE 155: A, E, H, N, or T) as a prerequisite");
			for(Course c : csce155s) {
				r.addRequirement(new Prerequisite(CourseUtils.getCourse(s), c));
			}
			requirements.add(r);
		}

		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE236"), CourseUtils.getCourse("CSCE230")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE322"), CourseUtils.getCourse("CSCE230")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE322"), CourseUtils.getCourse("CSCE156")));

		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE310"), CourseUtils.getCourse("CSCE235")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE310"), CourseUtils.getCourse("CSCE156")));

		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE378"), CourseUtils.getCourse("CSCE156")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE472"), CourseUtils.getCourse("CSCE156")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE473"), CourseUtils.getCourse("CSCE156")));

		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE425"), CourseUtils.getCourse("CSCE322")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE436"), CourseUtils.getCourse("CSCE236")));
		
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE430"), CourseUtils.getCourse("CSCE230")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE430"), CourseUtils.getCourse("CSCE310")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE438"), CourseUtils.getCourse("CSCE230")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE351"), CourseUtils.getCourse("CSCE230")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE451"), CourseUtils.getCourse("CSCE230")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE462"), CourseUtils.getCourse("CSCE230")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE462"), CourseUtils.getCourse("CSCE310")));

		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE432"), CourseUtils.getCourse("CSCE430")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE437"), CourseUtils.getCourse("CSCE430")));
		
		//430 and 462 require EITHER STAT380 OR ELEC305
		for(String s : Arrays.asList("CSCE430", "CSCE462")) {
			DisjunctiveRequirement r = new DisjunctiveRequirement(s + " requires either STAT380 or ELEC305 as a prerequisite");
			r.addRequirement(new Prerequisite(CourseUtils.getCourse(s), CourseUtils.getCourse("STAT380")));
			r.addRequirement(new Prerequisite(CourseUtils.getCourse(s), CourseUtils.getCourse("ELEC305")));
			requirements.add(r);
		}

		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE432"), CourseUtils.getCourse("MATH314")));

		for(String s : Arrays.asList("410", "413", "423", "421", "424", "425", "428", "430", "438", "435", "451", "436", "437", "456", "457", "475", "476", "478", "361", "464", "470", "471", "474", "477", "479")) {
			requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE"+s), CourseUtils.getCourse("CSCE310")));
		}
		
		//STAT 380 is a prereq for CSCE 430, CSCE 432, CSCE 462, CSCE 465, CSCE 471, CSCE 474
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE430"), CourseUtils.getCourse("STAT380")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE432"), CourseUtils.getCourse("STAT380")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE462"), CourseUtils.getCourse("STAT380")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE465"), CourseUtils.getCourse("STAT380")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE471"), CourseUtils.getCourse("STAT380")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE474"), CourseUtils.getCourse("STAT380")));

		//MATH 314 is a prereq for CSCE 432, 439, 441, 470, 477
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE432"), CourseUtils.getCourse("MATH314")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE439"), CourseUtils.getCourse("MATH314")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE441"), CourseUtils.getCourse("MATH314")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE470"), CourseUtils.getCourse("MATH314")));
		requirements.add(new Prerequisite(CourseUtils.getCourse("CSCE477"), CourseUtils.getCourse("MATH314")));
		
		requirements.add(new SemesterCreditHourRequirements());
		
		requirements.addAll(degreeRequirements);
		
		return requirements;
	}
}
