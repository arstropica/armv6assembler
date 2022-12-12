/**
 * @author Akin Williams<aowilliams@jhu.edu>
 */
package assembler.util;

/**
 * Binary Utilities Class
 */
public class Binary
{
	 static final int INT_BITS = 32;

	 /**
	  * Function to left rotate n by d bits
	  * 
	  * @param int n value
	  * @param int d bits
	  * @return int
	  */
	 public static int leftRotate( int n, int d )
	 {

			/*
			 * In n<<d, last d bits are 0. To put first 3 bits of n at last, do
			 * bitwise or of n<<d with n >>(INT_BITS - d)
			 */
			return ( n << d ) | ( n >> ( INT_BITS - d ) );
	 }

	 /**
	  * Function to right rotate n by d bits
	  * 
	  * @param int n value
	  * @param int d bits
	  * @return int
	  */
	 public static int rightRotate( int n, int d )
	 {

			/*
			 * In n>>d, first d bits are 0. To put last 3 bits of at first, do
			 * bitwise or of n>>d with n <<(INT_BITS - d)
			 */
			return ( n >> d ) | ( n << ( INT_BITS - d ) );
	 }

	 /**
	  * Function to calculate the log base 2 of an integer
	  * 
	  * @param int N
	  * @return int
	  */
	 public static int log2( int N )
	 {
			return (int) ( Math.log( N ) / Math.log( 2 ) );
	 }

}
