import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;


public class Borrower extends LMS {

	static Scanner brrIO = new Scanner(System.in);
	static int brrCard = 0;
	static ResultSet brrRS;
	
	public static void brrMethod() throws SQLException{
		boolean brrCardEntry = true;
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("Enter your Card Number:");
		brrCard = brrIO.nextInt();
		
		//Query needs to be written here to borrower's card no. validity.*****************************************************
		pstmt = conn.prepareStatement("select cardNo from tbl_borrower;");
		brrRS = pstmt.executeQuery();
		
		while(brrRS.next()){
			if(brrCard == brrRS.getInt("cardNo")){
				brrOperation(brrCard);
				
			}else{
				brrCardEntry =false;
			}
		}
		if(!brrCardEntry){
			System.out.println("\n--------------------------------------------------------------------------------------------------");
			System.out.println("Invalid card number.");
			System.out.println("Try again ? (Y/N)");
			String tryAgian = brrIO.next();
			if(tryAgian.equals("y")){
				brrMethod();
			}
			else {
				LMS.intialInputPage();
			}
		}
}


	public static void brrOperation(int brrNumber) throws SQLException {
		
		String checkInChoice = "CHECKIN",checkOutChoice = "CHECKOUT";
		
		boolean brrNumCheck = true, brrChoiceCheck = true;
		int brrChoice = 0;
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("1) Check out a book\n2) Return a Book\n3) Quit to Previous");
		
		while(brrNumCheck){
			try{
				brrChoice = brrIO.nextInt();
				brrNumCheck = false;
			} 
			catch (InputMismatchException e){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				brrIO.next();
				}
			
			}
		
		while (brrChoiceCheck){
			
		switch(brrChoice){
		
		case 1: brrBookCheckInOut(checkOutChoice);
				brrChoiceCheck =false;
				break;
		
		case 2: brrBookCheckInOut(checkInChoice);
				brrChoiceCheck =false;
				break;
				
		case 3: LMS.intialInputPage();
				brrChoiceCheck =false;
				break;
		
		default:System.out.println("Please enter a valid option.");
				System.out.println("P.S : Enter number present next to the option.");
				brrChoice = brrIO.nextInt();
				
			}
		
		}
		
	}

	//--------------------------------------------------BOOK CHECK OUT/IN----------------------------------------------------
	public static void brrBookCheckInOut(String brrOpChoice) throws SQLException {
		
		
		String typeOfOperation = brrOpChoice;
		
		HashMap<Integer,String> bookBranch = new HashMap<Integer,String>();
		boolean brrCheckNumChoice = true,brrBranchNumChoice = true;
		String DBMSbookTitle,DBMSbranchTitle,titleBranch;
		int brrBranchChoice = 0,counter = 0;
				
		if(typeOfOperation.equals("CHECKOUT")){
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("Pick the Branch you want to check out from:");
		pstmt = conn.prepareStatement("select a.title,b.branchName from tbl_book_copies c inner join tbl_book a on a.bookId=c.bookId inner join tbl_library_branch b on b.branchId = c.branchId where noOfCopies >= 1;");
		brrRS = pstmt.executeQuery();
		
		}
		else{
			
			System.out.println("\n--------------------------------------------------------------------------------------------------");
			System.out.println("Pick the Branch you want to check in to:");	
			pstmt = conn.prepareStatement("select a.title,b.branchName from tbl_book_loans c inner join tbl_book a on a.bookId=c.bookId inner join tbl_library_branch b on b.branchId = c.branchId where cardNo = ? AND dateIn IS NULL;");
			pstmt.setInt(1, brrCard);
			brrRS = pstmt.executeQuery();
						
		}
		
		while(brrRS.next()){
			++counter;
			DBMSbookTitle = brrRS.getString("title");
			DBMSbranchTitle = brrRS.getString("branchName");
			titleBranch = DBMSbookTitle+"|"+DBMSbranchTitle;
			bookBranch.put(counter, titleBranch);
			System.out.println(counter+") "+DBMSbookTitle+" | "+DBMSbranchTitle);
		}

		while(brrCheckNumChoice){
			try{
				brrBranchChoice = brrIO.nextInt();
				brrCheckNumChoice = false;
			} 
			catch (InputMismatchException e){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				brrIO.next();
				}
			
			}
		
		while (!brrBranchNumChoice){
			if(brrBranchChoice <= 0 || brrBranchChoice > counter){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				brrIO.next();
			}
			else {
				brrBranchNumChoice = true;
			}
		}
		
		titleBranch = bookBranch.get(brrBranchChoice);
		String[] titleBranchSplit = titleBranch.split("\\|");
		
		brrUpdateQuery(typeOfOperation,titleBranchSplit[0],titleBranchSplit[1]);
		
	}

	public static void brrUpdateQuery(String typeOperation, String brrBranchChoice, String brrBranchName) throws SQLException {
		
		int DBMSbrrBookId = 0,DBMSbrrBranchId = 0;
		
		
		// Query to retrieve bookId and branchId.**************************************************************
		pstmt = conn.prepareStatement("select bookId from tbl_book where title=?");
		pstmt.setString(1, brrBranchChoice);
		brrRS = pstmt.executeQuery();
		while(brrRS.next()){
			DBMSbrrBookId = brrRS.getInt("bookId");
		}
		
		pstmt = conn.prepareStatement("select branchId from tbl_library_branch where branchName=?");
		pstmt.setString(1, brrBranchName);
		brrRS = pstmt.executeQuery();
		while(brrRS.next()){
			DBMSbrrBranchId = brrRS.getInt("branchId");
		}
		
		//To print date in a particular format
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		
		//Fetching current date.
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		
		//Creating due date.
		Calendar nxtWeek = Calendar.getInstance();
		nxtWeek.setTime(currentTimestamp);
		nxtWeek.add(Calendar.DATE, 7);
		
		//-----------------------------------------------------CHECKIN----------------------------------------------------
		
		if(typeOperation.equals("CHECKIN")){
						
			pstmt = conn.prepareStatement("update tbl_book_loans SET dateIn = ? where bookId = ? AND branchId = ?;");
			pstmt.setTimestamp(1, currentTimestamp);
			pstmt.setInt(2, DBMSbrrBookId);
			pstmt.setInt(3, DBMSbrrBranchId);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement("update tbl_book_copies SET noOfCopies = noOfCopies+1 where bookId = ? AND branchId = ?;"); 
			pstmt.setInt(1, DBMSbrrBookId);
			pstmt.setInt(2, DBMSbrrBranchId);
			pstmt.executeUpdate();
			
			System.out.println("Check in successful. Thank you.");
			brrOperation(brrCard);
		}
		//-----------------------------------------------------CHECKOUT----------------------------------------------------
		else {
			
			try{
				
				//Changes need to made here to check out a book. 
				//Problem : com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry '2222-101-777' for key 'PRIMARY'

			pstmt = conn.prepareStatement("INSERT INTO tbl_book_loans(bookId,branchId,cardNo,dateOut,dueDate) VALUES (?,?,?,?,?)");
			pstmt.setInt(1, DBMSbrrBookId);
			pstmt.setInt(2, DBMSbrrBranchId);
			pstmt.setInt(3, brrCard);
			pstmt.setTimestamp(4, currentTimestamp);
			pstmt.setObject(5, nxtWeek.getTime());
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement("update tbl_book_copies SET noOfCopies = noOfCopies-1 where bookId = ? AND branchId = ?;"); 
			pstmt.setInt(1, DBMSbrrBookId);
			pstmt.setInt(2, DBMSbrrBranchId);
			pstmt.executeUpdate();
			
			System.out.println("Check out successful. \nPlease return the book by : "+format1.format(nxtWeek.getTime()));
			brrOperation(brrCard);
			
				
			}
			catch(MySQLIntegrityConstraintViolationException e){
				
				System.out.println("Sorry! Cannot check out same book twice.");
				brrOperation(brrCard);
				
			}
		}
				
	}
	
}
