/**
 * 
 */
package assembler.types;

import java.math.BigInteger;
import assembler.util.Binary;
import assembler.exceptions.InvalidCodeException;

/**
 * Type class for presenting ARM Instruction binary sequences.
 *
 */
public class Bits implements CharSequence
{

	 /**
	  * {@value #bin} Input binary sequence
	  */
	 private String bin;

	 /**
	  * {@value #dec} Input decimal value
	  */
	 private BigInteger dec;

	 /**
	  * {@value #hex} Input hex sequence
	  */
	 private String hex;

	 /**
	  * @return String Binary String
	  */
	 public String toBinaryString()
	 {
			return bin;
	 }

	 /**
	  * @return BigInteger decimal value
	  */
	 public BigInteger getDecimal()
	 {
			return dec;
	 }

	 /**
	  * @return Integer integer value
	  */
	 public Integer toInteger()
	 {
			return dec.intValue();
	 }

	 /**
	  * @return String hex string
	  */
	 public String toHexString()
	 {
			return hex;
	 }

	 /**
	  * @return String representation hex string
	  */
	 public String toString()
	 {
			return hex;
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
			return bin.substring( start );
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
			return bin.substring( start, end );
	 }

	 /**
	  * Returns a new Bits instance using a substring of the binary sequence.
	  * 
	  * @param int start
	  * @param int end
	  * @return
	  */
	 public Bits slice( int start, int end )
	 {
			try {
				 return new Bits( bin.substring( start, end ) );
			} catch (InvalidCodeException e) {
				 return null;
			}
	 }

	 /**
	  * Returns a new Bits object with the appended bits.
	  * 
	  * @param Bits bits
	  * @return Bits
	  * @throws InvalidCodeException
	  */
	 public Bits concat( Bits bits ) throws InvalidCodeException
	 {
			return new Bits( bin + bits.toBinaryString() );
	 }

	 /**
	  * Tells whether or not this Bits matches the given regular expression.
	  * 
	  * @param regex
	  * @return Boolean
	  */
	 public Boolean matches( String regex )
	 {
			return bin.matches( regex );
	 }

	 /**
	  * Performs a right rotation of the bits and returns a new Bit instance
	  * 
	  * @param n
	  * @return Bits
	  * @throws InvalidCodeException
	  */
	 public Bits rotate( int n ) throws InvalidCodeException
	 {
			return new Bits( String.valueOf( Binary.rightRotate( toInteger(), n ) ),
						10 );
	 }

	 @Override
	 public int length()
	 {
			return bin.length();
	 }

	 @Override
	 public char charAt( int index )
	 {
			return bin.charAt( index );
	 }

	 @Override
	 public CharSequence subSequence( int start, int end )
	 {
			try {
				 return new Bits( bin.substring( start, end ) );
			} catch (InvalidCodeException e) {
				 return null;
			}
	 }

	 /**
	  * Simple constructor using a binary string
	  * 
	  * @param String input
	  * @throws InvalidCodeException
	  */
	 public Bits( String input ) throws InvalidCodeException
	 {
			super();
			String _input = input.trim();
			if (_input.matches( "[^01]" ) || input.isBlank()) {
				 throw new InvalidCodeException();
			} else {
				 int len = _input.length();
				 this.bin = _input;
				 this.dec = new BigInteger( bin, 2 );
				 this.hex = String
							 .format( "%" + Math.max( 1, Math.round( len / 4 ) ) + "s",
										 dec.toString( 16 ) )
							 .replace( " ", "0" );
			}
	 }

	 /**
	  * Standard constructor with radix
	  * 
	  * @param String input
	  * @param int    radix
	  * @throws InvalidCodeException
	  */
	 public Bits( String input, int radix ) throws InvalidCodeException
	 {
			super();
			if (input.isBlank()) {
				 throw new InvalidCodeException();
			} else {
				 String _input = input.trim();
				 int len = _input.length();
				 int log_2 = Binary.log2( radix );
				 this.dec = new BigInteger( _input, radix );
				 this.bin = String
							 .format( "%" + Math.max( 1, Math.round( len * log_2 ) ) + "s",
										 dec.toString( 2 ) )
							 .replace( " ", "0" );
				 this.hex = String.format(
							 "%" + Math.max( 1, Math.round( len * log_2 / 4 ) ) + "s",
							 dec.toString( 16 ) ).replace( " ", "0" );
			}
	 }

}
