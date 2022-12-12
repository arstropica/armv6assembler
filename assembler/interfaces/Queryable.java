/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.interfaces;

import assembler.io.DB;

public interface Queryable
{
	 /**
	  * {@link DB} Database Instance
	  */
	 public static final DB db = DB.getInstance();

}
