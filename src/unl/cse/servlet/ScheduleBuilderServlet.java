package unl.cse.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import unl.cse.Degree;
import unl.cse.Schedule;
import unl.cse.ScheduleBuilder;

public class ScheduleBuilderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(ScheduleBuilderServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		String jsonResponse = null;
		String jsonp_callback = null;

		try {
			jsonp_callback = request.getParameter("callback");
			Integer termCode = Integer.parseInt(request.getParameter("termCode"));
			Integer numLangYears = Integer.parseInt(request.getParameter("numLangYears"));
			String firstMath = request.getParameter("firstMath");
			Degree d = Degree.getDegreeById(Integer.parseInt(request.getParameter("degreeTypeId")));
			
			Schedule s = ScheduleBuilder.getSchedule(d, termCode, numLangYears, firstMath);
			
			jsonResponse = s.toJson();
			
		} catch(Exception e) {
			
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
				out.print("{}");
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
}
