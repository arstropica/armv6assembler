/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.interfaces;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assembler.exceptions.InvalidCodeException;
import assembler.types.Code;

/**
 * Public interface for parsing ARM codes.
 *
 */
public interface Parsable
{

	 /**
	  * ARM code Regular Expression patterns
	  */
	 public Map<String, String> pSchema = new HashMap<String, String>()
	 {
			private static final long serialVersionUID = 1L;

			{
				 String command = "[A-Za-z]{3}";
				 String s_bit = "S";
				 String cond = "[A-Za-z]{2}";
				 String reg = "(?:r[0-9]{1,2})|(?:sp)|(?:lr)|(?:pc)";
				 String space = "[\\s]";
				 String b_open = "\\[";
				 String b_close = "\\]";
				 String pre_index = "!";

				 put( "command", String.format( "^(%s)", command ) );
				 put( "s_bit", String.format( "^%s(%s)", command, s_bit ) );
				 put( "condition", String.format( "^(?:%s)(?:%s)?(%s)%s+", command,
							 s_bit, cond, space ) );
				 put( "rd", String.format( "^(?:%s)(?:%s)?(?:%s)?%s+(%s)%s*,",
							 command, s_bit, cond, space, reg, space ) );
				 put( "rn", String.format(
							 "^(?:%s)(?:%s)?(?:%s)?%s+(?:%s)%s*,%s*(%s?%s*(?:%s)%s*%s?)",
							 command, s_bit, cond, space, reg, space, space, b_open, space,
							 reg, space, b_close ) );
				 put( "operand2",
							 String.format( "^(?:%s)(?:%s)?(?:%s)?%s+(?:%s)%s*,%s*(.*)%s?$",
										 command, s_bit, cond, space, reg, space, space,
										 pre_index ) );
				 put( "pre_index", String.format( "(%s)$", pre_index ) );
			}
	 };

	 /**
	  * Extract code parts from arm instruction.
	  * 
	  * @param Code code
	  * @return Map<String, Code>
	  * @throws InvalidCodeException
	  */
	 public default Map<String, Code> extract( Code code )
				 throws InvalidCodeException
	 {
			Map<String, Code> parts = new HashMap<String, Code>();
			if (code.isBlank()) {
				 throw new InvalidCodeException();
			} else {
				 for (Map.Entry<String, String> entry : pSchema.entrySet()) {
						String part = entry.getKey();
						String regex = entry.getValue();
						Pattern patt = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
						Matcher matcher = patt.matcher( code );
						Boolean found = matcher.find();
						switch (part) {
						case "command":
						case "rd":
							 if (!found || matcher.groupCount() == 0) {
									throw new InvalidCodeException(
												String.format( "%s not found in statement.", part ) );
							 } else {
									parts.put( part, new Code( matcher.group( 1 ).trim() ) );
							 }
							 break;
						default:
							 if (found && matcher.groupCount() > 0) {
									parts.put( part, new Code( matcher.group( 1 ).trim() ) );
							 } else {
									parts.put( part, null );
							 }
							 break;
						}
				 }
			}
			return parts;
	 }

	 /**
	  * Definition for parsing an ARM command or fragment.
	  * 
	  * @return Boolean
	  * @throws InvalidCodeException
	  */
	 public Boolean parse() throws InvalidCodeException;

	 /**
	  * Returns a string representation of the binary value of the object.
	  * 
	  * @return String
	  * @throws InvalidCodeException
	  */
	 public String toBinaryString() throws InvalidCodeException;

}
