package rsa;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestDivision {
	private static final long ITERATION_LEVEL = 5;
	
	//Additional cost n*2 + n*2 + N*2 (P, T, doubled N) 
	long mod(long N, long D, long n) {
		long P = N;
		D = D << n;// -- P and D need twice the word width of N and Q
		// for i = n-1..0 do -- for example 31..0 for 32 bits
		for (long i = n - 1; i >= 0; i--) {
			long T = P << 1;
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
		for (long n = 2; n < ITERATION_LEVEL; n++)
			for (long D = 2; D <= (1 << n) - 1; D++)
				for (long N = 1; N <= (1 << n) - 1; N++) {
					long reminder = mod(N, D, n);
					long modulus = N % D;
					String msg = String
							.format("(div( %d, %d, %d) yelds reminder: %d (expected: %d)\n",
									N, D, n, reminder, modulus);
					assertEquals(msg, reminder, modulus);
				}
	}

	// additional cost: n*2 (P,T). Very slow.
	long mod2(long N, long D, long n) {
		long P = N;
		long T = N;
		while(P>=0) {
			T = P;
			P -= D;
		}
		return T;
	}

	@Test
	public void test2() {
		for (long n = 2; n < ITERATION_LEVEL; n++)
			for (long D = 2; D <= (1 << n) - 1; D++)
				for (long N = 1; N <= (1 << n) - 1; N++) {
					long reminder = mod2(N, D, n);
					long modulus = N % D;
					String msg = String
							.format("(div( %d, %d, %d) yelds reminder: %d (expected: %d)\n",
									N, D, n, reminder, modulus);
					assertEquals(msg, reminder, modulus);
				}
	}

	long mod3(long N, long D, long n) {
		long P = N;
		long T;
		for (long i = n - 1; i >= 0; i--) {
			T = P;
			P = T - (D << i);// -- trial subtraction from shifted value
//			System.out.prlongf("i=%d N=%d D=%d T=%d P=%d\n", i, N, D, T, P);
			if (P >= 0) {
				//do not care about setting Q
			} else {
				//do not care about setting Q
				P = T;
			}
		}
		return P;
	}

	@Test
	public void test3() {
		for (long n = 2; n < ITERATION_LEVEL; n++)
			for (long D = 2; D <= (1 << n) - 1; D++)
				for (long N = 1; N <= (1 << n) - 1; N++) {
					long reminder = mod3(N, D, n);
					long modulus = N % D;
					String msg = String
							.format("(div( %d, %d, %d) yelds reminder: %d (expected: %d)\n",
									N, D, n, reminder, modulus);
					assertEquals(msg, modulus, reminder);
				}
	}


}
