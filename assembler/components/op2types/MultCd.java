/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.components.op2types;

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
import assembler.types.Bits;
import assembler.types.Code;

/**
 * MultCd Format Operand2 Value
 *
 */
public class MultCd implements Op2Type, Decodable, Parsable
{

	 /**
	  * MultCd Binary String
	  */
	 private static final String MULTCDBITS = "1001";

	 /**
	  * {@value #code} Source code
	  */
	 private Code code;

	 /**
	  * {@value #rm} rm register
	  */
	 private Register rm;

	 /**
	  * {@value #rs} rs register
	  */
	 private Register rs;

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
	  * @return Register rm
	  */
	 public Register getRm()
	 {
			return rm;
	 }

	 /**
	  * @return Register rs
	  */
	 public Register getRs()
	 {
			return rs;
	 }

	 /**
	  * @return String composite value
	  */
	 public String getComposite()
	 {
			Register rm = getRm();
			Register rs = getRs();
			String output = String.format( "%s, %s", rm, rs );
			return output;
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
	  * Sets rs Register
	  * 
	  * @param String rs
	  * @throws InvalidCodeException
	  */
	 public void setRs( String rs ) throws InvalidCodeException
	 {
			this.rs = new Register( new Bits( rs ) );
	 }

	 /**
	  * Sets rs Register
	  * 
	  * @param int rs
	  * @throws InvalidCodeException
	  */
	 public void setRs( int rs ) throws InvalidCodeException
	 {
			this.rs = new Register( new Bits( String.valueOf( rs ), 10 ) );
	 }

	 /**
	  * Sets rs Register
	  * 
	  * @param Code rs
	  * @throws InvalidCodeException
	  */
	 public void setRs( Code rs ) throws InvalidCodeException
	 {
			this.rs = new Register( rs );
	 }

	 /**
	  * Returns true if instruction is MUL
	  * 
	  * @return Boolean isMUL
	  */
	 public Boolean isMUL()
	 {
			Bits opCode = operation.getOpCode();
			Bits opType = operation.getOpType();
			return op2Code.substring( 4, 8 ).matches( MULTCDBITS )
						&& opCode.toInteger() == 0 && opType.toInteger() == 0;
	 }

	 /**
	  * Sets main Operation instruction.
	  * 
	  * @return void
	  */
	 public void setInstruction()
	 {
			Condition condition = operation.getCondition();
			String sFlag = operation.getSFlag();
			if (isMUL()) {
				 operation.setInstruction( "MUL" + sFlag + condition );
			}
	 }

	 @Override
	 public Boolean decode() throws InvalidCodeException
	 {
			String rs = op2Code.substring( 0, 4 );
			String rm = op2Code.substring( 8, 12 );
			this.setRs( rs );
			this.setRm( Integer.parseInt( rm, 2 ) );
			this.setInstruction();

			return true;
	 }

	 public Boolean parse() throws InvalidCodeException
	 {
			if (code != null && !code.isBlank()) {
				 Map<String, Code> parts = extract( code );
					Code op2 = parts.get( "operand2" );
				 
				 String reg = "(?:r[0-9]{1,2})|(?:sp)|(?:lr)|(?:pc)";
				 String space = "[\\s]";
				 String regex = String.format( "^(%s)%s*,%s*(%s)$", reg, space, space,
							 reg );
				 Pattern patt = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
				 Matcher matcher = patt.matcher( op2 );
				 Boolean found = matcher.find();
				 Bits opCode = new Bits( "0000" );
				 Bits opType = new Bits( "000" );
				 if (found && matcher.groupCount() >= 2) {
						Code rm = new Code( matcher.group( 1 ).trim() );
						Code rs = new Code( matcher.group( 2 ).trim() );

						this.setRm( rm );
						this.setRs( rs );
						op2Code = new Bits(
									String.format( "%s%s%s", this.rs.toBinaryString(),
												MULTCDBITS, this.rm.toBinaryString() ) );
						this.operation = new Operation( opType, opCode, condition );
						this.setInstruction();
						return true;
				 } else {
						throw new InvalidCodeException();
				 }
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
	  * @throws InvalidCodeException
	  */
	 public MultCd( Bits op2Code ) throws InvalidCodeException
	 {
			super();
			this.op2Code = op2Code;
			this.decode();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Code      code
	  * @param Condition condition
	  * @throws InvalidCodeException
	  */
	 public MultCd( Code code, Condition condition ) throws InvalidCodeException
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
	  * @throws InvalidCodeException
	  */
	 public MultCd( Bits op2Code, Operation operation )
				 throws InvalidCodeException
	 {
			super();
			this.op2Code = op2Code;
			this.operation = operation;
			this.decode();
	 }

}
