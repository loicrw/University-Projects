package courseProject;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.JTextField;


public class Admin {
	public static int activeAdmin = 0;
	public static int totalAdmins = 0;
	public static boolean adminLoggedIn = false;
	public static Admin[] allAdmins = new Admin[100];
	int adminID;
	String verifiedByAirport;
	private String username, password;

	//constructor for admin objects
	public Admin(int adminID, String verifiedByAirport) {
		this.adminID = adminID;
		this.verifiedByAirport = verifiedByAirport;
	}

	//constructor for admin objects
	public Admin(int adminID, String verifiedByAirport, String username, String password) {
		this.adminID = adminID;
		this.verifiedByAirport = verifiedByAirport;
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	//logon method for admins
	public static void logOnAdmin() {
		User.logOut(); //triggers method that resets all login values (NB: not only for users)

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

		//checks whether  input meets the login details of any admin
		case 0: {
			for (int i = 0; i < totalAdmins; i++) {
				//correct login, ends method and enables admin to continue
				if (username.getText().equals(allAdmins[i].getUsername())
						&& String.valueOf(password.getPassword()).equals(allAdmins[i].getPassword())) {
					activeAdmin = i;
					adminLoggedIn = true;
					JOptionPane.showMessageDialog(null, "Welcome back " + allAdmins[activeAdmin].getUsername(),
							"Login Success", JOptionPane.WARNING_MESSAGE);
					break;
				}
			}
			//incorrect login: start logOnAdmin method again
			if (adminLoggedIn != true) {
				JOptionPane.showConfirmDialog(null, "Incorrect login details: please try again ", "Login Failed",
						JOptionPane.CLOSED_OPTION);
				logOnAdmin();
			}

			break;
		}

		case 2: {
			System.exit(0);
		}

		}

	}

	//method that contains the menu for an admin that has logged in
	public static void adminMenu(){
		//GUI menu and user input
		String[] adminOptions = new String[] { "Add Customer", "Remove Customer", "View bookings", "Register new Admin"};
		int adminChoice = JOptionPane.showOptionDialog(null, "What would you like to do?",
				"Welcome Admin", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, adminOptions,
				adminOptions[0]);
		switch (adminChoice) {

		//cross button
		case -1:
			System.exit(0);

			//option that enables an admin to register new registered users (by accessing user class), then returns to the admin menu
		case 0:
			User.createUser();
			unregisteredCustomer.createRegisteredCustomer();
			adminMenu();

			//option that enables an admin to remove registered customers, then returns to the admin menu
		case 1:
			Admin.removeRegisteredCustomer();
			adminMenu();

			//option that enables an admin to view all bookings, then returns to the admin meny
		case 2:
			Admin.viewAllBookings();
			adminMenu();

			//option that enables an admin to register a new admin, then returns to the admin menu
		case 3:
			registerNewAdmin();
			adminMenu();
		}
	}


	//method that enables admin to remove registered customers based on their user ID
	public static void removeRegisteredCustomer() {

		//GUI and input of the user ID of the customer that must be removed
		JTextField removalIDString = new JTextField();
		int removalID = 0;
		boolean correctInput = false;
		while (correctInput == false) {
			try {
				Object[] removalDetails = { "Input the userID (Integer only) to remove the user: ", removalIDString };

				int cancel = JOptionPane.showConfirmDialog(null, removalDetails, "Delete a user", JOptionPane.OK_CANCEL_OPTION);
				if (cancel == 2) adminMenu(); //pressing cancel returns to the admin menu

				removalID = Integer.parseInt(removalIDString.getText());
				correctInput = true;
			} 

			//in case the input is incorrect, messages are displayed to indicate what went wrong
			catch (Exception e) {
				String[] editOrContinue = new String[] { "Try again", "Exit" };
				int registeredUserChoice = JOptionPane.showOptionDialog(null, "You can only input an integer!", "ERROR",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, editOrContinue,
						editOrContinue[0]);

				//enables admin to close the program 
				if (registeredUserChoice != 0) {
					System.exit(0);
				}

			}
		}

		RegisteredCustomer[] temporary1 = new RegisteredCustomer[200];
		int lineIndex = 0;
		int IDfound = 0;
		try {
			BufferedReader myFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("RegisteredUsers.txt"), "UTF-8"));
			myFile.mark(1);
			if (myFile.read() != 0xFEFF)
				myFile.reset();

			String sCurrentLine;
			String[] uCurrent = new String[8];
			while ((sCurrentLine = myFile.readLine()) != null) {
				uCurrent = sCurrentLine.split(",");

				//writes all the values into an array until a valid removal ID is found
				if (Integer.parseInt(uCurrent[0]) != removalID) {
					temporary1[lineIndex] = new RegisteredCustomer(Integer.parseInt(uCurrent[0]), (uCurrent[1]),
							uCurrent[2], uCurrent[3], uCurrent[4], uCurrent[5], uCurrent[6]);
					lineIndex++;

				} 
				//a matching ID is found, the loop breaks
				else {
					IDfound++;
				}
			}
			myFile.close();
		} catch (IOException e) {
			System.out.print("Wrong! (Reading)");
		}

		//new array to capture registered customer users objects after the user ID that is being removed
		RegisteredCustomer[] temporary2 = new RegisteredCustomer[lineIndex];
		//copies the array with values before the ID that is being removed into a new array
		System.arraycopy(temporary1, 0, temporary2, 0, lineIndex);
		//copies the array with all registered users (minus the removed user) into the customers array
		System.arraycopy(temporary2, 0, RegisteredCustomer.allRegisteredCustomers, 0, lineIndex);
		if (IDfound > 0) {
			RegisteredCustomer.totalRegisteredUsers--;
			JOptionPane.showMessageDialog(null, "You have removed the user with ID: " + removalID, "Removal Success",
					JOptionPane.WARNING_MESSAGE);

		} 
		//no valid ID was found
		else {
			String[] editOrContinue = new String[] { "Try again", "Exit" };
			int registeredUserChoice = JOptionPane.showOptionDialog(null,
					"No such user ID found, would you like to try again?", "ERROR", JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, editOrContinue, editOrContinue[0]);

			if (registeredUserChoice == 0) {
				removeRegisteredCustomer(); //reruns the admin to the start of the method
			} 
			else {
				System.exit(0);
			}
		}
		RegisteredCustomer.overWriteRegisteredUsers(); //triggers the method to write the new array (with the user removed) to the file

	}

	//method to view all bookings that have been made
	public static void viewAllBookings() {
		String details = "";

		//reads all bookings from the file, stores them in the details string variable and displays this into GUI
		try {
			BufferedReader myFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("Bookings.txt"), "UTF-8"));
			myFile.mark(1);
			if (myFile.read() != 0xFEFF)
				myFile.reset();

			String sCurrentline;
			String[] uCurrent = new String[7];
			while ((sCurrentline = myFile.readLine()) != null) {
				uCurrent = sCurrentline.split(",");
				details += "Booking ID: " + uCurrent[0] + " User: " + uCurrent[1] + " Departure: " + uCurrent[2] + " Destination: " + uCurrent[3] + " Flight:  " + uCurrent[4];
				if (Integer.parseInt(uCurrent[5]) != 0) details+= " Flight 2: " + uCurrent[5];
				if (Integer.parseInt(uCurrent[6]) != 0) details+= " Flight 3: " + uCurrent[6];
				details+= " Seats: "+ uCurrent[7] +"<br />"; //parses information from the file in the details variable
			}
			myFile.close();
		} catch (Exception e) {

			System.out.println("A problem occured when trying to read the Bookings.txt file!");
		}

		String message =  "<html><font color=blue>" + details+ "</font></html>"; //displays the String variable into the GUI

		JOptionPane.showMessageDialog(null, message, "Book Details", JOptionPane.PLAIN_MESSAGE);
	}

	//reads the file, creates admin objects and stores them into a static array
	public static void loadAdmins() {
		Admin[] temp = new Admin[100];
		int i = 0;
		try {
			BufferedReader myFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("Admins.txt"), "UTF-8"));
			myFile.mark(1);
			if (myFile.read() != 0xFEFF)
				myFile.reset();

			String sCurrentLine;
			String[] uCurrent = new String[4];
			while ((sCurrentLine = myFile.readLine()) != null) {
				uCurrent = sCurrentLine.split(",");
				temp[i] = new Admin(Integer.parseInt(uCurrent[0]), uCurrent[1], uCurrent[2], uCurrent[3]);
				i++;
			}

			myFile.close();
		} catch (IOException e) {
			System.out.print("Wrong! (Reading)");
		}
		Admin.totalAdmins = i;
		System.arraycopy(temp, 0, allAdmins, 0, i);
	}

	//method to register a new admin
	public static void registerNewAdmin(){

		String[] uniqueDepartures = Flight.uniqueDepartureAirport.toArray(new String[0]);

		//inputs from user (and the ID generator)
		int adminID = adminIDgenerator();
		JComboBox<String> verifiedByAirport = new JComboBox<>(uniqueDepartures);
		JTextField username = new JTextField();
		JPasswordField password = new JPasswordField();
		password.setEchoChar('*');

		Object[] adminInfo = { "You have been assigned the following ID:", adminID, "Verified by airport: ", verifiedByAirport, "Username: ", username, "Password: ",
				password};
		int response = JOptionPane.showConfirmDialog(null, adminInfo, "Create a new Admin", JOptionPane.OK_CANCEL_OPTION);
		//cancel button returns to admin menu
		if (response == 2) adminMenu();

//conditions that check whether criteria for input are being met, if valid it creates an object
		try {

			if (usernameUnique(username.getText()) == false) {
				int b = JOptionPane.showConfirmDialog(null, "The username you entered is already in use",
						"Error: Invalid inputs", JOptionPane.WARNING_MESSAGE);
				if (b == 0) {
					registerNewAdmin();
				}
			}
			if ((username.getText().length() <= 4 
					|| String.valueOf(password.getPassword()).length() <= 4)) {
				JOptionPane.showMessageDialog(null, "Your username, email, and password must be at least 5 characters!", "Registration Failed!",
						JOptionPane.WARNING_MESSAGE);
				registerNewAdmin();
			}

			//if input is valid, an object is created
			if (usernameUnique(username.getText()) == true 
					&& username.getText().length() > 4
					&& String.valueOf(password.getPassword()).length() > 4) {
				allAdmins[totalAdmins] = new Admin(
						(adminID), (String) verifiedByAirport.getSelectedItem(), username.getText(), String.valueOf(password.getPassword()));
				totalAdmins++;
				overWriteAdmins();
				JOptionPane.showMessageDialog(null, "The new admin has been created", "Registration Success!",
						JOptionPane.WARNING_MESSAGE);
				adminMenu();
			} 
			
			//cross button
			if (response == -1) System.exit(0);
			
			//cancel button returns to admin menu
			if (response == 2) adminMenu();

		} 
		catch (Exception e) {
			int a = JOptionPane.showConfirmDialog(null, "You had invalid inputs, Please try again",
					"Error: Invalid inputs", JOptionPane.WARNING_MESSAGE);
			if (a == 0) {
				registerNewAdmin();
			}
		}

	}

	//method that checks whether the username of an admin is unique
	public static boolean usernameUnique(String adminUsernameInput) {
		for (int i = 0; i < totalAdmins; i++) {
			if (adminUsernameInput.equals(allAdmins[i].getUsername())) {
				return false;
			}
		}
		return true;
	}

	//writes the new array of admins to the file
	public static void overWriteAdmins() {
		try {
			PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter("Admins.txt", false)));
			for (int i = 0; i < totalAdmins; i++) {
				wr.println(allAdmins[i].adminID + "," + allAdmins[i].verifiedByAirport + ","
						+ allAdmins[i].getUsername() + "," + allAdmins[i].getPassword());
			}
			wr.close();
		} catch (IOException e) {
			System.out.println("There is an I/O Problem!");
		}
	}
	
	//creates random numbers and checks whether it is a unique ID by inputting it in the checkinunique method
	public static int adminIDgenerator() {
		int a = (int) Math.floor(Math.random() * (999 - 100 + 1)) + 100;
		if (checkIfUnique(a) == false) {
			adminIDgenerator();
		}
		return a;
	}
	
	//method that checks whether the inputID of an admin is unique
	public static boolean checkIfUnique(int inputID) {
		for (int i = 0; i < totalAdmins; i++) {
			if (inputID == allAdmins[i].adminID) {
				return false;
			}
		}
		return true;
	}
}