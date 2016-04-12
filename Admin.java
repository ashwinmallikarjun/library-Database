import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

/*		Implement the following ADMINISTRATOR functions:

			1)	Add/Update/Delete Book and Author
			2)	Add/Update/Delete Publishers
			3)	Add/Update/Delete Library Branches
			4)	Add/Update/Delete Borrowers
			5)	Over-ride Due Date for a Book Loan
			
*/


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
		System.out.println("Choose the operation you would like to perform :"
				+ "\n1) Add/Update/Delete Book and Author"
				+ "\n2)	Add/Update/Delete Publishers"
				+ "\n3)	Add/Update/Delete Library Branches"
				+ "\n4)	Add/Update/Delete Borrowers"
				+ "\n5)	Over-ride Due Date for a Book Loan"
				+ "\n6) Return to previous menu.");
		
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
		
		System.out.println("You have choosen to modify "+adminOperation+" information.");
		boolean adminOverChoice = true,adminTableChoice = true;
		int adminOveride = 0,tableOperation = 0;
		
		if(adminOperation.equals("Book Loan")){
			
			tableList.clear();
			//Query needs to be written here to over ride due date.*****************************************************
			System.out.println("Query to over-ride due date!");
			pstmt = conn.prepareStatement("select * from tbl_book_loans;");
			adminRS = pstmt.executeQuery();
			while(adminRS.next()){
				++adminCounter;
				System.out.println(adminCounter+")");
				System.out.println(adminRS.getInt("bookId"));
				System.out.println(adminRS.getInt("branchId"));
				System.out.println(adminRS.getInt("cardNo"));
				System.out.println(adminRS.getDate("dateOut"));
				System.out.println(adminRS.getDate("dueDate"));
				System.out.println(adminRS.getDate("dateIn"));
				System.out.println("----------------------------------------------------------------");
				tableList.put(adminCounter, Integer.toString(adminRS.getInt("bookId"))+"|"+Integer.toString(adminRS.getInt("cardNo")));
			}
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
			System.out.println("Enter the new due date in (YYYY-MM-DD) format.");
						
			String date = adminIO.next();

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			java.util.Date newDueDate=null;
			try {
			    
				newDueDate = dateFormat.parse(date);
			} catch (ParseException e) {
			    
			    e.printStackTrace();
			}
			
			
			String integerSplit = tableList.get(adminOveride);
			String[] bookCard = integerSplit.split("\\|");
			pstmt = conn.prepareStatement("UPDATE tbl_book_loans SET dueDate = ? where cardNo = ? AND bookId = ?");
			pstmt.setObject(1, newDueDate);
			pstmt.setInt(2, Integer.parseInt(bookCard[1]));
			pstmt.setInt(3, Integer.parseInt(bookCard[0]));
			
			System.out.println("Due date successfully overridden.");
			
		}
		else {
			
			System.out.println("\n--------------------------------------------------------------------------------------------------");
			//Query needs to be written here to Add/Update/Delete table information.*************************************
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
			
			//Add methods here
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
		
		/*DELETE FROM table_name
		WHERE some_column=some_value;*/
		
		switch(adminOperation){
		
		case "Book and Author" : 	System.out.println("Delete method for book and author");
									break;
									
		case "Publishers" : 		deletePublisher();
									break;
		                         
		case "Library Branches" : 	deleteBranch();
									break;
        						
		case "Borrowers" : 			deleteBorrower();									
									break;
		
		
		}
		
		System.out.println("Information deleted successfully.");
		
		return;
		
		
	}
	
		
	private static void deleteBorrower() throws SQLException {
		
		tableList.clear();
				
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
		
		System.out.println("Choose the borrower details you would like to delete :");
		int userDeleteChoice = adminIO.nextInt();
		
		int userBrrChoice = Integer.parseInt(tableList.get(userDeleteChoice));
		
		pstmt = conn.prepareStatement("DELETE from tbl_library_branch where branchId = ?");
		pstmt.setInt(1, userBrrChoice);
		pstmt.executeUpdate();
		
		return;
	}

	private static void deleteBranch() throws SQLException {
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
		
		tableList.clear();
		
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
		
		System.out.println("Choose the publisher details you would like to delete : ");
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
		
		case "Book and Author" : 	System.out.println("Update method for book and author");
									break;
									
		case "Publishers" : 		updatePublisher();
									break;
		                         
		case "Library Branches" : 	updateLibraryBranch();
									break;
        						
		case "Borrowers" : 			updateBorrower();									
									break;
		
		
		}
		
		System.out.println("Information updated successfully.");
		
		return;
	
		
		
	}

	private static void updateBorrower() throws SQLException {
		
		tableList.clear();
		int userUpdateId = 0;
		String userUpdateName,userUpdateAddress,userUpdatePhone;
		
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
		
		System.out.println("Choose the borrower details you would like to update :");
		int userUpdateChoice = adminIO.nextInt();
		
		String detailsToSplit = tableList.get(userUpdateChoice);
		String[] splitDetails = detailsToSplit.split("\\|");
		userUpdateId = Integer.parseInt(splitDetails[0]);
		userUpdateName = splitDetails[1];
		userUpdateAddress = splitDetails[2];
		userUpdatePhone = splitDetails[3];
		
		
		System.out.println("Enter new borrower name or N/A if do like to make any change :");
		String updatebrrName = adminIO.next();
		
		if(updatebrrName.equalsIgnoreCase("N/A")){
			updatebrrName = userUpdateName;
		}
		
		System.out.println("Enter new borrower address or N/A if do like to make any change :");
		String updatebrrAddress = adminIO.next();
		
		if(updatebrrAddress.equalsIgnoreCase("N/A")){
			updatebrrAddress = userUpdateAddress;
		}
		System.out.println("Enter new publisher phone or N/A if do like to make any change :");
		String updatebrrPhone = adminIO.next();
		
		if(updatebrrPhone.equalsIgnoreCase("N/A")){
			updatebrrPhone = userUpdatePhone;
		}
		
		pstmt = conn.prepareStatement("UPDATE tbl_publisher SET name=?,address=?,phone=? WHERE cardNo = ?");
		pstmt.setString(1, updatebrrName);
		pstmt.setString(2, updatebrrAddress);
		pstmt.setString(3, updatebrrPhone);
		pstmt.setInt(4, userUpdateId);
		pstmt.executeUpdate();
		
		return;
		
	}

	private static void updateLibraryBranch() throws SQLException {
		
		tableList.clear();
		int userUpdateId = 0;
		String userUpdateName,userUpdateAddress;
		
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
		
		System.out.println("Choose the library branch details you would like to update :");
		int userUpdateChoice = adminIO.nextInt();
		
		String detailsToSplit = tableList.get(userUpdateChoice);
		String[] splitDetails = detailsToSplit.split("\\|");
		userUpdateId = Integer.parseInt(splitDetails[0]);
		userUpdateName = splitDetails[1];
		userUpdateAddress = splitDetails[2];
		
		System.out.println("Enter new library branch name or N/A if do like to make any change :");
		String updateLibName = adminIO.next();
		
		if(updateLibName.equalsIgnoreCase("N/A")){
			updateLibName = userUpdateName;
		}
		
		System.out.println("Enter new library branch address or N/A if do like to make any change :");
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
		
		tableList.clear();
		int userUpdateId = 0;
		String userUpdateName,userUpdateAddress,userUpdatePhone;
		
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
		
		System.out.println("Choose the publisher details you would like to update :");
		int userUpdateChoice = adminIO.nextInt();
		
		String detailsToSplit = tableList.get(userUpdateChoice);
		String[] splitDetails = detailsToSplit.split("\\|");
		userUpdateId = Integer.parseInt(splitDetails[0]);
		userUpdateName = splitDetails[1];
		userUpdateAddress = splitDetails[2];
		userUpdatePhone = splitDetails[3];
		
		
		System.out.println("Enter new publisher name or N/A if do like to make any change :");
		String updatePubName = adminIO.next();
		
		if(updatePubName.equalsIgnoreCase("N/A")){
			updatePubName = userUpdateName;
		}
		
		System.out.println("Enter new publisher address or N/A if do like to make any change :");
		String updatePubAddress = adminIO.next();
		
		if(updatePubAddress.equalsIgnoreCase("N/A")){
			updatePubAddress = userUpdateAddress;
		}
		System.out.println("Enter new publisher phone or N/A if do like to make any change :");
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
		
		case "Book and Author" : 	System.out.println("Add method for book and author");
									break;
									
		case "Publishers" : 		System.out.println("Enter publisher name : ");
									String userPubName = adminIO.next();
									System.out.println("Enter publisher address : ");
									String userPubAddress = adminIO.next();
									System.out.println("Enter publisher phone : ");
									String userPubPhone = adminIO.next();
									
									pstmt = conn.prepareStatement("INSERT INTO ? (publisherName,publisherAddress,publisherPhone) VALUES (?,?,?);");
									pstmt.setString(1, "tbl_publisher");
									pstmt.setString(2, userPubName);
									pstmt.setString(3, userPubAddress);
									pstmt.setString(4, userPubPhone);
									pstmt.executeUpdate();
									
									break;
		                         
		case "Library Branches" : 	System.out.println("Enter library branch name : ");
									String userLibName = adminIO.next();
									System.out.println("Enter library branch address : ");
									String userLibAddress = adminIO.next();
																		
									pstmt = conn.prepareStatement("INSERT INTO ? (branchName,branchAddress) VALUES (?,?);");
									pstmt.setString(1, "tbl_library_branch");
									pstmt.setString(2, userLibName);
									pstmt.setString(3, userLibAddress);
									pstmt.executeUpdate();
									
        							break;
        						
		case "Borrowers" : 			System.out.println("Enter borrower name : ");
									String userBrrName = adminIO.next();
									System.out.println("Enter borrower address : ");
									String userBrrAddress = adminIO.next();
									System.out.println("Enter borrower phone :");
									String userBrrPhone = adminIO.next();
									
									pstmt = conn.prepareStatement("INSERT INTO ? (name,address,phone) VALUES (?,?,?);");
									pstmt.setString(1, "tbl_borrower");
									pstmt.setString(2, userBrrName);
									pstmt.setString(3, userBrrAddress);
									pstmt.setString(4, userBrrPhone);
									pstmt.executeUpdate();
									
									break;
		
		
		}
		
		System.out.println("Information added successfully.");
		
		return;
		
		
	}
	
}
