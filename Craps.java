import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Craps {
	private static int balance = 1000;
	private static int numOfGames = 10;
	
	public int rollDices(){
		int num = rollADice() + rollADice();
		return num;
	}
	
	//return 1 to 6 as integer
	public int rollADice(){
		int num = 1 + (int) (Math.random() * 6);
		return num;
	}
	
	// return true means win and vice versa for false
	public boolean playAGame(){
		int firstRoll = rollDices();
		// first roll 7, 11 win the game and 2, 3, 11 lose
		if(firstRoll == 7 || firstRoll == 11){
			return true;
		}else if(firstRoll == 2 || firstRoll == 3 || firstRoll == 12){
			return false;
		}
		
		//second phase
		int secondRoll = rollDices();
		while(secondRoll != 7 && secondRoll != firstRoll){
			secondRoll = rollDices();
		}
		if(secondRoll == firstRoll){
			return true;
		}else{
			return false;
		}
	}
	
	public int[] evenWager(){
		int balance = this.balance;
		int numOfGames = 0;
		int chips = 100;
		// stops when numOfGames >= 10 or when balance <= 0
		while(numOfGames < this.numOfGames && balance > 0){
			balance = (playAGame()) ? (balance + chips) : (balance - chips);
			numOfGames++;
		}
		return new int[]{numOfGames, balance};
	}
	
	public int[] martingaleSystem(){
		int balance = this.balance;
		int numOfGames = 0;
		int chips = 100;
		boolean lastGame = true;
		
		// stops when numOfGames >= 10 or when balance <= 0
		while(numOfGames < this.numOfGames && balance > 0){
			if(lastGame){
				chips = 100;
			}else{
				chips *= 2;
				if(chips > balance){
					chips = balance;
				}
			}
			boolean game = playAGame();
			balance = (game) ? (balance + chips) : (balance - chips);
			lastGame = game;
			numOfGames++;
		}
		return new int[]{numOfGames, balance};		
	}

	public int[] reverseMartingaleSystem(){
		int balance = this.balance;
		int numOfGames = 0;
		int chips = 100;
		boolean lastGame = false;
		
		// stops when numOfGames >= 10 or when balance <= 0
		while(numOfGames < this.numOfGames && balance > 0){
			if(lastGame){
				chips *= 2;
				if(chips > balance){
					chips = balance;
				}
			}else{
				chips = 100;
			}
			boolean game = playAGame();
			balance = (game) ? (balance + chips) : (balance - chips);
			lastGame = game;
			numOfGames++;
		}
		return new int[]{numOfGames, balance};		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Craps test = new Craps();
		
	    try {
	        PrintStream out = new PrintStream(new FileOutputStream(args[0] + "\\lxc121830_OutFile.txt"));
	        for (int i = 1; i <= 5; i++){
	          out.println("Round " + i + ":");
	          out.println("Strategy   Number of Games   Ending Balance");
	          int[] result = test.evenWager();
	          out.println("1          " + result[0] + "                  " + ((result[0] == 10) ? "" : " ") + result[1]);
	          result = test.martingaleSystem();
	          out.println("2          " + result[0] + "                  " + ((result[0] == 10) ? "" : " ") + result[1]);
	          result = test.reverseMartingaleSystem();
	          out.println("3          " + result[0] + "                  " + ((result[0] == 10) ? "" : " ") + result[1]);
	          out.println();
	        }
	        out.close();
	      } catch (FileNotFoundException e) {
	        e.printStackTrace();
	      }
	}
}
