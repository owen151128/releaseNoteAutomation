package com.hpcnt.releaseNoteAutomation.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptUtil {

	private static final String CHAR_SET = "UTF-8";
	private static final String PROTOCOL = "AES";
	private static final String PROTOCOL_INFO = "AES/CBC/PKCS5Padding";

	private AESEncryptUtil() {
	}

	public static String Encode(String str, String key)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		String iv = key.substring(0, 16);
		Encoder encoder = Base64.getEncoder();
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes(CHAR_SET);
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, PROTOCOL);
		Key keySpec = secretKeySpec;

		Cipher c = Cipher.getInstance(PROTOCOL_INFO);
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

		byte[] encrypted = c.doFinal(str.getBytes(CHAR_SET));
		String enStr = new String(encoder.encode(encrypted));

		return enStr;
	}

	public static String Decode(String str, String key)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		String iv = key.substring(0, 16);
		Decoder decoder = Base64.getDecoder();
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes(CHAR_SET);
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, PROTOCOL);
		Key keySpec = secretKeySpec;

		Cipher c = Cipher.getInstance(PROTOCOL_INFO);
		c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes(CHAR_SET)));

		byte[] byteStr = decoder.decode(str.getBytes());

		return new String(c.doFinal(byteStr), CHAR_SET);
	}
}
