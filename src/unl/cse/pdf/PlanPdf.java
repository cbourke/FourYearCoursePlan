package unl.cse.pdf;

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import unl.cse.Course;
import unl.cse.CourseUtils;
import unl.cse.Schedule;
import unl.cse.entities.User;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PlanPdf {

	private static org.apache.log4j.Logger log = Logger.getLogger(PlanPdf.class);

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy HH:mm:ss");

	public static void createSchedulePdf(Document d, Schedule s, User u) throws DocumentException {
		
		d.add(new Paragraph("Four Year Course Plan", FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD)));
		d.add(new Paragraph("Department of Computer Science & Engineering", FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD)));

		if(u != null) {
	        d.add(new Paragraph("Schedule for: " + u.getName() + " ("+u.getNuid() +")"));
		}
		
        d.add(new Paragraph("Degree Plan: " + s.getDegree()));
        d.add(new Paragraph("Created on " + dateTimeFormatter.print(new DateTime())));

        int grandTotalCreditHours = 0;
        
//        if(!s.getTransferCourses().isEmpty()) {
//        	int total = 0;
//    		List l = new List();
//    		l.setSymbolIndent(36);
//    		for(Course c : s.getTransferCourses()) {
//    			ListItem item = new ListItem(c.getSubject() + " " + c.getNumber() + " - " + c.getTitle() + " (" + (c.getCreditHours() == null ? "?" : c.getCreditHours())  + ")");
//    			total += c.getCreditHours() == null ? 0 : c.getCreditHours();
//    			l.add(item);
//    		}
//    		grandTotalCreditHours += total;
//    		d.add(new Paragraph("Transfer Credits ("+total+")", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
//        	d.add(l);
//        	
//        }
        
        for(Integer termCode : s.getTermToCoursesMap().keySet()) {
        	if(!s.getTermToCoursesMap().get(termCode).isEmpty()) {
        		int total = 0;
        		List l = new List();
        		l.setSymbolIndent(36);
        		for(Course c : s.getTermToCoursesMap().get(termCode)) {
        			ListItem item = new ListItem(c.getSubject() + " " + c.getNumber() + " - " + c.getTitle() + " (" + (c.getCreditHours() == null ? "?" : c.getCreditHours())  + ")");
        			total += c.getCreditHours() == null ? 0 : c.getCreditHours();
        			l.add(item);
        		}
        		grandTotalCreditHours += total;
        		d.add(new Paragraph(CourseUtils.termCodeToString(termCode) + " ("+total+")", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
            	d.add(l);
        	}
        }
        
        d.add(new Paragraph("Total Credit Hours: " + grandTotalCreditHours));

        return;
	}
	
	public static void main(String args[]) {
		
		try {
			String sch = "{\"1138\":[\"CSCE155E\",\"MATH106\",\"PHYS211\",\"ACE5\",\"ENGR10\",\"PHIL106\"],\"1141\":[\"CSCE156\",\"CSCE235\",\"CSCE251\",\"MATH107\",\"PHYS212\"],\"1148\":[\"CSCE230\",\"MATH208\",\"NSCI003\",\"ELEC215\",\"ELEC235\",\"ENGR20\"],\"1151\":[\"CSCE236\",\"CSCE310\",\"MATH221\",\"ELEC216\",\"ELEC236\",\"JGEN200\"],\"1158\":[\"CSCE351\",\"CSCE361\",\"ELEC304\",\"ELEC316\",\"ACE6\",\"CSCEELC1\"],\"1161\":[\"CSCE335\",\"CSCE462\",\"MATH314\",\"ELEC305\",\"CSCEELC2\"],\"1168\":[\"CSCE340\",\"CSCEELC3\",\"CSCE488\",\"JGEN300\",\"ACE7\"],\"1171\":[\"CSCE489\",\"CSCEELC4\",\"CSCEELC5\",\"ACE9\"],\"degreeTypeId\":2,\"termCodes\":[\"1138\",\"1141\",\"1148\",\"1151\",\"1158\",\"1161\",\"1168\",\"1171\"]}";
			Schedule s = new Schedule(sch);
			//Document document = createSchedulePdf(s);

	        Document document = new Document();
	        // step 2
	        PdfWriter.getInstance(document, new FileOutputStream("WebContent/data/test.pdf"));
	        // step 3
	        document.open();
	        // step 4
	        createSchedulePdf(document, s, null);
	        // step 5
	        document.close();
			
		} catch (Exception e) {
			log.error("Encountered Exception: ", e);
		}
	}
}
