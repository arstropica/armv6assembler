/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.tests;

import java.io.*;
import java.util.*;

import assembler.core.Instruction;
import assembler.exceptions.InvalidCodeException;
import assembler.types.Bits;
import assembler.types.Code;

/**
 * Assembler Test Class
 *
 */
public class Tester
{

	 static final String TAB_DELIMITER = "\t";

	 /**
	  * {@value #file} Test data file path
	  */
	 static String file = "assembler/io/tests.csv";

	 /**
	  * {@value #records} Test Data
	  */
	 static List<Map<String, String>> records;

	 /**
	  * @param args
	  */
	 public static void main( String[] args )
	 {
			if (init()) {
				 try {
						assembly();
						disassembly();
				 } catch (Exception e) {
						e.printStackTrace( System.out );
				 }
			}

	 }

	 /**
	  * Test Initialization
	  * 
	  * @return Boolean success
	  */
	 public static Boolean init()
	 {
			InputStream in = Tester.class.getClassLoader()
						.getResourceAsStream( file );
			records = new ArrayList<>();
			try (BufferedReader br = new BufferedReader(
						new InputStreamReader( in ) )) {
				 int i = 0;
				 String line;
				 String[] headings = new String[4];
				 while (( line = br.readLine() ) != null) {
						if (i == 0) {
							 headings = line.split( TAB_DELIMITER );
						} else {
							 String[] values = line.split( TAB_DELIMITER );
							 Map<String, String> entry = new HashMap<String, String>();
							 for (int j = 0; j < headings.length; j++) {
									entry.put( headings[j],
												values[j].replaceAll( "\"", "" ).trim() );
							 }
							 records.add( entry );
						}
						i++;
				 }
			} catch (FileNotFoundException e) {
				 System.out.println( e );
				 return false;
			} catch (IOException e) {
				 System.out.println( e );
				 return false;
			}
			return true;
	 }

	 /**
	  * Runs assembly tests
	  * 
	  * @return Boolean success
	  * @throws InvalidCodeException
	  */
	 public static Boolean assembly() throws InvalidCodeException
	 {
			int total = 0;
			int fail = 0;
			System.out.println( "Start Assembly Test" );
			for (Map<String, String> test : records) {
				 String command = test.get( "command" ).toUpperCase();
				 String code = test.get( "hex" ).toUpperCase();
				 total++;
				 Code input = new Code( command );
				 try {
						Instruction instr = new Instruction( input );
						String result = instr.toHexString();
						Boolean success = result.equalsIgnoreCase( code );
						System.out.println( String.format(
									"\ninput: %-50s output: %-30s expected: %-20s result: %s\n",
									command, result, '"' + code + '"',
									success ? "pass" : "fail" ) );
						if (!success) {
							 fail++;
						}
				 } catch (Exception e) {
						System.out.println( String.format(
									"\ninput: %-50s output: %-30s expected: %-20s result: fail\n",
									command, "n/a", code ) );
						fail++;
						continue;
				 }
			}
			System.out.println( String.format( "Assembly Test: Total(%d)\tFail(%d)",
						total, fail ) );
			return fail == 0;
	 }

	 /**
	  * Runs disassembly tests
	  * 
	  * @return Boolean success
	  * @throws InvalidCodeException
	  */
	 public static Boolean disassembly() throws InvalidCodeException
	 {
			int total = 0;
			int fail = 0;
			System.out.println( "Start Disassembly Test" );
			for (Map<String, String> test : records) {
				 String command = test.get( "command" ).toUpperCase();
				 String code = test.get( "hex" ).toUpperCase();
				 total++;
				 Bits input = new Bits( code, 16 );
				 try {
						Instruction instr = new Instruction( input );
						String result = instr.toString();
						Boolean success = result.equalsIgnoreCase( command );
						System.out.println( String.format(
									"\ninput: %-20s output: %-30s expected: %-30s result: %s\n",
									code, result, '"' + command + '"',
									success ? "pass" : "fail" ) );
						if (!success) {
							 fail++;
						}
				 } catch (Exception e) {
						System.out.println( String.format(
									"\ninput: %-20s output: %-30s expected: %-30s result: fail\n",
									code, "n/a", command.toUpperCase() ) );
						fail++;
						continue;
				 }
			}
			System.out.println( String
						.format( "Disassembly Test: Total(%d)\tFail(%d)", total, fail ) );
			return fail == 0;
	 }

}
