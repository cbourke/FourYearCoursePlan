package unl.cse.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import unl.cse.ScheduleDatabase;
import unl.cse.entities.User;

public class StudentSearchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(StudentSearchServlet.class);
	
	private static List<User> USERS = ScheduleDatabase.getAllUsers();

	public static synchronized void loadUsers() {
		USERS =  ScheduleDatabase.getAllUsers();
	}
	
	private static List<User> searchUsers(String term, int limit) {
		List<User> results = new ArrayList<User>(limit);
		String terms[] = term.split("\\s+");
		for(int i=0; i<terms.length; i++) {
			terms[i] = terms[i].toLowerCase();
		}
		for(int i=0; i<USERS.size() && results.size() < limit; i++) {
			boolean noMatch = true;
			for(int j=0; j<terms.length && noMatch; j++) {
				User u = USERS.get(i);
				if( (u.getLastName() != null && u.getLastName().toLowerCase().contains(terms[j])) ||
					(u.getFirstName() != null && u.getFirstName().toLowerCase().contains(terms[j])) ||
					(u.getMyUnlLogin() != null && u.getMyUnlLogin().contains(terms[j]))) {
					noMatch = true;
					results.add(u);
				}
			}
		}
		return results;
	}
	
	private static String searchUsersJson(String term, int limit) {
		List<User> users = searchUsers(term, limit);
		JsonArray ja = new JsonArray();
		for(User u : users) {
			JsonObject jo = new JsonObject();
			jo.addProperty("label", u.getMyUnlLogin() + ((u.getName() == null) ? "" : " - " + u.getName()));
			jo.addProperty("value", u.getMyUnlLogin());
			ja.add(jo);
		}
		return ja.toString();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			String term = request.getParameter("term");
			int limit = Integer.parseInt(request.getParameter("limit"));
			String jsonp_callback = request.getParameter("callback");
			
			String users = searchUsersJson(term, limit);

			StringBuilder sb = new StringBuilder(users);
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
	
	public static void main(String args[]) {
		String users = searchUsersJson("ourke", 3);
		System.out.println(users);
		
	}
}
