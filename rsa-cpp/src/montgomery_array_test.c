#include <stdio.h>
#include <stdlib.h>
#include "montgomery_array.h"
#include "bignum_uint32_t.h"

const uint32_t TEST_CONSTANT_PRIME_15_1 = 65537;
const uint32_t TEST_CONSTANT_PRIME_31_1 = 2147483647u; // eighth Mersenne prime

void assertArrayEquals(int length, uint32_t *expected, uint32_t *actual) {
	int equals = 1;
	for (int i = 0; i < length; i++)
		equals &= expected[i] == actual[i];
	printf("%s expected: [", equals ? "PASS" : "FAIL");
	for (int i = 0; i < length - 1; i++)
		printf("%8x, ", expected[i]);
	printf("%8x] actual: [ ", expected[length - 1]);
	for (int i = 0; i < length - 1; i++)
		printf("%8x, ", actual[i]);
	printf("%8x]\n", actual[length - 1]);
}

void testShiftRight() {
	printf("=== Test shift right ===\n");
	uint32_t a[] = { 0x01234567, 0x89abcdef };
	shift_right_1_array(2, a, a);
	shift_right_1_array(2, a, a);
	shift_right_1_array(2, a, a);
	shift_right_1_array(2, a, a);
	uint32_t expected[] = { 0x00123456, 0x789abcde };
	assertArrayEquals(2, expected, a);
}

void testAdd() {
	printf("=== Test add ===\n");
	uint32_t a[] = { 0x01234567, 0x89abcdef };
	uint32_t b[] = { 0x12000002, 0x77000001 };
	uint32_t c[2];
	add_array(2, a, b, c);
	uint32_t expected[] = { 0x1323456a, 0x00abcdf0 };
	assertArrayEquals(2, expected, c);
}

void testSub() {
	printf("=== Test sub ===\n");
	uint32_t a[] = { 0x01234567, 0x89abcdef };
	uint32_t b[] = { 0x00200000, 0x8a001001 };
	uint32_t c[2];

	sub_array(2, a, b, c);
	uint32_t expected1[] = { 0x1034566, 0xffabbdee };
	assertArrayEquals(2, expected1, c);

	sub_array(2, b, a, c);
	uint32_t expected2[] = { 0xfefcba99u, 0x00544212u };
	assertArrayEquals(2, expected2, c);

	uint32_t aa[] = { 0, 0x01234567, 0x89abcdef };
	uint32_t bb[] = { 0, 0x00200000, 0x8a001001 };
	uint32_t cc[3];

	sub_array(3, aa, bb, cc);
	uint32_t expected3[] = { 0, 0x1034566, 0xffabbdee };
	assertArrayEquals(2, expected3, cc);

	sub_array(3, bb, aa, cc);
	uint32_t expected4[] = { 0xffffffff, 0xfefcba99u, 0x00544212u };
	assertArrayEquals(3, expected4, cc);
}

uint32_t m_residue(uint32_t A, uint32_t M) {
	uint64_t x = A & 0xFFFFFFFFFL;
	uint64_t m = M & 0xFFFFFFFFFL;
	x <<= 32;
	x %= m;
	return (uint32_t) x;
}

void test_montgomery_a_b_m(uint32_t A, uint32_t B, uint32_t M) {
	//int prodMod = (A * B) % M;
	uint32_t productModulus = A % M;
	productModulus *= B % M;
	productModulus %= M;
	uint32_t MM[] = { M };
	uint32_t Ar[] = { m_residue(A, M) };
	uint32_t Br[] = { m_residue(B, M) };
	uint32_t s[1];
	uint32_t temp[1];
	mont_prod_array(1, Ar, Br, MM, temp, s);
	uint32_t ONE[] = { 1 };
	uint32_t monProd[1];
	mont_prod_array(1, ONE, s, MM, temp, monProd);
	uint32_t productModulusMontgomery = monProd[0];
	uint32_t success = productModulus == productModulusMontgomery;
	printf("%c A=%3x B=%3x M=%3x A*B=%3x Ar=%3x Br=%3x Ar*Br=%3x A*B=%3x\n",
			success ? '*' : ' ', A, B, M, productModulus, Ar[0], Br[0], s[0],
			productModulusMontgomery);
}

void test_montgomery_modulus() {
	printf("=== Test mod ===\n");
	//printf("%lx\n", 2305843009213693951ul % 0x7ffffffful );
	uint32_t A[] = { 0, (1 << (61 - 32)) - 1, 0xffffffff }; //2^61-1 Ivan Mikheevich Pervushin
	uint32_t B[] = { (1 << (89 - 64)) - 1, 0xffffffff, 0xffffffff }; //2^89-1 R. E. Powers
	uint32_t M[] = { 0, 0, (1 << 31) - 1 }; //Leonhard Euler
	uint32_t temp[3];
	uint32_t actual1[3];
	uint32_t actual2[3];
	modulus_array(3, A, M, temp, actual1);
	modulus_array(3, B, M, temp, actual2);
	uint32_t expected1[] = { 0, 0, 1073741823 };
	uint32_t expected2[] = { 0, 0, 134217727 };
	assertArrayEquals(3, expected1, actual1);
	assertArrayEquals(3, expected2, actual2);
}

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

void test_montgomery_one_item_array() {
	printf("=== test_montgomery_one_item_array ===\n");
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

void test_montgomery_modexp() {
	printf("=== test_montgomery_modexp ===\n");
	uint32_t X[] = { 0, (1 << (61 - 32)) - 1, 0xffffffff }; //2^61-1 Ivan Mikheevich Pervushin
	uint32_t M[] = { (1 << (89 - 64)) - 1, 0xffffffff, 0xffffffff }; //2^89-1 R. E. Powers
	uint32_t E[] = { 0, 0, (1 << 31) - 1 }; //Leonhard Euler

	//temp variables
	uint32_t Nr[] = { 0, 0, 0 };
	uint32_t ONE[] = { 0, 0, 0 };
	uint32_t P[] = { 0, 0, 0 };
	uint32_t temp[] = { 0, 0, 0 };
	uint32_t temp2[] = { 0, 0, 0 };

	//output
	uint32_t Z[] = { 0, 0, 0 };

	mont_exp_array(3, ONE, ONE, M, Nr, P, ONE, temp, temp2, Z);
	assertArrayEquals(3, ONE, Z);

	mont_exp_array(3, ONE, E, M, Nr, P, ONE, temp, temp2, Z);
	assertArrayEquals(3, ONE, Z);

	mont_exp_array(3, X, E, M, Nr, P, ONE, temp, temp2, Z);
	uint32_t expected[] = { 0x0153db9b, 0x314b8066, 0x3462631f };
	assertArrayEquals(3, expected, Z);
}


void test_modExp_4096bit_e65537() {
	printf("=== test_modExp_4096bit_e65537 ===\n");
	uint32_t M[] = { 0x00000000, 0xecc9307c, 0x57a39970, 0x7e9e2569, 0x872cd790,
			0x0d4dddcc, 0x704fd131, 0x9395388d, 0x07e63a16, 0x37ea6fae,
			0x3873a01e, 0x0df4a57b, 0xb90bc708, 0xa05ade61, 0x91ef3868,
			0x58db06db, 0x893e2d41, 0xc75bb93d, 0x0c7f3be8, 0x8f57c9f9,
			0x477efa62, 0xf509e077, 0x568d59aa, 0x28552ee8, 0xa042f88d,
			0xf776a12d, 0x19f3685b, 0x1205c3f7, 0xfb7db6c5, 0x354908b1,
			0x099640c0, 0x709ab3e8, 0xe76149de, 0x6bc111d2, 0x95210730,
			0xbab8e493, 0x95168d09, 0x5242aba5, 0x4b98da8a, 0xb755eb64,
			0x246c6732, 0xc8fd54f4, 0xf6ed5686, 0x6ca61ceb, 0x239f1133,
			0x1abdc477, 0x24a35c02, 0xbaef93b4, 0x6b856235, 0xb34318c6,
			0x420da1a7, 0xa94a7298, 0x53141662, 0x0bfb5c3d, 0x183fa12c,
			0x5c4b3e4a, 0x6cd2f7cd, 0xc5446327, 0x6e90cf3e, 0x07fe2e20,
			0x78fe3b26, 0x73419d8f, 0xe5c5666d, 0xce01b1c7, 0xc45ce6da,
			0x9ca6e8ed, 0x42ec9161, 0x5ec6d3ec, 0x72921ad2, 0x8f4a9496,
			0xb146e974, 0xc9ca5c00, 0xfdea07f5, 0xd8a27ee8, 0x42507619,
			0x6ee518c8, 0x4a626aaf, 0xe099db09, 0xb2d44800, 0x44ca5299,
			0x74b3edd3, 0xbafd6615, 0x042e345d, 0xa39c8000, 0xbc42f7b0,
			0x1d8fc65b, 0x02a73859, 0xf1bf3dac, 0x33473f8a, 0xccd0d5c8,
			0x4e355e77, 0x008b1ae1, 0x77c43bde, 0xf2fa7e9a, 0x1828147e,
			0x2dca431f, 0x612fc4c0, 0x2c652d44, 0x55996f19, 0xb367f72e,
			0x9958b270, 0xa96f7b1f, 0xfbb230e3, 0xe70791fd, 0x6e9d6402,
			0x98dbd1dd, 0xea7f1494, 0x65a4602d, 0x93726a54, 0x53876bb3,
			0x57c6041b, 0x7a83ee09, 0x244588ce, 0xd4cf9317, 0xd77add56,
			0xc7e63f59, 0xc2b65e19, 0xb3982427, 0xcfc4c9a1, 0x8bef7de2,
			0xaddc6cad, 0xb4bee49f, 0x46edae94, 0xf3dba909, 0xc74d8a1c,
			0xbd470d28, 0x7f0dc6b1, 0xa5cc5313, 0xd47ef6b3 };
	uint32_t X[] = { 0x00000000, 0xffa525f2, 0x526335dd, 0xc145f9fd, 0xed720382,
			0x934b737a, 0x81cda0a4, 0x45e2aa14, 0x0089273f, 0x7a4c646c,
			0x183b156c, 0x92204c68, 0x865e5c35, 0x9ec60d43, 0x32b9a26c,
			0x2fbc9c6e, 0x37d61a87, 0x6e90f966, 0x3ff81ce1, 0x809cc453,
			0x2a48fc23, 0x82884020, 0xa6a5ace8, 0x245f7e64, 0x73d00956,
			0x4c23c4ff, 0x6c0e00d1, 0x522a138e, 0xeae0e578, 0xa9b77c44,
			0x4d69a705, 0xcb187201, 0xc2548a67, 0x1885d9ba, 0x8a2efeaf,
			0x1c0b04a0, 0x9317b984, 0xff667ee1, 0xad8f39d4, 0x3423e001,
			0x1be36670, 0xfd77ba6a, 0x325aa26e, 0x934d0eb6, 0x1b0a7066,
			0x41d6d679, 0x747bdbe1, 0x0eaa9370, 0xef8b3a3f, 0x695a3a93,
			0x3a615ff1, 0x222788e2, 0x0300db2a, 0xefbd5465, 0xed895ac5,
			0x993c94cc, 0x4ac55161, 0xe8cb628d, 0x8d8560ab, 0x51f8aa94,
			0x3f2e3412, 0x00adef44, 0x3faa81ef, 0xdacf51df, 0x9f908687,
			0x9fc8334f, 0x0dd0ae1d, 0x1bad0826, 0x93a55bcf, 0x1f74d9ba,
			0x448e9393, 0x7115db19, 0x9667b337, 0xd62700a3, 0x1ab551aa,
			0xf50113b3, 0xb9dbe164, 0x3241e264, 0x030de43d, 0x96fecc68,
			0x19d27dac, 0xd375d9c9, 0xedec942c, 0x3e574a2d, 0xcb9667f7,
			0x0c448d73, 0x60488187, 0x12177ef5, 0xfd07c0e8, 0x2753cf63,
			0xf9c1bac6, 0x856b05c6, 0x36d24921, 0x41bd5246, 0x2598118f,
			0x49e857cd, 0xc9692afc, 0x64d28a5b, 0x52aba529, 0xd6ef02ca,
			0x5b72b03c, 0x5cc04b9f, 0xf423b253, 0xf84c2697, 0x11b2cc10,
			0xa822c3b4, 0x3d581533, 0xaf3c56b7, 0x15d734b3, 0x93abdc84,
			0x970a504b, 0x8a867419, 0x8a4d46c8, 0x18c62d83, 0x088ff310,
			0x26f7808d, 0x7369e2d7, 0xa4a2cf6d, 0xc5efce92, 0xd6668160,
			0x5d1c3cae, 0xf62b2bd7, 0x9397ec83, 0x4577de0a, 0x4df48770,
			0x66203ab7, 0x27ba7480, 0x96bd4c9e, 0xc6f82263 };
	uint32_t E[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x00010001 };
	uint32_t expected[] = { 0, 0x41e92995, 0x9f90fac9, 0xd092a89b, 0xe906f13a,
			0x7dda3812, 0x8f580431, 0xed685e65, 0x6975838a, 0xc04c500a,
			0x440c8dc1, 0xdf61f134, 0x72111481, 0x438a0ad4, 0xd1901dbe,
			0x130b1261, 0x190a1cc9, 0xffb5def5, 0xabf8d5e5, 0x8e6c445b,
			0x7348af60, 0xc46a967a, 0x760bba83, 0x6eaec513, 0x9ad339fc,
			0x6b1d7f32, 0x42d7ca96, 0x39e76281, 0xc679ae04, 0xa961a991,
			0x54dd9103, 0x53c21900, 0xc305deeb, 0x8fc0707b, 0xe73cf97e,
			0xfe9199e6, 0x7d2fc3fe, 0xac909b86, 0x183c05b3, 0xe9c4c177,
			0x3bfbe011, 0x9fba913b, 0xa3b9cfeb, 0xd644a5d9, 0xb2e2e65d,
			0xca19686b, 0x246e180f, 0x8c5afaa5, 0xeb637e86, 0x8d646f80,
			0x319e795c, 0xcf0ac0d2, 0xcf2eb4fb, 0x1fd1ecf2, 0xd5cdb147,
			0xbbe06322, 0x7867ac67, 0xbe1e04e7, 0xfb987b8f, 0x0eac7562,
			0x2d1224c5, 0x6ca25b32, 0x4ffc44ce, 0x3ad6e3f0, 0xafff53ca,
			0x39ebd8cf, 0x0a428629, 0x4626403e, 0x6281df65, 0x52f47f9f,
			0x60c67231, 0xaa4d50e8, 0x1175a6be, 0x75326395, 0xce45d310,
			0x7e90216f, 0xef2c8b25, 0x755488f0, 0xe58ebd71, 0x8d47760e,
			0xd06aa6e5, 0x713a592c, 0x25a28fab, 0xaee62686, 0xf26ed6e9,
			0x7305f55a, 0x1231a3ac, 0x10823b96, 0x08934288, 0x58aa1a18,
			0x51a89325, 0x7de9ba06, 0xac9541e7, 0x4db4a59d, 0xa33cfaba,
			0xb2e4a7ee, 0x099c895c, 0x77e7f6a5, 0x922b9001, 0xd835a66b,
			0xdf594547, 0x4f6855c7, 0xe3f24218, 0xfe65e99f, 0x7e921b6e,
			0x8dac463e, 0x73b82b77, 0x24ef9883, 0x6ae91fee, 0xc55c4206,
			0x073ed7bc, 0xb780c3fe, 0x07a2f0d9, 0x3f8deace, 0x1332373b,
			0xd2a2c63c, 0xc674af4e, 0xd0aee696, 0x614b3969, 0x8fcac805,
			0x58fa13ff, 0x6cf0a5c2, 0x0ac24bf4, 0xd7901e58, 0x9b616f55,
			0x2517443d, 0xb00a5613, 0x217b8957, 0x5a4ba6c4 };

	//temp variables
	uint32_t Nr[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	uint32_t ONE[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	uint32_t P[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	uint32_t temp[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	uint32_t temp2[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	//output
	uint32_t Z[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	mont_exp_array(129, X, E, M, Nr, P, ONE, temp, temp2, Z);
	assertArrayEquals(129, expected, Z);
}


void montgomery_array_tests() {
	test_montgomery_modexp();
	test_modExp_4096bit_e65537();
	testShiftRight();
	testAdd();
	testSub();
	test_montgomery_one_item_array();
	test_montgomery_modulus();
}

