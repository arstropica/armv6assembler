/**
 * 
 */
package assembler.core;

import java.util.*;

import assembler.components.Condition;
import assembler.components.Operand2;
import assembler.components.Operation;
import assembler.components.Register;
import assembler.exceptions.InvalidCodeException;
import assembler.interfaces.Decodable;
import assembler.interfaces.Parsable;
import assembler.types.Bits;
import assembler.types.Code;

/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
public class Instruction implements Decodable, Parsable
{

	 /**
	  * {@value #bits} Instruction bits
	  */
	 private Bits bits;

	 /**
	  * {@value code} Source command code
	  */
	 private Code code;

	 /**
	  * {@value #cond} Condition
	  */
	 private Condition cond;

	 /**
	  * {@value #oper} Operation
	  */
	 private Operation oper;

	 /**
	  * {@value #rn} Operand Register
	  */
	 private Register rn;

	 /**
	  * {@value #rd} Destination Register
	  */
	 private Register rd;

	 /**
	  * {@value #operand2} Operand 2
	  */
	 private Operand2 operand2;

	 /**
	  * @return Code instruction code
	  */
	 public Code getCode()
	 {
			return code;
	 }

	 /**
	  * @return Bits decimal bits
	  */
	 public Bits getBits()
	 {
			return bits;
	 }

	 /**
	  * @return Condition cond
	  */
	 public Condition getCond()
	 {
			return cond;
	 }

	 /**
	  * @return Operation oper
	  */
	 public Operation getOper()
	 {
			return oper;
	 }

	 /**
	  * @return Register rn
	  */
	 public Register getRn()
	 {
			return rn;
	 }

	 /**
	  * @return Register rd
	  */
	 public Register getRd()
	 {
			return rd;
	 }

	 /**
	  * @return Operand2 operand2
	  */
	 public Operand2 getOperand2()
	 {
			return operand2;
	 }

	 /**
	  * @return String composite value
	  */
	 public String getComposite()
	 {
			String output;
			String operation = this.getOper().toString();
			String rd = this.getRd().toString();
			String rn = this.getRn().toString();
			String operand2 = this.getOperand2().toString();
			String loadStoreOp;
			Boolean isLoadStore = this.getOperand2().isLoadStore();
			Boolean zeroOp2;

			if (isLoadStore) {
				 loadStoreOp = this.getOper().getLoadStoreOp();
				 zeroOp2 = operand2.matches( "#0" );

				 switch (loadStoreOp) {
				 case "PRI":
						if (rn.isEmpty()) {
							 output = String.format( "%s %s, [%s]!", operation, rd,
										 operand2 );
						} else if (zeroOp2) {
							 output = String.format( "%s %s, [%s]!", operation, rd, rn );
						} else {
							 output = String.format( "%s %s, [%s, %s]!", operation, rd, rn,
										 operand2 );
						}
						break;
				 case "PSI":
						if (rn.isEmpty()) {
							 output = String.format( "%s %s, [%s]", operation, rd,
										 operand2 );
						} else if (zeroOp2) {
							 output = String.format( "%s %s, [%s]", operation, rd, rn );
						} else {
							 output = String.format( "%s %s, [%s], %s", operation, rd, rn,
										 operand2 );
						}
						break;
				 default:
						if (rn.isEmpty()) {
							 output = String.format( "%s %s, [%s]", operation, rd,
										 operand2 );
						} else if (zeroOp2) {
							 output = String.format( "%s %s, [%s]", operation, rd, rn );
						} else {
							 output = String.format( "%s %s, [%s, %s]", operation, rd, rn,
										 operand2 );
						}
						break;
				 }
			} else if (rn.isEmpty()) {
				 output = String.format( "%s %s, %s", operation, rd, operand2 );
			} else {
				 output = String.format( "%s %s, %s, %s", operation, rd, rn,
							 operand2 );
			}
			return output;
	 }

	 /**
	  * @param Bits bits the instruction bits to set
	  */
	 public void setBits( Bits bits )
	 {
			this.bits = bits;
	 }

	 /**
	  * @param Code code
	  */
	 public void setCode( Code code )
	 {
			this.code = code;
	 }

	 /**
	  * Parse bits
	  * 
	  * @throws InvalidCodeException
	  */
	 public Boolean decode() throws InvalidCodeException
	 {
			Map<String, Bits> bitmap = new HashMap<String, Bits>();
			if (bits.length() == 32) {
				 for (Map.Entry<String, int[]> entry : dSchema.entrySet()) {
						String group = entry.getKey();
						int[] limits = entry.getValue();
						int start = limits[0];
						int end = limits[0] + limits[1];
						// System.out.println( String.format(
						// "Group: %s; start: %d, end: %d", group, start, end ) );

						switch (group) {
						case "cond_code":
							 bitmap.put( "cond_code", bits.slice( start, end ) );
							 break;
						case "s_bit":
							 bitmap.put( "s_bit", bits.slice( start, end ) );
							 break;
						case "op_type":
							 bitmap.put( "op_type", bits.slice( start, end ) );
							 break;
						case "op_code":
							 bitmap.put( "op_code", bits.slice( start, end ) );
							 break;
						case "rn":
							 bitmap.put( "rn", bits.slice( start, end ) );
							 break;
						case "rd":
							 bitmap.put( "rd", bits.slice( start, end ) );
							 break;
						case "operand2":
							 bitmap.put( "operand2", bits.slice( start, end ) );
							 break;
						}
				 }

				 //this.debugD( bits, bitmap );

				 this.cond = new Condition( bitmap.get( "cond_code" ),
							 bitmap.get( "s_bit" ) );
				 this.oper = new Operation( bitmap.get( "op_type" ),
							 bitmap.get( "op_code" ), bitmap.get( "s_bit" ), this.cond );

				 // MOV/MVN
				 if (this.oper.getOpCode().matches( "11[01]1" )
							 && this.oper.getOpClass().matches( "DP" )) {
						this.rn = new Register();
				 } else {
						this.rn = new Register( bitmap.get( "rn" ) );
				 }

				 this.rd = new Register( bitmap.get( "rd" ) );
				 this.operand2 = new Operand2( bitmap.get( "operand2" ), this.oper );

				 // MUL
				 if (operand2.isMultCd()) {
						this.rd = new Register( bitmap.get( "rn" ) );
						this.rn = new Register();
				 }

				 return true;
			} else {
				 throw new InvalidCodeException();
			}
	 }

	 public Boolean parse() throws InvalidCodeException
	 {
			Map<String, Code> parts = extract( code );

			//this.debugA( code, parts );

			this.cond = new Condition( parts.get( "condition" ),
						parts.get( "s_bit" ) );
			this.operand2 = new Operand2( code, this.cond );
			this.oper = operand2.getOp();
			// MOV/MVN
			if (this.oper.getOpCode().matches( "11[01]1" )
						&& this.oper.getOpClass().matches( "DP" )) {
				 this.rn = new Register();
			} else {
				 this.rn = new Register( parts.get( "rn" ) );
			}

			this.rd = new Register( parts.get( "rd" ) );

			// MUL
			if (operand2.isMultCd()) {
				 this.rn = new Register( parts.get( "rd" ) );
				 this.rd = new Register();
			}

			this.bits = new Bits( toBinaryString() );
			//decode();
			return true;
	 }

	 /**
	  * Print assembly instruction data
	  * 
	  * @param Code input
	  * @param Map  codemap
	  */
	 public void debugA( Code input, Map<String, Code> codemap )
	 {
			System.out.println( String.format( "Input: %s\n", input ) );
			System.out.println( String.format( "%20s %10s %20s %10s %10s %40s %10s",
						"Command", "S-Bit", "Condition", "rd", "rn", "Operand2",
						"Pre-Index" ) );
			System.out.println( String.format( "%20s %10s %20s %10s %10s %40s %10s",
						codemap.get( "command" ), codemap.get( "s_bit" ),
						codemap.get( "condition" ), codemap.get( "rd" ),
						codemap.get( "rn" ), codemap.get( "operand2" ),
						codemap.get( "pre_index" ) ) );
			System.out.println();
	 }

	 /**
	  * Print disassembly instruction data
	  * 
	  * @param String input
	  * @param Map    bitmap
	  */
	 public void debugD( Bits input, Map<String, Bits> bitmap )
	 {
			System.out
						.println( String.format( "Input: %s\n", input.toHexString() ) );
			System.out.println( String.format( "Binary representation is: %s",
						input.toBinaryString() ) );
			System.out.println( String.format( "%4s %3s %4s %1s %4s %4s %14s",
						"cond", "OpT", "OpC", "S", "rn", "rd", "operand2" ) );
			System.out.println( "1098 765 4321 0 9876 5432 10987 654 3210" );
			System.out.println( String.format( "%s %s %s %s %s %s %s %s %s",
						bitmap.get( "cond_code" ).toBinaryString(),
						bitmap.get( "op_type" ).toBinaryString(),
						bitmap.get( "op_code" ).toBinaryString(),
						bitmap.get( "s_bit" ).toBinaryString(),
						bitmap.get( "rn" ).toBinaryString(),
						bitmap.get( "rd" ).toBinaryString(),
						bitmap.get( "operand2" ).substring( 0, 5 ),
						bitmap.get( "operand2" ).substring( 5, 8 ),
						bitmap.get( "operand2" ).substring( 8 ) ) );
	 }

	 /**
	  * Returns a hexadecimal representation of the instruction code.
	  * 
	  * @return String
	  * @throws InvalidCodeException
	  */
	 public String toHexString() throws InvalidCodeException
	 {
			return new Bits( toBinaryString() ).toHexString();
	 }

	 @Override
	 public String toBinaryString() throws InvalidCodeException
	 {
			Bits condCode, opType, opCode, sBit, rnBits, rdBits, op2Code;
			condCode = this.cond.getBits();
			opType = this.oper.getOpType();
			opCode = this.oper.getOpCode();
			sBit = this.cond.getsBit();
			rnBits = ( this.rn == null ) ? new Bits( "0000" ) : this.rn.getBits();
			rdBits = ( this.rd == null ) ? new Bits( "0000" ) : this.rd.getBits();
			op2Code = this.operand2.getOp2Code();
			return String
						.format( "%4s%3s%4s%1s%4s%4s%12s", condCode.toBinaryString(),
									opType.toBinaryString(), opCode.toBinaryString(),
									sBit.toBinaryString(), rnBits.toBinaryString(),
									rdBits.toBinaryString(), op2Code.toBinaryString() )
						.replaceAll( " ", "0" );
	 }

	 @Override
	 public String toString()
	 {
			return getComposite();
	 }

	 /**
	  * Returns true if instruction is immediate format.
	  * 
	  * @return Boolean isImmediate
	  */
	 public Boolean isImmediate()
	 {
			return oper.getCodeType() == 0;
	 }

	 /**
	  * Returns true if instruction is Register format.
	  * 
	  * @return Boolean isRegister
	  */
	 public Boolean isRegister()
	 {
			return oper.getCodeType() == 1;
	 }

	 /**
	  * Standard Assembly Constructor
	  * 
	  * @param Code code
	  * @throws InvalidCodeException
	  */
	 public Instruction( Code code ) throws InvalidCodeException
	 {
			super();
			this.code = code;
			this.parse();
	 }

	 /**
	  * Standard Disassembly Constructor
	  * 
	  * @param Bits bits the instruction bits
	  */
	 public Instruction( Bits bits ) throws InvalidCodeException
	 {
			super();
			// Set member variable
			this.bits = bits;
			this.decode();
	 }

}
