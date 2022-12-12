/**
 * @author Akin Williams<aowilliams@jhu.edu>
 *
 */
package assembler.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Assembler DB Class
 *
 */
public class DB
{

	 /**
	  * {@link DB} Singleton Instance
	  */
	 private static DB instance = null;

	 /**
	  * {@link Importer} SQL Importer object
	  */
	 private Import importer = null;

	 /**
	  * {@link Connection} DB Connection
	  */
	 public Connection conn = null;

	 /**
	  * {@value #sql} Path to imported SQL file
	  */
	 private String sql = "assembler/io/schema.sql";

	 /**
	  * {@value #url} DB path
	  */
	 private String url = "jdbc:sqlite::memory:";

	 /**
	  * @return Connection conn
	  */
	 public Connection getConn()
	 {
			return this.conn;
	 }

	 /**
	  * Imports initial data into database
	  */
	 private Boolean init() throws IOException
	 {
			InputStream in = this.getClass().getClassLoader()
						.getResourceAsStream( this.sql );
			File f = new File( this.sql ); // creates file reference
			// If file doesn't exist, exit with error message
			if (!f.exists()) {
				 // throw new IOException( "Could not locate SQL input file." );
			}

			try {
				 // registering the jdbc driver here
				 Class.forName( "org.sqlite.JDBC" );
				 // create a connection to the database
				 this.conn = DriverManager.getConnection( url );
				 this.importer = new Import( conn, false, true );
				 // Try-with-resources block for buffered reader
				 try (BufferedReader input = new BufferedReader(
							 new InputStreamReader( in ) )) {
						importer.runScript( input );
				 } catch (IOException e) {
						System.out.println( "File read failed with exception: " + e );
						e.printStackTrace(System.out);
						return false;
				 }
				 return true;
			} catch (SQLException e) {
				 System.out.println( e.getMessage() );
				 return false;
			} catch (ClassNotFoundException e) {
				 System.out.println( e.getMessage() );
				 return false;
			}
	 }

	 /**
	  * Singleton constructor
	  */
	 private DB()
	 {
			super();
			try {
				 if (!init()) {
						throw new IOException( "Failed to initialize database." );
				 }
			} catch (IOException e) {
				 System.out.println( "File read failed with exception: " + e );
				 e.printStackTrace(System.out);
				 System.exit( 1 );
			}
	 }

	 /**
	  * Creates and returns instance of singleton class.
	  * 
	  * @return DB instance
	  */
	 public static DB getInstance()
	 {
			if (instance == null) {
				 instance = new DB();
			}

			return instance;
	 }

}
