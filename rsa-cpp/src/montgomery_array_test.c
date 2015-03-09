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

void montgomery_array_tests() {
	test_montgomery_modexp();
	testShiftRight();
	testAdd();
	testSub();
	test_montgomery_one_item_array();
	test_montgomery_modulus();
}

