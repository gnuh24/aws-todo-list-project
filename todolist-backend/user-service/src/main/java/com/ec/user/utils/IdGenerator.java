package com.ec.user.utils;

import java.security.SecureRandom;

public class IdGenerator {
	
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789".toUpperCase();
	private static final int ID_LENGTH = 10;
	
	private static final String CHARACTERS_OTP = "0123456789".toUpperCase();
	private static final int ID_LENGTH_OTP = 6;
	
	private static final SecureRandom RANDOM = new SecureRandom();
	
	public static String generateId() {
		StringBuilder id = new StringBuilder(ID_LENGTH);
		for (int i = 0; i < ID_LENGTH; i++) {
			int index = RANDOM.nextInt(CHARACTERS.length());
			id.append(CHARACTERS.charAt(index));
		}
		return id.toString();
	}
	
	public static String generateOTP() {
		StringBuilder id = new StringBuilder(ID_LENGTH_OTP);
		for (int i = 0; i < ID_LENGTH_OTP; i++) {
			int index = RANDOM.nextInt(CHARACTERS_OTP.length());
			id.append(CHARACTERS_OTP.charAt(index));
		}
		return id.toString();
	}
}


