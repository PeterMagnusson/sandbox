package rsa;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Test;

public class GenerateNewTestCases {

	@Test
	public void generate1024() {
		generate(1024);
	}

	@Test
	public void generate2048() {
		generate(2048);
	}

	@Test
	public void generate4096() {
		generate(4096);
	}

	@Test
	public void generate8192() {
		generate(8192);
	}

	private void generate(int bitLength) {
		System.out.println("---- " + bitLength + " ----");
		int certainty = 1;
		SecureRandom random = new SecureRandom();
		BigInteger M = new BigInteger(bitLength, certainty, random);
		print("int[] M = ", M);
		BigInteger X = new BigInteger(bitLength, certainty, random);
		print("int[] X = ", X);
		BigInteger E = new BigInteger(bitLength, certainty, random);
		print("int[] E = ", E);
		BigInteger modExp = X.modPow(E, M);
		print("int[] expected = ", modExp);
	}

	private void print(String string, BigInteger m) {
		System.out.print(string);
		StringBuilder sb = new StringBuilder("{");
		byte[] bytes = m.toByteArray();
		int i = (4 - bytes.length % 4) % 4;
		int data = 0;
		for (byte b : bytes) {
			data = (data << 8) | (b & 0xFF);
			i = (i + 1) % 4;
			if (i == 0) {
				sb.append(String.format(" 0x%08x,", data));
				data = 0;
			}
		}
		sb.delete(sb.length() - 1, sb.length());
		sb.append(" };");
		assertEquals(0, i); // if i!=0 we got a bug in the print ;)
		System.out.println(sb);
		// System.out.println(bytes.length);
		// System.out.println(Arrays.toString(bytes));
	}

}
