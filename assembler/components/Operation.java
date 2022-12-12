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

/**
 * Class representing Op Type & Code in ARM Instruction.
 *
 */
public class Operation implements Decodable, Parsable, Queryable
{

	 /**
	  * {@value #opType} Op Type
	  */
	 private Bits opType;

	 /**
	  * {@value #opCode} Op Code
	  */
	 private Bits opCode;

	 /**
	  * {@value #opClass} Op Class: Data Processing, Load/Store
	  */
	 private String opClass;

	 /**
	  * {@value #sBit} sBit (0 == Store; 1 == Load)
	  */
	 private Bits sBit;

	 /**
	  * {@value #condition} Condition (EQ, NE, etc.)
	  */
	 private Condition condition;

	 /**
	  * {@value #codeType} Instruction Type: (Immediate = 0, Register = 1)
	  */
	 private int codeType;

	 /**
	  * {@value #instruction} Instruction (MUL, AND, etc.)
	  */
	 private String instruction;

	 /**
	  * {@value #inType} Instruction Type: (I == 0, R == 1, I/R == 2)
	  */
	 private int inType;

	 /**
	  * {@value #action} Action: Rn and/or Op2
	  */
	 private String[] action;

	 /**
	  * {@value #loadStoreOp} Load/Store Operation (SUB, PRI, etc.)
	  */
	 private String loadStoreOp;

	 /**
	  * @return Bits opType (00 = Data Processing, 01 = Load/Store)
	  */
	 public Bits getOpType()
	 {
			return opType;
	 }

	 /**
	  * @return Bits opCode
	  */
	 public Bits getOpCode()
	 {
			return opCode;
	 }

	 /**
	  * @return String opClass (Data Processing, Load/Store)
	  */
	 public String getOpClass()
	 {
			return opClass;
	 }

	 /**
	  * Returns Load / Store bit
	  * 
	  * @return Bits sBit
	  */
	 public Bits getLoadStore()
	 {
			return opType.matches( "^01(.*)" ) ? sBit : null;
	 }

	 /**
	  * @return Bits sBit
	  */
	 public Bits getSBit()
	 {
			return sBit;
	 }

	 /**
	  * Returns Instruction Command S Flag
	  * 
	  * @return String sBit
	  */
	 public String getSFlag()
	 {
			return opType.matches( "^00(.*)" ) && opClass.matches( "DP" )
						&& sBit.toBinaryString().equals( "1" ) ? "S" : "";
	 }

	 /**
	  * @return Condition condition (NE, EQ, etc)
	  */
	 public Condition getCondition()
	 {
			return condition;
	 }

	 /**
	  * @return int codeType (0 = Immediate, 1 = Register)
	  */
	 public int getCodeType()
	 {
			return codeType;
	 }

	 /**
	  * @return String instruction (MUL, AND, etc.)
	  */
	 public String getInstruction()
	 {
			return instruction;
	 }

	 /**
	  * @return int inType (0 = Imm, 1 = Register, 2 = Both)
	  */
	 public int getInType()
	 {
			return inType;
	 }

	 /**
	  * @return String[] action
	  */
	 public String[] getAction()
	 {
			return action;
	 }

	 /**
	  * @return String loadStoreOp (e.g. SUB, PSI, etc)
	  */
	 public String getLoadStoreOp()
	 {
			return loadStoreOp;
	 }

	 /**
	  * @param Bits sBit
	  * @throws InvalidCodeException
	  */
	 public void setSBit( Bits sBit ) throws InvalidCodeException
	 {
			this.sBit = sBit;
			this.decode();
	 }

	 /**
	  * @param Condition condition
	  * @throws InvalidCodeException
	  */
	 public void setCondition( Condition condition ) throws InvalidCodeException
	 {
			this.condition = condition;
			this.decode();
	 }

	 /**
	  * @param String instruction
	  */
	 public void setInstruction( String instruction )
	 {
			this.instruction = instruction;
	 }

	 /**
	  * @param String name
	  */
	 private void addAction( String name )
	 {
			if (this.action == null) {
				 this.action = new String[] { name };
			} else {
				 String[] tmp = this.action;
				 this.action = new String[tmp.length + 1];
				 for (int i = 0; i < tmp.length; i++) {
						this.action[i] = tmp[i];
				 }
				 this.action[tmp.length] = name;
			}
	 }

	 /**
	  * Returns true if opType is DP and opCode is 1101
	  * 
	  * @return Boolean isShift
	  */
	 public Boolean isShift()
	 {
			return opCode.matches( "1101" ) && opClass.matches( "DP" );
	 }

	 /**
	  * Returns true if register instruction
	  * 
	  * @return Boolean isRegister
	  */
	 public Boolean isRegister()
	 {
			return opType.matches( "(.*)0$" );
	 }

	 /**
	  * Returns true if immediate instruction
	  * 
	  * @return Boolean isImmediate
	  */
	 public Boolean isImmediate()
	 {
			return opType.matches( "(.*)1$" );
	 }

	 @Override
	 public String toString()
	 {
			return getInstruction();
	 }

	 @Override
	 public String toBinaryString()
	 {
			return opType.toBinaryString();
	 }

	 @Override
	 public Boolean decode() throws InvalidCodeException
	 {
			Connection conn = db.getConn();
			try {
				 String sql = "SELECT * FROM op_code "
							 + "JOIN op_type ON op_type.op_type = op_code.op_type "
							 + "WHERE op_code = ? AND op_code.op_type = ? "
							 + "AND load_store = ?";
				 PreparedStatement stmt = conn.prepareStatement( sql );
				 Bits loadStore = getLoadStore();
				 stmt.setString( 1, opCode.toBinaryString() );
				 stmt.setString( 2, opType.toBinaryString() );
				 stmt.setInt( 3, loadStore == null ? -1 : loadStore.toInteger() );
				 ResultSet rs = stmt.executeQuery();

				 if (!rs.isBeforeFirst()) {
						rs.close();
						stmt.close();
						throw new InvalidCodeException();
				 } else {
						while (rs.next()) {
							 int rn = rs.getInt( "rn" );
							 int op2 = rs.getInt( "op2" );
							 int codeType = rs.getInt( "data_type" );
							 String instruction = rs.getString( "instr" );
							 String condition = this.condition == null ? ""
										 : this.condition.toString();
							 this.opClass = rs.getString( "op_class" );
							 this.inType = rs.getInt( "instr_type" );
							 this.loadStoreOp = rs.getString( "load_store_instr" );
							 this.codeType = codeType;
							 this.instruction = instruction + getSFlag() + condition;
							 if (rn == 1) {
									this.addAction( "rn" );
							 }
							 if (op2 == 1) {
									this.addAction( "op2" );
							 }
							 break;
						}
				 }
				 rs.close();
				 stmt.close();
			} catch (SQLException e) {
				 throw new InvalidCodeException( e.getMessage() );
			}
			return true;
	 }

	 @Override
	 public Boolean parse()
	 {
			return true;
	 }

	 /**
	  * Simple constructor
	  * 
	  * @param Bits opType
	  * @param Bits opCode
	  * @throws InvalidCodeException
	  */
	 public Operation( Bits opType, Bits opCode ) throws InvalidCodeException
	 {
			super();
			this.opType = opType;
			this.opCode = opCode;
			this.decode();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Bits      opType
	  * @param Bits      opCode
	  * @param Condition condition
	  */
	 public Operation( Bits opType, Bits opCode, Condition condition )
				 throws InvalidCodeException
	 {
			super();
			this.opType = opType;
			this.opCode = opCode;
			this.condition = condition;
			this.sBit = new Bits( "0" );
			this.decode();
	 }

	 /**
	  * Overloaded constructor
	  * 
	  * @param Bits      opType
	  * @param Bits      opCode
	  * @param Bits      sBit
	  * @param Condition condition
	  */
	 public Operation( Bits opType, Bits opCode, Bits sBit,
				 Condition condition ) throws InvalidCodeException
	 {
			super();
			this.opType = opType;
			this.opCode = opCode;
			this.condition = condition;
			this.sBit = sBit;
			this.decode();
	 }

}
