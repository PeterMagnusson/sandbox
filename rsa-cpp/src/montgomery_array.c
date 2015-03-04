#include <stdio.h>
#include <stdlib.h>
#include "montgomery_array.h"

void copy_array(int length, int *src, int *dst) {
	for (int i = 0; i < length; i++)
		dst[i] = src[i];
}

void add_array(int length, uint32_t *a, uint32_t *b, uint32_t *result) {
	uint32_t carry = 0;
	for (int i = length - 1; i >= 0; i--) {
		int r = carry;
		uint32_t aa = a[i];
		uint32_t bb = b[i];
		r += aa;
		r += bb;
		result[i] = r;
		carry = ((aa >> 31) | (bb >> 31)) & ~(r >> 31);
	}
}

void sub_array(int length, uint32_t *a, uint32_t *b, uint32_t *result) {
	uint32_t carry = 1;
	for (int wordIndex = length - 1; wordIndex >= 0; wordIndex--) {
		int r = carry;
		uint32_t aa = a[wordIndex];
		uint32_t bb = ~b[wordIndex];
		r += aa;
		r += bb;
		result[wordIndex] = r;
		carry = ((aa >> 31) | (bb >> 31)) & ~(r >> 31);
	}
}

void shift_right_1_array(int length, uint32_t *a, uint32_t *result) {
	int prev = 0; // MSB will be zero extended
	for (int wordIndex = 0; wordIndex < length; wordIndex++) {
		uint32_t aa = a[wordIndex];
		result[wordIndex] = (aa >> 1) | (prev << 31);
		prev = aa & 1; // Lower word will be extended with LSB of this word
	}
}

void shift_left_1_array(int length, uint32_t *a, uint32_t *result) {
	int prev = 0; // LSB will be zero extended
	for (int wordIndex = length - 1; wordIndex >= 0; wordIndex--) {
		uint32_t aa = a[wordIndex];
		result[wordIndex] = (aa << 1) | prev;

		// Higher word will be extended with MSB of this word
		prev = aa >> 31;
	}
}

/*
void m_residue(int length, uint32_t *a, uint32_t *modulus, uint32_t *residue) {
	modulus_array(length, a, modulus, residue);
	for (int i = 0; i < 32; i++) {
		shift_left_1_array(length, residue, residue);
		modulus_array(length, residue, modulus, residue);
	}
}
*/

/*
void modulus_array(int length, uint32_t *a, uint32_t *modulus, uint32_t *temp,
		uint32_t *reminder) {
	 copy_array(length, a, reminder);

	 while (!greater_than_array(length, modulus, reminder)) {

	 copy_array(length, modulus, temp);

	 while (temp[0] & 0x8000_0000 = 0 & !greater_than_array(length, temp, reminder)) {
	 sub_array(length, a, temp );
	 shift_left_1_array(length, tmp);
	 }

	 sub_array(length, reminder, tmp2, reminder);

	 }
}
	 */

void zero_array(int length, uint32_t *a) {
	for (int i = 0; i < length; i++)
		a[i] = 0;
}

//TODO verify if this method works
int greater_than_array(int length, uint32_t *a, uint32_t *b) {
	for (int i = 0; i < length; i++) {
		if (a[i] > b[i])
			return 1;
	}
	return 0;
}

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

void m_residue_2_2N_array(int length, uint32_t *M, uint32_t *Nr) {
	zero_array(length, Nr);
	Nr[0] = 0x80000000;
	//Nr initilized to 2 ** N-1
}

void mont_exp_array(int length, uint32_t *X, uint32_t *E, uint32_t *M,
		uint32_t *Nr, uint32_t *P, uint32_t *ONE, uint32_t *temp, uint32_t *Z) {
	//1.
	//TODO implement calculating Nr = m_residue 2**(2N)
	m_residue_2_2N_array(length, M, Nr);

	//2.
	zero_array(length, ONE);
	ONE[length - 1] = 1;
	mont_prod_array(length, ONE, Nr, M, temp, Z);

	//3
	mont_prod_array(length, X, Nr, M, temp, P);

	//4
	for (int word_index = length - 1; word_index > 0; word_index++) {
		for (int i = 0; i < 32; i++) {
			uint32_t ei = (E[word_index] >> i) & 1;
			//6
			if (ei == 1) {
				mont_prod_array(length, Z, P, M, temp, Z);
			}
			//5
			mont_prod_array(length, P, P, M, temp, P);
			//7
		}
		//8
		mont_prod_array(length, ONE, Z, M, temp, Z);
		//9
	}
}

