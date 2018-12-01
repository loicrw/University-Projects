package courseProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public class Flight {
	public static Flight[] allFlights = new Flight[1000]; //array in which all flights from the file are loaded
	public static int totalFlightEntries = 0; //variable that counts how many entries are in the file
	public static String[] inputToLD = new String[100]; //array in which each index refers to String of flight information

	//list that contains the options in the dropbox menu's for arrival and departure airports
	public static ArrayList<String> uniqueArrivalAirport = new ArrayList<String>();
	public static ArrayList<String> uniqueDepartureAirport = new ArrayList<String>();

	String flightID, departureAirport, arrivalAirport, departureTime, arrivalTime;
	double price;
	int availableSeats;

	//constructor for Flight objects
	public Flight(String flightID, String departureAirport, String arrivalAirport, String departureTime, String arrivalTime, double price, int availableSeats) {
		this.flightID = flightID;
		this.departureAirport = departureAirport;
		this.arrivalAirport = arrivalAirport;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.price = price;
		this.availableSeats = availableSeats;
	}

	//method that overwrites the flight file with new values (e.g. in case of adjusted number of seats due to a booking)
	public static void overWriteFlights() {
		try {
			PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter("Flights.txt", false)));
			for (int i = 0; i < Flight.totalFlightEntries; i++) {
				wr.println(allFlights[i].flightID + "," + allFlights[i].departureAirport + ","
						+ allFlights[i].arrivalAirport + "," + allFlights[i].departureTime + ","
						+ allFlights[i].arrivalTime + "," + allFlights[i].price
						+ "," + allFlights[i].availableSeats);
			}
			wr.close();
		} catch (IOException e) {
			System.out.println("There is an I/O Problem!");
		}
	}

//method that counts how many flights there are in the total by reading the lines in the Flight file
	public static void loadFlightEntries() {
		int entriesCount = 0;
		try {

			BufferedReader myFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("Flights.txt"), "UTF-8"));
			myFile.mark(1);
			if (myFile.read() != 0xFEFF)
				myFile.reset();

			@SuppressWarnings("unused")
			String sCurrentLine;
			while ((sCurrentLine = myFile.readLine()) != null) {

				entriesCount++;
			}
			myFile.close();
		} catch (IOException e) {
			System.out.print("Wrong! (Reading)");
		}
		totalFlightEntries = entriesCount;
	}
	
	//reads the Flights.txt and creates Flight objects from them and stores these into a static array
	public static void loadFlights() {
		Flight[] temporary1 = new Flight[1000];
		int lineIndex = 0;
		try {
			BufferedReader myFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("Flights.txt"), "UTF-8"));
			myFile.mark(1);
			if (myFile.read() != 0xFEFF)
				myFile.reset();
			String sCurrentLine;
			String[] uCurrent = new String[8];
			while ((sCurrentLine = myFile.readLine()) != null) {
				try {
					uCurrent = sCurrentLine.split(",");
					temporary1[lineIndex] = new Flight(uCurrent[0], uCurrent[1], uCurrent[2], uCurrent[3], uCurrent[4],
							Double.parseDouble(uCurrent[5]), Integer.parseInt(uCurrent[6]));
										lineIndex++;
				} catch (Exception e) {
					System.out.println(lineIndex);
				}
			}
			myFile.close();
		} catch (IOException e) {
			System.out.print("Wrong! (Reading)");
		}
		Flight[] temporary2 = new Flight[lineIndex];
		System.arraycopy(temporary1, 0, temporary2, 0, lineIndex);
		System.arraycopy(temporary2, 0, allFlights, 0, lineIndex);

	}

	//method that seeks for unique arrival airports (used for input in the dropbox GUI) and stores it into a static array
	public static void getUniqueArrivals() {
		uniqueArrivalAirport.add(allFlights[0].arrivalAirport);
		for (int i = 0; i < Flight.totalFlightEntries; i++) {
			if (uniqueArrivalAirport.contains(allFlights[i].arrivalAirport)) {

			} else {
				uniqueArrivalAirport.add(allFlights[i].arrivalAirport);
			}

		}
		Collections.sort(uniqueArrivalAirport, String.CASE_INSENSITIVE_ORDER);

	}
	
	//method that seeks for unique arrival airports (used for input in the dropbox GUI) and stores it into a static array
	public static void getUniqueDepartures() {
		uniqueDepartureAirport.add(allFlights[0].departureAirport);
		for (int i = 0; i < Flight.totalFlightEntries; i++) {
			if (uniqueDepartureAirport.contains(allFlights[i].departureAirport)) {

			} else {
				uniqueDepartureAirport.add(allFlights[i].departureAirport);
			}

		}
		Collections.sort(uniqueDepartureAirport, String.CASE_INSENSITIVE_ORDER);

	}

}
