package unl.cse.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import unl.cse.Schedule;
import unl.cse.ScheduleDatabase;
import unl.cse.entities.User;
import unl.cse.pdf.PlanPdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

public class PlanPdfServlet extends HttpServlet {

    private static final long serialVersionUID = 4262544639420765610L;
    private static final String FILENAME = "schedule.pdf";

	private static org.apache.log4j.Logger log = Logger.getLogger(PlanPdfServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

		User u = null;
		String userMyUnlLogin = request.getParameter("user");
		if(userMyUnlLogin == null) {
			userMyUnlLogin = request.getRemoteUser();
		}
		if(userMyUnlLogin != null) {
			u = ScheduleDatabase.getOrCreateUser(userMyUnlLogin);
		}
		String jsonSchedule = request.getParameter("schedule");
		Schedule schedule = new Schedule(jsonSchedule);

		response.setHeader("Content-Disposition", "attachment; filename=\"" + FILENAME + "\"");
        response.setContentType("application/pdf");

        try {
	        Document document = new Document();
	        PdfWriter.getInstance(document, response.getOutputStream());
	        document.open();
	        PlanPdf.createSchedulePdf(document, schedule, u);
	        document.close();
			
        } catch (Exception e) {
        	log.error("Encountered Exception", e);
        }
    }
 
}
