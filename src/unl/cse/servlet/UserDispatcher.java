package unl.cse.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import unl.cse.ScheduleDatabase;
import unl.cse.entities.User;
import unl.cse.entities.UserPlan;

public class UserDispatcher extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(UserDispatcher.class);

	private static final String ADMIN_URL = "/secure/admin/index.jsp";
	private static final String NEW_USER_URL = "/secure/setup.html";
	private static final String USER_URL = "/secure/app.html?loadRemote=true";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			String url;
			String userMyUnlLogin = request.getRemoteUser(); //null or myred login
			if(userMyUnlLogin == null) {
				log.info("unauthenticated access attempted");
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "You must be logged in to access this area");
				return;
			}
			User u = ScheduleDatabase.getOrCreateUser(userMyUnlLogin);
			if(u.isAdmin()) {
				url = ADMIN_URL;
			} else {
				UserPlan up = ScheduleDatabase.getOrCreateUserPlanJDBC(u.getMyUnlLogin());
				if(up.getJsonPlan() == null) {
					url = NEW_USER_URL;
				} else {
					url = USER_URL;
				}
			}
			request.getSession().setAttribute("user", u);
			response.sendRedirect(request.getContextPath() + url);
			return;
			
		} catch (Exception e) {
			log.error("Encountered general exception: ", e);
			throw new RuntimeException(e);
		}

	}

}
