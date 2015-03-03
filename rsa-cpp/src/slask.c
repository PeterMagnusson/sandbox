
#include <stdio.h>
#include <stdlib.h>

void square(
		int shift_factor,
		uint32_t p[],
		uint32_t p_length,
		uint32_t p_squared[]
		) {
	uint32_t previous = 0;
	for(int i = 0; i < p_length; i++) {
		uint64_t shift = p[i];
		shift <<= shift_factor;
		p_squared[i] = (shift & 0xFFFFFFFF) | previous;
		previous = shift >> 32;
	}
	p_squared[p_length] = previous;
}

/*
void square_and_multiply(
		uint32_t e,
		uint32_t p[],
		uint32_t p_length,
		uint32_t pe[],
		uint32_t pe_length,
		uint32_t tmp[],
		uint32_t tmp_length
		) {
	memset(pe, 0, sizeof(uint32_t));
	pe[0] = 1;
	for(int i = 0; i < 32; i++) {
		if ((e & 1<<i) == 0) continue;
		square(i, p, p_length, tmp, tmp_length);
        multiply(pe, pe_length, tmp_length)
	}

}
*/
