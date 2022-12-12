/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.components.op2types;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assembler.components.Condition;
import assembler.components.Operation;
import assembler.components.Register;
import assembler.exceptions.InvalidCodeException;
import assembler.interfaces.Decodable;
import assembler.interfaces.Op2Type;
import assembler.interfaces.Parsable;
import assembler.interfaces.Queryable;
import assembler.types.Bits;
import assembler.types.Code;
import assembler.util.Binary;

/**
 * LoadStore Format Operand2 Value
 *
 */
public class LoadStore implements Op2Type, Decodable, Parsable, Queryable
{

	 /**
	  * {@value #code} operand2 source code
	  */
	 private Code code;

	 /**
	  * {@value #format} Operand2 Format (Immediate or Register)
	  */
	 private String format;

	 /**
	  * {@value #op2Code} operand2 binary code
	  */
	 private Bits op2Code;

	 /**
	  * {@value #loadStore} Load/Store (0 = Load, 1 = Store)
	  */
	 private Bits loadStore;

	 /**
	  * {@value #shiftMode} Register Format (0 = ShAmt, 1 = Register)
	  */
	 private int shiftMode;

	 /**
	  * {@link Register} rm register
	  */
	 private Register rm;

	 /**
	  * {@value #shAmt} Shift/Rotation Amount
	  */
	 private int shAmt;

	 /**
	  * {@value #shiftType} Shift Type (00, 01, etc)
	  */
	 private int shiftType;

	 /**
	  * {@value #shiftOp} Shift Operation (MOV, LSL, ASR, etc.)
	  */
	 private String shiftOp;

	 /**
	  * {@value #immediate} Immediate value (Immediate Format only)
	  */
	 private int immediate;

	 /**
	  * {@link Operation} Operation
	  */
	 private Operation operation;

	 /**
	  * {@value #condition} Condition
	  */
	 private Condition condition;

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
	  * @return Bits loadStore (0 = Load, 1 = Store)
	  */
	 public Bits getLoadStore()
	 {
			return loadStore;
	 }

	 /**
	  * @return int shAmt
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
	  * @return int shiftMode (0 = ShAmt, 1 = Register)
	  */
	 public int getShiftMode()
	 {
			return shiftMode;
	 }

	 /**
	  * @return Register rm
	  */
	 public Register getRm()
	 {
			return rm;
	 }

	 /**
	  * @return String shiftOp (MOV, LSL, ASR, etc.)
	  */
	 public String getShiftOp()
	 {
			return shiftOp;
	 }

	 /**
	  * @return int shiftType (00, 01, etc.)
	  */
	 public int getShiftType()
	 {
			return shiftType;
	 }

	 /**
	  * Returns ShiftType from Shift Op (01 = LSR)
	  * 
	  * @param String shiftOp
	  * @param int    format
	  * @return String shiftType
	  * @throws InvalidCodeException
	  */
	 public int getShiftType( String shiftOp, int format )
				 throws InvalidCodeException
	 {
			Connection conn = db.getConn();
			try {
				 String sql = "SELECT * FROM operand2 WHERE instr = ? AND instr_type = ?";
				 PreparedStatement stmt = conn.prepareStatement( sql );
				 stmt.setString( 1, shiftOp );
				 stmt.setInt( 2, format );
				 ResultSet rs = stmt.executeQuery();

				 while (rs.next()) {
						this.setShiftType( rs.getString( "shift_type" ) );
						break;
				 }
				 rs.close();
				 stmt.close();
				 return shiftType;
			} catch (SQLException e) {
				 throw new InvalidCodeException( e.getMessage() );
			}
	 }

	 /**
	  * @return String composite value
	  */
	 public String getComposite()
	 {
			int shAmt = getShAmt();
			Register rm = getRm();
			int shiftMode = getShiftMode();
			String format = getFormat();
			Bits opCode = operation.getOpCode();
			String shiftOp = getShiftOp();
			String output = "";

			if (format.matches( "Immediate" )) {
				 if (shAmt == 0) {
						output = String.format( "#%d", immediate );
				 } else {
						output = String.format( "#%d",
									Binary.rightRotate( immediate, shAmt ) );
				 }
			} else { // Register Instruction
				 if (shiftMode == 0) { // shAmt
						if (shiftOp.matches( "RRX" )) {
							 output = String.format( "%s, %s", rm, shiftOp );
						} else if (opCode.matches( "1101" ) && shAmt != 0) {
							 output = String.format( "%s, %s #%d", rm, shiftOp, shAmt );
						} else if (shAmt == 0) {
							 if (shiftOp.matches( "(.*)R$" )) {
									shAmt = 32;
									output = String.format( "%s, %s #%d", rm, shiftOp, shAmt );
							 } else {
									output = String.format( "%s", rm );
							 }
						} else {
							 output = String.format( "%s, %s #%d", rm, shiftOp, shAmt );
						}
				 }
			}
			return output;
	 }

	 /**
	  * @param String format (Immediate or Register)
	  */
	 public void setFormat( String format )
	 {
			this.format = format;
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
	  * Sets the operation
	  * 
	  * @param Operation operation
	  */
	 public void setOperation( Operation operation )
	 {
			this.operation = operation;
	 }

	 /**
	  * Sets the shiftMode
	  * 
	  * @param int shiftMode (0 = ShAmt, 1 = Register)
	  */
	 public void setShiftMode( int shiftMode )
	 {
			this.shiftMode = shiftMode;
	 }

	 /**
	  * Sets the shiftMode
	  * 
	  * @param String shiftMode
	  */
	 public void setShiftMode( String shiftMode )
	 {
			this.shiftMode = Integer.parseInt( shiftMode, 2 );
	 }

	 /**
	  * Sets shift type
	  * 
	  * @param String shiftType
	  */
	 public void setShiftType( String shiftType )
	 {
			this.shiftType = Integer.parseInt( shiftType, 2 );
	 }

	 /**
	  * Sets shift type
	  * 
	  * @param int shiftType
	  */
	 public void setShiftType( int shiftType )
	 {
			this.shiftType = shiftType;
	 }

	 /**
	  * Sets rm Register
	  * 
	  * @param String rm
	  * @throws InvalidCodeException
	  */
	 public void setRm( String rm ) throws InvalidCodeException
	 {
			this.rm = new Register( new Bits( rm ) );
	 }

	 /**
	  * Sets rm Register
	  * 
	  * @param int rm
	  * @throws InvalidCodeException
	  */
	 public void setRm( int rm ) throws InvalidCodeException
	 {
			this.rm = new Register( new Bits( String.valueOf( rm ), 10 ) );
	 }

	 /**
	  * Sets rm Register
	  * 
	  * @param Code rm
	  * @throws InvalidCodeException
	  */
	 public void setRm( Code rm ) throws InvalidCodeException
	 {
			this.rm = new Register( rm );
	 }

	 /**
	  * Set shift type
	  * 
	  * @param String shiftOp
	  */
	 public void setShiftOp( String shiftOp )
	 {
			this.shiftOp = shiftOp;
	 }

	 /**
	  * Set shift type from database
	  * 
	  * @param String shiftCode
	  * @param int    shiftMode
	  * @throws InvalidCodeException
	  */
	 public void setShiftOp( String shiftCode, int format )
				 throws InvalidCodeException
	 {
			String instr;
			int bits_7_11 = Integer.parseInt( op2Code.substring( 0, 4 ), 2 );
			int bits_4_11 = Integer.parseInt( op2Code.substring( 0, 7 ), 2 );
			Boolean bits_rrx = bits_7_11 + shiftMode == 0;
			Boolean bits_lsl = bits_4_11 != 0;
			Connection conn = db.getConn();
			try {
				 String sql = "SELECT * FROM operand2 WHERE shift_type = ? AND instr_type = ?";
				 PreparedStatement stmt = conn.prepareStatement( sql );
				 stmt.setString( 1, shiftCode );
				 stmt.setInt( 2, format );
				 ResultSet rs = stmt.executeQuery();

				 while (rs.next()) {
						instr = rs.getString( "instr" );
						if (instr.matches( "ROR" ) && bits_rrx) {
							 instr = "RRX";
						} else if (instr.matches( "MOV" ) && bits_lsl) {
							 instr = "LSL";
						}
						this.setShiftOp( instr );
						if (shAmt == 0) {
							 if (shiftOp.matches( "(.*)R$" )) {
									shAmt = 32;
							 }
						}
						break;
				 }
				 rs.close();
				 stmt.close();
			} catch (SQLException e) {
				 throw new InvalidCodeException( e.getMessage() );
			}
	 }

	 /**
	  * Returns a Map object with opCode and opType
	  * 
	  * @param Code   code
	  * @param int    inType
	  * @param String loadStoreOp
	  * @return Map<String, Bits>
	  * @throws InvalidCodeException
	  */
	 public Map<String, Bits> getOpCodes( Code code, int inType,
				 String loadStoreOp ) throws InvalidCodeException
	 {
			Bits opCode, opType;
			Connection conn = db.getConn();
			int loadStore;
			Map<String, Code> parts;
			Map<String, Bits> opCodes = null;
			Code command, sBit;
			if (code.isBlank()) {
				 throw new InvalidCodeException();
			} else {
				 parts = extract( code );
				 command = parts.get( "command" );
				 loadStore = command.matches( "LDR|STR" )
							 ? command.matches( "LDR" ) ? 1 : 0
							 : -1;
				 sBit = new Code( String.valueOf( loadStore == -1 ? 0 : loadStore ) );

				 try {
						String sql = "SELECT * FROM op_code " + "WHERE instr = ? "
									+ "AND load_store = ? " + "AND load_store_instr = ? "
									+ "AND instr_type = ?";
						PreparedStatement stmt = conn.prepareStatement( sql );
						stmt.setString( 1, command.toString() );
						stmt.setInt( 2, loadStore );
						stmt.setString( 3, loadStoreOp );
						stmt.setInt( 4, inType );
						ResultSet rs = stmt.executeQuery();

						if (!rs.isBeforeFirst()) {
							 rs.close();
							 stmt.close();
							 throw new InvalidCodeException();
						} else {
							 while (rs.next()) {
									opCode = new Bits( rs.getString( "op_code" ) );
									opType = new Bits( rs.getString( "op_type" ) );
									opCodes = new HashMap<String, Bits>()
									{
										 {
												put( "op_code", opCode );
												put( "op_type", opType );
												put( "s_bit", new Bits( sBit.toString() ) );
												put( "load_store",
															new Bits( String.valueOf( loadStore ) ) );
										 }
									};
									break;
							 }
						}
						rs.close();
						stmt.close();
						return opCodes;
				 } catch (SQLException e) {
						throw new InvalidCodeException( e.getMessage() );
				 }
			}
	 }

	 /**
	  * Decode immediate instruction
	  * 
	  * @return Boolean success
	  */
	 private Boolean decodeImmediate()
	 {
			String loadStoreOp = operation.getLoadStoreOp();
			int immVal = op2Code.toInteger()
						* ( loadStoreOp.matches( "SUB" ) ? -1 : 1 );
			this.setImmediate( immVal );
			return true;
	 }

	 /**
	  * Decode register instruction
	  * 
	  * @return Boolean success
	  * @throws InvalidCodeException
	  */
	 private Boolean decodeRegister() throws InvalidCodeException
	 {
			String rm = op2Code.substring( 8, 12 );
			String shiftType = op2Code.substring( 5, 7 );
			this.setRm( Integer.parseInt( rm, 2 ) );
			this.setShiftType( shiftType );

			switch (shiftMode) {
			case 0: // ShAmt
				 this.setShAmt( op2Code.substring( 0, 5 ) );
				 this.setShiftOp( shiftType, this.shiftMode );
				 break;
			default: // Register
				 throw new InvalidCodeException();
			}

			return true;
	 }

	 @Override
	 public Boolean decode() throws InvalidCodeException
	 {
			int codeType = operation.getCodeType();
			int shiftMode = Integer.parseInt( op2Code.substring( 7, 8 ) );
			this.setShiftMode( shiftMode );

			if (codeType == 0) { // Immediate Instruction
				 this.format = "Immediate";
				 decodeImmediate();
			} else if (shiftMode == 0) { // Scaled Register Instruction
				 this.format = "Register";
				 decodeRegister();
			} else {
				 throw new InvalidCodeException();
			}

			return true;
	 }

	 @Override
	 public Boolean parse() throws InvalidCodeException
	 {
			Bits opCode = null, opType = null, sBit = null, loadStore = null;
			int inType, shiftType;
			String form = null, mode = null, rgex = null, loadStoreOp = null;
			String immCode, shOpCode, shAmtCode, rmCode;
			Map<String, Code> parts = extract( code );
			Code op2 = parts.get( "operand2" );

			if (code.isBlank()) {
				 throw new InvalidCodeException();
			} else {
				 parseLoop: for (Map.Entry<String, Map<String, String>> codeMap : ldSchema
							 .entrySet()) {
						form = codeMap.getKey();
						Map<String, String> patterns = codeMap.getValue();

						for (Map.Entry<String, String> entry : patterns.entrySet()) {
							 mode = entry.getKey();
							 rgex = entry.getValue();

							 Pattern p = Pattern.compile( rgex, Pattern.CASE_INSENSITIVE );
							 Matcher matcher = p.matcher( op2 );
							 Boolean found = matcher.find();
							 if (found) {
									switch (form) {
									case "immediate":
										 this.format = "Immediate";
										 setImmediate( 0 );
										 this.op2Code = new Bits( "0".repeat( 12 ) );
										 if (matcher.groupCount() > 1
													 && matcher.group( 2 ) != null) {
												immCode = matcher.group( 2 );
												setImmediate(
															Integer.parseInt( immCode.substring( 1 ) ) );
												this.op2Code = new Bits( String
															.format( "%12s",
																		Integer.toBinaryString( immediate ) )
															.replace( " ", "0" ) );
										 }
										 this.setShiftMode( op2Code.substring( 7, 8 ) );
										 switch (mode) {
										 case "zero":
												loadStoreOp = "ADD";
												break;
										 case "offset":
												loadStoreOp = immediate < 0 ? "SUB" : "ADD";
												break;
										 case "pre_indexed":
												loadStoreOp = "PRI";
												break;
										 case "post_indexed":
												loadStoreOp = "PSI";
												break;
										 }
										 break parseLoop;
									case "register":
										 this.format = "Register";
										 rmCode = matcher.group( 2 );
										 setRm( new Code( rmCode ) );
										 this.op2Code = new Bits(
													 String.format( "%8s%4s", "0".repeat( 8 ),
																 rm.toBinaryString() ).replace( " ", "0" ) );
										 this.setShiftMode( op2Code.substring( 7, 8 ) );
										 switch (mode) {
										 case "offset":
												loadStoreOp = "ADD";
												break;
										 case "pre_indexed":
												loadStoreOp = "PRI";
												break;
										 case "post_indexed":
												loadStoreOp = "PSI";
												break;
										 }
										 break parseLoop;
									case "scaled":
										 this.format = "Register";
										 rmCode = matcher.group( 2 );
										 shOpCode = matcher.group( 3 );
										 shAmtCode = "#0";
										 setRm( new Code( rmCode ) );
										 setShiftOp( shOpCode );
										 shiftType = getShiftType( shOpCode, 0 );
										 if (matcher.groupCount() > 3
													 && matcher.group( 4 ) != null) {
												shAmtCode = matcher.group( 4 );
										 }
										 setShAmt( Integer.parseInt( shAmtCode.substring( 1 ) ) );
										 this.op2Code = new Bits( String.format( "%5s%2s0%4s",
													 Integer.toBinaryString( shAmt ),
													 Integer.toBinaryString( shiftType ),
													 rm.toBinaryString() ).replace( " ", "0" ) );
										 this.setShiftMode( op2Code.substring( 7, 8 ) );
										 this.setShiftType( shiftType );
										 switch (mode) {
										 case "offset":
												loadStoreOp = "ADD";
												break;
										 case "pre_indexed":
												loadStoreOp = "PRI";
												break;
										 case "post_indexed":
												loadStoreOp = "PSI";
												break;
										 }
										 break parseLoop;
									}
							 }
						}
				 }
				 if (loadStoreOp != null && format != null) {
						inType = format.matches( "Register" ) ? 1 : 0;
						Map<String, Bits> opCodes = getOpCodes( code, inType,
									loadStoreOp );
						opCode = opCodes.get( "op_code" );
						opType = opCodes.get( "op_type" );
						sBit = opCodes.get( "s_bit" );
						loadStore = opCodes.get( "load_store" );
						condition.setSBit( sBit );
						this.setOperation(
									new Operation( opType, opCode, sBit, condition ) );
						this.loadStore = loadStore;
				 } else {
						throw new InvalidCodeException();
				 }
			}
			return true;
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
	  * @param Bits loadStore
	  * @throws InvalidCodeException
	  */
	 public LoadStore( Bits op2Code, Bits loadStore )
				 throws InvalidCodeException
	 {
			super();
			this.op2Code = op2Code;
			this.loadStore = loadStore;
			this.decode();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Code      code
	  * @param Condition condition
	  * @throws InvalidCodeException
	  */
	 public LoadStore( Code code, Condition condition )
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
	  * @param Bits      loadStore
	  * @param Operation operation
	  * @throws InvalidCodeException
	  */
	 public LoadStore( Bits op2Code, Bits loadStore, Operation operation )
				 throws InvalidCodeException
	 {
			super();
			this.op2Code = op2Code;
			this.loadStore = loadStore;
			this.operation = operation;
			this.decode();
	 }

}
