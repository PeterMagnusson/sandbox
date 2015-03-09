package rsa;
import static rsa.BigNum.*;

public class MontgomeryArray {
	static void mont_prod_array(int length, int[] A, int B[], int M[], int[] s) {
		if (A == s || B == s)
			throw new IllegalArgumentException();
		zero_array(length, s);
		int[] qSub = new int[length];
		int[] sMA = new int[length];
		int[] sM = new int[length];
		int[] sA = new int[length];
		for (int wordIndex = length - 1; wordIndex >= 0; wordIndex--) {
			for (int i = 0; i < 32; i++) {
				sub_array(length, s, A, qSub); // int q = (s - b * A) & 1;
				add_array(length, s, M, sM);
				add_array(length, s, A, sA);
				add_array(length, sM, A, sMA);

				int b = (B[wordIndex] >>> i) & 1;
				int[] qSelect = (b == 1) ? qSub : s;
				int q = qSelect[length - 1] & 1; // int q = (s - b * A) & 1;

				// s = (s + q*M + b*A) >>> 1;
				if (q == 1 && b == 1) {
					copy_array(length, sMA, s);
				} else if (q == 1) {
					copy_array(length, sM, s);
				} else if (b == 1) {
					copy_array(length, sA, s);
				}
				shift_right_1_array(length, s, s);
			}
		}
	}


	public static void m_residue(int length, int[] a, int[] modulus,
			int[] residue) {
		modulus_array(length, a, modulus, residue);
		for (int i = 0; i < 32; i++) {
			shift_left_1_array(length, residue, residue);
			modulus_array(length, residue, modulus, residue);
		}
	}

	static void modulus_array(int length, int[] a, int[] modulus, int[] reminder) {

		/*
		 * int temp[] = new int[length]; copy_array(length, a, temp); //long P =
		 * N; copy_array(length, a, reminder); //long T = N; while ((temp[0] &
		 * 0x80000000) == 0) { //while(P>=0) { //debugArray("T= ", 3, temp);
		 * copy_array(length, temp, reminder); //T = P; sub_array(length, temp,
		 * modulus, temp); //P -= D; }
		 */

		int[] tmp = new int[length];
		int[] tmp2 = new int[length];
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

	public static void m_residue_2_2N_array(int length, int N, int[] M,
			int[] temp, int[] Nr) {
		zero_array(length, Nr);
		Nr[length - 1] = 1; // Nr = 1 == 2**(2N-2N)
		for (int i = 0; i < 2 * N ; i++) {
			shift_left_1_array(length, Nr, Nr);
			modulus_array(length, Nr, M, Nr);
//			debugArray(length, Nr);
		}
		// Nr = (2 ** 2N) mod M
	}

	private static int findN(int length, int[] E) {
		for (int i = 0; i < length; i++) {
			int e = E[i];
			if (e == 0)
				continue;
			for (int j = 31; i >= 0; j--) {
				if (((e >> j) & 1) == 1) {
					return j + 32 * (length - i - 1);
				}
			}
		}
		return 0;
	}

	public static void mont_exp_array(int length, int[] X, int[] E, int[] M,
			int[] Nr, int[] P, int[] ONE, int[] temp, int[] Z) {

		int n = findN(length, E);

		// 1. Nr := 2 ** 2N mod M
		m_residue_2_2N_array(length, n, M, temp, Nr);

		// 2. Z0 := MontProd( 1, Nr, M )
		zero_array(length, ONE);
		ONE[length - 1] = 1;
		mont_prod_array(length, ONE, Nr, M, Z);

		// 3. P0 := MontProd( X, Nr, M );
		mont_prod_array(length, X, Nr, M, P);

		// 4. for i = 0 to n-1 loop
		for (int i = 0; i < n; i++) {
			int ei_ = E[length - 1 - (i / 32)];
			int ei = (ei_ >> (i % 32)) & 1;
			// 6. if (ei = 1) then Zi+1 := MontProd ( Zi, Pi, M) else Zi+1 := Zi
			if (ei == 1) {
				mont_prod_array(length, Z, P, M, temp);
				copy_array(length, temp, Z);
			}
			// 5. Pi+1 := MontProd( Pi, Pi, M );
			mont_prod_array(length, P, P, M, temp);
			copy_array(length, temp, P);

		} // 7. end for
			// 8. Zn := MontProd( 1, Zn, M );
		mont_prod_array(length, ONE, Z, M, temp);
		copy_array(length, temp, Z);
		// 9. RETURN Zn
	}

}
