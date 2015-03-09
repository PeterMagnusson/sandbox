package rsa;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

public class MontgomeryArrayTest {
	static final int TEST_CONSTANT_PRIME_15_1 = 65537;
	static final int TEST_CONSTANT_PRIME_31_1 = 2147483647; // eighth Mersenne prime

	int m_residue(int A, int M) {
		long x = A & 0xFFFFFFFFFL;
		long m = M & 0xFFFFFFFFFL;
		x <<= 32;
		x %= m;
		return (int) x;
	}

	boolean test_montgomery_a_b_m(int A, int B, int M) {
		//int prodMod = (A * B) % M;
		int productModulus = A % M;
		productModulus *= B % M;
		productModulus %= M;
		int[] MM = { M };
		int[] Ar =  { m_residue(A, M) };
		int[] Br = { m_residue(B, M) };
		int[] s = new int[1];
		MontgomeryArray.mont_prod_array(1, Ar, Br, MM, s);
		int[] ONE = { 1 }; 
		int[] monProd = new int[1];
		MontgomeryArray.mont_prod_array(1, ONE, s, MM, monProd);
		int productModulusMontgomery = monProd[0];
		boolean success = productModulus == productModulusMontgomery;
		System.out.printf("%c A=%3x B=%3x M=%3x A*B=%3x Ar=%3x Br=%3x Ar*Br=%3x A*B=%3x\n",
				success ? '*' : ' ', A, B, M, productModulus, Ar[0], Br[0],
				s[0], productModulusMontgomery);
		return success;
	}

	@Test public void modulus2() {
		int[] actual0 = new int[2];
		int[] actual1 = new int[2];
		int[] actual2 = new int[2];
		int[] actual3 = new int[2];
		int[] M = { 0, (1<<31)-1 }; //Leonhard Euler
		int[] ZERO = { 0, 0 };
		int[] ONE = { 0, 1 };
		int[] X31 = { 0, (1<<31) }; //Leonhard Euler
		MontgomeryArray.modulus_array(2, ZERO, M, actual0);		
		MontgomeryArray.modulus_array(2, ONE, M, actual1);		
		MontgomeryArray.modulus_array(2, M, M, actual2);
		MontgomeryArray.modulus_array(2, X31, M, actual3);
		assertArrayEquals(ZERO, actual0);
		assertArrayEquals(ONE, actual1);
		assertArrayEquals(ZERO, actual2);
		assertArrayEquals(ONE, actual3);
	}
	
	@Test public void modulus3() {

		int[] A = { 0, (1<<(61-32)) - 1, 0xffff_ffff }; //2^61-1 Ivan Mikheevich Pervushin
		int[] B = { (1<<(89-64)) - 1,  0xffff_ffff, 0xffff_ffff }; //2^89-1 R. E. Powers
		int[] M = { 0, 0, (1<<31)-1 }; //Leonhard Euler
		int[] actual1 = new int[3];
		int[] actual2 = new int[3];
		MontgomeryArray.modulus_array(3, A, M, actual1);
		MontgomeryArray.modulus_array(3, B, M, actual2);
		int[] expected1 = {0, 0, 1073741823};
		int[] expected2 = {0, 0, 134217727};
		assertArrayEquals(expected1, actual1);
		assertArrayEquals(expected2, actual2);
	}

	@Test
	public void test_huge_numbers() {
		int[] A = { 0, (1<<(61-32)) - 1, 0xffff_ffff }; //2^61-1 Ivan Mikheevich Pervushin
		int[] B = { (1<<(89-64)) - 1,  0xffff_ffff, 0xffff_ffff }; //2^89-1 R. E. Powers
		int[] M = { 0, 0, (1<<31)-1 }; //Leonhard Euler
		int[] Ar = new int[3];
		int[] Br = new int[3];
		int[] s = new int[3];
		int[] ONE = { 0, 0, 1 }; 
		int[] monProd = new int[3];
		MontgomeryArray.m_residue(3, A, M, Ar);
		MontgomeryArray.m_residue(3, B, M, Br);
		MontgomeryArray.mont_prod_array(3, Ar, B, M, s);
		MontgomeryArray.mont_prod_array(3, ONE, s, M, monProd);
		System.out.printf("The solution: %8x %8x %8x\n", monProd[0], monProd[1], monProd[2]);
	}
	
	@Test
	public void test_montgomery_one_item_array() {
		test_montgomery_a_b_m(11, 17, 19);
		test_montgomery_a_b_m(11, 19, 17);
		test_montgomery_a_b_m(17, 11, 19);
		test_montgomery_a_b_m(17, 19, 11);
		test_montgomery_a_b_m(19, 11, 17);
		test_montgomery_a_b_m(19, 17, 11);

		test_montgomery_a_b_m(TEST_CONSTANT_PRIME_15_1, 17, 19);
		test_montgomery_a_b_m(TEST_CONSTANT_PRIME_15_1, 19, 17);
		test_montgomery_a_b_m(17, TEST_CONSTANT_PRIME_15_1, 19);
		test_montgomery_a_b_m(17, 19, TEST_CONSTANT_PRIME_15_1);
		test_montgomery_a_b_m(19, TEST_CONSTANT_PRIME_15_1, 17);
		test_montgomery_a_b_m(19, 17, TEST_CONSTANT_PRIME_15_1);

		test_montgomery_a_b_m(TEST_CONSTANT_PRIME_15_1, 17,
				TEST_CONSTANT_PRIME_31_1);
		test_montgomery_a_b_m(TEST_CONSTANT_PRIME_15_1, TEST_CONSTANT_PRIME_31_1,
				17);
		test_montgomery_a_b_m(17, TEST_CONSTANT_PRIME_15_1,
				TEST_CONSTANT_PRIME_31_1);
		test_montgomery_a_b_m(17, TEST_CONSTANT_PRIME_31_1,
				TEST_CONSTANT_PRIME_15_1);
		test_montgomery_a_b_m(TEST_CONSTANT_PRIME_31_1, TEST_CONSTANT_PRIME_15_1,
				17);
		test_montgomery_a_b_m(TEST_CONSTANT_PRIME_31_1, 17,
				TEST_CONSTANT_PRIME_15_1);
	}

	@Test
	public void test_montgomery_modexp_one_one() {
		int M[] = { (1 << (89 - 64)) - 1, 0xffffffff, 0xffffffff }; //2^89-1 R. E. Powers
		
		//temp variables
		int Nr[] = { 0, 0, 0 };
		int ONE[] = { 0, 0, 1 };
		int P[] = { 0, 0, 0 };
		int temp[] = { 0, 0, 0 };

		//output
		int Z[] = { 0, 0, 0 };

		MontgomeryArray.mont_exp_array(3, ONE, ONE, M, Nr, P, ONE, temp, Z);
		System.out.printf("%8x %8x %8x\n",Z[0], Z[1], Z[2]);
		int expected0[] = { 0, 0, 1 };
		assertArrayEquals(expected0, Z);
	}

	@Test
	public void test_montgomery_modexp_primes() {
		int X[] = { 0, (1 << (61 - 32)) - 1, 0xffffffff }; //2^61-1 Ivan Mikheevich Pervushin
		int M[] = { (1 << (89 - 64)) - 1, 0xffffffff, 0xffffffff }; //2^89-1 R. E. Powers
		int E[] = { 0, 0, (1 << 31) - 1 }; //Leonhard Euler

		//temp variables
		int Nr[] = { 0, 0, 0 };
		int ONE[] = { 0, 0, 1 };
		int P[] = { 0, 0, 0 };
		int temp[] = { 0, 0, 0 };

		//output
		int Z[] = { 0, 0, 0 };

		MontgomeryArray.mont_exp_array(3, X, E, M, Nr, P, ONE, temp, Z);
		System.out.printf("%8x %8x %8x\n",Z[0], Z[1], Z[2]);
		int expected[] = { 0x0153db9b, 0x314b8066, 0x3462631f };
		assertArrayEquals(expected, Z);
	}
	
	@Test
	public void test_montgomery_modexp_5_9_23() {
		int X[] = { 5 };
		int E[] = { 9 };
		int M[] = { 23 };

		//temp variables
		int Nr[] = { 0 };
		int ONE[] = { 1 };
		int P[] = { 0 };
		int temp[] = { 0 };

		//output
		int Z[] = { 0 };

		MontgomeryArray.mont_exp_array(1, X, E, M, Nr, P, ONE, temp, Z);
		System.out.printf("%8x\n",Z[0]);
		int expected[] = { 11 };
		assertArrayEquals(expected, Z);
	}
	
	@Test
	public void test_montgomery_modexp_3_7_19() {
		int X[] = { 3 };
		int E[] = { 7 };
		int M[] = { 19 };

		//temp variables
		int Nr[] = { 0 };
		int ONE[] = { 1 };
		int P[] = { 0 };
		int temp[] = { 0 };

		//output
		int Z[] = { 0 };

		MontgomeryArray.mont_exp_array(1, X, E, M, Nr, P, ONE, temp, Z);
		System.out.printf("%8x\n",Z[0]);
		int expected[] = { 2 };
		assertArrayEquals(expected, Z);
	}
	
	@Test
	public void testMresidue() {
		int[] M = { TEST_CONSTANT_PRIME_15_1 };
		int[] temp = { 0 };
		int[] Nr = { 0 };
		int N = 3;
		MontgomeryArray.m_residue_2_2N_array(1, N, M, temp, Nr);
		BigInteger exponent = new BigInteger("" + (2 * N));
		BigInteger modulus = new BigInteger("" + TEST_CONSTANT_PRIME_15_1);
		BigInteger mresidue_biginteger = new BigInteger("2").modPow(exponent,
				modulus);
		String expected = mresidue_biginteger.toString(16);
		String actual = Integer.toString(Nr[0], 16);
		assertEquals("expected: " + expected + " actual: " + actual, expected, actual);
	}
}
