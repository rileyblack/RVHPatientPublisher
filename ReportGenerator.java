import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReportGenerator {
	
	// Defining database connection variables
	static Connection connection = null;
	static String databaseName = "";
	static String url = "jdbc:mysql://localhost:3306/project";
	static String username = "root";
	static String password = "password";
	
	// Defining report publishing variables
	static String publishLocation = "C:\\Users\\Hospital\\Patients\\Reports\\";
	
	// Defining main procedure
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		// Extracting user-requested patient number passed through argument
		String patientNumber = args[0];
		
		// Creating report file
	    File filePath = new File(publishLocation + "patient" + patientNumber + "report.txt");
	    try {
	    	filePath.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Initializing JDBC
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		connection = DriverManager.getConnection(url, username, password);
		
		// Creating SQL query for header portion of report 
	    String query = "SELECT p.pid AS PatientNumber, p.name AS PatientName, c.name AS CareCentreName, n.name AS NameOfNurseInCharge\r\n" + 
	    		"FROM patients_t p, care_centres_t c, nurses_t n\r\n" + 
	    		"WHERE p.care_centre_id = c.cid\r\n" + 
	    		"AND c.nurse_charge_id = n.nid\r\n" + 
	    		"AND p.pid = " + patientNumber + ";";

	    // Creating JDBC statement
	    Statement jdbcStatement = connection.createStatement();
	      
	    // Executing SQL query via JDBC statement
	    ResultSet result = jdbcStatement.executeQuery(query);
	    
	    try {	
	    	// Preparing to write to report file
		    FileWriter writer = new FileWriter(publishLocation + "patient" + patientNumber + "report.txt");
		    
		    // Preparing query result for header portion of report 
	    	result.next();
	    	
		    // Writing header portion of report to file
		    writer.write("Patient Number: " + String.valueOf(result.getInt("PatientNumber")) + "\n\nPatient Name: " + result.getString("PatientName") + "\n\nCare Centre Name: " + result.getString("CareCentreName") + "\n\nName of Nurse-in-Charge: " + result.getString("NameOfNurseInCharge") + "\n\n");
		    
		    // Creating SQL query for list portion of report 
		    query = "SELECT tid AS TreatmentID, treatment_name AS TreatmentName, physician_id AS PhysicianID, date AS Date\r\n" + 
		    		"FROM treatments_t\r\n" + 
		    		"WHERE patient_id = " + patientNumber + "\n" + 
		    		"ORDER BY tid;";
		    
		    // Executing SQL query via JDBC statement
		    result = jdbcStatement.executeQuery(query);
		    
		    // Formatting list portion of report
		    writer.write("\nTreatment ID	Treatment Name		Physician ID	Date\n_______________________________________________________________\n\n");
		    
		    // Writing list portion of report to file
		    while (result.next()) {	    	
			    writer.write(String.valueOf(result.getInt("TreatmentID")) + "		" + result.getString("TreatmentName") + "		" + result.getString("PhysicianID") + "		" + result.getString("Date") + "\n");
		    }
		    
		    // Concluding writing to file
	        writer.close();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    jdbcStatement.close();
	}
}
