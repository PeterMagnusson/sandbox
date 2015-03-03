#include <stdio.h>
#include <stdlib.h>

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

void copy_array(int length, int *src, int *dst) {
	for (int i = 0; i < length; i++)
		dst[i] = src[i];
}

void add_array(int length, uint32_t *a, uint32_t b[], uint32_t result[]) {
	int carry = 0;
	for (int i = length - 1; i >= 0; i--) {
		int r = carry;
		int aa = a[i];
		int bb = b[i];
		r += aa;
		r += bb;
		result[i] = r;
		carry = ((aa >> 31) | (bb >> 31)) & ~(r >> 31);
	}
}

void sub_array(int length, uint32_t *a, uint32_t *b, uint32_t *result) {
	int carry = 1;
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
		int aa = a[wordIndex];
		result[wordIndex] = (aa >> 1) | (prev << 31);
		prev = aa & 1; // Lower word will be extended with LSB of this word
	}
}

void shift_left_1_array(int length, uint32_t *a, uint32_t *result) {
	int prev = 0; // LSB will be zero extended
	for (int wordIndex = length - 1; wordIndex >= 0; wordIndex--) {
		int aa = a[wordIndex];
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
void modulus_array(int length, uint32_t *a, uint32_t *modulus,
		uint32_t *reminder) {
	 int tmp[] = new int[length];
	 int tmp2[] = new int[length];
	 copy_array(length, a, reminder);

	 while (!greater_than_array(length, modulus, reminder)) {
	 copy_array(length, modulus, tmp);
	 zero_array(length, tmp2);

	 while (!greater_than_array(length, tmp, reminder)) {
	 copy_array(length, tmp, tmp2);
	 shift_left_1_array(length, tmp, tmp);
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

