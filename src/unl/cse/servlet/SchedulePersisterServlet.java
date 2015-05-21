package unl.cse.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import unl.cse.ScheduleDatabase;
import unl.cse.entities.User;
import unl.cse.entities.UserPlan;

public class SchedulePersisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(SchedulePersisterServlet.class);
	
	private static final String JSON_SUCCESS_RESPONSE = "{\"status\":\"success\",\"message\":\"Schedule saved to database.\"}";
	//private static final String JSON_FAILURE_RESPONSE = "{\"status\":\"error\",\"message\":\"Unable to save your schedule.\"}";

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		try {
			String jsonp_callback = request.getParameter("callback");
			String jsonSchedule = request.getParameter("schedule");
			String userMyUnlLogin = request.getRemoteUser(); //null or myred login
			String user = request.getParameter("user");
			String userOfSchedule = null;
	
			if(userMyUnlLogin == null) {
				//unauthenticated user, give a 404
				log.info("unauthenticated access attempted");
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "You must be logged in to save schedules to the database");
				return;
			} else if(user != null) {
				//user is attempting to save a schedule of a different user, they must be an admin to do this...
				User adminUser = ScheduleDatabase.getOrCreateUser(userMyUnlLogin);
				if(adminUser == null || !adminUser.isAdmin()) {
					log.info("unauthenticated admin access attempted for user " + userMyUnlLogin + " (attempted to save plan for " + user + ")");
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "You must be logged in and be an advisor/admin to save schedules from the database for a different user");
					return;
				}
				log.info("admin access made for user " + userMyUnlLogin + " (saved plan for user " + user + ")");
				userOfSchedule = user;
			} else {
				//logged in, attempting to get their own schedule
				log.info("regular user access made for user " + userMyUnlLogin);
				userOfSchedule = userMyUnlLogin;
			}
	
			//get the schedule of userOfSchedule
			UserPlan up = ScheduleDatabase.getOrCreateUserPlanJDBC(userOfSchedule);
			up.setJsonPlan(jsonSchedule);
			ScheduleDatabase.persist(up);
			
			StringBuilder sb = new StringBuilder(JSON_SUCCESS_RESPONSE);
			if(jsonp_callback != null) {
				sb.insert(0, jsonp_callback+"(");
				sb.append(");");
			}
	
			response.setContentType("application/json");
			// Get the printwriter object from response to write the required json object to the output stream      
			PrintWriter out = response.getWriter();
			out.print(sb.toString());
			out.flush();
		} catch (Exception e) {
			log.error("Encountered general exception: ", e);
			throw new RuntimeException(e);
		}
		
	}
	
}
