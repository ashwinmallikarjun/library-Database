import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

//Modify to display dynamic data of branch name and book data!!!


public class Librarian extends LMS {
	
	static Scanner libIO = new Scanner(System.in);
	static ResultSet libRS;
			
	public static void libMethod() throws SQLException {
		
		int libChoice = 0 ;
		boolean libChoiceCheck = true, libNumCheck = true;
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("\n1) Manage a branch.\n2) Quit to previous.");
				
		
		while(libNumCheck){
			try{
				libChoice = libIO.nextInt();
				libNumCheck = false;
			} 
			catch (InputMismatchException e){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				libIO.next();
				}
			
			}
		
		while (libChoiceCheck){
			
		switch(libChoice){
		
		case 1: libBranch();
				libChoiceCheck =false;
				break;
		
		case 2: LMS.intialInputPage();
				libChoiceCheck =false;
				break;
		
		default:System.out.println("Please enter a valid option.");
				System.out.println("P.S : Enter number present next to the option.");
				libChoice = libIO.nextInt();
				
			}
		
		}
		return;
	}

	public static void libBranch() throws SQLException {
		
		boolean branchNumCheck = true;
		int branchChoice = 0,counter = 0;
		String DBMSbranchName;
		
		pstmt = conn.prepareStatement("select branchName from tbl_library_branch;");
		libRS = pstmt.executeQuery();
		
		System.out.println("\n--------------------------------------------------------------------------------------------------\n");
		while(libRS.next()){
			++counter;
			DBMSbranchName = libRS.getString("branchName");
			branchList.put(counter, DBMSbranchName);
			System.out.println(+counter+") "+libRS.getString("branchName"));
		}
		
		while(branchNumCheck){
			try{
				branchChoice = libIO.nextInt();
				branchNumCheck = false;
			} 
			catch (InputMismatchException e){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				libIO.next();
				}
			
			}
		while(!branchNumCheck){
			if(branchChoice <= 0 || branchChoice > counter){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				libIO.next();
			}
			else {
				branchNumCheck = true;
			}
		}
		
		
		if(branchChoice == 5){
			libMethod();
		}
		else{
			libQueryMethod(branchChoice);
		}
	
	}

	public static void libQueryMethod(int userBranch) throws SQLException {
		
		
		boolean queryChoiceCheck = true,queryChoiceType = true;
		int queryChoice = 0;
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("\n1) Update the details of a library branch.\n2) Add copies of Book to the Branch.\n3) Quit to previous.");
				
		
		while(queryChoiceCheck){
		try{
			queryChoice = libIO.nextInt();
			queryChoiceCheck = false;
		} 
		catch (InputMismatchException e){
			System.out.println("Please enter a valid number.");
			System.out.println("P.S : Enter number present next to the option.");
			libIO.next();
			}
		
		}
		

		while (queryChoiceType){
			
		switch(queryChoice){
		
		case 1: updateQuery(userBranch,branchList.get(userBranch));
				queryChoiceType =false;
				break;
		
		case 2: addBookQuery(userBranch,branchList.get(userBranch));
				queryChoiceType =false;
				break;
				
		case 3: libBranch();
				queryChoiceType =false;
				break;
		
		default:System.out.println("Please enter a valid user category.");
			queryChoice = libIO.nextInt();
				
			}
		}
		
	}

	public static void addBookQuery(int branchID, String userBranchname) throws SQLException {
		
		
		boolean addBookCheck = true;
		String DBMSbookTitle;
		int userBookType = 0,counter =0,DBMSbookId = 0,DBMSbranchId=0;
		
		pstmt = conn.prepareStatement("select title from tbl_book_copies a inner join tbl_library_branch b on b.branchId = a.branchId	inner join tbl_book c on c.bookId = a.bookId where b.branchName = ?;");
		pstmt.setString(1,userBranchname);
		libRS = pstmt.executeQuery();
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		
		System.out.println("Pick the Book you want to add copies of, to your branch:");
		while(libRS.next()){

			++counter;
			DBMSbookTitle = libRS.getString("title");
			bookList.put(counter,DBMSbookTitle);
			System.out.println(counter+") "+DBMSbookTitle);
			
		}

/*		if(!libRS.next()){
			System.out.println("\nNo books available to add any additional copies.");
			libQueryMethod(branchID);
		}*/
		
			
		while(addBookCheck){
		try{
			userBookType = libIO.nextInt();
			addBookCheck = false;
		} 
		catch (InputMismatchException e){
			System.out.println("Please enter a valid number.");
			System.out.println("P.S : Enter number present next to the option.");
			libIO.next();
			}
		
		}
		
		while(!addBookCheck){
			
			if(userBookType <= 0 || userBookType > counter){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				libIO.next();
			}
			else {
				addBookCheck = true;
			}
		
		}
		
		String bookDetails = bookList.get(userBookType);
		
		//Query to update book copies info in to database.*****************************************************
		System.out.println("You have picked '"+bookDetails+"' at "+userBranchname+" branch.");
		
		pstmt = conn.prepareStatement("select noOfCopies from tbl_book_copies a inner join tbl_library_branch b on b.branchId = a.branchId inner join tbl_book c on c.bookId = a.bookId where b.branchName = ? AND c.title = ?;");
		
		pstmt.setString(1,userBranchname);
		pstmt.setString(2, bookDetails);
		libRS = pstmt.executeQuery();
		
		System.out.println("Existing number of copies : ");
		while(libRS.next()){
			System.out.println(libRS.getString("noOfCopies"));
		}
		System.out.println("Enter new number of copies : ");
		int newNoOfCopies = libIO.nextInt();
		
		//Query to acquire bookId and branchId.*************************************************************************
		pstmt = conn.prepareStatement("select a.bookId,a.branchId from tbl_book_copies a inner join tbl_library_branch b on b.branchId = a.branchId inner join tbl_book c on c.bookId = a.bookId where b.branchName = ? AND c.title = ?;");
		
		pstmt.setString(1,userBranchname);
		pstmt.setString(2, bookDetails);
		
		libRS = pstmt.executeQuery();
		while(libRS.next()){
		DBMSbookId = libRS.getInt("bookId");
		DBMSbranchId = libRS.getInt("branchID");
		}
		
		//Query to update noOfCopies.**************************************************************************
		pstmt = conn.prepareStatement("UPDATE tbl_book_copies SET noOfCopies=? WHERE bookId=? AND branchId=?");
		pstmt.setInt(1,newNoOfCopies);
		pstmt.setInt(2,DBMSbookId);
		pstmt.setInt(3,DBMSbranchId);
		pstmt.executeUpdate();
		
		System.out.println("Number of copies successfully modified.");
		libMethod();

			
	}

	public static void updateQuery(int branchID,String userBranchName) throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("\nYou have chosen to update the Branch with Branch Name: "+userBranchName+"."
							+ "\nEnter ‘Q’ at any prompt to cancel operation.");
		
		//Query to display contents of tbl_library_branch.*******************************************
		System.out.println("Contents of library branch table :");
			pstmt = conn.prepareStatement("select * from tbl_library_branch;");
			libRS = pstmt.executeQuery();
			while(libRS.next()){
				System.out.println(libRS.getInt("branchId")+" | "+libRS.getString("branchName")+" | "+libRS.getString("branchAddress"));
				System.out.println("-------------------------------------------------");
			}
				
		System.out.println("Please enter new branch name or enter N/A for no change:");
		String newBranchName = libIO.next();
		
		int updateCheckRes = updateQueryCheck(newBranchName);
		if(updateCheckRes == 1){
			libQueryMethod(branchID);
		}
		else if(updateCheckRes == 2) {
			newBranchName = userBranchName;
			
		}

				System.out.println("Please enter new branch address or enter N/A for no change: ");
				String newBranchAddress = libIO.next();
				
				int updateCheckRes2 = updateQueryCheck(newBranchAddress);
				if(updateCheckRes == 1){
					libQueryMethod(branchID);
				}
				else if(updateCheckRes == 2 && updateCheckRes2 == 2) {
					System.out.println("\nNo changes made.\n");
					libMethod();					
				}
				else if(updateCheckRes2 == 2){
					System.out.println(userBranchName);
					pstmt = conn.prepareStatement("select branchAddress from tbl_library_branch where branchName = ?;");
					pstmt.setString(1, userBranchName);
					libRS = pstmt.executeQuery();
					while(libRS.next())
					newBranchAddress = libRS.getString("branchAddress");
					
				}
					
				//Query to add new branch and its address.*****************************************************
				
				pstmt = conn.prepareStatement("UPDATE tbl_library_branch SET branchname=?,branchAddress=? WHERE branchName=?");
				pstmt.setString(1, newBranchName);
				pstmt.setString(2, newBranchAddress);
				pstmt.setString(3, userBranchName);
				pstmt.executeUpdate();
				
				System.out.println("\nNew branch details added to database.\n");
				
				libMethod();
		
	}
		

	public static int updateQueryCheck(String checkString) {
		
		if(checkString.equalsIgnoreCase("Q")){
			System.out.println("Returning to previous page.");
			return 1;
		}
		else if(checkString.equalsIgnoreCase("N/A")){
			return 2;
		}
		else {
			return 0;
		}
		
	}
}