/**
 * 
 */
package assembler.exceptions;

/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
public class InvalidInstructionException extends Exception
{

	 /**
	  * {@value #serialVersionUID} version number
	  */
	 private static final long serialVersionUID = 1L;

	 /**
	  * Simple constructor
	  * 
	  * @param errorMessage String 
	  */
	 public InvalidInstructionException()
	 {
			super();
	 }
	 
	 /**
	  * Standard constructor
	  * 
	  * @param errorMessage String 
	  */
	 public InvalidInstructionException(String errorMessage)
	 {
			super(errorMessage);
	 }
	 
	 /**
	  * Overloaded constructor
	  * 
	  * @param errorMessage String
	  * @param err Throwable 
	  */
	 public InvalidInstructionException(String errorMessage, Throwable err) {
			super(errorMessage, err);
	 }

}
