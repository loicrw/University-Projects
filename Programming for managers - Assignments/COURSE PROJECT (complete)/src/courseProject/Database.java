package courseProject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JOptionPane;

public class Database {

	public static void main(String[] args) {
		User.logOut();
		
		//loads all data into static arrays that are accessible to relevant methods
		loadAllData();
		
		//takes the user type from user input: unregistered customer, registered customer, admin
		int userChoice = getUserType();
		switch (userChoice) {

		//cross button
		case -1:
			System.exit(0);
			
		case 0:
			unregisteredCustomer.introduction();

			break;
		case 1:
			RegisteredCustomer.logOnRegisteredUser();
			RegisteredCustomer.registeredCustomerMenu();
			break;

		case 2:
			Admin.logOnAdmin();
			Admin.adminMenu();
			break;
		}

	}

	public static int getUserType() {
		String[] options = new String[] { "New User", "Registered User", "Admin" };
		int response = JOptionPane.showOptionDialog(null, "Are you a new user, returning user, or admin?",
				"Welcome to Flight Finder", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
				options[0]);
		return (response);
	}

	public static void loadAllData() {
		Booking.loadBookings();
		Admin.loadAdmins();
		Flight.loadFlights();
		Flight.loadFlightEntries();
		RegisteredCustomer.loadRegisteredUserCount();
		RegisteredCustomer.loadRegisteredUser();
		Flight.getUniqueArrivals();
		Flight.getUniqueDepartures();
	}
	
	public static String[] convertDates (String date){

		
		SimpleDateFormat setDate = new SimpleDateFormat("dd/MM/yyyy HH:mm"); //this is to read the date from a string, it needs to be the same as what we have in our file
		SimpleDateFormat searchDate = new SimpleDateFormat("dd/MM/yyyy");
		
		String[] dates = new String[1];

		try{
			Calendar temp = Calendar.getInstance(); //creates a new calendar
			temp.setTime(setDate.parse(date)); //and sets its date to the input of the method

			dates[0] = searchDate.format(temp.getTime()); //this entry contains a workable version of dates&time in the file

		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("There is a ParseException");
		}
		return dates;
	}
}