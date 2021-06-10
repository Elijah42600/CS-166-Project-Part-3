/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.sql.Date;
//import java.util.Date;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Doctor");
				System.out.println("2. Add Patient");
				System.out.println("3. Add Appointment");
				System.out.println("4. Make an Appointment");
				System.out.println("5. List appointments of a given doctor");
				System.out.println("6. List all available appointments of a given department");
				System.out.println("7. List total number of different types of appointments per doctor in descending order");
				System.out.println("8. Find total number of patients per doctor with a given status");
				System.out.println("9. < EXIT");
				
				switch (readChoice()){
					case 1: AddDoctor(esql); break;
					case 2: AddPatient(esql); break;
					case 3: AddAppointment(esql); break;
					case 4: MakeAppointment(esql); break;
					case 5: ListAppointmentsOfDoctor(esql); break;
					case 6: ListAvailableAppointmentsOfDepartment(esql); break;
					case 7: ListStatusNumberOfAppointmentsPerDoctor(esql); break;
					case 8: FindPatientsCountWithStatus(esql); break;
					case 9: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddDoctor(DBproject esql) {//1
          try{
            int doctor_id1, did1;
            String name1, specialty1;
            String query1 = "SELECT MAX(d.doctor_id) FROM Doctor d";
            List<List<String>> rs = esql.executeQueryAndReturnResult(query1);

            doctor_id1 = Integer.parseInt(rs.get(0).get(0));

            doctor_id1 += 1;
            //System.out.println("Max ID: " + doctor_id1);
            if (doctor_id1 <= 0) {
              doctor_id1 = 0;
            }

            System.out.println("Input Doctor Name: ");
            name1 = in.readLine();
            System.out.println("Input department ID: ");
            did1 = Integer.parseInt(in.readLine());
            System.out.println("Input specialty: ");
            specialty1 = in.readLine();

            //error handling when did does not exist. - Handled by sql.
            String query = "INSERT INTO Doctor(doctor_ID, name, specialty, did) VALUES(?, ?, ?, ?);\n";
            PreparedStatement preparedStmt = esql._connection.prepareStatement(query);
            preparedStmt.setInt (1, doctor_id1);
            preparedStmt.setString (2, name1);
            preparedStmt.setString (3, specialty1);
            preparedStmt.setInt (4, did1);
            preparedStmt.execute();
            
            System.out.println("Doctor ID: " + doctor_id1);
          }catch(Exception e){
            System.err.println (e.getMessage());
          }
	}

	public static void AddPatient(DBproject esql) {//2
          try{
            int patient_id1, age1;
            int num_appts1 = 0;
            String name1, address1, gender1;
            String query1 = "SELECT MAX(p.patient_id) FROM Patient p";
            List<List<String>> rs = esql.executeQueryAndReturnResult(query1);

            patient_id1 = Integer.parseInt(rs.get(0).get(0));

            patient_id1 += 1;
            //System.out.println("Max ID: " + patient_id1);
            if (patient_id1 <= 0) {
              patient_id1 = 0;
            }

            System.out.println("Input Patient Name: ");
            name1 = in.readLine();
            System.out.println("Input Patient Age: ");
            age1 = Integer.parseInt(in.readLine());
            System.out.println("Input Patient Gender (F or M): ");
            gender1 = in.readLine();
            System.out.println("Input Patient Address: ");
            address1 = in.readLine();


            String query = "INSERT INTO Patient(patient_ID, name, gtype, age, address, number_of_appts) VALUES(?, ?, ?, ?, ?, ?);\n";
            PreparedStatement preparedStmt = esql._connection.prepareStatement(query);
            preparedStmt.setInt (1, patient_id1);
            preparedStmt.setString (2, name1);
            preparedStmt.setString (3, gender1);
            preparedStmt.setInt (4, age1);
            preparedStmt.setString(5, address1);
            preparedStmt.setInt(6, num_appts1);
            preparedStmt.execute();

            System.out.println("Patient ID: " + patient_id1);
          }catch(Exception e){
            System.err.println (e.getMessage());
          }
	}

	public static void AddAppointment(DBproject esql) {//3
          try{
            int appnt_id1;
            java.sql.Date adate1;
            String status1, time_slot1, stringDate;
            String query1 = "SELECT MAX(a.appnt_ID) FROM Appointment a";
            List<List<String>> rs = esql.executeQueryAndReturnResult(query1);

            appnt_id1 = Integer.parseInt(rs.get(0).get(0));

            appnt_id1 += 1;
            System.out.println("Max ID: " + appnt_id1);
            if (appnt_id1 <= 0) {
              appnt_id1 = 0;
            }

            System.out.println("Input Appointment Date (YYYY/MM/DD): ");
            stringDate = in.readLine();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            java.util.Date date1 = sdf1.parse(stringDate);
            adate1 = new java.sql.Date(date1.getTime());
            System.out.println("Input Appointment Time Slot (HH:MM-HH:MM): ");
            time_slot1 = in.readLine();
            System.out.println("Input Appointment Status (PA, AC, AV, or WL): ");
            status1 = in.readLine();


            String query = "INSERT INTO Appointment (appnt_ID, adate, time_slot, status) VALUES(?, ?, ?, ?);\n";
            PreparedStatement preparedStmt = esql._connection.prepareStatement(query);
            preparedStmt.setInt (1, appnt_id1);
            preparedStmt.setDate (2, adate1);
            preparedStmt.setString (3, time_slot1);
            preparedStmt.setString(4, status1);
            preparedStmt.execute();

            System.out.println("Appointment ID: " + appnt_id1);
          }catch(Exception e){
            System.err.println (e.getMessage());
          }
        }


	public static void MakeAppointment(DBproject esql) {//4
	  // Given a patient, a doctor and an appointment of the doctor that s/he wants to take, add an appointment to the DB
	  try{
            String patient_name1, patient_gender1, address1;
            int patient_age1 = -1, patient_id1 = -1, total = 0, appnt_id1 = -1, doctor_id1 = -1;
            
            System.out.println("Input Patient Name: ");
            patient_name1 = in.readLine();
            System.out.println("Input Patient Gender (F or M): ");
            patient_gender1 = in.readLine();
            System.out.println("Input Patient Age: ");
            patient_age1 = Integer.parseInt(in.readLine());
            System.out.println("Input Patient Address: ");
            address1 = in.readLine();

            String query = "SELECT p.patient_ID FROM Patient p WHERE p.name = ? AND p.gtype = ? AND p.age = ? AND p.address = ?;\n";
            PreparedStatement preparedStmt = esql._connection.prepareStatement(query);
            preparedStmt.setString(1, patient_name1);
            preparedStmt.setString(2, patient_gender1);
            preparedStmt.setInt(3, patient_age1);
            preparedStmt.setString(4, address1);
            ResultSet rs1 = preparedStmt.executeQuery();
            if (rs1.next() ) {
              patient_id1 = rs1.getInt("patient_ID");
            } else {
              patient_id1 = -1;
            }


            //List<List<String>> rs = esql.executeQueryAndReturnResult(query);
            //patient_id1 = Integer.parseInt(rs.get(0).get(0));
            if (patient_id1 == -1) { //Create Patient
              System.out.println("Patient does not exist. Creating patient");
              query = "SELECT MAX(p.patient_id) FROM Patient p";
              List<List<String>> rs = esql.executeQueryAndReturnResult(query);

              patient_id1 = Integer.parseInt(rs.get(0).get(0));

              patient_id1 += 1;
              //System.out.println("Max ID: " + patient_id1);
              if (patient_id1 <= 0) {
                patient_id1 = 0;
              }

              query = "INSERT INTO Patient(patient_ID, name, gtype, age, address, number_of_appts) VALUES(?, ?, ?, ?, ?, ?);\n";
              preparedStmt = esql._connection.prepareStatement(query);
              preparedStmt.setInt (1, patient_id1);
              preparedStmt.setString (2, patient_name1);
              preparedStmt.setString (3, patient_gender1);
              preparedStmt.setInt (4, patient_age1);
              preparedStmt.setString(5, address1);
              preparedStmt.setInt(6, 0);
              preparedStmt.execute();
              //Patient Created.
            }
            //Check if doctor ID and appointment ID exist. Also increment number_of_appts
            System.out.println("Input Doctor ID: ");
            doctor_id1 = Integer.parseInt(in.readLine());
            query = "SELECT COUNT(*) AS total FROM Doctor d WHERE d.doctor_ID = ?;\n";
            preparedStmt = esql._connection.prepareStatement(query);
            preparedStmt.setInt(1, doctor_id1);
            rs1 = preparedStmt.executeQuery();
            rs1.next();
            total = rs1.getInt("total");
            if (total < 1) {
              System.out.println("Doctor does not exist. Returning to menu.");
            }
            else { //Doctor and patient exist
              System.out.println("Input Appointment ID: ");
              appnt_id1 = Integer.parseInt(in.readLine());
              query = "SELECT COUNT(*) AS total2 FROM has_appointment ha WHERE ha.appt_id = ? AND ha.doctor_id = ?;\n";
              preparedStmt = esql._connection.prepareStatement(query);
              preparedStmt.setInt(1, appnt_id1);
              preparedStmt.setInt(2, doctor_id1);
              rs1 = preparedStmt.executeQuery();
              rs1.next();
              total = rs1.getInt("total2");
              if (total < 1) {
                System.out.println("Error. Appointment ID is not associated with Doctor ID");
              }
              else { //Appointment ID with matching doctor ID exists.
                query = "SELECT a.status FROM Appointment a WHERE a.appnt_ID = ?;\n";
                preparedStmt = esql._connection.prepareStatement(query);
                preparedStmt.setInt(1, appnt_id1);
                rs1 = preparedStmt.executeQuery();
                rs1.next();
                String stat1 = rs1.getString("status");
                if (stat1 == "AV") { //Update Status and increment appnt_num
                  query = "UPDATE Appointment SET status = AC WHERE appnt_ID = ?;\n";
                  preparedStmt = esql._connection.prepareStatement(query);
                  preparedStmt.setInt(1, appnt_id1);
                  preparedStmt.execute();
                  query = "UPDATE Patient SET number_of_appts = number_of_appts + 1 WHERE patient_ID = ?;\n";
                  preparedStmt = esql._connection.prepareStatement(query);
                  preparedStmt.setInt(1, patient_id1);
                  preparedStmt.execute();
                } else if (stat1 == "AC") { //Update Status and increment appnt_num
                  query = "UPDATE Appointment SET status = WL WHERE appnt_ID = ?;\n";
                  preparedStmt = esql._connection.prepareStatement(query);
                  preparedStmt.setInt(1, appnt_id1);
                  preparedStmt.execute();
                  query = "UPDATE Patient SET number_of_appts = number_of_appts + 1 WHERE patient_ID = ?;\n";
                  preparedStmt = esql._connection.prepareStatement(query);
                  preparedStmt.setInt(1, patient_id1);
                  preparedStmt.execute();
                }
                
              }
            }
            

          }catch(Exception e){
            System.err.println (e.getMessage());
          }
	}

	public static void ListAppointmentsOfDoctor(DBproject esql) {//5
	  // For a doctor ID and a date range, find the list of active and available appointments of the doctor
          try{
            int doctor_id1;
            String stringDate;
            java.sql.Date start_date1, end_date1;
            //String query1 = "SELECT MAX(d.doctor_id) FROM Doctor d";
            //List<List<String>> rs = esql.executeQueryAndReturnResult(query1);

            //doctor_id1 = Integer.parseInt(rs.get(0).get(0));

            System.out.println("Input Doctor ID: ");
            doctor_id1 = Integer.parseInt(in.readLine());
            System.out.println("Input Start Date (YYYY/MM/DD): ");
            stringDate = in.readLine();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            java.util.Date date1 = sdf1.parse(stringDate);
            start_date1 = new java.sql.Date(date1.getTime());
            System.out.println("Input End Date (YYYY/MM/DD): ");
            stringDate = in.readLine();
            date1 = sdf1.parse(stringDate);
            end_date1 = new java.sql.Date(date1.getTime());

            //Initial Input Read In. Write Query
            String query = "SELECT a.appnt_ID, a.adate, a.time_slot, a.status FROM Appointment a INNER JOIN has_appointment ha ON a.appnt_id = ha.appt_id WHERE a.adate > ? AND a.adate < ? AND (a.status = 'AV' OR a.status = 'AC') AND ha.doctor_id = ?;\n";
            PreparedStatement preparedStmt = esql._connection.prepareStatement(query);
            preparedStmt.setDate (1, start_date1);
            preparedStmt.setDate (2, end_date1);
            preparedStmt.setInt (3, doctor_id1);
            ResultSet rs = preparedStmt.executeQuery();

            while(rs.next()) {
              System.out.println("appnt_ID: " + rs.getInt("appnt_ID") + ", date: " + rs.getDate("adate") + ", time_slot: " + rs.getString("time_slot") + ", status: " + rs.getString("status"));
            }
          }catch(Exception e){
            System.err.println (e.getMessage());
          }
	}

	public static void ListAvailableAppointmentsOfDepartment(DBproject esql) {//6
	  // For a department name and a specific date, find the list of available appointments of the department
	  try{
            String department_name1, stringDate;
            java.sql.Date adate1;
            String query = "SELECT a.appnt_ID, a.adate, a.time_slot, a.status FROM Appointment a INNER JOIN has_appointment ha ON a.appnt_id = ha.appt_id INNER JOIN Doctor d ON ha.doctor_id = d.doctor_ID INNER JOIN Department de ON d.did = de.dept_ID WHERE a.adate = ? AND de.name = ? AND a.status = 'AV'";

            System.out.println("Input Department Name: ");
            department_name1 = in.readLine();
            System.out.println("Input Date (YYYY/MM/DD): ");
            stringDate = in.readLine();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            java.util.Date date1 = sdf1.parse(stringDate);
            adate1 = new java.sql.Date(date1.getTime());
            System.out.println("FIXME");
            PreparedStatement preparedStmt = esql._connection.prepareStatement(query);
            preparedStmt.setString (2, department_name1);
            preparedStmt.setDate (1, adate1);
            ResultSet rs = preparedStmt.executeQuery();

            while (rs.next() ) {
              System.out.println("appnt_ID: " + rs.getInt("appnt_ID") + ", date: " + rs.getDate("adate") + ", time_slot: " + rs.getString("time_slot") + ", status: " + rs.getString("status"));
            }
          }catch(Exception e){
            System.err.println (e.getMessage());
          }
	}

	public static void ListStatusNumberOfAppointmentsPerDoctor(DBproject esql) {//7
		// Count number of different types of appointments per doctors and list them in descending order
	}

	
	public static void FindPatientsCountWithStatus(DBproject esql) {//8
		// Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that number per doctor.
	}
}
