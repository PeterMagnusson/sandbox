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
	
	@Test 
	public void modExp_2048bit() {
		int[] M = { 0x00000000, 0xbabd2bd0, 0x49134ac0, 0x9f7ba172, 0x7c567712, 0x4959b4dd, 0x988b6a82, 0xa5e187fc, 0x979ef67d, 0x4b8e65dc, 0x70756ee7, 0x8ee2e817, 0x8f769500, 0x7a18ab0a, 0x832ca9f0, 0xa04e190d, 0xb16161b8, 0xda67893e, 0xf713d2ae, 0x421f76cf, 0xf4f2dffb, 0x851c6c28, 0x4b738fbf, 0xe5f9eab3, 0x5a17b01b, 0x8cce1ecf, 0x2c3226c9, 0x79da1b1a, 0xf729ff83, 0xacf9081b, 0xcf52b417, 0x7ff57950, 0x1eb124f7, 0x1209d650, 0xee2746e5, 0x2ee5005e, 0x4ca60b49, 0x4733d138, 0x071d62ab, 0x7047b3b1, 0x65ff5536, 0x786abe51, 0x2dd76c89, 0x811dec28, 0x7b9d8437, 0xf441095c, 0x83f96b85, 0x01a9ff3c, 0xcafe5c0d, 0x2bba7de2, 0x1c4135d0, 0xf934f989, 0x47a5548a, 0x8f65371d, 0x4dbee1bb, 0xb5815f3a, 0x1bafa308, 0xfe649584, 0xf5f02605, 0x3cd4785d, 0xe5e2c6d4, 0xc8f6d299, 0x08c47b1e, 0xcb7bc2d3, 0x8917f679 };
		int[] X = { 0x00000000, 0xa8520a18, 0x21cd5bd6, 0xc94458fd, 0x3f0c13e7, 0x40fdb912, 0xeb130776, 0x4f0348a6, 0x2d06d859, 0x755c2b31, 0xd54254db, 0x21ac971d, 0xfe50a709, 0xb2c9c74d, 0xf60411d6, 0xda2238a9, 0xa3e79272, 0x61be7298, 0x62dd2996, 0x0e7d622b, 0xce3adca7, 0xaf011b66, 0x33159576, 0x92f9d262, 0xe9c9b3d8, 0xd6630e7a, 0xbd3649be, 0x8838742b, 0x30afbfa8, 0x74d6f192, 0xe6100f63, 0xae852607, 0x2cd1ca9f, 0x8f1528ac, 0x30d9bcb1, 0xa057e51f, 0xb795c8f2, 0x9b69e8da, 0xbd7cbc6d, 0x6fdf0ef2, 0x1a8d4daf, 0x65c5ae52, 0x78acdb7e, 0x28ae6632, 0x903ce033, 0x4bc76576, 0xec9f1fc2, 0x9730d56a, 0xbf1a350a, 0x623e6373, 0xf683342c, 0x29144c29, 0xffa5cc32, 0xd61323b0, 0x2ab10b34, 0xe800d329, 0xe037904f, 0x1681f23b, 0xcd726c15, 0x62b18ecb, 0x7a3f41fb, 0x43406ef6, 0x3090ba30, 0x15b39fd9, 0xa4f8e4ef };
		int[] E = { 0x00000000, 0xba277c40, 0xd1691168, 0x4ed26f37, 0x91210762, 0x2e8517a8, 0xc4929470, 0x3d2a6b33, 0x04676f89, 0x47d6d30b, 0x0a6ebebe, 0x50d6c74a, 0xa010fbb5, 0x2bf3322a, 0x39f9f795, 0x3bf48c65, 0x1af17424, 0x39de5bb7, 0x2a8441f6, 0x7a0b1ef4, 0x1b9d0b4a, 0x2d72c4b9, 0x923c9b54, 0xbe1b199e, 0x8d2e8459, 0x9763e669, 0xb5ec792e, 0x0f3d51f5, 0xf54aeae5, 0x1e07a2ec, 0x08dfbf96, 0xf58f528a, 0x78fb3125, 0x822f2c82, 0xb74045f4, 0x35dc9df6, 0xb83e0297, 0x05f85f14, 0xf5e6ee73, 0xbbfe63f4, 0x2ffb1a4c, 0x75faac89, 0xce2b4e74, 0x86f8afc4, 0x0ec90ccc, 0xc806ae7b, 0xa80ab49d, 0x9058308d, 0x9a2408c5, 0x70a8c7d3, 0x89f41d12, 0xa7cc438a, 0xeacea367, 0x89efff68, 0x046cf3a8, 0x0d92c516, 0xf7d10bd0, 0xb0c1c29e, 0x25e1cd2b, 0xd55fdc93, 0x04df6487, 0xe495eccd, 0x79d8d876, 0xe4b09425, 0x0a028ee9 };
		int[] expected = { 0, 0x684d16b8, 0x527b87e5, 0xdab4be72, 0xa5ed41f1, 0x28118f23, 0x2161a6e6, 0xdeeaf3bb, 0xfdcfee74, 0x2a204534, 0x165ba00d, 0x2cb7cb98, 0xeaa74d9a, 0x0f1fd9a5, 0x6e7f6d4b, 0xf05b530e, 0x2a6f7b6b, 0x966f3edb, 0xd3dbee46, 0x2dba818b, 0x11b5c81c, 0x3a50567f, 0xd5ee07e1, 0x5572a048, 0xc8fa3e2d, 0x96137ea3, 0x76b0a11e, 0xe6ba817d, 0xeeeed912, 0xbc5fe979, 0x0c7548e2, 0x7a821cf6, 0x12b0c13f, 0x5203373d, 0xfb59e773, 0xca8a59fc, 0x86bfbfb0, 0x6e8a03d5, 0xe3f1f8c5, 0xae8e12eb, 0x21ee505d, 0x1a10d6b5, 0x57bbe56d, 0xa18050a0, 0x611df8f7, 0xddc52268, 0xc146e885, 0x0f112bf1, 0x57eaa5b9, 0x83ff369e, 0x95dfcf9f, 0x75b90a28, 0x5bf4aec6, 0x33a51ecb, 0xdcaa5035, 0xd20219ef, 0xa0e4eb41, 0xb43c87d0, 0x33d7a22e, 0x67c9eace, 0xa56f9701, 0x1553d28a, 0x0a0751cc, 0x915ffbce, 0x3528049c };
		assertEquals(expected.length, E.length);
		
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
