/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler;

import java.util.Scanner;

import assembler.core.Instruction;
import assembler.types.Bits;

/**
 * Disassembly Main Class
 */
public class Disassembler
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
			Instruction instruction;
			Bits inputValue = null;

			// Create a Scanner to obtain user input
			input = new Scanner( System.in ).useDelimiter( System.lineSeparator() );

			System.out.print( "Enter code (hex or binary): " );
			userInput = input.next(); // Read user input
			try {
				 if (userInput.matches( "^0x(.*)" )
							 || userInput.matches( "^0b(.*)" )) {
						userInput = userInput.substring( 2 );
				 }
				 if (userInput.length() == 8) {
						// Hex code
						inputValue = new Bits( userInput, 16 );
						;
				 } else if (userInput.length() == 32) {
						// Binary code
						inputValue = new Bits( userInput, 2 );
						;
				 } else {
						System.out.println( "Unknown format. Exiting." );
						System.exit( 1 );
				 }
				 instruction = new Instruction( inputValue );
				 System.out.println( String.format( "The instruction command is: %S.",
							 instruction.toString() ) );
			} catch (Exception e) {
				 e.printStackTrace( System.out );
			}
	 }

}
