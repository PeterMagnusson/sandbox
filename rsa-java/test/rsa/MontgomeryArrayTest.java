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

	/*
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
	*/

	/*
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
	*/
	
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
	
	@Test 
	public void modexp_1024bit(){
		int[] M = { 0x00000000, 0xf14b5a0a, 0x122ff247, 0x85813db2, 0x02c0d3af, 0xbd0a4615, 0x2889ff7d, 0x8f655e9e, 0xc866e586, 0xf21003a0, 0xe969769b, 0x127ec8a5, 0x67f07708, 0x217775f7, 0x7654cabc, 0x3a624f9b, 0x4074bdf1, 0x55fa84c0, 0x0354fe59, 0x0ad04cfd, 0x14e666c0, 0xce6cea72, 0x788c31f4, 0xedcf3dd7, 0x3a5a59c1, 0xb9b3ef41, 0x565df033, 0x69a82de8, 0xf18c2793, 0x0abd5502, 0xf3730ec0, 0xd1943dc4, 0xa660a267 };
		int[] X = { 0x00000000, 0x9d1bae15, 0x0a45f239, 0xa8f834fd, 0x8cf0949d, 0x12132d6e, 0xd44f66a0, 0x804f391a, 0x734478ba, 0x2824fb60, 0x7e17e700, 0xefb5cce4, 0x18af6421, 0x80a65d05, 0xd16b6576, 0x779f7e12, 0xcfe3361e, 0x7eb770fc, 0xae7d96a6, 0x38263976, 0x71866d16, 0x26c5c8a3, 0x8d58f1d7, 0x470ca2b0, 0x0b83271b, 0x3e813d98, 0x18488750, 0xf2677228, 0x22814759, 0x0e68a1ab, 0x9292780a, 0x5534c1c0, 0xcc79656d };
		int[] E = { 0x00000000, 0x88922607, 0x4c33dad4, 0xd0df0b97, 0x79bb949e, 0xa1251d46, 0xa1632d0e, 0xb46e60b1, 0x99ceb59c, 0x1174bd3f, 0xa74940d7, 0x61967db2, 0x8432260d, 0x2104f45b, 0xb909c8a2, 0x68f1a414, 0x5aa486eb, 0xfbd72a88, 0x8394e7ea, 0xea8c4455, 0xd1089385, 0xd0ddf9d3, 0x6a7cf991, 0xdaa0ff1d, 0x76b0fac9, 0xb0e0c103, 0xfb651a47, 0x35ccd8e7, 0x47ce3f0e, 0xe850aa13, 0xffdd2a63, 0xc1091e79, 0x6a4bca3b };
		int[] expected = { 0, 0x73d96ee6, 0xf13de4d1, 0xc8ff2e25, 0x5437e73b, 0xc21b19ca, 0x0c52e33d, 0x520d932c, 0xd637c600, 0x52fa431a, 0x08fb779b, 0x8209ed20, 0xa9ef4ff0, 0x72897e89, 0xb88b1264, 0x0b9c6cf1, 0x29a60f29, 0x02d28305, 0xc1d0dfbd, 0x57418839, 0xd71e3567, 0xee3b1269, 0x39371030, 0x1a21d278, 0x66400276, 0x676bc980, 0xf9b2a48f, 0x53126d91, 0xfca61c62, 0x7aac7ac1, 0x01808c23, 0xddd7dced, 0xf15cee08 };
		
		//temp variables
		int Nr[] = new int[M.length];
		int ONE[] = new int[M.length];
		int P[] = new int[M.length];
		int temp[] = new int[M.length];

		//output
		int Z[] = new int[M.length];

		MontgomeryArray.mont_exp_array(M.length, X, E, M, Nr, P, ONE, temp, Z);
		assertArrayEquals(expected, Z);
	
	}
}
