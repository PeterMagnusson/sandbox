#include <stdio.h>
#include <stdlib.h>
#include "montgomery_array.h"

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
	uint32_t expected[] = { 0x1034566, 0xffabbdee };
	assertArrayEquals(2, expected, c);
}

int m_residue(int A, int M) {
	uint64_t x = A & 0xFFFFFFFFFL;
	uint64_t m = M & 0xFFFFFFFFFL;
	x <<= 32;
	x %= m;
	return (int) x;
}

int test_montgomery_a_b_m(int A, int B, int M) {
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
	return success;
}

/*
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

void montgomery_array_tests() {
	testShiftRight();
	testAdd();
	testSub();
	test_montgomery_one_item_array();

}

