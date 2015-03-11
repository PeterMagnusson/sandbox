package rsa;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Test;

public class GenerateNewTestCases {

	@Test
	public void generate1024() {
		int bitLength = 1024;
		int certainty = 12;
		SecureRandom random = new SecureRandom();
		BigInteger M = new BigInteger(bitLength, certainty, random);
		BigInteger X = new BigInteger(bitLength, certainty, random);
		BigInteger E = new BigInteger(bitLength, certainty, random);
		BigInteger modExp = X.modPow(E, M);
		print("int[] M = ", M);
		print("int[] X = ", X);
		print("int[] E = ", E);
		print("int[] expected = ", modExp);		
	}

	private void print(String string, BigInteger m) {
		System.out.print(string);
		StringBuilder sb = new StringBuilder("{");
		byte[] bytes = m.toByteArray();
		int i = (4 - bytes.length%4) % 4;
		int data = 0;
		for(byte b : bytes) {
			data = (data<<8) | (b & 0xFF);
			i = (i+1) % 4;
			if (i == 0) {
				sb.append(String.format(" 0x%08x,", data));
				data = 0;
			}
		}
		sb.delete(sb.length()-1, sb.length());
		sb.append(" };");
		assertEquals(0, i); //if i!=0 we got a bug in the print ;)
		System.out.println(sb);
//		System.out.println(bytes.length);
//		System.out.println(Arrays.toString(bytes));
	}

}
