package assembler.interfaces;

import java.util.HashMap;
import java.util.Map;

import assembler.components.Operation;
import assembler.exceptions.InvalidCodeException;
import assembler.types.Bits;

public interface Op2Type
{

	 /**
	  * ARM Load/Store Regular Expression patterns
	  */
	 public Map<String, Map<String, String>> ldSchema = new HashMap<String, Map<String, String>>()
	 {
			private static final long serialVersionUID = 1L;

			{
				 String reg = "(?:r[0-9]{1,2})|(?:sp)|(?:lr)|(?:pc)";
				 String space = "[\\s]";
				 String b_open = "\\[";
				 String b_close = "\\]";
				 String imm = "#-?[0-9]+";
				 String pimm = "#[0-9]+";
				 String shOp = "[A-Za-z]{3}";
				 String pre_index = "!";

				 Map<String, String> immediates = new HashMap<String, String>()
				 {
						{
							 put( "zero", String.format( "^%s%s*(%s)%s*%s$", b_open, space,
										 reg, space, b_close ) );
							 put( "offset",
										 String.format( "^%s%s*(%s)%s*,%s*(%s)%s*%s$", b_open,
													 space, reg, space, space, imm, space, b_close ) );
							 put( "pre_indexed",
										 String.format( "^%s%s*(%s)%s*,%s*(%s)%s*%s%s$", b_open,
													 space, reg, space, space, imm, space, b_close,
													 pre_index ) );
							 put( "post_indexed", String.format( "^%s%s*(%s)%s*%s,%s*(%s)$",
										 b_open, space, reg, space, b_close, space, imm ) );
						}
				 };
				 Map<String, String> registers = new HashMap<String, String>()
				 {
						{
							 put( "offset",
										 String.format( "^%s%s*(%s)%s*,%s*(%s)%s*%s$", b_open,
													 space, reg, space, space, reg, space, b_close ) );
							 put( "pre_indexed",
										 String.format( "^%s%s*(%s)%s*,%s*(%s)%s*%s%s$", b_open,
													 space, reg, space, space, reg, space, b_close,
													 pre_index ) );
							 put( "post_indexed", String.format( "^%s%s*(%s)%s*%s,%s*(%s)$",
										 b_open, space, reg, space, b_close, space, reg ) );
						}
				 };
				 Map<String, String> scaled = new HashMap<String, String>()
				 {
						{
							 put( "offset",
										 String.format(
													 "^%s%s*(%s)%s*,%s*(%s)%s*,%s*(%s)%s*(%s)?%s*%s$",
													 b_open, space, reg, space, space, reg, space,
													 space, shOp, space, pimm, space, b_close ) );
							 put( "pre_indexed", String.format(
										 "^%s%s*(%s)%s*,%s*(%s)%s*,%s*(%s)%s*(%s)?%s*%s%s$",
										 b_open, space, reg, space, space, reg, space, space,
										 shOp, space, pimm, space, b_close, pre_index ) );
							 put( "post_indexed",
										 String.format(
													 "^%s%s*(%s)%s*%s,%s*(%s)%s*,%s*(%s)%s*(%s)?$",
													 b_open, space, reg, space, b_close, space, reg,
													 space, space, shOp, space, pimm ) );
						}
				 };
				 put( "immediate", immediates );
				 put( "register", registers );
				 put( "scaled", scaled );
			}
	 };

	 /**
	  * ARM Register Regular Expression patterns
	  */
	 public Map<String, String> rSchema = new HashMap<String, String>()
	 {
			private static final long serialVersionUID = 1L;

			{
				 String reg = "(?:r[0-9]{1,2})|(?:sp)|(?:lr)|(?:pc)";
				 String space = "[\\s]";
				 String imm = "#-?[0-9]+";
				 String pimm = "#[0-9]+";
				 String shOp = "[A-Za-z]{3}";
				 String rrx = "RRX";

				 put( "register", String.format( "^%s*(%s)$", space, reg ) );
				 put( "register_register",
							 String.format( "^(%s)%s*,%s*(%s)$", reg, space, space, reg ) );
				 put( "register_shift", String.format( "^(%s)%s*,%s*(%s)%s*(%s)$",
							 reg, space, space, shOp, space, pimm ) );
				 put( "register_rrx",
							 String.format( "^(%s)%s*,%s*(%s)$", reg, space, space, rrx ) );
				 put( "register_immediate",
							 String.format( "^(%s)%s*,%s*(%s)$", reg, space, space, imm ) );
				 put( "register_register_shift",
							 String.format( "^(%s)%s*,%s*(%s)%s*,%s*(%s)%s*(%s)$", reg,
										 space, space, reg, space, space, shOp, space, pimm ) );
				 put( "register_register_rrx",
							 String.format( "^(%s)%s*,%s*(%s)%s*,%s*(%s)$", reg, space,
										 space, reg, space, space, rrx ) );
				 put( "register_shift_register",
							 String.format( "^(%s)%s*,%s*(%s)%s*(%s)$", reg, space, space,
										 shOp, space, reg ) );
				 put( "register_register_shift_register",
							 String.format( "^(%s)%s*,%s*(%s)%s*,%s*(%s)%s*(%s)$", reg,
										 space, space, reg, space, space, shOp, space, reg ) );
			}
	 };

	 /**
	  * Decode op2Code
	  * 
	  * @return Boolean success
	  * @throws InvalidCodeException
	  */
	 abstract Boolean decode() throws InvalidCodeException;

	 /**
	  * @return Operation operation
	  */
	 abstract Operation getOperation();

	 /**
	  * @return Bits op2Code
	  */
	 abstract Bits getOp2Code();

	 /**
	  * Gets composite value
	  * 
	  * @return String composite
	  */
	 abstract String getComposite();

	 /**
	  * Return a string representation of the object instance
	  * 
	  * @return String
	  */
	 public String toString();

}
