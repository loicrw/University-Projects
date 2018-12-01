package assignment3;

import java.util.*;
import java.io.*;

public class A3_409763 {
	static Scanner userInput1 = new Scanner(System.in);

	public static void main(String[] args){

		int userChoice = getUserChoice();
		switch (userChoice) {
		case 1:
			newEntry();
			break;
		case 2:
			viewData();
			break;
		case 3:
			dataStats();
			break;
		case 4:
			dataCleaning();
			break;
		}
	}

	// since it is not explicitly stated in the assignment description, the name of the text file is always the same and assumed to be "Heights.txt"
	// This method asks and returns what the user wants to do
	public static int getUserChoice(){
		System.out.println("****************************************************************");
		System.out.println("****************************************************************");
		System.out.println("************                 DataI/O                  **********");
		System.out.println("****************************************************************");
		System.out.println("****************************************************************");
		System.out.println("");
		System.out.println("What do you wan to do?");
		System.out.println("(1) Data entry");
		System.out.println("(2) View Data");
		System.out.println("(3) Data stats");
		System.out.println("(4) Data cleaning");
		System.out.println("****************************************************************");
		System.out.print("Please enter your choice (1, 2, 3, or 4): ");
		return userInput1.nextInt();
	}

	// This method adds a new entry at the end of the file
	public static void newEntry(){
		System.out.println("How many new heights would you like to enter?");
		
		int noOfNewEntries = userInput1.nextInt();
		int newEntry = 0;

		for (int i = 1; i <= noOfNewEntries; i++){
			System.out.print("Please enter new height #" + i + ": ");
			newEntry = userInput1.nextInt();
			appendFile(newEntry); //calls for the appendFile method to add the newEntry to the text file
		}
		if (noOfNewEntries == 1)
			System.out.println("Your new entry has been saved!"); 
		else 
			System.out.println("Your entries have been saved!");
	}

	// This method prints out the content of the file in the console
	public static void viewData(){
		System.out.println("The currently saved heights are: ");
		
		int[] allHeights = readFile(); //calls for the readFile method to return an array of all the values in the text file
		for (int i = 0; i < allHeights.length; i++)
			System.out.println(allHeights[i]);
	}

	// This method prints out the min, max, and the average of the heights
	public static void dataStats(){
		int[] myArray = readFile();
		
		int minHeight = minArray(myArray);
		int maxHeight = maxArray(myArray);
		double meanHeight = meanArray(myArray);
		
		System.out.println("The stats of your data are the following:");
		System.out.println("The minimum height is: " + minHeight);
		System.out.println("The maximum height is: " + maxHeight);
		System.out.println("The mean height is: " + meanHeight);
	}

	// This method removes all non-reasonable entries from the file
	public static void dataCleaning(){
		int[] heights = readFile();	//initialize an array with all the currently saved heights	
		int numClean = 0; //initialize variable for the number of reasonable entries
		
		int[] copied = new int[heights.length]; //initialize array to keep track of which entries have been copied

		for (int i = 0; i < heights.length; i++){
			if (heights[i] >= 0 && heights[i] <= 250){
				numClean++; //count the number of reasonable entries (to make the new array the same length)
			}
		}

		int[] myArray = new int[numClean]; //initialize the new cleaned data array

		for (int i = 0; i < myArray.length; i++){
			for (int j = 0; j < heights.length; j++){
				if(copied[j] == 0 && heights[j] >= 0 && heights[j] <= 250){
					myArray[i] = heights[j]; //if a number has not been copied and is reasonable, add it to the clean array
					copied[j] = 1;
					break; //once a number has been copied over, check the next number
				}
			}
		}
		overWriteFile(myArray); //call for the method to overwrite the existing file with the cleaned array
		System.out.println("Your data has been cleaned!");
	}


	// This method gets myArray as input and returns its minimum value
	public static int minArray(int[] myArray){
		int minimum = 2147483647; //initialize minimum with largest possible int value (to guarantee any int is the same or lower; same afterwards with max)
		
		for (int i = 0; i < myArray.length; i++){
			if (myArray[i] < minimum) minimum = myArray[i]; //while going through the array, replace the minimum value if the array value is smaller (same afterwards with max)
		}
		return minimum;
	}

	// This method gets myArray as input and returns its maximum value
	public static int maxArray(int[] myArray){
		int maximum = -2147483647;
		
		for (int i = 0; i < myArray.length; i++){
			if (myArray[i] > maximum) maximum = myArray[i];
		}
		return maximum;
	}

	// This method gets myArray as input and returns its average
	public static double meanArray(int[] myArray){
		double mean = 0;
		
		for (int i = 0; i < myArray.length; i++) mean += myArray[i];
		mean /= myArray.length;
		return mean;
	}

	// This method reads the file and returns the values
	public static int[] readFile(){
		int numSavedHeights = 0; //initialize the length of the array
		
		try {
			File fileName = new File("Heights.txt"); //say which file you want to use
			Scanner x = new Scanner(fileName); //use the file you chose 

			while (x.hasNextInt()){
				numSavedHeights++; //count how many numbers the text file has
				x.nextInt();
			}
			x.close();
		}
		catch (IOException ex) {
			System.out.println("There is an I/O problem.");
		}

		int[] allHeights = new int[numSavedHeights]; //create array with the same length as the number of numbers in the text file

		try {
			File fileName = new File("Heights.txt"); //say which file you want to use
			Scanner x = new Scanner(fileName); //use the file you chose

			for (int i = 0; i < allHeights.length; i++) 
				allHeights[i] = x.nextInt(); //Successively add each number of the text file as an integer to the array
			x.close();	
		}
		catch (IOException ex) {
			System.out.println("There is an I/O problem.");
		}
		return allHeights; 
	}

	// This method writes a new entry to the file
	public static void appendFile(int newEntry){
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Heights.txt", true))); //this code appends and does not overwrite
			out.println(newEntry);
			out.close();
		} catch (IOException ex) {
			System.out.println("There is an I/O problem.");
		}
	}

	// This method writes a new entry to the file
	public static void overWriteFile(int[] myArray){
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Heights.txt"))); // this code overwrites the old text file
			for (int i = 0; i < myArray.length; i++){
				out.println(myArray[i]); //it is overwritten with the cleaned dataset (one element at a time)
			}

			out.close();
		} catch (IOException ex) {
			System.out.println("There is an I/O problem.");
		}
	}
}