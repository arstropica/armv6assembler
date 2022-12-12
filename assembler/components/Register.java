/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assembler.exceptions.InvalidCodeException;
import assembler.interfaces.Decodable;
import assembler.interfaces.Parsable;
import assembler.types.Bits;
import assembler.types.Code;

/**
 * Class representing a Register in ARM Instruction.
 *
 */
public class Register implements Decodable, Parsable
{

	 /**
	  * {@value #code} Source code
	  */
	 private Code code;

	 /**
	  * {@value #bits} Register bits
	  */
	 private Bits bits;

	 /**
	  * {@value #name} Register name
	  */
	 private String name;

	 /**
	  * @return Code source code
	  */
	 public Code getCode()
	 {
			return code;
	 }

	 /**
	  * Gets Register bits
	  * 
	  * @return Bits bits
	  */
	 public Bits getBits()
	 {
			return bits;
	 }

	 /**
	  * Sets Register bits
	  * 
	  * @param Bits bits
	  */
	 public void setBits( Bits bits )
	 {
			this.bits = bits;
	 }

	 /**
	  * Sets Register bits
	  * 
	  * @param int number
	  */
	 public void setBits( int number ) throws InvalidCodeException
	 {
			this.bits = new Bits(
						String.format( "%4s", Integer.toBinaryString( number ) )
									.replace( " ", "0" ) );
	 }

	 /**
	  * Gets Register name
	  * 
	  * @return String name
	  */
	 public String getName()
	 {
			return name;
	 }

	 /**
	  * Sets Register bits
	  * 
	  * @param String name
	  */
	 public void setName( String name )
	 {
			this.name = name.matches( "^(r|sp|lr|pc)(.*)" ) ? name : "r" + name;
	 }

	 /**
	  * Sets Register name
	  * 
	  * @param int bits
	  */
	 public void setName( int num )
	 {
			String name;
			switch (num) {
			case 13:
				 name = "sp";
				 break;
			case 14:
				 name = "lr";
				 break;
			case 15:
				 name = "pc";
				 break;
			default:
				 name = "r" + String.valueOf( num );
				 break;
			}
			this.name = name;
	 }

	 @Override
	 public Boolean decode()
	 {
			if (bits != null) {
				 this.setName( bits.toInteger() );
			}
			return true;
	 }

	 @Override
	 public Boolean parse() throws InvalidCodeException
	 {
			if (code != null && !code.isBlank()) {
				 String name;
				 int num;
				 String regex = "((?:r[0-9]{1,2})|(?:sp)|(?:lr)|(?:pc))";
				 Pattern patt = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
				 Matcher matcher = patt.matcher( code );
				 Boolean found = matcher.find();
				 if (found) {
						name = matcher.group( 1 ).trim().toLowerCase();
						switch (name) {
						case "sp":
							 num = 13;
							 break;
						case "lr":
							 num = 14;
							 break;
						case "pc":
							 num = 15;
							 break;
						default:
							 regex = "r([0-9]{1,2})";
							 patt = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
							 matcher = patt.matcher( name );
							 found = matcher.find();
							 if (found) {
									num = Integer.parseInt( matcher.group( 1 ).trim() );
							 } else {
									throw new InvalidCodeException();
							 }
							 break;
						}
						this.setBits( num );
						this.setName( bits.toInteger() );
						return true;
				 } else {
						throw new InvalidCodeException();
				 }
			}
			return false;
	 }

	 @Override
	 public String toString()
	 {
			return this.name == null ? "" : this.name;
	 }

	 @Override
	 public String toBinaryString()
	 {
			return bits == null ? Integer.toBinaryString( 0 )
						: bits.toBinaryString();
	 }

	 /**
	  * Simple constructor
	  * 
	  * @throws InvalidCodeException
	  */
	 public Register() throws InvalidCodeException
	 {
			super();
			this.code = new Code( "" );
			this.bits = new Bits( "0000" );
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param code
	  * @throws InvalidCodeException
	  */
	 public Register( Code code ) throws InvalidCodeException
	 {
			super();
			this.code = code;
			parse();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Bits bits
	  */
	 public Register( Bits bits )
	 {
			super();
			this.bits = bits;
			this.decode();
	 }

}
