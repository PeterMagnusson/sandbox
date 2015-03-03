
/*
#include <stdio.h>
#include <stdlib.h>

static const uint32_t TEST_CONSTANT_PRIME_15_1 = 65537;
static const uint32_t TEST_CONSTANT_PRIME_31_1 = (uint32_t) 2147483647u; // eighth Mersenne prime

uint32_t mont_prod(uint32_t A, uint32_t B, uint32_t M) {
	uint32_t s = 0;
	for(int i = 0; i < 32; i++) {
		int b = (B >> i) & 1;
		uint32_t q = (s - b * A) & 1;
		s = (s + q*M + b*A) >> 1;
	}
	return s;
}

uint32_t m_residue(uint32_t A, uint32_t M) {
	uint64_t x = A;
	x <<= 32;
	return x % M;
}

void test_montgomery_a_b_m(uint32_t A, uint32_t B, uint32_t M) {
	//uint32_t prodMod = (A * B) % M;
	uint32_t prodMod = A % M;
	prodMod *= B % M;
	prodMod %= M;

	uint32_t Ar = m_residue(A, M);
	uint32_t Br = m_residue(B, M);
	uint32_t monProdMod = mont_prod(Ar, Br, M);
	uint32_t monProdMod_ = mont_prod(1, monProdMod, M);
	printf("%c A=%3x B=%3x M=%3x A*B=%3x Ar=%3x Br=%3x Ar*Br=%3x A*B=%3x\n",
			prodMod == monProdMod_ ? '*' : ' ', A, B, M, prodMod, Ar, Br,
			monProdMod, monProdMod_);
}

void test_montgomery() {
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
*/
