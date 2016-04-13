import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class Admin extends LMS {

	static Scanner adminIO = new Scanner(System.in);
	static HashMap<Integer,String> tableList = new HashMap<Integer,String>();
	static int adminCounter = 0;
	static ResultSet adminRS;
	
		
	public static void adminMethod() throws SQLException{
		
		
		boolean adminNumCheck = true, adminChoiceCheck = true;
		int adminNumChoice = 0;
		
		tableList.put(1,"Book and Author");
		tableList.put(2,"Publishers");
		tableList.put(3,"Library Branches");
		tableList.put(4,"Borrowers");
		tableList.put(5,"Book Loan");
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("\nChoose the operation you would like to perform :\n\n1)	Add/Update/Delete Book and Author\n2)	Add/Update/Delete Publishers\n3)	Add/Update/Delete Library Branches\n4)	Add/Update/Delete Borrowers\n5)	Over-ride Due Date for a Book Loan\n6)	Return to previous menu.");
		
		while(adminChoiceCheck){
			try{
				adminNumChoice = adminIO.nextInt();
				adminChoiceCheck = false;
			} 
			catch (InputMismatchException e){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				adminIO.next();
				}
			
			}
		
		while (!adminNumCheck){
			if(adminNumChoice <= 0 || adminNumChoice > 6){
				System.out.println("Please enter a valid number.");
				System.out.println("P.S : Enter number present next to the option.");
				adminIO.next();
			}
			else {
				adminNumCheck = true;
			}
		}
		
		if(adminNumChoice == 6){
			LMS.intialInputPage();
		}
		else{
			adminQuery(adminNumChoice,tableList.get(adminNumChoice));
		}
		
		
	}

	public static void adminQuery(int adminNumChoice, String adminOperation) throws SQLException {
		
		
		boolean adminOverChoice = true,adminTableChoice = true;
		int adminOveride = 0,tableOperation = 0;
		
		if(adminOperation.equals("Book Loan")){
			
			tableList.clear();
			adminCounter = 0;
			//Query needs to be written here to over ride due date.*****************************************************
			System.out.println("\n");
			pstmt = conn.prepareStatement("select * from tbl_book_loans;");
			adminRS = pstmt.executeQuery();
			while(adminRS.next()){
				++adminCounter;
				System.out.println(adminCounter+" | "+adminRS.getInt("bookId")+" | "+adminRS.getInt("branchId")+" | "+adminRS.getInt("cardNo")+" | "+adminRS.getDate("dateOut")+" | "+adminRS.getDate("dueDate")+" | "+adminRS.getDate("dateIn"));
				tableList.put(adminCounter, Integer.toString(adminRS.getInt("bookId"))+"|"+Integer.toString(adminRS.getInt("cardNo")));
			}
			System.out.println("\nSelect the entry you would like to over-ride : ");
			
			while(adminOverChoice){
				try{
					adminOveride = adminIO.nextInt();
					adminOverChoice = false;
				} 
				catch (InputMismatchException e){
					System.out.println("Please enter a valid number.");
					System.out.println("P.S : Enter number present next to the option.");
					adminIO.next();
					}
				
				}
			while (!adminOverChoice){
				
				if(adminOveride <= 0 || adminOveride > adminCounter){
					System.out.println("Please enter a valid number.");
					System.out.println("P.S : Enter number present next to the option.");
					adminIO.next();
				}
				else {
					adminOverChoice = true;
				}
			}
			
			adminIO.nextLine();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
			System.out.println("\nEnter new due date and time in the format yyyy-MM-dd");
			System.out.println("For example, it is now " + format.format(new Date()));
			Date date = null;
			while (date == null) {
				String line = adminIO.nextLine();
				try {
					date = format.parse(line);
				} catch (ParseException e) {
					System.out.println("Sorry, that's not valid. Please try again.");
				}
			}
			
			
			String integerSplit = tableList.get(adminOveride);
			String[] bookCard = integerSplit.split("\\|");
			
			pstmt = conn.prepareStatement("UPDATE tbl_book_loans SET dueDate = ? where cardNo = ? AND bookId = ?;");
			pstmt.setObject(1, date);
			pstmt.setInt(2, Integer.parseInt(bookCard[1]));
			pstmt.setInt(3, Integer.parseInt(bookCard[0]));
			
			System.out.println("\nDue date successfully overridden.");
			
		}
		else {
			
			System.out.println("\n--------------------------------------------------------------------------------------------------");
			System.out.println("Choose the type of operation you would like to perform:"
					+ "\n1) Add"
					+ "\n2) Update"
					+ "\n3) Delete");
			
						
			while(adminTableChoice){
				try{
					tableOperation = adminIO.nextInt();
					adminTableChoice = false;
				} 
				catch (InputMismatchException e){
					System.out.println("Please enter a valid number.");
					System.out.println("P.S : Enter number present next to the option.");
					adminIO.next();
					}
				
				}
			
			while (!adminTableChoice){
				if(tableOperation <= 0 || tableOperation > 3){
					System.out.println("Please enter a valid number.");
					System.out.println("P.S : Enter number present next to the option.");
					adminIO.next();
				}
				else {
					adminTableChoice = true;
				}
			}
			
			switch(tableOperation){
			
			case 1: addInfoQuery(adminOperation);
					break;
	
			case 2: updateInfoQuery(adminOperation);
					break;
	
			case 3: deleteInfoQuery(adminOperation);
					break;
	
			}
			
		}
		adminMethod();
		
	}
	
	
//Table Delete operation.-----------------------------------------------------------------------	
	
	
	private static void deleteInfoQuery(String adminOperation) throws SQLException {
		
		switch(adminOperation){
		
		case "Book and Author" : 	deleteBookAuthor();
									break;
									
		case "Publishers" : 		deletePublisher();
									break;
		                         
		case "Library Branches" : 	deleteBranch();
									break;
        						
		case "Borrowers" : 			deleteBorrower();									
									break;
		
		
		}
		
		System.out.println("\nInformation deleted successfully.");
		
		return;
		
		
	}
	
		
	private static void deleteBookAuthor() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		adminCounter = 0 ;
		System.out.println("Which table information would you like to delete ?");
		System.out.println("1) Book\n2) Author");
		int userTableChoice = adminIO.nextInt();
		
		if(userTableChoice == 1){
			
			System.out.println("\n");
			pstmt = conn.prepareStatement("select * from tbl_book");
			adminRS = pstmt.executeQuery();
			tableList.clear();
			
			while(adminRS.next()){
				++adminCounter;
				System.out.println(adminCounter+" | "
						+adminRS.getInt("bookId")+" | "
						+adminRS.getString("title")+" | "
						+adminRS.getInt("pubId"));
				tableList.put(adminCounter, Integer.toString(adminRS.getInt("bookId")));
			}
			
			System.out.println("\nChoose the book information that you would like to delete : ");
			int userRowChoice = adminIO.nextInt();
			
			pstmt = conn.prepareStatement("DELETE from tbl_book where bookId = ?");
			pstmt.setInt(1, Integer.parseInt(tableList.get(userRowChoice)));
			pstmt.executeUpdate();
			
		}
		else{

			pstmt = conn.prepareStatement("select * from tbl_author");
			adminRS = pstmt.executeQuery();
			tableList.clear();
			
			while(adminRS.next()){
				++adminCounter;
				System.out.println(adminCounter+" | "
						+adminRS.getInt("authorId")+" | "
						+adminRS.getString("authorName"));
				tableList.put(adminCounter, Integer.toString(adminRS.getInt("authorId")));
			}
			
			System.out.println("Choose the author information that you would like to delete : ");
			int userRowChoice = adminIO.nextInt();
			
			pstmt = conn.prepareStatement("DELETE from tbl_author where authorId = ?");
			pstmt.setInt(1, Integer.parseInt(tableList.get(userRowChoice)));
			pstmt.executeUpdate();
		}
		
		return;
		
	}

	private static void deleteBorrower() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		tableList.clear();
		
		System.out.println("\n");
		pstmt = conn.prepareStatement("select * from tbl_borrower");
		adminRS = pstmt.executeQuery();
		adminCounter = 0;
		while(adminRS.next()){
			++adminCounter;
			
			System.out.println(+adminCounter+" | "
					+adminRS.getInt("cardNo")+" | "
					+adminRS.getString("name")+" | "
					+adminRS.getString("address")+" | "
					+adminRS.getString("phone"));
			
			tableList.put(adminCounter, Integer.toString(adminRS.getInt("cardNo")));
		}
		
		System.out.println("\nChoose the borrower details you would like to delete :");
		int userDeleteChoice = adminIO.nextInt();
		
		int userBrrChoice = Integer.parseInt(tableList.get(userDeleteChoice));
		
		pstmt = conn.prepareStatement("DELETE from tbl_library_branch where branchId = ?");
		pstmt.setInt(1, userBrrChoice);
		pstmt.executeUpdate();
		
		return;
	}

	private static void deleteBranch() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		tableList.clear();
		
		pstmt = conn.prepareStatement("select * from tbl_library_branch");
		adminRS = pstmt.executeQuery();
		adminCounter = 0;
		while(adminRS.next()){
			++adminCounter;
			System.out.println(+adminCounter+" | "
					+adminRS.getInt("branchId")+" | "
					+adminRS.getString("branchName")+" | "
					+adminRS.getString("branchAddress"));
			
			tableList.put(adminCounter, Integer.toString(adminRS.getInt("branchId")));
		}
		
		System.out.println("Choose the library branch details you would like to delete :");
		int userDeleteChoice = adminIO.nextInt();
		
		int userLibChoice = Integer.parseInt(tableList.get(userDeleteChoice));
		
		pstmt = conn.prepareStatement("DELETE from tbl_library_branch where branchId = ?");
		pstmt.setInt(1, userLibChoice);
		pstmt.executeUpdate();
		
		return;
		
	}

	private static void deletePublisher() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		tableList.clear();
		System.out.println("\n");
		pstmt = conn.prepareStatement("select * from tbl_publisher");
		adminRS = pstmt.executeQuery();
		adminCounter = 0;
		while(adminRS.next()){
			++adminCounter;
			
			System.out.println(+adminCounter+" | "
					+adminRS.getInt("publisherId")+" | "
					+adminRS.getString("publisherName")+" | "
					+adminRS.getString("publisherAddress")+" | "
					+adminRS.getString("publisherPhone"));
			
			tableList.put(adminCounter, Integer.toString(adminRS.getInt("publisherId")));
		}
		
		System.out.println("\nChoose the publisher details you would like to delete : ");
		int userDeleteChoice = adminIO.nextInt();
		
		int userPubChoice = Integer.parseInt(tableList.get(userDeleteChoice));
		
		pstmt = conn.prepareStatement("DELETE from tbl_publisher where publisherId = ?");
		pstmt.setInt(1, userPubChoice);
		pstmt.executeUpdate();
		
		return;

		
	}
	
	
//Table update operation.----------------------------------------------------------------------

	private static void updateInfoQuery(String adminOperation) throws SQLException {

		switch(adminOperation){
		
		case "Book and Author" : 	updateBookAuthor();
									break;
									
		case "Publishers" : 		updatePublisher();
									break;
		                         
		case "Library Branches" : 	updateLibraryBranch();
									break;
        						
		case "Borrowers" : 			updateBorrower();									
									break;
		
		
		}
		
		System.out.println("\nInformation updated successfully.");
		
		return;
	
		
		
	}

	private static void updateBookAuthor() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		tableList.clear();
		int userUpdateId = 0;
		String userUpdateName;
		
		System.out.println("Which table information would you like to update ?");
		System.out.println("1) Book\n2) Author");
		int userTableChoice = adminIO.nextInt();
		
		if(userTableChoice == 1){
			
			System.out.println("\n");
			pstmt = conn.prepareStatement("select * from tbl_book");
			adminRS = pstmt.executeQuery();
			adminCounter = 0;
			
			while(adminRS.next()){
				++adminCounter;
				System.out.println(+adminCounter+" | "
						+adminRS.getInt("bookId")+" | "
						+adminRS.getString("title"));
				
				tableList.put(adminCounter, Integer.toString(adminRS.getInt("bookId"))+"|"+adminRS.getString("title"));
			}
			
			System.out.println("\n");
			System.out.println("Choose the book details you would like to update :");
			int userUpdateChoice = adminIO.nextInt();
			
			String detailsToSplit = tableList.get(userUpdateChoice);
			String[] splitDetails = detailsToSplit.split("\\|");
			userUpdateId = Integer.parseInt(splitDetails[0]);
			userUpdateName = splitDetails[1];
						
			System.out.println("\nEnter new title or N/A if do like to make any change :");
			String updateBookName = adminIO.next();
			
			if(updateBookName.equalsIgnoreCase("N/A")){
				updateBookName = userUpdateName;
			}
			
			
			pstmt = conn.prepareStatement("UPDATE tbl_book SET title=? WHERE bookId = ?");
			pstmt.setString(1, updateBookName);
			pstmt.setInt(2, userUpdateId);
			pstmt.executeUpdate();
			
			}
		else {
			System.out.println("\n");
			pstmt = conn.prepareStatement("select * from tbl_author");
			adminRS = pstmt.executeQuery();
			adminCounter = 0;
			
			while(adminRS.next()){
				++adminCounter;
				System.out.println(+adminCounter+" | "
						+adminRS.getInt("authorId")+" | "
						+adminRS.getString("authorName"));
				
				tableList.put(adminCounter, Integer.toString(adminRS.getInt("authorId"))+"|"+adminRS.getString("authorName"));
			}
			
			System.out.println("Choose the author details you would like to update :");
			int userUpdateChoice = adminIO.nextInt();
			
			String detailsToSplit = tableList.get(userUpdateChoice);
			String[] splitDetails = detailsToSplit.split("\\|");
			userUpdateId = Integer.parseInt(splitDetails[0]);
			userUpdateName = splitDetails[1];
						
			System.out.println("Enter new author name or N/A if do like to make any change :");
			String updateAuthorName = adminIO.next();
			
			if(updateAuthorName.equalsIgnoreCase("N/A")){
				updateAuthorName = userUpdateName;
			}
			
			
			pstmt = conn.prepareStatement("UPDATE tbl_book SET title=? WHERE bookId = ?");
			pstmt.setString(1, updateAuthorName);
			pstmt.setInt(2, userUpdateId);
			pstmt.executeUpdate();
						
		}
		
		return;
		
	}

	private static void updateBorrower() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		tableList.clear();
		int userUpdateId = 0;
		String userUpdateName,userUpdateAddress,userUpdatePhone;
		System.out.println("\n");
		pstmt = conn.prepareStatement("select * from tbl_borrower");
		adminRS = pstmt.executeQuery();
		adminCounter = 0;
		while(adminRS.next()){
			++adminCounter;
			
			System.out.println(+adminCounter+" | "
					+adminRS.getInt("cardNo")+" | "
					+adminRS.getString("name")+" | "
					+adminRS.getString("address")+" | "
					+adminRS.getString("phone"));
			
			tableList.put(adminCounter, Integer.toString(adminRS.getInt("cardNo"))+"|"+adminRS.getString("name")+"|"+adminRS.getString("address")+"|"+adminRS.getString("phone"));
		}
		
		System.out.println("\nChoose the borrower details you would like to update :");
		int userUpdateChoice = adminIO.nextInt();
		
		String detailsToSplit = tableList.get(userUpdateChoice);
		String[] splitDetails = detailsToSplit.split("\\|");
		userUpdateId = Integer.parseInt(splitDetails[0]);
		userUpdateName = splitDetails[1];
		userUpdateAddress = splitDetails[2];
		userUpdatePhone = splitDetails[3];
		
		adminIO.nextLine();
		System.out.println("\nEnter new borrower name or N/A if do like to make any change :");
		String updatebrrName = adminIO.nextLine();
		
		if(updatebrrName.equalsIgnoreCase("N/A")){
			updatebrrName = userUpdateName;
		}
		
		System.out.println("\nEnter new borrower address or N/A if do like to make any change :");
		String updatebrrAddress = adminIO.nextLine();
		
		if(updatebrrAddress.equalsIgnoreCase("N/A")){
			updatebrrAddress = userUpdateAddress;
		}
		System.out.println("\nEnter new publisher phone or N/A if do like to make any change :");
		String updatebrrPhone = adminIO.next();
		
		if(updatebrrPhone.equalsIgnoreCase("N/A")){
			updatebrrPhone = userUpdatePhone;
		}
		
		pstmt = conn.prepareStatement("UPDATE tbl_borrower SET name=?,address=?,phone=? WHERE cardNo = ?");
		pstmt.setString(1, updatebrrName);
		pstmt.setString(2, updatebrrAddress);
		pstmt.setString(3, updatebrrPhone);
		pstmt.setInt(4, userUpdateId);
		pstmt.executeUpdate();
		
		return;
		
	}

	private static void updateLibraryBranch() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		tableList.clear();
		int userUpdateId = 0;
		String userUpdateName,userUpdateAddress;
		System.out.println("\n");
		pstmt = conn.prepareStatement("select * from tbl_library_branch");
		adminRS = pstmt.executeQuery();
		adminCounter = 0;
		while(adminRS.next()){
			++adminCounter;
			System.out.println(+adminCounter+" | "
					+adminRS.getInt("branchId")+" | "
					+adminRS.getString("branchName")+" | "
					+adminRS.getString("branchAddress"));
			
			tableList.put(adminCounter, Integer.toString(adminRS.getInt("branchId"))+"|"+adminRS.getString("branchName")+"|"+adminRS.getString("branchAddress"));
		}
		
		System.out.println("\nChoose the library branch details you would like to update :");
		int userUpdateChoice = adminIO.nextInt();
		
		String detailsToSplit = tableList.get(userUpdateChoice);
		String[] splitDetails = detailsToSplit.split("\\|");
		userUpdateId = Integer.parseInt(splitDetails[0]);
		userUpdateName = splitDetails[1];
		userUpdateAddress = splitDetails[2];
		
		System.out.println("\nEnter new library branch name or N/A if do like to make any change :");
		String updateLibName = adminIO.next();
		
		if(updateLibName.equalsIgnoreCase("N/A")){
			updateLibName = userUpdateName;
		}
		
		System.out.println("\nEnter new library branch address or N/A if do like to make any change :");
		String updateLibAddress = adminIO.next();
		
		if(updateLibAddress.equalsIgnoreCase("N/A")){
			updateLibAddress = userUpdateAddress;
		}
		
		pstmt = conn.prepareStatement("UPDATE tbl_library_branch SET branchName=?,branchAddress=? WHERE branchId = ?");
		pstmt.setString(1, updateLibName);
		pstmt.setString(2, updateLibAddress);
		pstmt.setInt(3, userUpdateId);
		pstmt.executeUpdate();
		
		return;
		
		
	}

	private static void updatePublisher() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		tableList.clear();
		int userUpdateId = 0;
		String userUpdateName,userUpdateAddress,userUpdatePhone;
		System.out.println("\n");
		pstmt = conn.prepareStatement("select * from tbl_publisher");
		adminRS = pstmt.executeQuery();
		adminCounter = 0;
		while(adminRS.next()){
			++adminCounter;
			
			System.out.println(+adminCounter+" | "
					+adminRS.getInt("publisherId")+" | "
					+adminRS.getString("publisherName")+" | "
					+adminRS.getString("publisherAddress")+" | "
					+adminRS.getString("publisherPhone"));
			
			tableList.put(adminCounter, Integer.toString(adminRS.getInt("publisherId"))+"|"+adminRS.getString("publisherName")+"|"+adminRS.getString("publisherAddress")+"|"+adminRS.getString("publisherPhone"));
		}
		
		System.out.println("\nChoose the publisher details you would like to update :");
		int userUpdateChoice = adminIO.nextInt();
		
		String detailsToSplit = tableList.get(userUpdateChoice);
		String[] splitDetails = detailsToSplit.split("\\|");
		userUpdateId = Integer.parseInt(splitDetails[0]);
		userUpdateName = splitDetails[1];
		userUpdateAddress = splitDetails[2];
		userUpdatePhone = splitDetails[3];
		
		adminIO.nextLine();
		System.out.println("\nEnter new publisher name or N/A if do like to make any change :");
		String updatePubName = adminIO.nextLine();
				
		if(updatePubName.equalsIgnoreCase("N/A")){
			updatePubName = userUpdateName;
		}
		
		System.out.println("\nEnter new publisher address or N/A if do like to make any change :");
		String updatePubAddress = adminIO.nextLine();
				
		if(updatePubAddress.equalsIgnoreCase("N/A")){
			updatePubAddress = userUpdateAddress;
		}
		
		System.out.println("\nEnter new publisher phone or N/A if do like to make any change :");
		String updatePubPhone = adminIO.next();
		
		if(updatePubPhone.equalsIgnoreCase("N/A")){
			updatePubPhone = userUpdatePhone;
		}
		
		pstmt = conn.prepareStatement("UPDATE tbl_publisher SET publisherName=?,publisherAddress=?,publisherPhone=? WHERE publisherId = ?");
		pstmt.setString(1, updatePubName);
		pstmt.setString(2, updatePubAddress);
		pstmt.setString(3, updatePubPhone);
		pstmt.setInt(4, userUpdateId);
		pstmt.executeUpdate();
		
		return;
		
	}

//Table insert operation. ------------------------------------------------------------------
	
	
	private static void addInfoQuery(String adminOperation) throws SQLException {
		
		
		switch(adminOperation){
		
		case "Book and Author" : 	addBookAuthor();
									break;
									
		case "Publishers" : 		addPublisher();
									break;
		                         
		case "Library Branches" : 	addLibBranch();
        							break;
        						
		case "Borrowers" : 			addBorrower();									
									break;
				
		}
		
		System.out.println("\nInformation added successfully.");
		
		return;
		
		
	}

	private static void addBorrower() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		adminIO.nextLine();
		System.out.println("\nEnter borrower name : ");
		String userBrrName = adminIO.nextLine();
		System.out.println("\nEnter borrower address : ");
		String userBrrAddress = adminIO.nextLine();
		System.out.println("\nEnter borrower phone :");
		String userBrrPhone = adminIO.nextLine();
		
		pstmt = conn.prepareStatement("INSERT INTO tbl_borrower (name,address,phone) VALUES (?,?,?);");
		pstmt.setString(1, userBrrName);
		pstmt.setString(2, userBrrAddress);
		pstmt.setString(3, userBrrPhone);
		pstmt.executeUpdate();
		
		return;
	}

	private static void addLibBranch() throws SQLException {
		
		adminIO.nextLine();
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("\nEnter library branch name : ");
		String userLibName = adminIO.nextLine();
		System.out.println("\nEnter library branch address : ");
		String userLibAddress = adminIO.nextLine();
											
		pstmt = conn.prepareStatement("INSERT INTO tbl_library_branch (branchName,branchAddress) VALUES (?,?);");
		pstmt.setString(1, userLibName);
		pstmt.setString(2, userLibAddress);
		pstmt.executeUpdate();
		
		return;
	}

	private static void addPublisher() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		adminIO.nextLine();
		System.out.println("Enter publisher name : ");
		String userPubName = adminIO.nextLine();
		System.out.println("Enter publisher address : ");
		String userPubAddress = adminIO.nextLine();
		System.out.println("Enter publisher phone : ");
		String userPubPhone = adminIO.nextLine();
		
		pstmt = conn.prepareStatement("INSERT INTO tbl_publisher (publisherName,publisherAddress,publisherPhone) VALUES (?,?,?);");
		pstmt.setString(1, userPubName);
		pstmt.setString(2, userPubAddress);
		pstmt.setString(3, userPubPhone);
		pstmt.executeUpdate();
		
		return;
	}

	private static void addBookAuthor() throws SQLException {
		
		System.out.println("\n--------------------------------------------------------------------------------------------------");
		System.out.println("Which table information would you like to add?");
		System.out.println("1) Book\n2) Author");
		int userTableChoice = adminIO.nextInt();
		
		if(userTableChoice == 1){
			//Add to book table
			System.out.println("\n");
			adminIO.nextLine();
			pstmt = conn.prepareStatement("select * from tbl_book");
			adminRS = pstmt.executeQuery();
						
			while(adminRS.next()){
				System.out.println(adminRS.getString("title"));
						
			}
			System.out.println("\nEnter book title : ");
			String userBookName = adminIO.nextLine();
			
			//Selecting a author.-----------------------------------------------------------------------------------
			System.out.println("\n");
			pstmt = conn.prepareStatement("select * from tbl_author");
			adminRS = pstmt.executeQuery();
			tableList.clear();
			adminCounter = 0;
			
			while(adminRS.next()){
				++adminCounter;
				System.out.println(adminCounter+" | "+adminRS.getString("authorName"));
				tableList.put(adminCounter, adminRS.getString("authorName"));
				
			}
			
			System.out.println("\nSelect author or enter NEW to add a new author : ");
			String userBookAuth = adminIO.next();
			String userBookAuthName;
			if(userBookAuth.equalsIgnoreCase("NEW")){
				adminIO.nextLine();
				System.out.println("\nEnter author name : ");
				userBookAuthName = adminIO.nextLine();
								
				pstmt = conn.prepareStatement("insert into tbl_author (authorName) values (?);");
				pstmt.setString(1, userBookAuthName);
				pstmt.executeUpdate();
				
			}
			else{
				int userBookAuthChoice = Integer.parseInt(userBookAuth);
				userBookAuthName = tableList.get(userBookAuthChoice);
			}
															
			//Selecting a publisher.----------------------------------------------------------------------------
			System.out.println("\n");
			pstmt = conn.prepareStatement("select * from tbl_publisher");
			adminRS = pstmt.executeQuery();
			tableList.clear();
			adminCounter = 0;
			
			while(adminRS.next()){
				++adminCounter;
				System.out.println(adminCounter+" | "+adminRS.getString("publisherName"));
				tableList.put(adminCounter, adminRS.getString("publisherName"));
				
			}
						
			System.out.println("\nSelect publisher or enter NEW to add a new publisher : ");
			String userBookPub = adminIO.next();
			String userPubName;
			if(userBookPub.equalsIgnoreCase("NEW")){
				adminIO.nextLine();
				System.out.println("\nEnter publisher name : ");
				userPubName = adminIO.nextLine();
				System.out.println("\nEnter publisher address : ");
				String userPubAddress = adminIO.next();
				adminIO.nextLine();
				System.out.println("\nEnter publisher phone : ");
				String userPubPhone = adminIO.next();
				
				System.out.println("\n");
				pstmt = conn.prepareStatement("INSERT INTO tbl_publisher (publisherName,publisherAddress,publisherPhone) VALUES (?,?,?);");
				pstmt.setString(1, userPubName);
				pstmt.setString(2, userPubAddress);
				pstmt.setString(3, userPubPhone);
				pstmt.executeUpdate();				
			}
			else{
				int userBookPubChoice = Integer.parseInt(userBookPub);
				userPubName = tableList.get(userBookPubChoice);
			}
			
			//Selecting genre.---------------------------------------------------------------------------------
			System.out.println("\n");
			pstmt = conn.prepareStatement("select * from tbl_genre");
			adminRS = pstmt.executeQuery();
			tableList.clear();
			adminCounter = 0;
			
			while(adminRS.next()){
				++adminCounter;
				System.out.println(adminCounter+" | "+adminRS.getString("genre_name"));
				tableList.put(adminCounter, adminRS.getString("genre_name"));
				
			}
						
			System.out.println("\nSelect genre or enter NEW to add a new publisher : ");
			String userBookGenre = adminIO.next();
			String userBookGenreName;
			if(userBookGenre.equalsIgnoreCase("NEW")){
				adminIO.nextLine();
				System.out.println("Enter genre name : ");
				userBookGenreName = adminIO.nextLine();
							
				pstmt = conn.prepareStatement("INSERT INTO tbl_genre (genre_name) VALUES (?);");
				pstmt.setString(1, userBookGenreName);
				pstmt.executeUpdate();				
			}
			else{
				int userBookGenreChoice = Integer.parseInt(userBookGenre);
				userBookGenreName = tableList.get(userBookGenreChoice);
			}
			
			//Linking all above data to each other.--------------------------------------------------------------------------------
			
			int userBookNameId = 0,userBookAuthId =0,userBookPubId =0,userBookGenreId = 0;
			
			//Fetching publisherId
			pstmt = conn.prepareStatement("select publisherId from tbl_publisher where publisherName = ?;");
			pstmt.setString(1, userPubName);
			adminRS = pstmt.executeQuery();
			while(adminRS.next()){
				userBookPubId = adminRS.getInt("publisherId");
			}
			
			//Inserting tbl_book
			pstmt = conn.prepareStatement("INSERT INTO tbl_book (title,pubId) VALUES (?,?)");
			pstmt.setString(1, userBookName);
			pstmt.setInt(2, userBookPubId);
			pstmt.executeUpdate();
			
			//Fetching bookId
			pstmt = conn.prepareStatement("select bookId from tbl_book where title = ?;");
			pstmt.setString(1, userBookName);
			adminRS = pstmt.executeQuery();
			while(adminRS.next()){
				userBookNameId = adminRS.getInt("bookId");
			}
			
			//Fetching authorId
			pstmt = conn.prepareStatement("select authorId from tbl_author where authorName = ?;");
			pstmt.setString(1, userBookAuthName);
			adminRS = pstmt.executeQuery();
			while(adminRS.next()){
				userBookAuthId = adminRS.getInt("authorId");
			}
			
						
			//Fetching genre_id
			pstmt = conn.prepareStatement("select genre_id from tbl_genre where genre_name = ?;");
			pstmt.setString(1, userBookGenreName);
			adminRS = pstmt.executeQuery();
			while(adminRS.next()){
				userBookGenreId = adminRS.getInt("genre_id");
			}
			
			//Inserting tbl_book_authors
			pstmt = conn.prepareStatement("INSERT INTO tbl_book_authors (bookId,authorId) VALUES (?,?);");
			pstmt.setInt(1, userBookNameId);
			pstmt.setInt(2, userBookAuthId);
			pstmt.executeUpdate();
			
			//Inserting tbl_book_gener
			pstmt = conn.prepareStatement("INSERT INTO tbl_book_genres (genre_id,bookId) VALUES (?,?);");
			pstmt.setInt(1, userBookGenreId);
			pstmt.setInt(2, userBookNameId);
			pstmt.executeUpdate();
						
		}
		else{
			//Add to author table
			System.out.println("Enter author name : ");
			String userAuthorName = adminIO.next();
												
			pstmt = conn.prepareStatement("INSERT INTO ? (title) VALUES (?);");
			pstmt.setString(1, "tbl_author");
			pstmt.setString(2, userAuthorName);
			pstmt.executeUpdate();
						
		}
		
		return;
	}
	
}
