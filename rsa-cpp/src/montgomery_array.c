#include <stdio.h>
#include <stdlib.h>
#include "bignum_uint32_t.h"
#include "montgomery_array.h"

void mont_prod_array(int length, uint32_t *A, uint32_t *B, uint32_t *M,
		uint32_t *temp, uint32_t *s) {
	zero_array(length, s);
	for (int wordIndex = length - 1; wordIndex >= 0; wordIndex--) {
		for (int i = 0; i < 32; i++) {

			int b = (B[wordIndex] >> i) & 1;

			//q = (s - b * A) & 1;
			sub_array(length, s, A, temp);
			int q;
			if (b == 1) {
				q = temp[length - 1] & 1;
			} else {
				q = s[length - 1] & 1;
			}

			// s = (s + q*M + b*A) >>> 1;
			if (q == 1) {
				add_array(length, s, M, s);
			} else {
				//TODO possibly do some sub operation to temporary here just to force constant execution time.
			}

			if (b == 1) {
				add_array(length, s, A, s);
			} else {
				//TODO possibly do some sub operation to temporary here just to force constant execution time.
			}

			shift_right_1_array(length, s, s);
		}
	}
}

void m_residue_2_2N_array(int length, uint32_t *M, uint32_t *temp, uint32_t *Nr) {
	zero_array(length, Nr);
	Nr[0] = 0x40000000; //Nr  = 2 ** N-2
	modulus_array(length, Nr, M, temp, Nr); //Nr = (2 ** N-2) mod M
	int N = 32 * length;
	for (int i = 0; i < (N) + 2; i++) {
		shift_left_1_array(length, Nr, Nr);
		modulus_array(length, Nr, M, temp, Nr);
	}
	//Nr = (2 ** 2N) mod M
}

void mont_exp_array(int length, uint32_t *X, uint32_t *E, uint32_t *M,
		uint32_t *Nr, uint32_t *P, uint32_t *ONE, uint32_t *temp, uint32_t *temp2, uint32_t *Z) {
	//1.
	//TODO implement calculating Nr = m_residue 2**(2N)
	m_residue_2_2N_array(length, M, temp, Nr);

	//2.
	zero_array(length, ONE);
	ONE[length - 1] = 1;
	mont_prod_array(length, ONE, Nr, M, temp, Z);

	//3
	mont_prod_array(length, X, Nr, M, temp, P);

	//4
	for (int word_index = length - 1; word_index > 0; word_index--) {
		for (int i = 0; i < 32; i++) {
			uint32_t ei = (E[word_index] >> i) & 1;
			//6
			if (ei == 1) {
				mont_prod_array(length, Z, P, M, temp, temp2);
				copy_array(length, temp2, Z);
			}
			//5
			mont_prod_array(length, P, P, M, temp, temp2);
			copy_array(length, temp2, Z);
			//7
		}
		//8
		mont_prod_array(length, ONE, Z, M, temp, temp2);
		copy_array(length, temp2, Z);
		//9
	}
}

