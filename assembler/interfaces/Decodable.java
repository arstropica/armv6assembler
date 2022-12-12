/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.interfaces;

import java.util.HashMap;
import java.util.Map;

import assembler.exceptions.InvalidCodeException;

/**
 * Public interface for decoding bit values
 */
public interface Decodable
{

	 /**
	  * 32-bit Instruction binary schema
	  */
	 public Map<String, int[]> dSchema = new HashMap<String, int[]>()
	 {
			private static final long serialVersionUID = 1L;

			{
				 put( "cond_code", new int[] { 0, 4 } );
				 put( "op_type", new int[] { 4, 3 } );
				 put( "op_code", new int[] { 7, 4 } );
				 put( "s_bit", new int[] { 11, 1 } );
				 put( "rn", new int[] { 12, 4 } );
				 put( "rd", new int[] { 16, 4 } );
				 put( "operand2", new int[] { 20, 12 } );
			}
	 };

	 /**
	  * Definition for decoding a bit value.
	  * 
	  * @return Boolean
	  * @throws InvalidCodeException
	  */
	 public Boolean decode() throws InvalidCodeException;

	 /**
	  * Returns a string representation of object
	  * 
	  * @return String (object)
	  */
	 public String toString();
}
