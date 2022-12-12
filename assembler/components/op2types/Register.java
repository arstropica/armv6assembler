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
import assembler.exceptions.InvalidCodeException;
import assembler.interfaces.Decodable;
import assembler.interfaces.Op2Type;
import assembler.interfaces.Parsable;
import assembler.interfaces.Queryable;
import assembler.types.Bits;
import assembler.types.Code;

/**
 * Register Format Operand2 Value
 *
 */
public class Register implements Op2Type, Decodable, Parsable, Queryable
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
	  * {@value #shiftMode} Register Format (0 = ShAmt, 1 = Register)
	  */
	 private int shiftMode;

	 /**
	  * {@link assembler.components.Register} rm register
	  */
	 private assembler.components.Register rm;

	 /**
	  * {@link assembler.components.Register} rs register #
	  */
	 private assembler.components.Register rs;

	 /**
	  * {@value #shAmt} Rotation Amount (ShAmt Format only)
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

	 private Map<String, Boolean> actions = new HashMap<>()
	 {
			{
				 put( "rn", false );
				 put( "op2", false );
			}
	 };

	 /**
	  * {@link Operation} Operation
	  */
	 private Operation operation;

	 /**
	  * {@value #condition} Condition
	  */
	 private Condition condition;

	 /**
	  * {@value #op2Code} Op2Type raw code
	  */
	 private Bits op2Code;

	 /**
	  * @return Code source code
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
	  * @return Bits op2Code (raw operand2 code)
	  */
	 public Bits getOp2Code()
	 {
			return op2Code;
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
	  * @return assembler.components.Register rm
	  */
	 public assembler.components.Register getRm()
	 {
			return rm;
	 }

	 /**
	  * @return assembler.components.Register rs
	  */
	 public assembler.components.Register getRs()
	 {
			return rs;
	 }

	 /**
	  * @return String shiftOp (MOV, LSL, ASR, etc.)
	  */
	 public String getShiftOp()
	 {
			return shiftOp;
	 }

	 /**
	  * @return Map<String, Boolean> actions
	  */
	 public Map<String, Boolean> getActions()
	 {
			return actions;
	 }

	 /**
	  * Gets state for a single action.
	  * 
	  * @param String action
	  * @return Boolean state
	  */
	 public Boolean getAction( String action )
	 {
			return actions.get( action );
	 }

	 /**
	  * @return int shAmt (ShAmt shiftMode only)
	  */
	 public int getShAmt()
	 {
			return shAmt;
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
	  * @param int    shiftMode (0 = shAmt, 1 = R)
	  * @return String shiftType
	  * @throws InvalidCodeException
	  */
	 public int getShiftType( String shiftOp, int shiftMode )
				 throws InvalidCodeException
	 {
			Connection conn = db.getConn();
			try {
				 String sql = "SELECT * FROM operand2 WHERE instr = ? AND instr_type = ?";
				 PreparedStatement stmt = conn.prepareStatement( sql );
				 stmt.setString( 1, shiftOp );
				 stmt.setInt( 2, shiftMode );
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
			assembler.components.Register rs = getRs();
			assembler.components.Register rm = getRm();
			int shAmt = getShAmt();
			int shiftMode = getShiftMode();
			String shiftOp = getShiftOp();
			String output = "";
			if (shiftMode == 0) { // shAmt
				 if (!operation.isShift() && isRRX()) {
						output = String.format( "%s, %s", rm, shiftOp );
				 } else if (shAmt == 0) {
						output = String.format( "%s", rm );
				 } else if (operation.isShift()) {
						output = String.format( "%s, #%d", rm, shAmt );
				 } else {
						output = String.format( "%s, %s #%d", rm, shiftOp, shAmt );
				 }
			} else { // Register
				 if (operation.isShift()) {
						output = String.format( "%s, %s", rm, rs );
				 } else {
						output = String.format( "%s, %s %s", rm, shiftOp, rs );
				 }
			}
			return output;
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
			this.rm = new assembler.components.Register( new Bits( rm ) );
	 }

	 /**
	  * Sets rm Register
	  * 
	  * @param int rm
	  * @throws InvalidCodeException
	  */
	 public void setRm( int rm ) throws InvalidCodeException
	 {
			this.rm = new assembler.components.Register(
						new Bits( String.valueOf( rm ), 10 ) );
	 }

	 /**
	  * Sets rm Register
	  * 
	  * @param Code rm
	  * @throws InvalidCodeException
	  */
	 public void setRm( Code rm ) throws InvalidCodeException
	 {
			this.rm = new assembler.components.Register( rm );
	 }

	 /**
	  * Sets rs Register
	  * 
	  * @param String rs
	  * @throws InvalidCodeException
	  */
	 public void setRs( String rs ) throws InvalidCodeException
	 {
			this.rs = new assembler.components.Register( new Bits( rs ) );
	 }

	 /**
	  * Sets rs Register
	  * 
	  * @param int rs
	  * @throws InvalidCodeException
	  */
	 public void setRs( int rs ) throws InvalidCodeException
	 {
			this.rs = new assembler.components.Register(
						new Bits( String.valueOf( rs ), 10 ) );
	 }

	 /**
	  * Sets rs Register
	  * 
	  * @param Code rm
	  * @throws InvalidCodeException
	  */
	 public void setRs( Code rs ) throws InvalidCodeException
	 {
			this.rs = new assembler.components.Register( rs );
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
			int bits_4_11 = Integer.parseInt( op2Code.substring( 0, 7 ), 2 );
			Boolean bits_lsl = bits_4_11 != 0;
			Connection conn = db.getConn();
			try {
				 String sql = "SELECT * FROM operand2 WHERE shift_type = ? AND instr_type = ?";
				 PreparedStatement stmt = conn.prepareStatement( sql );
				 stmt.setString( 1, shiftCode );
				 stmt.setInt( 2, format );
				 ResultSet rs = stmt.executeQuery();

				 if (!rs.isBeforeFirst()) {
						rs.close();
						stmt.close();
						throw new InvalidCodeException();
				 } else {
						while (rs.next()) {
							 instr = rs.getString( "instr" );
							 if (instr.matches( "ROR" ) && isRRX()) {
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
				 }
				 rs.close();
				 stmt.close();
			} catch (SQLException e) {
				 throw new InvalidCodeException( e.getMessage() );
			}
	 }

	 /**
	  * Update action
	  * 
	  * @param String  action
	  * @param Boolean state
	  */
	 public void setAction( String action, Boolean state )
	 {
			this.actions.put( action, state );
	 }

	 /**
	  * Sets main Operation instruction using opCode and shift operation.
	  * 
	  * @return void
	  */
	 public void setInstruction()
	 {
			Condition condition = operation.getCondition();
			String sFlag = operation.getSFlag();
			if (operation.isShift()) {
				 if (isMOV()) {
						operation.setInstruction( "MOV" + sFlag + condition );
				 } else {
						operation.setInstruction( shiftOp + sFlag + condition );
				 }
			}
	 }

	 /**
	  * Returns true if Operand2 MOV instruction
	  * 
	  * @return Boolean isMOV
	  */
	 public Boolean isMOV()
	 {
			return operation.isShift()
						&& Integer.parseInt( op2Code.substring( 0, 8 ), 2 ) == 0;
	 }

	 /**
	  * Returns true if Operand2 MOV instruction
	  * 
	  * @param Code command
	  * @return Boolean isMOV
	  */
	 public Boolean isMOV( Code command )
	 {
			return command.matches( "MOV|LSL|LSR|ASR|ROR|RRX" );
	 }

	 /**
	  * Returns true if Operand2 RRX Instruction
	  * 
	  * @return Boolean isRRX
	  */
	 public Boolean isRRX()
	 {
			String bits_instr = op2Code.substring( 5, 8 );
			int bits_7_11 = Integer.parseInt( op2Code.substring( 0, 5 ), 2 );
			Boolean bits_rrx = bits_7_11 + shiftMode == 0;
			return bits_instr.matches( "110" ) && bits_rrx;
	 }

	 /**
	  * Returns true if Operand2 RRX Instruction
	  * 
	  * @param Code command
	  * @return Boolean isRRX
	  */
	 public Boolean isRRX( Code command )
	 {
			return command.matches( "RRX" );
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
				 opType = new Bits( "000" );

				 try {
						String sql = "SELECT * FROM op_code " + "WHERE instr = ? "
									+ "AND load_store = -1 " + "AND instr_type = 1";
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
												put( "s_bit", new Bits( sBit == null ? "0" : "1" ) );
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
												put( "s_bit", new Bits( sBit == null ? "0" : "1" ) );
										 }
									};
									break;
							 }
						}
						rs.close();
						stmt.close();
						return opCodes;
				 } catch (

				 SQLException e) {
						throw new InvalidCodeException( e.getMessage() );
				 }
			}
	 }

	 @Override
	 public Boolean decode() throws InvalidCodeException
	 {
			String rm = op2Code.substring( 8, 12 );
			String shiftMode = op2Code.substring( 7, 8 );
			String shiftType = op2Code.substring( 5, 7 );
			this.setRm( Integer.parseInt( rm, 2 ) );
			this.setShiftMode( shiftMode );
			this.setShiftType( shiftType );

			switch (shiftMode) {
			case "0": // ShAmt
				 this.setShAmt( op2Code.substring( 0, 5 ) );
				 this.setShiftOp( shiftType, this.shiftMode );
				 // System.out.println(String.format( "shAmt: %s; Shift Type: %s; S/R:
				 // %s; rm: %s", op2Code.substring( 0, 5 ), shiftType, shiftMode, rm )
				 // );
				 break;
			case "1": // Register
				 this.setRs( op2Code.substring( 0, 4 ) );
				 this.setShiftOp( shiftType, this.shiftMode );
				 // System.out.println( String.format( "rs: %s; Shift Type: %s; S/R:
				 // %s; rm: %s", op2Code.substring( 0, 4 ), shiftType, shiftMode, rm )
				 // );
				 break;
			}
			this.setInstruction();

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
			Code command = parts.get( "command" );
			String mode = null, rgex = null;
			String shOpCode, shAmtCode, rmCode, rsCode;
			Boolean found = false;
			Boolean isMOV = isMOV( command );

			parseLoop: for (Map.Entry<String, String> entry : rSchema.entrySet()) {
				 mode = entry.getKey();
				 rgex = entry.getValue();

				 Pattern p = Pattern.compile( rgex, Pattern.CASE_INSENSITIVE );
				 Matcher matcher = p.matcher( op2 );
				 found = matcher.find();
				 if (found) {
						switch (mode) {
						case "register":
							 rmCode = matcher.group( 1 );
							 setRm( new Code( rmCode ) );
							 shOpCode = isMOV( command ) ? command.toString() : "LSL";
							 shiftMode = 0;
							 shiftType = getShiftType( shOpCode, shiftMode );
							 shAmt = 0;
							 op2Code = new Bits( String
										 .format( "%5s%2s%1s%4s", Integer.toBinaryString( shAmt ),
													 Integer.toBinaryString( shiftType ),
													 Integer.toBinaryString( shiftMode ),
													 rm.toBinaryString() )
										 .replaceAll( " ", "0" ) );
							 break parseLoop;
						case "register_register":
							 shiftType = 0;
							 if (isMOV) {
									rmCode = matcher.group( 1 );
									setRm( new Code( rmCode ) );
									rsCode = matcher.group( 2 );
									setRs( new Code( rsCode ) );
									shiftMode = 1;
									op2Code = new Bits(
												String.format( "%4s0%2s%1s%4s", rs.toBinaryString(),
															Integer.toBinaryString( shiftType ),
															Integer.toBinaryString( shiftMode ),
															rm.toBinaryString() ).replaceAll( " ", "0" ) );
							 } else {
									rmCode = matcher.group( 2 );
									setRm( new Code( rmCode ) );
									shiftMode = 0;
									shAmt = 0;
									op2Code = new Bits( String.format( "%5s%2s%1s%4s",
												Integer.toBinaryString( shAmt ),
												Integer.toBinaryString( shiftType ),
												Integer.toBinaryString( shiftMode ),
												rm.toBinaryString() ).replaceAll( " ", "0" ) );
							 }
							 break parseLoop;
						case "register_immediate":
							 rmCode = matcher.group( 1 );
							 setRm( new Code( rmCode ) );
							 shOpCode = isMOV( command ) ? command.toString() : "LSL";
							 shiftMode = 0;
							 shiftType = getShiftType( shOpCode, shiftMode );
							 shAmtCode = matcher.group( 2 );
							 shAmt = Integer.parseInt( shAmtCode.substring( 1 ) );
							 if (shAmt == 32) {
									shAmt = 0;
							 }
							 op2Code = new Bits( String
										 .format( "%5s%2s%1s%4s", Integer.toBinaryString( shAmt ),
													 Integer.toBinaryString( shiftType ),
													 Integer.toBinaryString( shiftMode ),
													 rm.toBinaryString() )
										 .replaceAll( " ", "0" ) );
							 break parseLoop;
						case "register_shift":
							 rmCode = matcher.group( 1 );
							 setRm( new Code( rmCode ) );
							 shOpCode = matcher.group( 2 );
							 shiftMode = 0;
							 shAmt = 0;
							 shiftType = getShiftType( shOpCode, shiftMode );
							 if (matcher.groupCount() > 2 && matcher.group( 3 ) != null) {
									shAmtCode = matcher.group( 3 );
									shAmt = Integer.parseInt( shAmtCode.substring( 1 ) );
									if (shAmt == 32) {
										 shAmt = 0;
									}
							 }
							 op2Code = new Bits( String
										 .format( "%5s%2s%1s%4s", Integer.toBinaryString( shAmt ),
													 Integer.toBinaryString( shiftType ),
													 Integer.toBinaryString( shiftMode ),
													 rm.toBinaryString() )
										 .replaceAll( " ", "0" ) );
							 break parseLoop;
						case "register_rrx":
							 rmCode = matcher.group( 1 );
							 setRm( new Code( rmCode ) );
							 shOpCode = matcher.group( 2 );
							 shiftMode = 0;
							 shAmt = 0;
							 shiftType = getShiftType( shOpCode, shiftMode );
							 op2Code = new Bits( String
										 .format( "%5s%2s%1s%4s", Integer.toBinaryString( shAmt ),
													 Integer.toBinaryString( shiftType ),
													 Integer.toBinaryString( shiftMode ),
													 rm.toBinaryString() )
										 .replaceAll( " ", "0" ) );
							 break parseLoop;
						case "register_register_shift":
							 rmCode = matcher.group( 2 );
							 setRm( new Code( rmCode ) );
							 shOpCode = matcher.group( 3 );
							 shiftMode = 0;
							 shAmt = 0;
							 shiftType = getShiftType( shOpCode, shiftMode );
							 if (matcher.groupCount() > 3 && matcher.group( 4 ) != null) {
									shAmtCode = matcher.group( 4 );
									shAmt = Integer.parseInt( shAmtCode.substring( 1 ) );
									if (shAmt == 32) {
										 shAmt = 0;
									}
							 }
							 op2Code = new Bits( String
										 .format( "%5s%2s%1s%4s", Integer.toBinaryString( shAmt ),
													 Integer.toBinaryString( shiftType ),
													 Integer.toBinaryString( shiftMode ),
													 rm.toBinaryString() )
										 .replaceAll( " ", "0" ) );
							 break parseLoop;
						case "register_register_rrx":
							 rmCode = matcher.group( 2 );
							 setRm( new Code( rmCode ) );
							 shOpCode = matcher.group( 3 );
							 shiftMode = 0;
							 shAmt = 0;
							 shiftType = getShiftType( shOpCode, shiftMode );
							 op2Code = new Bits( String
										 .format( "%5s%2s%1s%4s", Integer.toBinaryString( shAmt ),
													 Integer.toBinaryString( shiftType ),
													 Integer.toBinaryString( shiftMode ),
													 rm.toBinaryString() )
										 .replaceAll( " ", "0" ) );
							 break parseLoop;
						case "register_shift_register":
							 rmCode = matcher.group( 1 );
							 setRm( new Code( rmCode ) );
							 rsCode = matcher.group( 3 );
							 setRs( new Code( rsCode ) );
							 shOpCode = matcher.group( 2 );
							 shiftMode = 1;
							 shiftType = getShiftType( shOpCode, shiftMode );
							 op2Code = new Bits( String.format( "%4s0%2s%1s%4s",
										 rs.toBinaryString(), Integer.toBinaryString( shiftType ),
										 Integer.toBinaryString( shiftMode ),
										 rm.toBinaryString() ).replaceAll( " ", "0" ) );
							 break parseLoop;
						case "register_register_shift_register":
							 rmCode = matcher.group( 2 );
							 setRm( new Code( rmCode ) );
							 rsCode = matcher.group( 4 );
							 setRs( new Code( rsCode ) );
							 shOpCode = matcher.group( 3 );
							 shiftMode = 1;
							 shiftType = getShiftType( shOpCode, shiftMode );
							 op2Code = new Bits( String.format( "%4s0%2s%1s%4s",
										 rs.toBinaryString(), Integer.toBinaryString( shiftType ),
										 Integer.toBinaryString( shiftMode ),
										 rm.toBinaryString() ).replaceAll( " ", "0" ) );
							 break parseLoop;
						}
				 }
			}
			if (found) {
				 this.operation = new Operation( opType, opCode, sBit, condition );
				 this.setInstruction();
				 return true;
			} else {
				 throw new InvalidCodeException( "Operand2 Code not recognized." );
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
	 public Register( Bits op2Code ) throws InvalidCodeException
	 {
			super();
			this.op2Code = op2Code;
			this.format = "Register";
			this.decode();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Code      code
	  * @param Condition condition
	  * @throws InvalidCodeException
	  */
	 public Register( Code code, Condition condition )
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
	  * @param String    op2Code
	  * @param Operation operation
	  */
	 public Register( Bits op2Code, Operation operation )
				 throws InvalidCodeException
	 {
			super();
			this.op2Code = op2Code;
			this.format = "Register";
			this.operation = operation;
			this.decode();
	 }

}
