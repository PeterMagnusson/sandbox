package rsa;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestDivision {
	int mod(int N, int D, int n) {
		int P = N;
		D = D << n;// -- P and D need twice the word width of N and Q
		// for i = n-1..0 do -- for example 31..0 for 32 bits
		for (int i = n - 1; i >= 0; i--) {
			int T = P << 1;
			P = T - D;// -- trial subtraction from shifted value
			if (P >= 0) {
				//do not care about setting Q
			} else {
				//do not care about setting Q
				P = T;
			}
		}
		return P >> n;
	}
	
	@Test
	public void test() {
		for (int n = 2; n < 5; n++)
			for (int D = 2; D <= (1 << n) - 1; D++)
				for (int N = 1; N <= (1 << n) - 1; N++) {
					int reminder = mod(N, D, n);
					int modulus = N % D;
					String msg = String
							.format("(div( %d, %d, %d) yelds reminder: %d (expected: %d)\n",
									N, D, n, reminder, modulus);
					assertEquals(msg, reminder, modulus);
				}
	}

	int mod2(int N, int D, int n) {
		int P = N;
		int T = N;
		while(P>=0) {
			T = P;
			P -= D;
		}
		return T;
	}

	@Test
	public void test2() {
		for (int n = 2; n < 4; n++)
			for (int D = 2; D <= (1 << n) - 1; D++)
				for (int N = 1; N <= (1 << n) - 1; N++) {
					int reminder = mod2(N, D, n);
					int modulus = N % D;
					String msg = String
							.format("(div( %d, %d, %d) yelds reminder: %d (expected: %d)\n",
									N, D, n, reminder, modulus);
					assertEquals(msg, reminder, modulus);
				}
	}

}
