/*
 * montgomery_array.h
 *
 *  Created on: Mar 3, 2015
 *      Author: psjm
 */

#ifndef MONTGOMERY_ARRAY_H_
#define MONTGOMERY_ARRAY_H_

void add_array(int length, uint32_t *a, uint32_t b[], uint32_t result[]);
void sub_array(int length, uint32_t *a, uint32_t *b, uint32_t *result);
void shift_right_1_array(int length, uint32_t *a, uint32_t *result);
void mont_prod_array(int length, uint32_t *A, uint32_t *B, uint32_t *M,
		uint32_t *temp, uint32_t *s);


#endif /* MONTGOMERY_ARRAY_H_ */
