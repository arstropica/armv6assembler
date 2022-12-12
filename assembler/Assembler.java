/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler;

import java.util.Scanner;

import assembler.core.Instruction;
import assembler.types.Code;

/**
 * Assembly Main Class
 */
public class Assembler
{

	 /**
	  * {@value #input} // simple text scanner that can parse primitive types
	  * and strings
	  */
	 static private Scanner input;

	 /**
	  * @param args
	  */
	 public static void main( String[] args )
	 {
			String userInput;
			Code inputValue;
			Instruction instruction;

			// Create a Scanner to obtain user input
			input = new Scanner( System.in ).useDelimiter( System.lineSeparator() );

			System.out.print( "Enter ARM Command: " );
			userInput = input.next(); // Read user input
			inputValue = new Code( userInput );
			try {
				 instruction = new Instruction( inputValue );
				 System.out.println( String.format( "The instruction code is: %S.",
							 instruction.toHexString() ) );
			} catch (Exception e) {
				 e.printStackTrace( System.out );
			}
	 }

}
