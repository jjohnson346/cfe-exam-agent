package financial.fraud.cfe.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * CFEExamDBUtility encapsulates logic for connecting to and loading a database designed to 
 * store the data contained in the files for exam questions.  This class is not currently being 
 * used as the approach for storing the data in a db was aborted in favor of storing/accessing the 
 * exam questions from the files directly.
 * 
 * @author jjohnson346
 *
 */
public class CFEExamDBUtility {

	private static final String URL = "jdbc:mysql://localhost:3306/cfe_exam_db";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "password";
	private static final String DRIVERNAME = "com.mysql.jdbc.Driver";

	/**
	 * returns a connection to the local database, as given by the URL constant.
	 * 
	 * @return a db connection to the cfe exam db.
	 */
	@SuppressWarnings({"finally"})
	public static Connection getConnection() {
		Connection connection = null;
		try {
			//Connect to the database.
			Class.forName(DRIVERNAME).newInstance();
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch(ClassNotFoundException cnfex) {
			System.err.println("Failed to load driver.");
			cnfex.printStackTrace();
		} catch(SQLException sqlex) {
			System.err.println("Encountered a sql exception.");
			sqlex.printStackTrace();
		} finally {
			return connection;
		}
	}

	/**
	* inserts a record into the cfe_question table with the given input parameters.
	*/
	public static void insertCFEExamQuestion(String name, String section, String stem, int correctResponse, String explanation) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			Class.forName(DRIVERNAME);
			connection = DriverManager.getConnection(URL, USERNAME, "password");
			callableStatement = connection.prepareCall("{call cfe_question_insert_sp(?,?,?,?,?)}");
			callableStatement.setString(1, name);
			callableStatement.setString(2, section);
			callableStatement.setString(3, stem);
			callableStatement.setInt(4, correctResponse);
			callableStatement.setString(5, explanation);
			callableStatement.execute();
		} catch(ClassNotFoundException cnfex) {
			System.err.println("Failed to load driver.");
			cnfex.printStackTrace();
		} catch(SQLException sqlex) {
			System.err.println("Encountered a sql exception.");
			sqlex.printStackTrace();
		} finally {
	      	try {
				callableStatement.close();
	      		connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* inserts a record into the cfe_question_option table with the given input parameters.
	*/
	public static void insertCFEExamQuestionOption(String questionName, int optionId, String optionText) {
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			Class.forName(DRIVERNAME);
			connection = DriverManager.getConnection(URL, USERNAME, "password");
			callableStatement = connection.prepareCall("{call cfe_question_option_insert_sp(?,?,?)}");
			callableStatement.setString(1, questionName);
			callableStatement.setInt(2, optionId);
			callableStatement.setString(3, optionText);
			callableStatement.execute();
		} catch(ClassNotFoundException cnfex) {
			System.err.println("Failed to load driver.");
			cnfex.printStackTrace();
		} catch(SQLException sqlex) {
			System.err.println("Encountered a sql exception.");
			sqlex.printStackTrace();
		} finally {
	      	try {
				callableStatement.close();
	      		connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * runs a simple test by inserting a single row into the cfe exam db by calling the 
	 * inserCFEExamQuestionOption() method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//CFEExamDataManager.insertCFEExamQuestion("bankruptcy fraud 2", "bankruptcy fraud", "Is claiming bankuptcy legal?", 0, "Of course it is...");
		CFEExamDBUtility.insertCFEExamQuestionOption("bankruptcy fraud 2", 0, "Yes");
	}

}
