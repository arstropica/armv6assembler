
/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.exceptions;

/**
 * Handles invalid code errors.
 *
 */
public class InvalidCodeException extends InvalidInstructionException
{

	 /**
	  * {@value #serialVersionUID} version number
	  */
	 private static final long serialVersionUID = 1L;

	 /**
	  * {@value #errorMessage} error message
	  */
	 private static String errorMessage = "The input binary code is invalid.";

	 /**
	  * Simple constructor
	  * 
	  * @param errorMessage String
	  */
	 public InvalidCodeException()
	 {
			super( errorMessage );
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param errorMessage String
	  */
	 public InvalidCodeException( String errorMessage )
	 {
			super( errorMessage );
	 }
	 
	 /**
	  * Overloaded constructor
	  * 
	  * @param errorMessage String
	  * @param err          Throwable
	  */
	 public InvalidCodeException( String errorMessage, Throwable err )
	 {
			super( errorMessage, err );
	 }

}
