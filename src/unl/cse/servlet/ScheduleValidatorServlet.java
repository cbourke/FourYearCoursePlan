package unl.cse.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import unl.cse.Degree;
import unl.cse.Schedule;
import unl.cse.validation.DegreeRequirements;
import unl.cse.validation.Requirement;
import unl.cse.validation.ScheduleValidatorResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ScheduleValidatorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(ScheduleValidatorServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		String jsonResponse = null;
		String jsonp_callback = null;
		String exceptionMsg = null;
		try {
			jsonp_callback = request.getParameter("callback");
			String schedule = request.getParameter("schedule");
			jsonResponse = process(schedule);
		} catch(Exception e) {
			log.error("Encountered an Exception: ", e);
			exceptionMsg = "The server experienced a general error and was unable to validate your schedule.";
		}
		
		response.setContentType("application/json");
		// Get the printwriter object from response to write the required json object to the output stream      
		PrintWriter out;
		try {
			out = response.getWriter();
			if(jsonp_callback != null) {
				out.print(jsonp_callback + "(");
			}
			if(jsonResponse == null) {
				out.print("{\"status\":\"error\",\"errorMessages\":[\""+exceptionMsg+"\"],\"warningMessages\":[]}");
			} else {
				out.print(jsonResponse);
			}
			if(jsonp_callback != null) {
				out.print(");");
			}
			out.flush();
		} catch (Exception e) {
			log.error("Encountered an IOException: ", e);
		}
	}
	
	private static String process(String jsonSchedule) {
		
		Schedule s = new Schedule(jsonSchedule);

		ScheduleValidatorResponse report = validate(s);
		
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(report);
	}

	private static ScheduleValidatorResponse validate(Schedule s) {
		List<String> errorMessages = new ArrayList<String>();
		List<String> warnMessages = new ArrayList<String>();

		for(Requirement r : DegreeRequirements.requirements) {
			errorMessages.addAll(r.validate(s));
		}
		if(s.getDegree() == Degree.COMPUTER_SCIENCE) {
			for(Requirement r : DegreeRequirements.csRequirements) {
				errorMessages.addAll(r.validate(s));
			}
		} else if(s.getDegree() == Degree.COMPUTER_ENGINEERING) {
			for(Requirement r : DegreeRequirements.ceRequirements) {
				errorMessages.addAll(r.validate(s));
			}
		} else {
			errorMessages.add("You must choose a degree program (Action, Options)");
		}
		
		return new ScheduleValidatorResponse(errorMessages, warnMessages);
	}
	
	public static void main(String args[]) {
		String sch = "{\"1138\":[\"CSCE155E\",\"MATH106\",\"PHYS211\",\"ACE5\",\"ENGR10\",\"PHIL106\"],\"1141\":[\"CSCE156\",\"CSCE235\",\"CSCE251\",\"MATH107\",\"PHYS212\"],\"1148\":[\"CSCE230\",\"MATH208\",\"NSCI003\",\"ELEC215\",\"ELEC235\",\"ENGR20\"],\"1151\":[\"CSCE236\",\"CSCE310\",\"MATH221\",\"ELEC216\",\"ELEC236\",\"JGEN200\"],\"1158\":[\"CSCE351\",\"CSCE361\",\"ELEC304\",\"ELEC316\",\"ACE6\",\"CSCEELC1\"],\"1161\":[\"CSCE335\",\"CSCE462\",\"MATH314\",\"ELEC305\",\"CSCEELC2\"],\"1168\":[\"CSCE340\",\"CSCEELC3\",\"CSCE488\",\"JGEN300\",\"ACE7\"],\"1171\":[\"CSCE489\",\"CSCEELC4\",\"CSCEELC5\",\"CSCEELC6\",\"ACE9\"],\"degreeTypeId\":2,\"termCodes\":[\"1138\",\"1141\",\"1148\",\"1151\",\"1158\",\"1161\",\"1168\",\"1171\"]}";

		System.out.println(process(sch));
	}
}
