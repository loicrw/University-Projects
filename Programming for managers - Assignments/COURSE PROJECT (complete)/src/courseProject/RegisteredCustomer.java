package courseProject;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public class RegisteredCustomer extends User {
	public static int activeUser = 0;
	public static boolean registeredUserLoggedIn = false;
	public static int totalRegisteredUsers = 0;
	public static RegisteredCustomer[] allRegisteredCustomers = new RegisteredCustomer[200];

	static Scanner userInputString = new Scanner(System.in);
	String firstName, lastName, email, preferenceDeparture;
	private String username, password;

	RegisteredCustomer(int ID, String email, String username, String password, String firstName, String lastName, String preferenceDeparture) {
		super(ID);
		this.email = email;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.preferenceDeparture = preferenceDeparture;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	//takes the username and password from the user
	public static void logOnRegisteredUser() {
		//clears all previous login data (in case of logging in after just having registered an account)
		User.logOut();

		//user input
		JTextField username = new JTextField();
		JPasswordField password = new JPasswordField();
		password.setEchoChar('*');

		//GUI
		Object[] logindetails = { "Username:", username, "Password:", password };
		int clickedOn = JOptionPane.showConfirmDialog(null, logindetails, "Login", JOptionPane.OK_CANCEL_OPTION);

		switch (clickedOn) {

		//cross button
		case -1: {
			System.exit(0);

		}

		//checks whether user input meets the login details of any registered user
		case 0: {
			for (int i = 0; i < totalRegisteredUsers; i++) {

				//correct login, ends method and enables user to continue
				if (username.getText().equals(allRegisteredCustomers[i].getUsername())
						&& String.valueOf(password.getPassword()).equals(allRegisteredCustomers[i].getPassword())) {
					activeUser = i;
					registeredUserLoggedIn = true;
					JOptionPane.showMessageDialog(null,
							"Welcome back " + allRegisteredCustomers[activeUser].getUsername(), "Login Success",
							JOptionPane.WARNING_MESSAGE);
				} 
				//incorrect login: enables user to try again
				else if (i == (totalRegisteredUsers - 1) && registeredUserLoggedIn != true) {
					JOptionPane.showConfirmDialog(null, "Incorrect login details: please try again ", "Login Failed",
							JOptionPane.CLOSED_OPTION);
					logOnRegisteredUser();
				}

			}
			break;
		}

		//cancel button closes the program
		case 2: {
			System.exit(0);
		}

		}

	}

	//method that contains the menu for a registered customer that has logged in
	public static void registeredCustomerMenu() {

		//menu and user input
		String[] registeredUserOptions = new String[] { "Change preference", "Continue Search", "View my bookings" };
		int registeredUserChoice = JOptionPane.showOptionDialog(null,
				"Current departure preference: " + RegisteredCustomer.getPreference()
				+ "\nWould you like to change your preference?",
				"Changing departure preference", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				registeredUserOptions, registeredUserOptions[0]);

		switch (registeredUserChoice) {

		//cross button
		case -1:
			System.exit(0);

			//set or adjust the preferred airport of departure (as an optional variable for registered customers), after completion it triggers the searchFlight method and book a flight from the results
		case 0:
			RegisteredCustomer.savePreference();
			if (User.searchFlights() == true) {
				User.bookflight();
			}
			break;

			//triggers searchflight method, then enables the user to book one of the search results
		case 1:
			if (User.searchFlights() == true) {
				User.bookflight();
			}
			break;

			//displays the bookings that were made by the user
		case 2: 
			int totalBookings = 0;
			String details = "";
			for (int i = 0; i < Booking.totalBookings; i++) {
				if (Booking.allBookings[i].userID.equals(Integer.toString(allRegisteredCustomers[activeUser].userID))) {
					details += "Booking ID: " + Booking.allBookings[i].bookingID + " Departure: " + Booking.allBookings[i].departureAirport
							+ " Destination: " + Booking.allBookings[i].destinationAirport + " Flight: " + Booking.allBookings[i].flightID;
					//only printed in case of 1 stop-indirect flight
					if (Booking.allBookings[i].flightID2 != 0) details += " Flight 2: " + Booking.allBookings[i].flightID2;
					//only printed in case of 2 stop-indirect flight, print these details too
					if (Booking.allBookings[i].flightID3 != 0) details += " Flight 3: " + Booking.allBookings[i].flightID3;
					details += Booking.allBookings[i].seats + "<br />";
					totalBookings++;
				}
			}
			
			//GUI in which flight details are displayed
			if (totalBookings > 0) {
				String message = "<html><font color=blue>" + details + "</font></html>";
				JOptionPane.showMessageDialog(null, message, "Book Details", JOptionPane.PLAIN_MESSAGE);
			}
			
			//in case the user hasn't made any bookings and returns the user to the registered customer menu
			if (totalBookings == 0)
				JOptionPane.showConfirmDialog(null, "You have no bookings saved", "Error", JOptionPane.CLOSED_OPTION);
			registeredCustomerMenu();
		}
	}

	//checks whether a preference is stored for a registered user (in the file). If so, it returns the preference airport. If not, it returns that no preference has been set.
	public static String getPreference() {
		String preference = "null";

		try {

			BufferedReader myFile = new BufferedReader(new FileReader("RegisteredUsers.txt"));
			String sCurrentLine;
			String[] uCurrent = new String[8];
			while ((sCurrentLine = myFile.readLine()) != null) {
				uCurrent = sCurrentLine.split(",");
				if (Integer.parseInt(uCurrent[0]) == allRegisteredCustomers[activeUser].userID) {
					preference = uCurrent[6];
				}
			}
			myFile.close();
		} catch (IOException e) {
			System.out.print("Wrong! (Reading)");
		}
		if (preference.equals("null")) {
			return "no preference set";
		} else {
			return preference;
		}
	}

//method that enables a user to set preference departure airport and saves it to the file
	public static void savePreference() { 

		//dropbox meny in GUI that enables user to select a preference departure airport
		String[] uniqueDepartures = Flight.uniqueDepartureAirport.toArray(new String[0]);
		JComboBox<String> preference = new JComboBox<>(uniqueDepartures);

		Object[] preferencedetails = { "What is your preference departure?", preference };

		int a = JOptionPane.showConfirmDialog(null, preferencedetails, "Input Preference", JOptionPane.CLOSED_OPTION);

		//cross button
		if (a == -1)
		{
			System.exit(0);
		}

		allRegisteredCustomers[activeUser].preferenceDeparture = (String) preference.getSelectedItem(); //changes the preference value in the static array

		overWriteRegisteredUsers(); //triggers method to store the preference value in the file
	}

	//method that overwrites the registered users file (e.g. when an admin removes a registered user)
	public static void overWriteRegisteredUsers() {
		try {
			PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter("RegisteredUsers.txt", false)));
			for (int i = 0; i < totalRegisteredUsers; i++) {
				wr.println(allRegisteredCustomers[i].userID + "," + allRegisteredCustomers[i].email + ","
						+ allRegisteredCustomers[i].getUsername() + "," + allRegisteredCustomers[i].getPassword() + ","
						+ allRegisteredCustomers[i].firstName + "," + allRegisteredCustomers[i].lastName + "," + allRegisteredCustomers[i].preferenceDeparture);
			}
			wr.close();
		} catch (IOException e) {
			System.out.println("There is an I/O Problem!");
		}
	}

	// Reads the amount of registeredUsers and updates the static variable
	// "totalRegisteredUsers"
	public static void loadRegisteredUserCount() {
		int userCount = 0;
		try {
			BufferedReader myFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("RegisteredUsers.txt"), "UTF-8"));
			myFile.mark(1);
			if (myFile.read() != 0xFEFF)
				myFile.reset();

			@SuppressWarnings("unused")
			String sCurrentLine;
			while ((sCurrentLine = myFile.readLine()) != null) {
				userCount++;
			}
			myFile.close();
		} catch (IOException e) {
			System.out.print("Wrong! (Reading)");
		}
		totalRegisteredUsers = userCount;
	}

	// Copies the array from a file into the object array "allRegisteredCustomers"
	// and updates it
	public static void loadRegisteredUser() {
		RegisteredCustomer[] temporary1 = new RegisteredCustomer[200];
		int lineIndex = 0;
		try {
			BufferedReader myFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("RegisteredUsers.txt"), "UTF-8"));
			myFile.mark(1);
			if (myFile.read() != 0xFEFF)
				myFile.reset();

			String sCurrentLine;
			String[] uCurrent = new String[4];
			while ((sCurrentLine = myFile.readLine()) != null) {
				uCurrent = sCurrentLine.split(",");
				temporary1[lineIndex] = new RegisteredCustomer(Integer.parseInt(uCurrent[0]), (uCurrent[1]),
						uCurrent[2], uCurrent[3], uCurrent[4], uCurrent[5], uCurrent[6]);
				lineIndex++;
			}
			myFile.close();
		} catch (IOException e) {
			System.out.print("Wrong! (Reading)");
		}
		RegisteredCustomer[] temporary2 = new RegisteredCustomer[lineIndex];
		System.arraycopy(temporary1, 0, temporary2, 0, lineIndex);
		System.arraycopy(temporary2, 0, allRegisteredCustomers, 0, lineIndex);

	}

}