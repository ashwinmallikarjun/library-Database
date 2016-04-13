import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;


public class LMS {
	
	static HashMap<Integer,String> branchList = new HashMap<Integer,String>();
	static HashMap<Integer,String> bookList = new HashMap<Integer,String>();
	static Connection conn;
	static PreparedStatement pstmt;
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		databaseConnection();
		System.out.println("Welcome to the GCIT Library Management System.");
		intialInputPage();
		
		
	}

	public static void databaseConnection() throws ClassNotFoundException, SQLException {
		
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root","0706");
				
	}

	public static void intialInputPage() throws SQLException {
		
		boolean userTypeCheck = true, userNumCheck = true;
		int userType = 0;
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("\nWhich category of a user are you?");		
		System.out.println("1)Librarian. \n2)Administrator. \n3)Borrower.");
		@SuppressWarnings("resource")
		Scanner userIO = new Scanner(System.in);
		
		
		while(userNumCheck){
		try{
			userType = userIO.nextInt();
			userNumCheck = false;
		} 
		catch (InputMismatchException e){
			System.out.println("Please enter a valid number.");
			System.out.println("P.S : Enter number present next to the option.");
			userIO.next();
			}
		
		}
		
		while (userTypeCheck){
			
		switch(userType){
		
		case 1: userTypeCheck =false; 
				Librarian.libMethod();
				break;
		
		case 2: userTypeCheck =false;
				Admin.adminMethod();
				break;
		
		case 3: userTypeCheck =false; 
				Borrower.brrMethod();
				break;
		
		default:System.out.println("Please enter a valid user category.");
				userType = userIO.nextInt();
				
			}
		}
	}
}
