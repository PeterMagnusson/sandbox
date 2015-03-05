package rsa;


public class MontgomeryArray {
	static void mont_prod_array(int length, int[] A, int B[], int M[], int[] s) {
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

	static void copy_array(int length, int[] src, int[] dst) {
		for (int i = 0; i < length; i++)
			dst[i] = src[i];
	}

	static void add_array(int length, int[] a, int b[], int result[]) {
		long carry = 0;
		for (int i = length - 1; i >= 0; i--) {
			long r = carry;
			int aa = a[i];
			int bb = b[i];
			r += aa & 0xFFFFFFFFL;
			r += bb & 0xFFFFFFFFL;
			carry = ((int) (r>>32l)) & 1;
			result[i] = (int) r;
		}
	}

	static void sub_array(int length, int[] a, int[] b, int result[]) {
		long carry = 1;
		for (int wordIndex = length - 1; wordIndex >= 0; wordIndex--) {
			long r = carry;
			int aa = a[wordIndex];
			int bb = ~b[wordIndex];
			r += aa & 0xFFFFFFFFL;
			r += bb & 0xFFFFFFFFL;
			carry = (r>>32l) & 1;
			result[wordIndex] = (int) r;
		}
	}

	static void shift_right_1_array(int length, int[] a, int result[]) {
		int prev = 0; // MSB will be zero extended
		for (int wordIndex = 0; wordIndex < length; wordIndex++) {
			int aa = a[wordIndex];
			result[wordIndex] = (aa >>> 1) | (prev << 31);
			prev = aa & 1; // Lower word will be extended with LSB of this word
		}
	}

	static void shift_left_1_array(int length, int[] a, int result[]) {
		int prev = 0; // LSB will be zero extended
		for (int wordIndex = length - 1; wordIndex >= 0; wordIndex--) {
			int aa = a[wordIndex];
			result[wordIndex] = (aa << 1) | prev;
			prev = aa >>> 31; // Lower word will be extended with LSB of this
								// word
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
		int temp[] = new int[length];
		copy_array(length, a, temp); //long P = N;
		copy_array(length, a, reminder); //long T = N;
		while ((temp[0] & 0x80000000) == 0) { //while(P>=0) {
			//debugArray("T= ", 3, temp);
			copy_array(length, temp, reminder); //T = P;
			sub_array(length, temp, modulus, temp); //P -= D;
		}*/

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

	private static void zero_array(int length, int[] a) {
		for (int i = 0; i < length; i++)
			a[i] = 0;
	}

	private static boolean greater_than_array(int length, int[] a, int[] b) {
		for (int i = 0; i < length; i++) {
			boolean toobig = (a[i] & 0x0FFFF_FFFFL) > (b[i] & 0x0FFFF_FFFFL);
			if (toobig) {
				return true;
			}
		}
		return false;
	}
	
	private static void m_residue_2_2N_array(int length, int[] M, int[] temp, int[] Nr) {
		zero_array(length, Nr);
		Nr[0] = 0x40000000; //Nr  = 2 ** N-2
		modulus_array(length, Nr, M, Nr); //Nr = (2 ** N-2) mod M
		int N = 32 * length;
		for (int i = 0; i < (N) + 2; i++) {
			shift_left_1_array(length, Nr, Nr);
			modulus_array(length, Nr, M, Nr);
		}
		//Nr = (2 ** 2N) mod M
	}

	public static void mont_exp_array(int length, int[] X, int[] E, int[] M,
			int[] Nr, int[] P, int[] ONE, int[] temp, int[] Z) {
		//1.
		//TODO implement calculating Nr = m_residue 2**(2N)
		m_residue_2_2N_array(length, M, temp, Nr);

		//2.
		zero_array(length, ONE);
		ONE[length - 1] = 1;
		mont_prod_array(length, ONE, Nr, M, Z);

		//3
		mont_prod_array(length, X, Nr, M, P);

		//4
		for (int word_index = length - 1; word_index > 0; word_index--) {
			for (int i = 0; i < 32; i++) {
				int ei = (E[word_index] >> i) & 1;
				//6
				if (ei == 1) {
					mont_prod_array(length, Z, P, M, Z);
				}
				//5
				mont_prod_array(length, P, P, M, P);
				//7
			}
			//8
			mont_prod_array(length, ONE, Z, M, Z);
			//9
		}
	}

}
