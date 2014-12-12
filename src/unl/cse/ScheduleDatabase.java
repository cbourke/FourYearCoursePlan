package unl.cse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import unl.cse.entities.User;
import unl.cse.entities.UserPlan;
import unl.cse.ldap.LdapData;

public class ScheduleDatabase {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(ScheduleDatabase.class);

	/**
	 * Loads the UserPlan for the given user from the database.  If the user
	 * does not exist, a new record is inserted; if a UserPlan does not exist
	 * for the user, a new record is inserted.
	 * @param userMyUnlLogin
	 * @return
	 */
	public static UserPlan getOrCreateUserPlanJDBC(String userMyUnlLogin) {
		
		User u = ScheduleDatabase.getOrCreateUser(userMyUnlLogin);
		UserPlan up = ScheduleDatabase.getUserPlanJDBC(u);
		if(up == null) {
			log.info("Creating new UserPlan for user, "+u.getMyUnlLogin());
			Connection conn = null;
			try {
				conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO UserPlan (user_id,create_date,update_date) VALUES (?,?,?)");
				ps.setInt(1, u.getUserId());
				ps.setString(2, new DateTime().toString());
				ps.setString(3, new DateTime().toString());
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				log.error("SQLException: ", e);
				throw new RuntimeException(e);
			} finally {
				ConnectionFactory.closeConn(conn);
			}
			up = ScheduleDatabase.getUserPlanJDBC(u);
		}
		return up;
	}
	
	private static UserPlan getUserPlanJDBC(User u) {
		
		Connection conn = ConnectionFactory.getConnection();

		String query = "SELECT * FROM UserPlan up WHERE up.user_id = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserPlan up = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, u.getUserId());
			rs = ps.executeQuery();
			if(rs.next()) {
				up = new UserPlan(
						rs.getInt("user_plan_id"),
						u, 
						rs.getString("json_plan"),
						rs.getString("create_date"),
						rs.getString("update_date"));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConn(conn);
		}

		return up;
	}

	/**
	 * Persists the UserPlan to the database (by updating the json_plan and update_date fields only).
	 * Assumes that the record already exists.
	 * @param up
	 */
	public static void persist(UserPlan up) {
		
		Connection conn = ConnectionFactory.getConnection();

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("UPDATE UserPlan SET json_plan = ?, update_date = ? WHERE user_plan_id = ?");
			ps.setString(1, up.getJsonPlan());
			ps.setString(2, new DateTime().toString());
			ps.setInt(3, up.getUserPlanId());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConn(conn);
		}
	}
	
	private static void persistUserJDBC(User u) {
		Connection conn = ConnectionFactory.getConnection();
		String query = "INSERT INTO User (first_name,last_name,nuid,my_unl_login,cse_login) VALUES (?,?,?,?,?)";

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, u.getFirstName());
			ps.setString(2, u.getLastName());
			ps.setString(3, u.getNuid());
			ps.setString(4, u.getMyUnlLogin());
			ps.setString(5, u.getCseLogin());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConn(conn);
		}
	}
	
	/**
	 * Returns a list of users sorted according to last name/first name
	 * @return
	 */
	public static List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		
		Connection conn = ConnectionFactory.getConnection();

		String query = "SELECT * FROM User";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				users.add(new User(rs.getInt("user_id"),
						     rs.getString("nuid"),
						     rs.getString("my_unl_login"),
						     rs.getString("cse_login"),
						     rs.getString("last_name"),
						     rs.getString("first_name"),
						     rs.getBoolean("is_admin")));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConn(conn);
		}

		Collections.sort(users, new Comparator<User>() {
			@Override
			public int compare(User a, User b) {
				if(a.getLastName() != null && b.getLastName() != null) {
					if(a.getLastName().compareTo(b.getLastName()) == 0) {
						if(a.getFirstName() != null && b.getFirstName() != null) {
							return a.getFirstName().compareTo(b.getFirstName());
						} else {
							return a.getMyUnlLogin().compareTo(b.getMyUnlLogin());
						}
					} else {
						return a.getLastName().compareTo(b.getLastName());
					}
				} else {
					return a.getMyUnlLogin().compareTo(b.getMyUnlLogin());
				}
			}
		});
		return users;
	}
	
	public static User getOrCreateUser(String myUnlLogin) {
		User u = getUserJDBC(myUnlLogin);
		if(u == null) {
			//create the user, grab the first name, last name, and NUID from LDAP if possible:
			String firstName = null, lastName = null, nuid = null;
			Map<String, Set<String>> attr = LdapData.getUserData(myUnlLogin);
			Set<String> tmp;
			tmp = attr.get(LdapData.FIRST_NAME_ATTR);
			if(tmp != null) {
				firstName = tmp.iterator().next();
			}
			tmp = attr.get(LdapData.LAST_NAME_ATTR);
			if(tmp != null) {
				lastName = tmp.iterator().next();
			}
			tmp = attr.get(LdapData.NUID_ATTR);
			if(tmp != null) {
				nuid = tmp.iterator().next();
			}
			u = new User(null, nuid, myUnlLogin, null, lastName, firstName, false);
			//persist
			persistUserJDBC(u);
			//reload
			u = getUserJDBC(myUnlLogin);
		}
		return u;
	}
	
	/**
	 * Returns the User object of the user corresponding to the given MyUNL login, 
	 * <code>null</code> if the user does not exist.
	 * @param myUnlLogin
	 * @return
	 */
	private static User getUserJDBC(String myUnlLogin) {
		
		if(myUnlLogin == null) {
			return null;
		}
		
		Connection conn = ConnectionFactory.getConnection();

		String query = "SELECT * FROM User WHERE my_unl_login = ?";
		User u = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, myUnlLogin);
			rs = ps.executeQuery();
			if(rs.next()) {
				u = new User(rs.getInt("user_id"),
						     rs.getString("nuid"),
						     rs.getString("my_unl_login"),
						     rs.getString("cse_login"),
						     rs.getString("last_name"),
						     rs.getString("first_name"),
						     rs.getBoolean("is_admin"));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
			throw new RuntimeException(e);
		} finally {
			ConnectionFactory.closeConn(conn);
		}
		return u;
	}

}
