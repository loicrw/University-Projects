package Assignment1_package;
import java.util.*;

public class A2_409763 {
	static Scanner x = new Scanner(System.in);
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		// I. Start with welcome message
		System.out.println("****************************************************************");
		System.out.println("****************************************************************");
		System.out.println("************              Mastermind                  **********");
		System.out.println("****************************************************************");
		System.out.println("****************************************************************");
		System.out.println("");

		System.out.println("Manual:");
		System.out.println("(1) Computer chooses a 4-tuple integer values between 0-9");
		System.out.println("(2) Order matters in the 4-tuple and can be repetitive");
		System.out.println("(3) You can set the number of iterations (guesses) per game");
		System.out.println("(4) In each iteration you guess the 4-tuple");		
		System.out.println("(5) Enter four numbers with space in between and enter when done");
		System.out.println("****************************************************************");		
		System.out.println("");		


		//Initialise all variables
		int maxrand = 10; //choose the range of the random numbers	
		int numberComp = 4; //number of random numbers the computer generates
		int[] compArray = new int[numberComp]; //the numbers that the computer will choose
		int[] userGuess = new int [numberComp]; //the numbers that the user guesses
		int[] redPoint = new int [numberComp]; //whether a given number has been guessed redPointly 
		int[] whitePoint_comp = new int [numberComp]; //whether a guessed number should be used somewhere else (for a number the computer picked)
		int[] whitePoint_user = new int [numberComp]; //whether a guessed number should be used somewhere else (for a number the guest picked
		int whitepoints = 0;  //sum of each type of point, this is shorter than adding the points through a for loop
		int redpoints = 0;
		String replay = "Y"; //replay if answer is yes
		String replayAnswer = "Y"; //initialize the answer so the game starts
		int iterations = 0;

		
		while (replay.equals(replayAnswer)){ //repeat the game as long as the player wants to play again
			//III. Ask for the number of iterations
			System.out.print("How many iterations do you want to have? ");
			iterations = x.nextInt(); 

			//II. Randomly pick 4 integer numbers for the computer
			for (int i = 0; i < numberComp; i++){
				compArray[i] = (int) (Math.random() * maxrand);
			}
									
			
			//the loop for each round/guess
			for (int i = 1; i <= iterations; i++){
				//reset all points
				whitepoints = 0;
				redpoints = 0; 
				for (int j = 0; j < numberComp; j++){
					redPoint[j] = 0;
					whitePoint_comp[j] = 0;
					whitePoint_user[j] = 0;
				}

				//IV. ask the user to guess the 4-tuples
				System.out.print("Iteration " + i + ". Next Guess: ");
				for (int j = 0; j < numberComp; j++){
					userGuess[j]= x.nextInt(); //guess#1
				}

				//calculate points
				for (int j = 0; j < numberComp; j++){
					if (userGuess[j] == compArray[j]){
						redpoints++;
						redPoint[j] = 1;
					} 
				}
				
				for (int j = 0; j < numberComp; j++){
					for (int z = 0; z < numberComp; z++){
						if (compArray[z] == userGuess[j] && redPoint[z] != 1 && redPoint[j] != 1 && whitePoint_comp[z] != 1 && whitePoint_user[j] != 1 ){ //conditions to prevent double counting of points
							whitepoints++;
							whitePoint_comp[z]++;
							whitePoint_user[j]++;
						}
					}
				}

				if (redpoints == 4) { //if the player guesses it, the game ends
					break;
				} 

				
				//V. report the number of white and red points
				System.out.println(" ---------------------------------------");
				System.out.println("|       Your choice      || white | red |");
				System.out.println(" ---------------------------------------");
				System.out.print("|      (" + userGuess[0] + ", " + userGuess[1] + ", " + userGuess[2] + ", " + userGuess[3] + ")      |");
				System.out.println("|   " + whitepoints + "   |  " + redpoints + "  |");
				System.out.println(" ---------------------------------------");

			}

			
			//VI. show end of game screen
			if (redpoints < 4) { //if the user fails
				System.out.println("I am sorry. You failed");
				System.out.println("Player 1 choice was :");
				System.out.println(" -------------");
				System.out.println("|(" +compArray[0] + ", " + compArray[1] + ", " + compArray[2] + ", " + compArray[3] + ") |");
				System.out.println(" -------------");
			} else { //if the user wins
				System.out.println("Congratulations! You could decode it.");
			}

			redpoints = 0; //prevents the player from winning after choosing "0" iterations after choosing to replay
			
			//VII. ask whether the user wants to play another game
			String buffer = x.nextLine(); //buffer so answer input works
			System.out.print("Do you want to play another round? (Y/N) ");
			replayAnswer = x.nextLine();	
		}

		//show game over message if the user doesn't want to play again
		System.out.println("****************************************************************");
		System.out.println("****************************************************************");
		System.out.println("************              Game Over!                  **********");
		System.out.println("****************************************************************");
		System.out.println("****************************************************************");
	}
}

