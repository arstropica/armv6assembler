/**
 * 
 */
package assembler.types;

/**
 * Type class for presenting ARM source code.
 *
 */
public class Code implements CharSequence
{

	 /**
	  * {@value #source} Raw input source
	  */
	 private String source;

	 /**
	  * @return String source
	  */
	 public String getSource()
	 {
			return source;
	 }

	 /**
	  * @return String representation of source code
	  */
	 public String toString()
	 {
			return source;
	 }

	 /**
	  * Returns a string that is a substring of this string. The substring
	  * begins with the character at the specified index and extends to the end
	  * of this string.
	  * 
	  * @param int start
	  * @return String substring
	  */
	 public String substring( int start )
	 {
			return source.substring( start );
	 }

	 /**
	  * Returns a string that is a substring of this string. The substring
	  * begins at the specified beginIndex and extends to the character at index
	  * endIndex - 1. Thus the length of the substring is endIndex-beginIndex.
	  * 
	  * @param int start
	  * @param int end
	  * @return String substring
	  */
	 public String substring( int start, int end )
	 {
			return source.substring( start, end );
	 }

	 /**
	  * Returns a new Code instance using a substring of the source.
	  * 
	  * @param int start
	  * @param int end
	  * @return
	  */
	 public Code slice( int start, int end )
	 {
			return new Code( source.substring( start, end ) );
	 }

	 /**
	  * Returns a new Code object with the appended code.
	  * 
	  * @param Code code
	  * @return Code
	  */
	 public Code concat( Code code )
	 {
			return new Code( source + code.toString() );
	 }

	 /**
	  * Tells whether or not this code matches the given regular expression.
	  * 
	  * @param regex
	  * @return Boolean
	  */
	 public Boolean matches( String regex )
	 {
			return source.matches( regex );
	 }

	 /**
	  * Returns true if, and only if, length() is 0.
	  * 
	  * @return Boolean
	  */
	 public Boolean isEmpty()
	 {
			return source.isEmpty();
	 }

	 /**
	  * Returns true if the string is empty or contains only white space
	  * codepoints, otherwise false.
	  * 
	  * @return Boolean
	  */
	 public Boolean isBlank()
	 {
			return source.isBlank();
	 }

	 @Override
	 public int length()
	 {
			return source.length();
	 }

	 @Override
	 public char charAt( int index )
	 {
			return source.charAt( index );
	 }

	 @Override
	 public CharSequence subSequence( int start, int end )
	 {
			return new Code( source.substring( start, end ) );
	 }

	 /**
	  * Simple constructor
	  * 
	  * @param source
	  */
	 public Code( String source )
	 {
			super();
			this.source = source.trim();
	 }

}
