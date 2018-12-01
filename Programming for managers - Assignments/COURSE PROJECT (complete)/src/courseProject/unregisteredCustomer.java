package courseProject;

import java.util.regex.Pattern;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class unregisteredCustomer extends User {

	unregisteredCustomer(int ID) {
		super(ID);

	}

	// this method introduces the user and asks whether he would like to register
	public static void introduction() {
		unregisteredCustomer.createUser();
		String[] introOptions = new String[] { "Yes", "No, continue to search" };
		int response = JOptionPane.showOptionDialog(null, "Would you like to register?",
				"You have been assigned the user ID: " + User.allUsers[User.totalUsers - 1].userID,
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, introOptions, introOptions[0]);

		//registers a new user
		if (response == 0) {
			createRegisteredCustomer();
			resetResult();

			//forwards user to a screen that enables him to log in and continue as a registered user that is logged in
			RegisteredCustomer.logOnRegisteredUser();
			RegisteredCustomer.registeredCustomerMenu();
		}

		//enables an unregistered user to search flights and books
		if (response == 1) {
			if (User.searchFlights() ==true) User.bookflight();
		}

		//cross button
		if (response == -1) {
			System.exit(0);
		}
	}

	//takes input from the user, checks whether it meets the criteria. If so, it creates an account. If not, it displays the error type and restarts the method.
	public static void createRegisteredCustomer() {

		//User input
		JTextField email = new JTextField();
		JTextField username = new JTextField();
		JPasswordField password = new JPasswordField();
		password.setEchoChar('*');
		Object[] registerInfo = { "e-mail: ", email, "Username: ", username, "Password: ", password };
		int response = JOptionPane.showConfirmDialog(null, registerInfo, "Input your account details", JOptionPane.OK_CANCEL_OPTION);

		//cross button
		if (response == -1) System.exit(0);

		//if admin; takes you back to admin menu. if not, to introduction.
		if (Admin.adminLoggedIn == true && response == 2) Admin.adminMenu();
		else if (response == 2)  introduction();

		//checks whether criteria are met
		boolean check = checkIfCorrect (email, username, password);
		if (check == false) createRegisteredCustomer();
		else createAccount(email, username, password);

	}

	//creates an account and initiates method to write the input to a file
	public static void createAccount (JTextField email, JTextField username, JTextField password) {
		RegisteredCustomer.allRegisteredCustomers[RegisteredCustomer.totalRegisteredUsers] = new RegisteredCustomer(allUsers[totalUsers - 1].userID,
				email.getText(), username.getText(), String.valueOf(((JPasswordField) password).getPassword()), null, null, null);
		RegisteredCustomer.totalRegisteredUsers++;
		RegisteredCustomer.overWriteRegisteredUsers();

		//prevents users that registered from being counted double
		RegisteredCustomer.activeUser = RegisteredCustomer.totalRegisteredUsers - 1;
		JOptionPane.showMessageDialog(null, "Congratulations, your account has been created!\ne-mail: "
				+ email.getText() + "\nUsername: " + username.getText() + "\nPassword: " + String.valueOf(((JPasswordField) password).getPassword()));
	}


	//conditions that must be met to create an account. If method returns false it first displays what the user did wrong.
	public static boolean checkIfCorrect (JTextField email, JTextField username, JTextField password){

		if (usernameUnique(username.getText()) == true 
				&& username.getText().length() > 4 
				&& emailUnique(email.getText()) == true 
				&& email.getText().length() > 4 
				&& Pattern.matches("^[^@]*@[^@]*$", email.getText()) == false 
				&& String.valueOf(((JPasswordField) password).getPassword()).length() > 4) {
			JOptionPane.showMessageDialog(null, "Please enter a valid email address", "Registration Failed!",
					JOptionPane.WARNING_MESSAGE);
			return false;
		} 

		if (username.getText().length() <= 4 
				|| email.getText().length() <= 4 
				|| String.valueOf(((JPasswordField) password).getPassword()).length() <= 4) {
			JOptionPane.showMessageDialog(null, "Your username, email, and password must be at least 5 characters!", "Registration Failed!",
					JOptionPane.WARNING_MESSAGE);
			return false;
		} 

		if (emailUnique(email.getText()) == false){
			JOptionPane.showMessageDialog(null, "Error: Email address already exists", "Registration Failed!",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (usernameUnique(username.getText()) == false){
			JOptionPane.showMessageDialog(null, "Error: username already exists", "Registration Failed!",
					JOptionPane.WARNING_MESSAGE);
			return false;
		} 

		if (usernameUnique(username.getText()) == true 
				&& username.getText().length() > 4
				&& emailUnique(email.getText()) == true 
				&& email.getText().length() > 4 
				&& Pattern.matches("^[^@]*@[^@]*$", email.getText()) == true 
				&& String.valueOf(((JPasswordField) password).getPassword()).length() > 4) 
			return true;

		//in case of any unexpected errors
		else return false;
	}

	// checks if username is unique
	public static boolean usernameUnique(String usernameInput) {
		for (int i = 0; i < RegisteredCustomer.totalRegisteredUsers; i++) {
			if (usernameInput.equals(RegisteredCustomer.allRegisteredCustomers[i].getUsername())) {
				return false;
			}
		}
		return true;
	}

	//checks if email is unique 
	public static boolean emailUnique(String emailInput) {
		for (int i = 0; i < RegisteredCustomer.totalRegisteredUsers; i++) {
			if (emailInput.equals(RegisteredCustomer.allRegisteredCustomers[i].email)) {
				return false;
			}
		}
		return true;
	}
}