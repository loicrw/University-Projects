package courseProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Booking {
	public static int totalBookings = 0;
	public static Booking[] allBookings = new Booking[200];

	String userID, departureAirport, destinationAirport;
	int bookingID, flightID, flightID2, flightID3, seats;

	//constructor for bookings
	public Booking(int bookingID, String userID, String departureAirport, String destinationAirport, int flightID,
			int flightID2, int flightID3, int seats) {
		this.bookingID = bookingID;
		this.userID = userID;
		this.departureAirport = departureAirport;
		this.destinationAirport = destinationAirport;
		this.flightID = flightID;
		this.flightID2 = flightID2;
		this.flightID3 = flightID3;
	}

	//method that adjusts the number of seats for a flight if a booking is being made
	public static void adjustSeats(int flightChoice) {

		if (User.searchResult[flightChoice][1] == 99999) { // then it's a direct flight and one flight needs to be altered
			Flight.allFlights[(int)User.searchResult[flightChoice][0]].availableSeats -= User.seatsChosen;
			Flight.overWriteFlights(); //activates method that overwrites the flight file with the adjusted number of available seats
		} else if (User.searchResult[flightChoice][1] != 99999 && User.searchResult[flightChoice][2] == 99999) { // then it's an indirect flight (1 stop) and two flights need to be altered
			Flight.allFlights[(int)User.searchResult[flightChoice][0]].availableSeats -= User.seatsChosen;
			Flight.allFlights[(int)User.searchResult[flightChoice][1]].availableSeats -= User.seatsChosen;
			Flight.overWriteFlights();
		} else if (User.searchResult[flightChoice][2] != 99999) { // then it's an indirect flight (1 stop) and three flights need to be altered
			Flight.allFlights[(int)User.searchResult[flightChoice][0]].availableSeats -= User.seatsChosen;
			Flight.allFlights[(int)User.searchResult[flightChoice][1]].availableSeats -= User.seatsChosen;
			Flight.allFlights[(int)User.searchResult[flightChoice][2]].availableSeats -= User.seatsChosen;
			Flight.overWriteFlights();
		}
	}

	//takes the index of a search to store a booking in the booking file
	public static void storeBooking(int flightChoice) {
		//condition that checks if user is a registered customer, to store booking as a registered customer
		if (RegisteredCustomer.registeredUserLoggedIn == true) {

			//in case of a direct flight
			if (User.searchResult[flightChoice][1] == 99999) {
				try {
					PrintWriter myFile = new PrintWriter(new BufferedWriter(new FileWriter("Bookings.txt", true)));
					myFile.println(Booking.IDgenerator() + ","
							+ RegisteredCustomer.allRegisteredCustomers[RegisteredCustomer.activeUser].userID + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].departureAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].arrivalAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].flightID + "," + 0 + "," + 0 + "," //stores first flight and sets other 2 flights to 0
							+ User.seatsChosen);
					myFile.close();
				} catch (IOException e) {
					System.out.println("The file does not exist");
				}
			} 
			//in case of an indirect flight with 1 stopover
			else if (User.searchResult[flightChoice][1] != 99999 && User.searchResult[flightChoice][2] == 99999) {
				try {
					PrintWriter myFile = new PrintWriter(new BufferedWriter(new FileWriter("Bookings.txt", true)));
					myFile.println(Booking.IDgenerator() + ","
							+ RegisteredCustomer.allRegisteredCustomers[RegisteredCustomer.activeUser].userID + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].departureAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][1]].arrivalAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].flightID + "," //stores first flight
							+ Flight.allFlights[(int)User.searchResult[flightChoice][1]].flightID + "," + 0 + "," //stores second flight and sets 3rd flight to 0
							+ User.seatsChosen);
					myFile.close();
				} catch (IOException e) {
					System.out.println("The file does not exist");
				}
				//in case of an indirect flight with 2 stopovers
			} else if (User.searchResult[flightChoice][2] != 99999) {
				try {
					PrintWriter myFile = new PrintWriter(new BufferedWriter(new FileWriter("Bookings.txt", true)));
					myFile.println(Booking.IDgenerator() + ","
							+ RegisteredCustomer.allRegisteredCustomers[RegisteredCustomer.activeUser].userID + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].departureAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][2]].arrivalAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].flightID + ","//stores first flight
							+ Flight.allFlights[(int)User.searchResult[flightChoice][1]].flightID + "," //stores second flight
							+ Flight.allFlights[(int)User.searchResult[flightChoice][2]].flightID+ ","  //stores third flight
							+ User.seatsChosen);
					myFile.close();
				} catch (IOException e) {
					System.out.println("The file does not exist");
				}

			}

		}
		//in case the user is an unregistered customer (comments are identical to the condition above) only difference is in writing "UnCu" instead of the registered customer ID
		else
		{
			if (User.searchResult[flightChoice][1] == 99999) {
				try {

					PrintWriter myFile = new PrintWriter(new BufferedWriter(new FileWriter("Bookings.txt", true)));
					myFile.println(Booking.IDgenerator() + "," + "UnCu" + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].departureAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].arrivalAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].flightID + "," + 0 + "," + 0 + "," + User.seatsChosen);
					myFile.close();
				} catch (IOException e) {
					System.out.println("The file does not exist");
				}
			} 
			else if (User.searchResult[flightChoice][1] != 99999 && User.searchResult[flightChoice][2] == 99999) {
				try {
					PrintWriter myFile = new PrintWriter(new BufferedWriter(new FileWriter("Bookings.txt", true)));
					myFile.println(Booking.IDgenerator() + "," + "UnCu" + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].departureAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][1]].arrivalAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].flightID + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][1]].flightID + "," + 0 + "," + User.seatsChosen);
					myFile.close();
				} catch (IOException e) {
					System.out.println("The file does not exist");
				}
			} else if (User.searchResult[flightChoice][2] != 99999) {
				try {
					PrintWriter myFile = new PrintWriter(new BufferedWriter(new FileWriter("Bookings.txt", true)));
					myFile.println(Booking.IDgenerator() + "," + "UnCu" + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].departureAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][2]].arrivalAirport + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][0]].flightID + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][1]].flightID + ","
							+ Flight.allFlights[(int)User.searchResult[flightChoice][2]].flightID + "," + User.seatsChosen);
					myFile.close();
				} catch (IOException e) {
					System.out.println("The file does not exist");
				}
			}
		}
	}

	//method that creates a unique booking ID
	public static int IDgenerator() {
		int a = (int) Math.floor(Math.random() * (9999 - 1000 + 1)) + 1000;
		if (Booking.checkIfUnique(a) == false) {
			IDgenerator();
		}
		return a;
	}

	//checks whether inputID is already existent as a booking ID
	public static boolean checkIfUnique(int inputID) {
		for (int i = 0; i < Booking.totalBookings; i++) {
			if (inputID == allBookings[i].bookingID) {
				return false;
			}
		}
		return true;
	}

	//method that reads all bookings from a file and stores it into a static array
	public static void loadBookings() {
		int i = 0;
		try {
			BufferedReader myFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("Bookings.txt"), "UTF-8"));
			myFile.mark(1);
			if (myFile.read() != 0xFEFF)
				myFile.reset();

			String sCurrentLine;
			String[] uCurrent = new String[8];
			while ((sCurrentLine = myFile.readLine()) != null) {
				uCurrent = sCurrentLine.split(",");
				allBookings[i] = new Booking(Integer.parseInt(uCurrent[0]), uCurrent[1], (uCurrent[2]), uCurrent[3],
						Integer.parseInt(uCurrent[4]), Integer.parseInt(uCurrent[5]), Integer.parseInt(uCurrent[6]), Integer.parseInt(uCurrent[7]));
				totalBookings++;
				i++;
			}

			myFile.close();
		} catch (IOException e) {
			System.out.print("Wrong! (Reading)");
		}
	}

}
