package rsa;

import static org.junit.Assert.*;

import org.junit.Test;

public class MontgomeryArrayTest {
	static final int TEST_CONSTANT_PRIME_15_1 = 65537;
	static final int TEST_CONSTANT_PRIME_31_1 = 2147483647; // eighth Mersenne prime

	@Test
	public void testShiftRight() {
		int[] a = { 0x01234567, 0x89abcdef };
		MontgomeryArray.shift_right_1_array(2, a, a);
		MontgomeryArray.shift_right_1_array(2, a, a);
		MontgomeryArray.shift_right_1_array(2, a, a);
		MontgomeryArray.shift_right_1_array(2, a, a);
		int[] expected = { 0x00123456, 0x789abcde };
		assertArrayEquals(expected, a);
	}

	@Test
	public void testAdd() {
		int[] a = { 0x01234567, 0x89abcdef };
		int[] b = { 0x12000002, 0x77000001 };
		int[] c = new int[2];
		MontgomeryArray.add_array(2, a, b, c);
		int[] expected = { 0x1323456a, 0x00abcdf0 };
//		System.out.printf("%x %x %x\n", c[0], c[1], 0x0123456789abcdefL + 0x1200000277000001L);
		assertArrayEquals(expected, c);
	}

	@Test
	public void testSub() {
		int[] a = { 0x01234567, 0x89abcdef };
		int[] b = { 0x00200000, 0x8a001001 };
		int[] c = new int[2];
		MontgomeryArray.sub_array(2, a, b, c);
		int[] expected = { 0x1034566, 0xffabbdee };
//		System.out.printf("%x %x %x\n", c[0], c[1], 0x0123456789abcdefL - 0x002000008a001001L);
		assertArrayEquals(expected, c);
	}
	
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

	@Test public void modulus() {
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

}
