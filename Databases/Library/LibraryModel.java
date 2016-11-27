package library;

/*
 * LibraryModel.java
 * Author: David Thomsen
 * Created on:
 */

import java.sql.*;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;

public class LibraryModel {

	// For use in creating dialogs and making them modal
	private Connection con = null;
	private JFrame dialogParent;

	public LibraryModel(JFrame parent, String userid, String password) {
		dialogParent = parent;

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Can not find the driver class: \nEither I have not installed it"
					+ "properly or \n postgresql.jar file is not in my CLASSPATH");
		}

		String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/" + userid + "_jdbc";

		try {
			con = DriverManager.getConnection(url, userid, password);
		} catch (SQLException sqlex) {
			System.out.println("Can not connect");
			System.out.println(sqlex.getMessage());
		}
	}

	public String bookLookup(int isbn) {
		String result = "BOOK LOOKUP:\n";

		// do not look for the default book.
		if (isbn == 0) {
			result = result + "no match";
			return result;
		}

		try {
			Statement s = openReadOnlyStatement();

			String query = "SELECT * FROM Book WHERE ISBN = " + isbn + ";";
			ResultSet rs = s.executeQuery(query);

			Book book = null;

			if (rs.next()) {
				book = new Book(rs.getString("Title").trim(), rs.getInt("Edition_No"), rs.getInt("NumOfCop"),
						rs.getInt("NumLeft"));
			} else {
				result = result + "  no match";
				closeReadOnlyStatement(s);
				return result;
			}

			query = "SELECT * FROM Book_Author NATURAL JOIN Author WHERE ISBN = " + isbn + "ORDER BY AuthorSeqNo;";
			rs = s.executeQuery(query);

			while (rs.next()) {
				String name = rs.getString("Name");
				if (name != null) {
					name = name.trim();
				} else {
					name = "---";
				}

				book.addAuthor(name, rs.getString("Surname").trim());
			}

			result = result + printBook(isbn, book, true, false);

			closeReadOnlyStatement(s);
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String showCatalogue() {
		String result = "SHOW CATALOGUE:\n";

		try {
			Statement s = openReadOnlyStatement();

			String query = "SELECT * FROM Book WHERE ISBN <> 0 ORDER BY ISBN;";
			ResultSet rs = s.executeQuery(query);

			// They will be returned in order so the ISBNs arraylist will keep them in order while the map will not.
			Map<Integer, Book> books = new HashMap<Integer, Book>();
			ArrayList<Integer> isbns = new ArrayList<Integer>();

			while (rs.next()) {
				books.put(rs.getInt("ISBN"), new Book(rs.getString("Title").trim(), rs.getInt("Edition_No"),
						rs.getInt("NumOfCop"), rs.getInt("NumLeft")));
				isbns.add(rs.getInt("ISBN"));
			}

			if (isbns.size() == 0) {
				result = result + "  no books found";
				closeReadOnlyStatement(s);
				return result;
			}

			query = "SELECT ISBN, Name, Surname FROM Book_Author NATURAL JOIN Author WHERE ISBN <> 0 ORDER BY AuthorSeqNo;";
			rs = s.executeQuery(query);

			while (rs.next()) {
				String name = rs.getString("Name");
				if (name != null) {
					name = name.trim();
				} else {
					name = "---";
				}

				books.get(rs.getInt("ISBN")).addAuthor(name, rs.getString("Surname").trim());
			}

			boolean first = true;

			for (int i = 0; i < isbns.size(); i++) {
				if (!first) {
					result = result + "\n    ---\n";
				}

				Book book = books.get(isbns.get(i));
				result = result + printBook(isbns.get(i), book, true, false);
				first = false;
			}

			closeReadOnlyStatement(s);
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String showLoanedBooks() {
		String result = "SHOW LOANED BOOKS:\n";

		try {
			Statement s = openReadOnlyStatement();

			String query = "SELECT * FROM Book NATURAL JOIN (SElECT DISTINCT ISBN FROM Cust_Book) AS ISBNs ORDER BY"
					+ " ISBN;";
			ResultSet rs = s.executeQuery(query);

			// They will be returned in order so the ISBNs arraylist will keep them in order while the map will not.
			Map<Integer, Book> books = new HashMap<Integer, Book>();
			ArrayList<Integer> isbns = new ArrayList<Integer>();

			while (rs.next()) {
				books.put(rs.getInt("ISBN"), new Book(rs.getString("Title").trim(), rs.getInt("Edition_No"),
						rs.getInt("NumOfCop"), rs.getInt("NumLeft")));
				isbns.add(rs.getInt("ISBN"));
			}

			if (books.isEmpty()) {
				result = result + "  no match";
				closeReadOnlyStatement(s);
				return result;
			}

			query = "SELECT ISBN, Name, Surname FROM (SELECT DISTINCT ISBN FROM Cust_Book) AS ISBNs NATURAL JOIN"
					+ " Book_Author NATURAL JOIN Author ORDER BY authorseqno;";
			rs = s.executeQuery(query);

			while (rs.next()) {
				String name = rs.getString("Name");
				if (name != null) {
					name = name.trim();
				} else {
					name = "---";
				}

				books.get(rs.getInt("ISBN")).addAuthor(name, rs.getString("Surname").trim());
			}

			query = "SELECT ISBN, CustomerID, L_Name, F_Name, City FROM (SELECT DISTINCT ISBN FROM Cust_Book) AS ISBNs"
					+ " NATURAL JOIN Cust_Book NATURAL JOIN Customer ORDER BY CustomerID";
			rs = s.executeQuery(query);

			while (rs.next()) {
				String city = rs.getString("City");
				if (city != null) {
					city = city.trim();
				} else {
					city = "---";
				}

				String fName = rs.getString("F_Name");
				if (fName != null) {
					fName = fName.trim();
				} else {
					fName = "---";
				}

				books.get(rs.getInt("ISBN")).addCustomer(rs.getInt("CustomerID"), rs.getString("L_Name").trim(), fName,
						city);
			}

			boolean first = true;

			for (int i = 0; i < books.size(); i++) {
				if (!first) {
					result = result + "\n    ---\n";
				}

				Book book = books.get(isbns.get(i));
				result = result + printBook(isbns.get(i), book, false, true);
				first = false;
			}

			closeReadOnlyStatement(s);
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String showAuthor(int authorID) {
		String result = "SHOW AUTHOR:\n";

		// Do not bother searching for default author.
		if (authorID == 0) {
			result = result + "  no match";
			return result;
		}

		try {
			Statement s = openReadOnlyStatement();

			String query = "SELECT Name, Surname FROM Author WHERE AuthorId = " + authorID + ";";
			ResultSet rs = s.executeQuery(query);

			if (rs.next()) {
				String name = rs.getString("Name");
				if (name != null) {
					name = name.trim();
				} else {
					name = "---";
				}

				result = result + "  Name: " + name + " " + rs.getString("Surname").trim() + "\n";
				result = result + "  Author ID: " + authorID + "\n";
			} else {
				result = result + "  no match";
				closeReadOnlyStatement(s);
				return result;
			}

			query = "SELECT ISBN, Title, Edition_No, NumOfCop, NumLeft FROM Book_Author NATURAL JOIN Book WHERE"
					+ " AuthorId = " + authorID + " AND ISBN <> 0 ORDER BY ISBN;";
			rs = s.executeQuery(query);

			// They will be returned in order so the ISBNs arraylist will keep them in order while the map will not.
			Map<Integer, Book> books = new HashMap<Integer, Book>();
			ArrayList<Integer> isbns = new ArrayList<Integer>();

			while (rs.next()) {
				books.put(rs.getInt("ISBN"), new Book(rs.getString("Title").trim(), rs.getInt("Edition_No"),
						rs.getInt("NumOfCop"), rs.getInt("NumLeft")));
				isbns.add(rs.getInt("ISBN"));
			}

			if (books.size() == 0) {
				result = result + "BOOK(S):\n    No Books Found";
				closeReadOnlyStatement(s);
				return result;
			} else if (books.size() == 1) {
				result = result + "BOOK:\n";
			} else {
				result = result + "BOOKS:\n";
			}

			boolean first = true;

			for (int i = 0; i < books.size(); i++) {
				if (!first) {
					result = result + "\n    ---\n";
				}

				if (isbns.get(i) != 0) {
					Book book = books.get(isbns.get(i));
					result = result + printBook(isbns.get(i), book, false, false);
					first = false;
				}
			}

			closeReadOnlyStatement(s);
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String showAllAuthors() {
		String result = "SHOW ALL AUTHORS:";

		try {
			Statement s = openReadOnlyStatement();

			String query = "SELECT AuthorId, Name, Surname FROM Author WHERE AuthorID <> 0 ORDER BY AuthorId;";
			ResultSet rs = s.executeQuery(query);

			// These lists will not be rearranged to it is fine, otherwise I'd create Author classes
			ArrayList<Integer> authorIds = new ArrayList<Integer>();
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<String> surnames = new ArrayList<String>();

			while (rs.next()) {
				String name = rs.getString("Name");
				if (name != null) {
					name = name.trim();
				} else {
					name = "---";
				}

				authorIds.add(rs.getInt("AuthorID"));
				names.add(name);
				surnames.add(rs.getString("Surname").trim());
			}

			if (authorIds.isEmpty()) {
				result = result + "\n  no authors found";
			} else {
				for (int i = 0; i < authorIds.size(); i++) {
					result = result + "\n  " + authorIds.get(i) + ": ";
					result = result + surnames.get(i) + ", ";
					result = result + names.get(i);
				}
			}

			closeReadOnlyStatement(s);
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String showCustomer(int customerID) {
		String result = "SHOW CUSTOMER:\n";

		// Do not bother searching for default customer.
		if (customerID == 0) {
			result = result + "  no match";
			return result;
		}

		try {
			Statement s = openReadOnlyStatement();

			String query = "SELECT * FROM Customer WHERE CustomerId = " + customerID + ";";
			ResultSet rs = s.executeQuery(query);

			if (rs.next()) {
				String city = rs.getString("City");
				if (city != null) {
					city = city.trim();
				} else {
					city = "---";
				}

				String fName = rs.getString("F_Name");
				if (fName != null) {
					fName = fName.trim();
				} else {
					fName = "---";
				}

				result = result + "  " + fName + " " + rs.getString("L_Name").trim() + ", " + city + "\n";
			} else {
				result = result + "  no match";
				closeReadOnlyStatement(s);
				return result;
			}

			query = "SELECT ISBN, DueDate, Title, Edition_No FROM (SELECT DueDate, ISBN FROM Cust_Book WHERE"
					+ " CustomerId = " + customerID + ") AS ISBNs NATURAL JOIN Book ORDER BY ISBN;";
			rs = s.executeQuery(query);

			result = result + "  Books Borrowed:";

			boolean first = true;
			boolean borrowed = false;

			while (rs.next()) {
				borrowed = true;
				if (!first) {
					result = result + "\n      ---";
				}
				result = result + "\n    Title: " + rs.getString("Title").trim() + ", "
						+ parseEdition(rs.getInt("Edition_No")) + " Edition";
				result = result + "\n    ISBN: " + rs.getInt("ISBN");

				Date date = rs.getDate("DueDate");
				String dateString = "";

				if (date == null) {
					dateString = "---";
				} else {
					dateString = date.toString();
					;
				}

				result = result + "\n    Due Date: " + dateString;
				first = false;
			}

			if (!borrowed) {
				result = result + "\n    none found";
			}

			closeReadOnlyStatement(s);
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String showAllCustomers() {
		String result = "SHOW ALL CUSTOMERS:";

		try {
			Statement s = openReadOnlyStatement();

			String query = "SELECT * FROM Customer WHERE CustomerID <> 0 ORDER BY CustomerId;";
			ResultSet rs = s.executeQuery(query);

			while (rs.next()) {
				String city = rs.getString("City");
				if (city != null) {
					city = city.trim();
				} else {
					city = "---";
				}

				String fName = rs.getString("F_Name");
				if (fName != null) {
					fName = fName.trim();
				} else {
					fName = "---";
				}

				result = result + "\n  " + rs.getInt("CustomerID") + ": " + fName + " " + rs.getString("L_Name").trim()
						+ ", " + city;
			}

			if (result == "SHOW ALL CUSTOMERS:") {
				result = result + "\n  no customers found";
			}

			closeReadOnlyStatement(s);
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String borrowBook(int isbn, int customerID, int day, int month, int year) {
		String result = "BORROW BOOK:\n";

		// Do not bother searching for default customers or books.
		if (customerID == 0) {
			result = result + "  no customer found";
			return result;
		}

		if (isbn == 0) {
			result = result + "no book found";
			return result;
		}

		try {
			Statement s = beginAndLock();

			String query = "SELECT * FROM Customer WHERE CustomerId = " + customerID + " FOR UPDATE;";
			ResultSet rs = s.executeQuery(query);

			String fName = "";
			String lName = "";

			if (!rs.next()) {
				result = result + "  no customer found";

				commitAndClose(s);

				return result;
			} else {
				fName = rs.getString("F_Name");
				if (fName != null) {
					fName = fName.trim();
				} else {
					fName = "---";
				}

				lName = rs.getString("L_Name").trim();
			}

			query = "SELECT ISBN FROM Cust_Book WHERE ISBN = " + isbn + " AND CustomerID = " + customerID + ";";
			rs = s.executeQuery(query);

			if (rs.next()) {
				result = result + "  customer already has book on loan";

				commitAndClose(s);

				return result;
			}

			query = "SELECT Title, NumLeft FROM Book WHERE ISBN = " + isbn + " FOR UPDATE;";
			rs = s.executeQuery(query);

			int copies = 0;
			String title = "";

			if (rs.next()) {
				if (rs.getInt("NumLeft") == 0) {
					result = result + "  not enough copies of book";

					commitAndClose(s);

					return result;
				} else {
					copies = rs.getInt("NumLeft");
					title = rs.getString("Title").trim();
				}
			} else {
				result = result + "  no book found";

				commitAndClose(s);

				return result;
			}

			JOptionPane.showMessageDialog(dialogParent, "Locked the tuble(s), ready to update. Click OK to continue");

			query = "UPDATE Book SET NumLeft = " + (copies - 1) + " WHERE ISBN = " + isbn + ";";
			int updateBook = s.executeUpdate(query);

			query = "INSERT INTO cust_book (ISBN, DueDate, CustomerId) VALUES (" + isbn + ", '" + year + "-" + month
					+ "-" + day + "', " + customerID + ");";
			int insertCustBook = s.executeUpdate(query);

			if (updateBook == 0 || insertCustBook == 0) {
				result = result + "  borrow failed";

				rollbackAndClose(s);

				return result;
			}

			commitAndClose(s);

			result = result + "  Book: " + isbn + " (" + title + ")\n";
			result = result + "  Loaned to: " + customerID + " (" + fName + " " + lName + ")\n";
			result = result + "  Due Date: " + day + " "
					+ Month.of(month + 1).getDisplayName(TextStyle.SHORT, Locale.UK) + " " + year;

		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String returnBook(int ISBN, int customerID) {
		String result = "RETURN:\n";

		try {
			Statement s = beginAndLock();

			String query = "SELECT NumLeft FROM Book WHERE ISBN = " + ISBN + " FOR UPDATE;";
			ResultSet rs = s.executeQuery(query);

			int numLeft = 0;

			if (rs.next()) {
				numLeft = rs.getInt("NumLeft");
			} else {
				result = result + "  Book does not exist with this ISBN";

				commitAndClose(s);

				return result;
			}

			query = "SELECT * FROM Cust_Book WHERE CustomerID = " + customerID + " and ISBN = " + ISBN + " FOR UPDATE;";
			rs = s.executeQuery(query);

			if (!rs.next()) {
				result = result + "  Book not on loan to Customer with this CustomerID";

				commitAndClose(s);

				return result;
			}

			JOptionPane.showMessageDialog(dialogParent, "Locked the tuble(s), ready to update. Click OK to continue");

			query = "UPDATE Book SET NumLeft = " + (numLeft + 1) + " WHERE ISBN = " + ISBN + ";";
			int updateBook = s.executeUpdate(query);

			query = "DELETE FROM Cust_Book WHERE ISBN = " + ISBN + " AND CustomerID = " + customerID + ";";
			int deleteCustBook = s.executeUpdate(query);

			if (updateBook == 0 || deleteCustBook == 0) {
				result = result + "  return failed";

				rollbackAndClose(s);

				return result;
			}

			commitAndClose(s);
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		result = result + "  Book " + ISBN + " returned for Customer " + customerID;

		return result;
	}

	public void closeDBConnection() {
		try {
			con.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		}

	}

	// The Delete methods don't need to worry about Rollback because they only have one action query to be actioned.

	public String deleteCus(int customerID) {
		String result = "DELETE CUSTOMER:\n";

		// Do not bother showing default customer.
		if (customerID == 0) {
			result = result + "  no customer found";
			return result;
		}

		try {
			Statement s = con.createStatement();

			String query = "SELECT ISBN, DueDate, Title FROM (SELECT ISBN, DueDate FROM Cust_Book WHERE CustomerID = "
					+ customerID + ") as Cust_Book NATURAL JOIN Book;";
			ResultSet rs = s.executeQuery(query);

			if (rs.next()) {
				result = result + "  customer still has book(s) on loan:";
				result = result + "\n    " + rs.getInt("ISBN") + ": " + rs.getString("Title").trim();

				Date date = rs.getDate("DueDate");
				if (date != null){
					result = result + "\n    Due Date: " + date.toString();
				} else {
					result = result + "\n    Due Date: ---";
				}

				while (rs.next()) {
					result = result + "\n      ---";
					result = result + "\n    " + rs.getInt("ISBN") + ": " + rs.getString("Title").trim();
					date = rs.getDate("DueDate");
					result = result + "\n    Due Date: " + date.toString();
				}

				s.close();

				return result;
			}

			query = "DELETE FROM Customer WHERE CustomerID = " + customerID + ";";
			int r = s.executeUpdate(query);

			s.close();

			if (r == 0) {
				result = result + "  no customer found";
			} else {
				result = result + "  Customer " + customerID + " deleted";
			}

		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	public String deleteAuthor(int authorID) {
		String result = "DELETE AUTHOR:\n";

		if (authorID == 0) {
			result = result + "  no author found";
			return result;
		}

		try {
			Statement s = con.createStatement();

			String query = "DELETE FROM Author WHERE AuthorID = " + authorID + ";";
			int r = s.executeUpdate(query);

			s.close();

			if (r == 0) {
				result = result + "  no author found";
			} else {
				result = result + "  Author " + authorID + " deleted";
			}

		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}


		return result;
	}

	public String deleteBook(int ISBN) {
		String result = "DELETE BOOK:\n";

		if (ISBN == 0) {
			result = result + "  no book found";
			return result;
		}

		try {
			Statement s = con.createStatement();

			String query = "SELECT CustomerID, L_Name, F_Name, City, DueDate FROM (SELECT CustomerID, DueDate FROM"
					+ " Cust_Book WHERE ISBN = " + ISBN + ") as Cust_Book NATURAL JOIN Customer;";
			ResultSet rs = s.executeQuery(query);

			if (rs.next()) {
				result = result + "  customers still have book on loan:";

				String city = rs.getString("City");
				if (city != null) {
					city = city.trim();
				} else {
					city = "---";
				}

				String fName = rs.getString("F_Name");
				if (fName != null) {
					fName = fName.trim();
				} else {
					fName = "---";
				}

				result = result + "\n    " + rs.getInt("CustomerID") + ": " + fName + " "
						+ rs.getString("L_Name").trim() + ", " + city;

				Date date = rs.getDate("DueDate");
				if (date != null) {
					result = result + "\n      Due Date: " + date.toString();
				} else {
					result = result + "\n      Due Date: ---";
				}

				while (rs.next()) {
					result = result + "\n    ---";

					city = rs.getString("City");
					if (city != null) {
						city = city.trim();
					} else {
						city = "---";
					}

					fName = rs.getString("F_Name");
					if (fName != null) {
						fName = fName.trim();
					} else {
						fName = "---";
					}

					result = result + "\n    " + rs.getInt("CustomerID") + ": " + fName + " "
							+ rs.getString("L_Name").trim() + ", " + city;

					date = rs.getDate("DueDate");
					if (date != null) {
						result = result + "\n      Due Date: " + date.toString();
					} else {
						result = result + "\n      Due Date: ---";
					}
				}

				s.close();

				return result;
			}

			query = "DELETE FROM Book WHERE ISBN = " + ISBN + ";";
			int r = s.executeUpdate(query);

			s.close();

			if (r == 0) {
				result = result + "  no book found";
			} else {
				result = result + "  Book " + ISBN + " deleted";
			}

		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return "There was a problem with the database, please contact admin";
		}

		return result;
	}

	// These methods made it easy for me to set up read only statements.

	private Statement openReadOnlyStatement() throws SQLException {
		con.setReadOnly(true);
		return con.createStatement();
	}

	private void closeReadOnlyStatement(Statement s) throws SQLException {
		s.close();
		con.setReadOnly(false);
	}

	// The following three methods just made it easier for me to lock, rollback and commit my changes.

	private Statement beginAndLock() throws SQLException {
		Statement s = con.createStatement();
		s.executeUpdate("BEGIN;");
		return s;
	}

	private void commitAndClose(Statement s) throws SQLException {
		s.executeUpdate("COMMIT;");
		s.close();
	}

	private void rollbackAndClose(Statement s) throws SQLException {
		s.executeUpdate("ROLLBACK;");
		s.close();
	}

	// I often want to print out my books in a nicely formatted way
	private String printBook(int ISBN, Book book, boolean authors, boolean customers) {
		String result = "  Title: " + book.title + "\n";
		result = result + "  ISBN: " + ISBN + "\n";
		result = result + "  Edition: ";

		result = result + parseEdition(book.editionNo) + "\n";

		if (authors) {
			if (book.names.size() == 0) {
				result = result + "  Author(s): None Listed";
			} else if (book.names.size() == 1) {
				result = result + "  Author: " + book.names.get(0) + " " + book.surnames.get(0);
			} else {
				result = result + "  Authors: ";
				result = result + book.names.get(0) + " " + book.surnames.get(0);

				for (int i = 1; i < book.names.size(); i++) {
					result = result + ", " + book.names.get(i) + " " + book.surnames.get(i);
				}
			}
			result = result + "\n";
		}

		result = result + "  " + book.numOfCop + " copies owned, " + book.numLeft + " copies available";

		if (customers) {
			result = result + "\n  Customers:";
			for (int i = 0; i < book.customerIDs.size(); i++) {
				result = result + "\n    " + book.customerIDs.get(i) + ": " + book.fNames.get(i) + " "
						+ book.lNames.get(i) + ", " + book.cities.get(i);
			}
		}

		return result;
	}

	// I often want to convert edition numbers like '1' to prober words like '1st'.
	private String parseEdition(int ed) {
		String result = "";

		switch (ed) {
		case (0):
			result = result + "---";
			break;
		case (1):
			result = result + "1st";
			break;
		case (2):
			result = result + "2nd";
			break;
		case (3):
			result = result + "3rd";
			break;
		default:
			// valid up to the 21st edition...
			result = result + ed + "th";
		}

		return result;
	}
}
