/**
 * 
 */
package assembler.exceptions;

/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
public class InvalidCommandException extends InvalidInstructionException
{

	 /**
	  * {@value #serialVersionUID} version number
	  */
	 private static final long serialVersionUID = 1L;

	 /**
	  * {@value #errorMessage} error message
	  */
	 private static String errorMessage = "The input command is invalid.";

	 /**
	  * Simple constructor
	  * 
	  * @param errorMessage String
	  */
	 public InvalidCommandException()
	 {
			super( errorMessage );
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param errorMessage String
	  */
	 public InvalidCommandException( String errorMessage )
	 {
			super( errorMessage );
			// TODO Auto-generated constructor stub
	 }

	 /**
	  * Overloaded constructor
	  * 
	  * @param errorMessage String
	  * @param err          Throwable
	  */
	 public InvalidCommandException( String errorMessage, Throwable err )
	 {
			super( errorMessage, err );
			// TODO Auto-generated constructor stub
	 }

}
