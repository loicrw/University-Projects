package courseProject;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class User {
	public static int totalUsers = 0;
	public static User[] allUsers = new User[200];

	int userID;
	static Scanner userInputString = new Scanner(System.in);
	public static Flight[] flightResults = new Flight[100];
	public static Flight[] searchResultDirect = new Flight[100];
	public static boolean resultFound = false;
	public static int seatsChosen = 0;
	public static int totalMatches = 0;
	public static double[][] searchResult = new double[100][6]; //array of arrays that is filled with indexes for flights, prices, stopovers and duration
	public static boolean descending;
	public static int uglyCounter;

	public User(int ID) {
		this.userID = ID;
	}

	//method for searching flights
	@SuppressWarnings("rawtypes")
	public static boolean searchFlights() {
		//resets the searching parameters to enable a new search
		resetResult();

		//enables the dropdown menu for departure airports
		String[] departureExamples = Flight.uniqueDepartureAirport.toArray(new String[0]);
		JComboBox<String> departureAirport = new JComboBox<>(departureExamples);

		//checks whether a preference airport was set, if not it sets it to default (the top one: Amsterdam)
		if (RegisteredCustomer.registeredUserLoggedIn == true) {
			if (RegisteredCustomer.getPreference().equals("null")) {
				JComboBox<String> departureAirport1 = new JComboBox<>(departureExamples);
				departureAirport = departureAirport1;

			} 
			//if a preference has been set, it sets this preference as the default departure airport
			else {
				departureAirport.setSelectedItem(RegisteredCustomer.getPreference());
			}
		}

		//options in dropdown menus for the amount of seats and the sorting options
		String[] totalNumberOfSeats = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		String[] sortOptions = { "Price ascending", "Price descending", "Least no. of stops", "Most no. of stops",
				"Fastest", "Slowest" };

		//dropdown for arrival airports
		String[] arrivalExamples = Flight.uniqueArrivalAirport.toArray(new String[0]);
		JComboBox<String> arrivalAirport = new JComboBox<>(arrivalExamples);

		//textfield in which the user must type the date
		JTextField departureDate = new JTextField();

		//dropdown menus for seats and sorting options
		JComboBox<String> seats = new JComboBox<>(totalNumberOfSeats);
		JComboBox<String> sort = new JComboBox<>(sortOptions);

		//instructions for user input fields (textfield and dropdown)
		Object[] searchInfo = {"Departure Airport: ", departureAirport, "Arrival Airport: ", arrivalAirport,
				"Departure Date (DD/MM/YYYY): ", departureDate, "Passengers: ", seats, "Sort by: ", sort };
		int cancel =	 JOptionPane.showConfirmDialog(null, searchInfo, "Search Details", JOptionPane.OK_CANCEL_OPTION);

		//cross button and cancel button
		if (cancel == -1 || cancel == 2) System.exit(0);

		//takes the user input and uses it as input for the different searching methods
		User.seatsChosen = Integer.parseInt((String) seats.getSelectedItem());
		searchDirect(departureAirport, arrivalAirport, departureDate, seatsChosen);
		searchOneStop(departureAirport, arrivalAirport, departureDate, seatsChosen);
		searchTwoStops(departureAirport, arrivalAirport, departureDate, seatsChosen);

		//enables sorting on descending prices and sets a static boolean so this is taken into account when displaying the results
		if (sort.getSelectedItem() == "Price descending") {
			sortOnPriceDescending();
			descending = true;
		}
		//enables sorting on ascending prices
		if (sort.getSelectedItem() == "Price ascending")
			sortOnPriceAscending();

		//enables sorting from most to least no. of stops and sets a static boolean so this is taken into account when displaying the results
		if (sort.getSelectedItem() == "Most no. of stops") {
			sortOnStopsDescending();
			descending = true;
		}
		//enables sorting from least to most no. of stops
		if (sort.getSelectedItem() == "Least no. of stops")
			sortOnStopsAscending();

		//enables sorting from long to short travel duration and sets a static boolean so this is taken into account when displaying the results
		if (sort.getSelectedItem() == "Slowest") {
			sortOnTimeDescending();
			descending = true;
		}

		//enables sorting from short to long travel duration
		if (sort.getSelectedItem() == "Fastest")
			sortOnTimeAscending();


		//checks whether any flights were found
		if (totalMatches > 0) {

			//method that displays the flight results in GUI
			putFlights();

			String[] shortenedLD = new String[totalMatches];
			System.arraycopy(Flight.inputToLD, 0, shortenedLD, 0, totalMatches);

			//enables the user to select a flight for booking in the GUI
			@SuppressWarnings("unchecked")
			JList<?> list = new JList(shortenedLD);
			ListDialog dialog = new ListDialog("Please select an item in the list: ", list);
			dialog.setOnOk(e -> ListDialog.chosenFlight = (int) dialog.getSelectedItem());
			dialog.show();
			resultFound = true;
			return resultFound;

		} 

		//message that is displayed in case no results were found, then enables the user to search again by rerunning the method
		else {
			String[] options = new String[] { "Yes, Search Again", "No, Exit" };
			int response = JOptionPane.showOptionDialog(null, "No results found, would you like to search again?",
					"Welcome to Flight Finder", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
					options[0]);
			if (response == 0) {
				resetResult();
				searchFlights();
			} 
			else {
				System.exit(0);
			}
			return resultFound;
		}
	}

	//method that searches for direct flights by taking the user input
	public static void searchDirect(JComboBox<String> departureAirport, JComboBox<String> arrivalAirport,
			JTextField departureDate, int seatsChosen) {

		for (int i = 0; i < Flight.totalFlightEntries; i++) {
			if (Flight.allFlights[i].departureAirport.equals(departureAirport.getSelectedItem()) //checks if departure airport of any flight is identical to user input
					&& Flight.allFlights[i].arrivalAirport.equals(arrivalAirport.getSelectedItem()) //checks if arrival airport of any flight is identical to user input
					&& Database.convertDates(Flight.allFlights[i].departureTime)[0].equals(departureDate.getText()) //checks if departure date (which is concerted to the right format) of any flight is identical to user input
					&& Flight.allFlights[i].availableSeats >= seatsChosen) { //checks if the requested amount of seats is still available for a flight
				searchResult[totalMatches][0] = i; //returns flight index in the static array of flights that was loaded from the file
				searchResult[totalMatches][3] = Flight.allFlights[i].price; //stores the price for the flight
				searchResult[totalMatches][4] = 0; //stores the amount of stopovers, which is zero in the case of a direct flight
				searchResult[totalMatches][5] = duration(Flight.allFlights[i].departureTime,
						Flight.allFlights[i].arrivalTime); //stores the duration of a flight by calculation the distance between departure time and arrival time at destination
				totalMatches++;//increments the counter for matches that have been found
			}
		}
	}

	//method that searches for indirect flights with one stop
	public static void searchOneStop(JComboBox<String> departureAirport, JComboBox<String> arrivalAirport,
			JTextField departureDate, int seatsChosen) {
		//checks whether flights are departing from the desired departure airport, regardless of the arrival airport
		for (int i = 0; i < Flight.totalFlightEntries; i++) {
			if (Flight.allFlights[i].departureAirport.equals(departureAirport.getSelectedItem())
					&& !(Flight.allFlights[i].arrivalAirport.equals(arrivalAirport.getSelectedItem()))
					&& Database.convertDates(Flight.allFlights[i].departureTime)[0].equals(departureDate.getText())
					&& Flight.allFlights[i].availableSeats >= seatsChosen
					) {
				//for all the flights found in the previous loop, it checks whether any of the flights correspond the desired arrival airport of the user
				for (int k = 0; k < Flight.totalFlightEntries; k++) {
					if (!(Flight.allFlights[k].departureAirport.equals(departureAirport.getSelectedItem()))
							&& Flight.allFlights[k].arrivalAirport.equals(arrivalAirport.getSelectedItem()) 
							&& Database.convertDates(Flight.allFlights[k].departureTime)[0]
									.equals(departureDate.getText()) // identical departure date
									&& duration(Flight.allFlights[i].arrivalTime, Flight.allFlights[k].departureTime) > 59 // only display results with a minimal layover time of 60 minutes
									&& duration(Flight.allFlights[i].arrivalTime, Flight.allFlights[k].departureTime) < 481 // only display results with a maximal layover time of 8 hours
									&& Flight.allFlights[k].availableSeats >= seatsChosen
									&& Flight.allFlights[k].departureAirport.equals(Flight.allFlights[i].arrivalAirport)) { 
						searchResult[totalMatches][0] = i; //stores the index of the first flight 
						searchResult[totalMatches][1] = k; //stores the index of the second flight
						searchResult[totalMatches][3] = Flight.allFlights[i].price + Flight.allFlights[k].price;
						searchResult[totalMatches][4] = 1; //stores the amount of stops
						searchResult[totalMatches][5] = duration(Flight.allFlights[i].departureTime,
								Flight.allFlights[k].arrivalTime); //calculates difference between departure airport and the final arrival airport and stores this value
						totalMatches++;
					}
				}
			}
		}
	}

	//method that searches for indirect flights with two stops
	public static void searchTwoStops(JComboBox<String> departureAirport, JComboBox<String> arrivalAirport,
			JTextField departureDate, int seatsChosen) {
		//checks whether flights are departing from the desired departure airport, regardless of the arrival airport
		for (int i = 0; i < Flight.totalFlightEntries; i++) {
			if (Flight.allFlights[i].departureAirport.equals(departureAirport.getSelectedItem())
					&& !(Flight.allFlights[i].arrivalAirport.equals(arrivalAirport.getSelectedItem()))
					&& Database.convertDates(Flight.allFlights[i].departureTime)[0].equals(departureDate.getText())
					&& Flight.allFlights[i].availableSeats >= seatsChosen) {
				//for all the flights found in the previous loop, it checks whether any of the flights depart within the time limit (same date or layover time)
				for (int k = 0; k < Flight.totalFlightEntries; k++) {
					if (!(Flight.allFlights[k].departureAirport.equals(departureAirport.getSelectedItem()))
							&& !(Flight.allFlights[k].arrivalAirport.equals(arrivalAirport.getSelectedItem()))
							&& Database.convertDates(Flight.allFlights[k].departureTime)[0]
									.equals(departureDate.getText())
									&& duration(Flight.allFlights[i].arrivalTime, Flight.allFlights[k].departureTime) > 59
									&& duration(Flight.allFlights[i].arrivalTime, Flight.allFlights[k].departureTime) < 481
									&& Flight.allFlights[k].availableSeats >= seatsChosen
									&& Flight.allFlights[k].departureAirport.equals(Flight.allFlights[i].arrivalAirport)) {
						//for all the flights found in the previous loop, it checks whether any of the flights correspond to the user's desired arrival airport
						for (int m = 0; m < Flight.totalFlightEntries; m++) {
							if (!(Flight.allFlights[m].departureAirport.equals(departureAirport.getSelectedItem()))
									&& Flight.allFlights[m].arrivalAirport.equals(arrivalAirport.getSelectedItem())
									&& Database.convertDates(Flight.allFlights[m].departureTime)[0]
											.equals(departureDate.getText())
											&& duration(Flight.allFlights[k].arrivalTime,
													Flight.allFlights[m].departureTime) > 59
											&& duration(Flight.allFlights[k].arrivalTime,
													Flight.allFlights[m].departureTime) < 481
											&& Flight.allFlights[m].availableSeats >= seatsChosen
											&& Flight.allFlights[m].departureAirport
											.equals(Flight.allFlights[k].arrivalAirport)) {
								searchResult[totalMatches][0] = i; //stores index of the first flight
								searchResult[totalMatches][1] = k; //stores index of the second flight
								searchResult[totalMatches][2] = m; //stores index of the last flight
								searchResult[totalMatches][3] = Flight.allFlights[i].price + Flight.allFlights[k].price
										+ Flight.allFlights[m].price;
								searchResult[totalMatches][4] = 2; //stores the amount of stopovers
								searchResult[totalMatches][5] = duration(Flight.allFlights[i].departureTime,
										Flight.allFlights[m].arrivalTime); //calculates difference between departure airport and the final arrival airport and stores this value
								totalMatches++;
							}
						}
					}
				}
			}
		}
	}


	//GUI for showing flight result and allowing the user to click, select and book a flight
	public static void putFlights() {
		int indexLD = 0;

		//looks for and counts the number default values in the search results
		for (int a = 0; a < searchResult.length; a++) {
			if (searchResult[a][0] == 99999.0) {
				uglyCounter++;
				continue;
			}

			String detail1, detail2, detail3, detail4, detail5;

			//prints general information of the flight in GUI
			detail1 = "Departure: " + Flight.allFlights[(int) searchResult[a][0]].departureAirport + "<br />";
			detail2 = "Destination: ";
			if ((int) searchResult[a][1] == 99999)
				detail2 += Flight.allFlights[(int) searchResult[a][0]].arrivalAirport + "<br />";
			else if ((int) searchResult[a][2] == 99999)
				detail2 += Flight.allFlights[(int) searchResult[a][1]].arrivalAirport + "<br />";
			else if ((int) searchResult[a][2] != 99999)
				detail2 += Flight.allFlights[(int) searchResult[a][2]].arrivalAirport + "<br />";
			detail3 = "Total travel time: " + (int) Math.floor(searchResult[a][5] / 60) + " hours "
					+ (int) searchResult[a][5] % 60 + " minutes" + "<br />";
			detail4 = "Flights: <br />";

			//prints the info for a direct flight or the first flight of indirect flights in GUI
			detail4 += Flight.allFlights[(int) searchResult[a][0]].flightID + "\tDate: "
					+ Flight.allFlights[(int) searchResult[a][0]].departureTime + "\tFrom: "
					+ Flight.allFlights[(int) searchResult[a][0]].departureAirport + "\t" + "\tDate: "
					+ Flight.allFlights[(int) searchResult[a][0]].arrivalTime + "\tTo: "
					+ Flight.allFlights[(int) searchResult[a][0]].arrivalAirport + "<br />";
			//prints the final flight info for 1 stop indirect flights or the 2nd flight for 2 stop indirect flights in GUI
			if ((int) searchResult[a][1] != 99999) {
				detail4 += Flight.allFlights[(int) searchResult[a][1]].flightID + "\tDate: "
						+ Flight.allFlights[(int) searchResult[a][1]].departureTime + "\tFrom: "
						+ Flight.allFlights[(int) searchResult[a][1]].departureAirport + "\t" + "\tDate: "
						+ Flight.allFlights[(int) searchResult[a][1]].arrivalTime + "\tTo: "
						+ Flight.allFlights[(int) searchResult[a][1]].arrivalAirport + "<br />";
			}

			//prints final flight info for 2 stop indirect flights in GUI
			if ((int) searchResult[a][2] != 99999) {
				detail4 += Flight.allFlights[(int) searchResult[a][2]].flightID + "\tDate: "
						+ Flight.allFlights[(int) searchResult[a][2]].departureTime + "\tFrom: "
						+ Flight.allFlights[(int) searchResult[a][2]].departureAirport + "\t" + "\tDate: "
						+ Flight.allFlights[(int) searchResult[a][2]].arrivalTime + "\tTo: "
						+ Flight.allFlights[(int) searchResult[a][2]].arrivalAirport + "<br />";
			}

			//prints the price per ticket and total price (for all seats) for the booking in the GUI
			detail5 = "Price per ticket: EUR " + (double) Math.round(searchResult[a][3] * 100d) / 100d + "<br />";
			double totalFlightPrice = searchResult[a][3] * seatsChosen;
			detail5 += "Total price: EUR " + (double) Math.round(totalFlightPrice * 100d) / 100d;

			//general GUI settings
			String color = "black";
			if (indexLD % 2 == 0)
				color = "black";
			else
				color = "blue";
			Flight.inputToLD[indexLD] = "<html><font color=" + color + ">" + detail1 + detail2 + detail3 + detail4
					+ detail5 + "</font></html>";
			indexLD++;

		}
	}

	//method that enables the user to book a flight
	public static void bookflight() {

		//takes the flight that was selected by the user in the GUI and stores it into a variable
		int flightChoice = ListDialog.chosenFlight;

		//if no flight is chosen and the user clicks book, the program assumes the user wants to book the flight that is listed at the top
		if (flightChoice == -1) flightChoice = 0;

		//asks for confirmation for booking the flight that was selected by the user
		if (flightChoice != 99999 && flightChoice != -1) {
			String[] userBookYesNo = new String[] { "Yes! BOOK IT BABY!", "No, Exit" };
			int booked = JOptionPane.showOptionDialog(null, "Do you want to book result #" + (flightChoice + 1),
					"Confirmation", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, userBookYesNo,
					userBookYesNo[0]);

			if (booked == 0) {
				int correctIndex;
				if (descending) //in case of reversed sorting this condition enables that the indexes are still listed correctly
					correctIndex = flightChoice + uglyCounter;
				else
					correctIndex = flightChoice;
				Booking.adjustSeats(correctIndex); //triggers the method to adjust the number of seats that is available for a flight with the amount of seats that are being booked
				Booking.storeBooking(correctIndex); //triggers method that stores the booking with the selected flight

				//confirms the booking and closes the program after OK was pressed
				JOptionPane.showMessageDialog(null,
						"Congratulations, you have successfully booked your flight! Safe travels!\n"
								+ "Press OK to exit the program.",
								"Booking Success", JOptionPane.WARNING_MESSAGE);
				System.exit(0);
			} 
			else {
				System.exit(0);
			}
		}

		else System.exit(0);
	}

	// assigns an ID to a user and indicates that a user is active, creates a user object based on the random ID
	public static void createUser() {
		int a = IDgenerator();
		allUsers[totalUsers] = new User(a);
		totalUsers++;
	}

	// checks whether inputID is already existing
	public static boolean checkIfUnique(int inputID) {
		for (int i = 0; i < RegisteredCustomer.totalRegisteredUsers; i++) {
			if (inputID == RegisteredCustomer.allRegisteredCustomers[i].userID) {
				return false;
			}
		}
		return true;
	}

	//method that creates a random number between 1000 and 9999 and checks whether it is already being used as an ID, if so it runs again if not it returns the value
	public static int IDgenerator() {
		int a = (int) Math.floor(Math.random() * (9999 - 1000 + 1)) + 1000;
		if (checkIfUnique(a) == false) {
			IDgenerator();
		}
		return a;
	}

	//method to reset all search parameters to the default values to enable a new, clean search
	public static void resetResult() {
		totalMatches = 0;
		seatsChosen = 0;
		descending = false;
		uglyCounter = 0;

		//resets the array of arrays to default values (which we determined to be 99999)
		for (int row = 0; row < searchResult.length; row++)
			for (int col = 0; col < User.searchResult[0].length; col++)
				User.searchResult[row][col] = 99999.0;
	}

	//method to enable a format that makes dates workable in computations
	public static double duration(String start, String end) {
		SimpleDateFormat setDate = new SimpleDateFormat("dd/MM/yyyy HH:mm"); // this is to read the date from a string
		double diff = 0;

		try {
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();

			startDate.setTime(setDate.parse(start));
			endDate.setTime(setDate.parse(end));

			diff = startDate.getTimeInMillis() - endDate.getTimeInMillis();
			diff = diff / -60000;
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("There is a ParseException");
		}
		return diff;
	}

	//method that sorts prices low-high
	public static void sortOnPriceAscending() {
		Arrays.sort(searchResult, new Comparator<double[]>() {
			public int compare(double[] a, double[] b) {
				return Double.compare(a[3], b[3]);
			}
		});
	}

	//method that sorts prices high-low
	public static void sortOnPriceDescending() {
		Arrays.sort(searchResult, new Comparator<double[]>() {
			public int compare(double[] a, double[] b) {
				return Double.compare(b[3], a[3]);
			}
		});
	}

	//method that sorts based on stopovers few-many
	public static void sortOnStopsAscending() {
		Arrays.sort(searchResult, new Comparator<double[]>() {
			public int compare(double[] a, double[] b) {
				return Double.compare(a[4], b[4]);
			}
		});
	}

	//method that sorts based on stopover many-few
	public static void sortOnStopsDescending() {
		Arrays.sort(searchResult, new Comparator<double[]>() {
			public int compare(double[] a, double[] b) {
				return Double.compare(b[4], a[4]);
			}
		});
	}

	//method that sorts based on duration short-long
	public static void sortOnTimeAscending() {
		Arrays.sort(searchResult, new Comparator<double[]>() {
			public int compare(double[] a, double[] b) {
				return Double.compare(a[5], b[5]);
			}
		});
	}

	//method that sorts based on duration long-short
	public static void sortOnTimeDescending() {
		Arrays.sort(searchResult, new Comparator<double[]>() {
			public int compare(double[] a, double[] b) {
				return Double.compare(b[5], a[5]);
			}
		});
	}

	//method that resets and clears all user login values
	public static void logOut() {
		totalUsers =0;
		RegisteredCustomer.activeUser=0;
		RegisteredCustomer.registeredUserLoggedIn = false;
		Admin.activeAdmin = 0;
		Admin.adminLoggedIn = false;
	}
}
