package library;

import java.util.ArrayList;
/*
 * This is my Book class for storing Books and details about Books. While it is not the best form to have all fields
 * public, the Books do not persist after they have been created by a method so it is safe to say no other method will
 * access the data. The ArrayLists assume all authors and customers will have all their details added in the same
 * sequence and that sequence will not be manipulated, so it is assume, for example, to say that the customerID in the
 * fifth position will match the lName, fName and city in the fifth position of their respective arrays.
 */
public class Book {

	public String title;
	public int editionNo;
	public int numOfCop;
	public int numLeft;

	// Authors
	public ArrayList<String> names = new ArrayList<String>();
	public ArrayList<String> surnames = new ArrayList<String>();

	// Customers
	public ArrayList<Integer> customerIDs = new ArrayList<Integer>();
	public ArrayList<String> lNames = new ArrayList<String>();
	public ArrayList<String> fNames = new ArrayList<String>();
	public ArrayList<String> cities = new ArrayList<String>();

	public Book(String title, int editionNo, int numOfCop, int numLeft) {
		this.title = title;
		this.editionNo = editionNo;
		this.numOfCop = numOfCop;
		this.numLeft = numLeft;
	}

	public void addAuthor(String name, String surname) {
		names.add(name);
		surnames.add(surname);
	}

	public void addCustomer(int customerID, String lName, String fName, String city) {
		customerIDs.add(customerID);
		lNames.add(lName);
		fNames.add(fName);
		cities.add(city);
	}
}
