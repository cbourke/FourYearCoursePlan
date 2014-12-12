package unl.cse.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import unl.cse.Course;
import unl.cse.CourseUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class CourseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(CourseServlet.class);
		
	private static String coursesByUIGroupJson;
	
	static {
		initialize();
	}
	
	public static synchronized void initialize() {
		log.info("CourseServlet is (re)-initializing");
		Map<String, List<Course>> coursesByUiGroup = new HashMap<String, List<Course>>();
		
		Set<Course> courses = CourseUtils.getCoursesFromDatabase();
		for(Course c : courses) {
			List<Course> s = coursesByUiGroup.get(c.getUiGroup());
			if(s == null) {
				s = new ArrayList<Course>();
				coursesByUiGroup.put(c.getUiGroup(), s);
			}
			s.add(c);
		}

		//sort each group
		for(String s : coursesByUiGroup.keySet()) {
			Collections.sort(coursesByUiGroup.get(s), CourseUtils.byNumber);
		}
		
		//"sort" each group
		List<String> uiGroups = new ArrayList<String>(Arrays.asList(
				"Core CSCE Courses", 
				"MATH Courses",
				"ELEC Courses", 
				"CSCE - Artificial Intelligence",
				"CSCE - Foundations",
				"CSCE - Informatics",
				"CSCE - Networking and HPC",
				"CSCE - Software Engineering",
				"CSCE - Other Electives", 
				"Misc",
				"Placeholders"));
		for(String s : coursesByUiGroup.keySet()) {
			if(!uiGroups.contains(s)) {
				log.error("misplaced UI Group: "+s);
			}
		}
		
		//now build the json:		
		Gson gson = new GsonBuilder().serializeNulls().create();
		JsonObject response = new JsonObject();
		for(String s : uiGroups) {
			response.add(s, gson.toJsonTree(coursesByUiGroup.get(s)));
		}
		coursesByUIGroupJson = response.toString();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		
		//support optional jsonp:
		String jsonp_callback = request.getParameter("callback");
		
		response.setContentType("application/json");
		// Get the printwriter object from response to write the required json object to the output stream      
		PrintWriter out;
		try {
			out = response.getWriter();
			if(jsonp_callback != null) {
				out.print(jsonp_callback + "(");
			}
			out.print(coursesByUIGroupJson);
			if(jsonp_callback != null) {
				out.print(");");
			}
			out.flush();
		} catch (Exception e) {
			log.error("Encountered an IOException: ", e);
		}
	}
	
	
}
