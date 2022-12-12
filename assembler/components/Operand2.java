/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.components;

import java.util.Map;

import assembler.components.op2types.Immediate;
import assembler.components.op2types.LoadStore;
import assembler.components.op2types.MultCd;
import assembler.components.op2types.Register;
import assembler.exceptions.InvalidCodeException;
import assembler.interfaces.Decodable;
import assembler.interfaces.Op2Type;
import assembler.interfaces.Parsable;
import assembler.types.Bits;
import assembler.types.Code;

/**
 * Class representing Operand2 in ARM Instruction.
 *
 */
public class Operand2 implements Decodable, Parsable
{

	 /**
	  * {@value #code} Source code
	  */
	 private Code code;

	 /**
	  * {@value #op2Code} Operand2 raw code
	  */
	 private Bits op2Code;

	 /**
	  * {@link Op2Type} Op2Type Operand2 value object instance
	  */
	 private Op2Type value;

	 /**
	  * {@value #format} Operand2 format (Immediate or Register)
	  */
	 private String format;

	 /**
	  * {@link Operation} Operation Instance
	  */
	 private Operation op;

	 /**
	  * {@link Condition} Condition Code Instance
	  */
	 private Condition condition;

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
	  * Get Op2Type value
	  * 
	  * @return Op2Type
	  */
	 public Op2Type getValue()
	 {
			return value;
	 }

	 /**
	  * @return String format (Immediate or Register)
	  */
	 public String getFormat()
	 {
			return format;
	 }

	 /**
	  * @return Operation op
	  */
	 public Operation getOp()
	 {
			return op;
	 }

	 /**
	  * @return Condition condition
	  */
	 public Condition getCondition()
	 {
			return condition;
	 }

	 /**
	  * Returns true if Load/Store instruction
	  * 
	  * @return Boolean isLoadStore
	  */
	 public Boolean isLoadStore()
	 {
			String opClass = op.getOpClass();
			return op != null && opClass.matches( "LS" );
	 }

	 /**
	  * Returns true if Load/Store instruction
	  * 
	  * @param Code command
	  * @return Boolean isLoadStore
	  */
	 public Boolean isLoadStore( Code command )
	 {
			return command.matches( "LDR|STR" );
	 }

	 /**
	  * Returns true if MultCd instruction
	  * 
	  * @return Boolean isMultCd
	  */
	 public Boolean isMultCd()
	 {
			return op != null
						&& Integer.parseInt( op2Code.substring( 4, 8 ), 2 ) == 9
						&& op.getOpCode().toInteger() == 0
						&& op.getOpType().toInteger() == 0;
	 }

	 /**
	  * Returns true if MultCd instruction
	  * 
	  * @param Code command
	  * @return Boolean isMultCd
	  */
	 public Boolean isMultCd( Code command )
	 {
			return command.matches( "MUL" );
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
	  * @param Code command
	  * @return Boolean isRRX
	  */
	 public Boolean isRRX( Code command )
	 {
			return command.matches( "RRX" );
	 }

	 @Override
	 public Boolean decode() throws InvalidCodeException
	 {
			int codeType = op.getCodeType();
			Bits loadStore = op.getLoadStore();

			if (isMultCd()) {
				 this.format = "multcd";
				 this.value = new MultCd( op2Code, op );
			} else if (isLoadStore()) { // Load/Store instruction
				 if (codeType == 0) { // Immediate Instruction
						this.format = "Immediate";
						this.value = new LoadStore( op2Code, loadStore, op );
				 } else if (codeType == 1) { // Register Instruction
						this.format = "Register";
						this.value = new LoadStore( op2Code, loadStore, op );
				 } else {
						return false;
				 }
			} else if (codeType == 0) { // Immediate Instruction
				 this.format = "Immediate";
				 this.value = new Immediate( op2Code, op );
			} else if (codeType == 1) { // Register Instruction
				 this.format = "Register";
				 this.value = new Register( op2Code, op );
			} else {
				 return false;
			}

			return true;
	 }

	 @Override
	 public Boolean parse() throws InvalidCodeException
	 {
			Map<String, Code> parts = extract( code );

			Code command = parts.get( "command" );
			Code op2 = parts.get( "operand2" );
			Code rn = parts.get( "rn" );
			String any = ".*";
			String space = "[\\s]";
			String imm = "#-?[0-9]+";
			int codeType = op2.matches(
						String.format( "^(?:(%s),%s*(%s)(%s))|(?:(%s))$", any, space, imm, any, imm ) )
						&& ( rn == null || !isMOV( command ) ) ? 0 : 1;

			if (isMultCd( command )) {
				 this.format = "multcd";
				 this.value = new MultCd( code, condition );
			} else if (isLoadStore( command )) { // Load/Store instruction
				 if (codeType == 0) { // Immediate Instruction
						this.format = "Immediate";
						this.value = new LoadStore( code, condition );
				 } else if (codeType == 1) { // Register Instruction
						this.format = "Register";
						this.value = new LoadStore( code, condition );
				 } else {
						return false;
				 }
			} else if (codeType == 0) { // Immediate Instruction
				 this.format = "Immediate";
				 this.value = new Immediate( code, condition );
			} else if (codeType == 1) { // Register Instruction
				 this.format = "Register";
				 this.value = new Register( code, condition );
			} else {
				 throw new InvalidCodeException();
			}
			this.op = value.getOperation();
			this.op2Code = value.getOp2Code();
			return true;

	 }

	 @Override
	 public String toString()
	 {
			return String.valueOf( getValue() );
	 }

	 @Override
	 public String toBinaryString()
	 {
			return "";
	 }

	 /**
	  * Simple constructor
	  * 
	  * @param Bits op2Code
	  */
	 public Operand2( Bits op2Code ) throws InvalidCodeException
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
	 public Operand2( Code code, Condition condition )
				 throws InvalidCodeException
	 {
			super();
			this.code = code;
			this.condition = condition;
			this.parse();
	 }

	 /**
	  * Overloaded constructor
	  * 
	  * @param Code      code
	  * @param Condition condition
	  * @param Operation op
	  * @throws InvalidCodeException
	  */
	 public Operand2( Code code, Condition condition, Operation op )
				 throws InvalidCodeException
	 {
			super();
			this.code = code;
			this.op = op;
			this.parse();
	 }

	 /**
	  * Standard constructor
	  * 
	  * @param Bits op2Code
	  * @param Bits opType
	  * @param Bits opCode
	  */
	 public Operand2( Bits op2Code, Bits opType, Bits opCode )
				 throws InvalidCodeException
	 {
			super();
			this.op2Code = op2Code;
			this.op = new Operation( opType, opCode );
			this.decode();
	 }

	 /**
	  * Overloaded constructor
	  * 
	  * @param Bits      op2Code
	  * @param Operation op
	  */
	 public Operand2( Bits op2Code, Operation op ) throws InvalidCodeException
	 {
			super();
			this.op2Code = op2Code;
			this.op = op;
			this.decode();
	 }

}
