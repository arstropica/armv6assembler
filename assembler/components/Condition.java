/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.components;

import java.sql.*;

import assembler.exceptions.InvalidCodeException;
import assembler.interfaces.Decodable;
import assembler.interfaces.Parsable;
import assembler.interfaces.Queryable;
import assembler.types.Bits;
import assembler.types.Code;

/**
 * Class representing Cond Code in ARM Instruction.
 *
 */
public class Condition implements Decodable, Parsable, Queryable
{

	 /**
	  * {@value #bits} Condition bits
	  */
	 private Bits bits;

	 /**
	  * {@value #code} Condition code
	  */
	 private Code code;

	 /**
	  * {@value #sBit} S Bit
	  */
	 private Bits sBit;

	 /**
	  * {@value #suffix} Suffix
	  */
	 private String suffix;

	 /**
	  * @return Bits bits
	  */
	 public Bits getBits()
	 {
			return bits;
	 }

	 /**
	  * @return Code condition code
	  */
	 public Code getCode()
	 {
			return code;
	 }

	 /**
	  * @return Bits sBit
	  */
	 public Bits getsBit()
	 {
			return sBit;
	 }

	 /**
	  * @return String suffix
	  */
	 public String getSuffix()
	 {
			return suffix;
	 }

	 /**
	  * @param Bits sBit
	  */
	 public void setSBit( Bits sBit )
	 {
			this.sBit = sBit;
	 }

	 @Override
	 public String toString()
	 {
			return suffix.matches( "AL" ) ? "" : suffix;
	 }

	 @Override
	 public String toBinaryString()
	 {
			return bits.toBinaryString();
	 }

	 @Override
	 public Boolean decode() throws InvalidCodeException
	 {
			Connection conn = db.getConn();
			try {
				 String sql = "SELECT * FROM cond_code WHERE code = ?";
				 PreparedStatement stmt = conn.prepareStatement( sql );
				 stmt.setString( 1, bits.toBinaryString() );
				 ResultSet rs = stmt.executeQuery();

				 while (rs.next()) {
						this.suffix = rs.getString( "suffix" );
						this.code = new Code( rs.getString( "suffix" ) );
				 }
				 rs.close();
				 stmt.close();
				 return true;
			} catch (SQLException e) {
				 throw new InvalidCodeException( e.getMessage() );
			}
	 }

	 public Boolean parse() throws InvalidCodeException
	 {
			Connection conn = db.getConn();
			try {
				 String sql = "SELECT * FROM cond_code WHERE suffix = ?";
				 PreparedStatement stmt = conn.prepareStatement( sql );
				 stmt.setString( 1, code.toString() );
				 ResultSet rs = stmt.executeQuery();

				 while (rs.next()) {
						this.suffix = rs.getString( "suffix" );
						this.bits = new Bits( rs.getString( "code" ) );
				 }
				 rs.close();
				 stmt.close();
				 return true;
			} catch (SQLException e) {
				 throw new InvalidCodeException( e.getMessage() );
			}

	 }

	 /**
	  * Simple constructor
	  * 
	  * @throws InvalidCodeException
	  */
	 public Condition() throws InvalidCodeException
	 {
			super();
			this.code = new Code( "AL" );
			this.sBit = new Bits( "0" );
			parse();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Code code
	  * @throws InvalidCodeException
	  */
	 public Condition( Code code ) throws InvalidCodeException
	 {
			super();
			if (code == null || code.isBlank()) {
				 code = new Code( "AL" );
			}
			this.code = code;
			this.sBit = new Bits( "0" );
			parse();
	 }

	 /**
	  * Overloaded constructor
	  * 
	  * @param Bits bits
	  * @param Bits sBit
	  * 
	  */
	 public Condition( Bits bits, Bits sBit ) throws InvalidCodeException
	 {
			super();
			this.bits = bits;
			this.sBit = sBit;
			decode();
	 }

	 /**
	  * Overloaded constructor
	  * 
	  * @param Code code
	  * @param Code sBit
	  * @throws InvalidCodeException
	  */
	 public Condition( Code code, Code sBit ) throws InvalidCodeException
	 {
			super();
			if (code == null || code.isBlank()) {
				 code = new Code( "AL" );
			}
			this.code = code;
			if (sBit == null) {
				 this.sBit = new Bits( "0" );
			} else {
				 this.sBit = new Bits( sBit.isBlank() ? "0" : "1" );
			}
			parse();
	 }

}
