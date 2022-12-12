/**
 * 
 */
package assembler.components.op2types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assembler.components.Condition;
import assembler.components.Operation;
import assembler.exceptions.InvalidCodeException;
import assembler.interfaces.Decodable;
import assembler.interfaces.Op2Type;
import assembler.interfaces.Parsable;
import assembler.interfaces.Queryable;
import assembler.types.Bits;
import assembler.types.Code;
import assembler.util.Binary;

/**
 * Op2Type Type Class
 *
 */
public class Immediate implements Op2Type, Queryable, Decodable, Parsable
{

	 /**
	  * {@value #code} Source code
	  */
	 private Code code;

	 /**
	  * {@value #format} Operand2 Format (Immediate or Register)
	  */
	 private String format;

	 /**
	  * {@value #op2Code} Op2Type raw code
	  */
	 private Bits op2Code;

	 /**
	  * {@value #shAmt} Rotation Amount (Immediate Format only)
	  */
	 private int shAmt;

	 /**
	  * {@value #immediate} Immediate value (Immediate Format only)
	  */
	 private int immediate;

	 /**
	  * {@link Operation} Operation
	  */
	 private Operation operation;

	 /**
	  * {@link Condition} Condition
	  */
	 private Condition condition;

	 /**
	  * @return Code code
	  */
	 public Code getCode()
	 {
			return code;
	 }

	 /**
	  * @return String format (Immediate or Register)
	  */
	 public String getFormat()
	 {
			return format;
	 }

	 /**
	  * @return Bits op2Code
	  */
	 public Bits getOp2Code()
	 {
			return op2Code;
	 }

	 /**
	  * @return int shAmt (Immediate Format only)
	  */
	 public int getShAmt()
	 {
			return shAmt;
	 }

	 /**
	  * @return int immediate
	  */
	 public int getImmediate()
	 {
			return immediate;
	 }

	 /**
	  * @return Operation operation
	  */
	 public Operation getOperation()
	 {
			return operation;
	 }

	 /**
	  * @return Condition condition
	  */
	 public Condition getCondition()
	 {
			return condition;
	 }

	 /**
	  * Sets the immediate value
	  * 
	  * @param String immediate
	  */
	 public void setImmediate( String immediate )
	 {
			this.immediate = Integer.parseInt( immediate, 2 );
	 }

	 /**
	  * Sets the immediate value
	  * 
	  * @param int immediate
	  */
	 public void setImmediate( int immediate )
	 {
			this.immediate = immediate;
	 }

	 /**
	  * Sets shift amount
	  * 
	  * @param String shAmt
	  */
	 public void setShAmt( String shAmt )
	 {
			this.shAmt = Integer.parseInt( shAmt, 2 );
	 }

	 /**
	  * Sets shift amount
	  * 
	  * @param int shAmt
	  */
	 public void setShAmt( int shAmt )
	 {
			this.shAmt = shAmt;
	 }

	 /**
	  * @return String composite value
	  */
	 public String getComposite()
	 {
			if (shAmt == 0) {
				 return String.format( "#%d", immediate );
			} else {
				 return String.format( "#%d",
							 Binary.rightRotate( immediate, shAmt ) );
			}
	 }

	 /**
	  * Calculate fix-up for immediate value
	  * 
	  * @param int number
	  * @return int[] {rotAmt, immediate, +/-}
	  */
	 public int[] getFixup( int number )
	 {
			String bin = Integer.toBinaryString( number );
			String imm = bin.replaceAll( "^[0]+|[0]+$", "" );
			int[] fixup = null;
			int rotAmt = 0, width = 32;
			if (number >= 0 && number < 256) {
				 fixup = new int[] { 0, number, 1 };
			} else if (number < 0) {
				 fixup = getFixup( ( number * -1 ) - 1 );
				 if (fixup != null) {
						fixup[2] = -1;
				 }
			} else if (imm.length() <= 8) {
				 rotAmt = width - bin.length() + imm.length();
				 if (rotAmt % 2 == 1) {
						if (imm.length() < 8) {
							 imm += "0";
							 rotAmt -= 1;
							 fixup = new int[] { rotAmt / 2, Integer.parseInt( imm, 2 ),
										 1 };
						}
				 } else {
						fixup = new int[] { rotAmt / 2, Integer.parseInt( imm, 2 ), 1 };
				 }
			}
			return fixup;
	 }

	 /**
	  * Get operation binary codes
	  * 
	  * @param Code source code
	  * @return Map<String, Bits> {op_code, op_type, s_bit}
	  * @throws InvalidCodeException
	  */
	 public Map<String, Bits> getOpCodes( Code code )
				 throws InvalidCodeException
	 {
			Bits opCode, opType;
			Connection conn = db.getConn();
			Map<String, Code> parts;
			Map<String, Bits> opCodes = null;
			Code command, sBit;
			if (code.isBlank()) {
				 throw new InvalidCodeException();
			} else {
				 parts = extract( code );
				 command = parts.get( "command" );
				 sBit = parts.get( "s_bit" );
				 opType = new Bits( "001" );
				 try {
						String sql = "SELECT * FROM op_code " + "WHERE instr = ? "
									+ "AND load_store = -1 " + "AND instr_type = 0";
						PreparedStatement stmt = conn.prepareStatement( sql );
						stmt.setString( 1, command.toString() );
						ResultSet rs = stmt.executeQuery();

						if (!rs.isBeforeFirst()) {
							 rs.close();
							 stmt.close();
							 sql = "SELECT * FROM operand2 WHERE instr = ?";
							 stmt = conn.prepareStatement( sql );
							 stmt.setString( 1, command.toString() );
							 rs = stmt.executeQuery();
							 if (!rs.isBeforeFirst()) {
									rs.close();
									stmt.close();
									throw new InvalidCodeException();
							 } else {
									opCode = new Bits( "1101" );
									opCodes = new HashMap<String, Bits>()
									{
										 {
												put( "op_code", opCode );
												put( "op_type", opType );
												put( "s_bit",
															new Bits( sBit == null ? "0" : "1" ) );
										 }
									};
							 }
						} else {
							 while (rs.next()) {
									opCode = new Bits( rs.getString( "op_code" ) );
									opCodes = new HashMap<String, Bits>()
									{
										 {
												put( "op_code", opCode );
												put( "op_type", opType );
												put( "s_bit",
															new Bits( sBit == null ? "0" : "1" ) );
										 }
									};
									break;
							 }
						}
						rs.close();
						stmt.close();
						return opCodes;
				 } catch (SQLException e) {
						throw new InvalidCodeException(e.getMessage());
				 }
			}
	 }

	 @Override
	 public Boolean decode()
	 {
			String shAmtCode = op2Code.substring( 0, 4 );
			String immCode = op2Code.substring( 4 );
			int shVal = Integer.parseInt( shAmtCode + "0", 2 );
			int immVal = Integer.parseInt( immCode, 2 );
			this.setShAmt( shVal );
			this.setImmediate( immVal );
			return true;
	 }

	 @Override
	 public Boolean parse() throws InvalidCodeException
	 {
			if (code.isBlank()) {
				 throw new InvalidCodeException();
			}
			Bits opCode, opType, sBit;
			Map<String, Bits> opCodes;
			opCodes = getOpCodes( code );
			opCode = opCodes.get( "op_code" );
			opType = opCodes.get( "op_type" );
			sBit = opCodes.get( "s_bit" );

			Map<String, Code> parts = extract( code );
			Code op2 = parts.get( "operand2" );
			int immVal, shAmt;
			String space = "[\\s]";
			String imm = "#-?[0-9]+";
			String sh = "[0-9]+";
			String regex = String.format( "(%s)(?:%s*,%s*(%s))?", imm, space, space,
						sh );
			Pattern patt = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
			Matcher matcher = patt.matcher( op2 );
			Boolean found = matcher.find();
			if (found) {
				 String immCode = matcher.group( 1 );
				 immVal = Integer.parseInt( immCode.substring( 1 ) );
				 if (matcher.groupCount() > 1 && matcher.group( 2 ) != null) {
						String shCode = matcher.group( 2 );
						shAmt = Integer.parseInt( shCode.substring( 1 ) );
				 } else {
						if (immVal < 0) {
							 if (opCode.matches( "1101|1111" )) {
									opCode = new Bits(
												opCode.matches( "1111" ) ? "1101" : "1111" );
							 } else if (opCode.matches( "0010|0100" )) {
									immVal *= -1;
									opCode = new Bits(
												opCode.matches( "0010" ) ? "0100" : "0100" );
							 }
						}
						int[] fixup = getFixup( immVal );
						if (fixup != null) {
							 shAmt = fixup[0];
							 immVal = fixup[1];
						} else {
							 throw new InvalidCodeException(
										 "Error: invalid constant after fixup" );
						}
				 }
				 this.setImmediate( immVal );
				 this.setShAmt( shAmt );
				 this.op2Code = new Bits( String
							 .format( "%4s%8s", Integer.toBinaryString( shAmt ),
										 Integer.toBinaryString( immVal ) )
							 .replaceAll( " ", "0" ) );
				 this.operation = new Operation( opType, opCode, sBit, condition );
				 return true;
			} else {
				 throw new InvalidCodeException();
			}
	 }

	 @Override
	 public String toString()
	 {
			return getComposite();
	 }

	 @Override
	 public String toBinaryString()
	 {
			return op2Code.toBinaryString();
	 }

	 /**
	  * Simple constructor
	  * 
	  * @param Bits op2Code
	  */
	 public Immediate( Bits op2Code )
	 {
			super();
			this.op2Code = op2Code;
			this.format = "Immediate";
			this.decode();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Code      code
	  * @param Condition condition
	  * @throws InvalidCodeException
	  */
	 public Immediate( Code code, Condition condition )
				 throws InvalidCodeException
	 {
			super();
			this.code = code;
			this.condition = condition;
			this.parse();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Bits      op2Code
	  * @param Operation operation
	  */
	 public Immediate( Bits op2Code, Operation operation )
	 {
			super();
			this.op2Code = op2Code;
			this.format = "Immediate";
			this.operation = operation;
			this.decode();
	 }

}
