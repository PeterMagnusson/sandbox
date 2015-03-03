/*
 ============================================================================
 Name        : RSATestBench.c
 Author      : 
 Version     :
 Copyright   : Your copyright notice
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>

//void test_square() {
//	uint32_t p[] = { 0x12345678, 0xdeadbeef, 0x01020304 };
//	uint32_t p_length = 3;
//	uint32_t tmp_length = p_length + 1;
//	uint32_t *tmp = calloc(tmp_length, sizeof(uint32_t));
//	for (int i = 0; i <= 32; i++) {
//		square(i, p, p_length, tmp);
//		printf("%02d: %08x %08x %08x %08x\n", i, tmp[3], tmp[2], tmp[1],
//				tmp[0]);
//	}
//}



int main(void) {
	test_montgomery();
//	test_square();
	/*
	 uint32_t p_length = 1;
	 uint32_t tmp_length = p_length + 1;
	 uint32_t pe_length = p_length + 1;//TODO: length????
	 uint32_t e = 65537;

	 uint32_t p[] = { 1 };
	 uint32_t *pe = calloc(pe_length, sizeof(uint32_t));
	 uint32_t *tmp = calloc(tmp_length, sizeof(uint32_t));
	 //square_and_multiply( e, p, p_length, pe, pe_length, tmp, tmp_length );


	 puts("!!!Hello World!!!"); */
	return EXIT_SUCCESS;
}
