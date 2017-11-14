package com.iaasimov.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

import play.Logger;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class EncryptionUtils {
    private static final String HASH_PREP = "HASHED";
    private static final String HASH_APPE = "/HASHED";

    /**
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static String hash(String input) {
        try {
            if (StringUtils.isEmpty(input)) {
                return input;
            }
            Base64 b64e = new Base64();
            MessageDigest md = MessageDigest.getInstance("SHA-256", "SUN");
            //System.out.println(md.getProvider().getName());
            md.update(input.getBytes());
            byte[] output = b64e.encode(md.digest());
            return HASH_PREP + new String(output) + HASH_APPE;
        } catch (NoSuchAlgorithmException ex) {
            Logger.error("NoSuchAlgorithmException", ex);
            throw new RuntimeException(ex);
        } catch (NoSuchProviderException ex) {
            Logger.error("NoSuchProviderException", ex);
            throw new RuntimeException(ex);
        }

    }

    public static String md5(String input) throws Exception {
        return md5(input, null);
    }

    public static String md5(String input, String salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5", "SUN");
        if (salt != null) {
            md.update(salt.getBytes());
        }

        md.update(input.getBytes());
        byte[] digestBytes = md.digest();
        /* convert to hexstring */
        String hex = null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digestBytes.length; i++) {
            hex = Integer.toHexString(0xFF & digestBytes[i]);

            if (hex.length() < 2) {
                sb.append("0");

            }
            sb.append(hex);
        }
        return sb.toString();

    }

    public static boolean isHashed(String text) {
        if (text != null && text.startsWith(HASH_PREP) && text.endsWith(HASH_APPE)) {
            return true;
        }
        return false;
    }

    /**
     * Decode base64
     *
     * @param input
     * @return
     */
    public static String base64Decode(String input) {
        Base64 b64e = new Base64();
        byte[] valueText = b64e.decode(input.getBytes());

        return new String(valueText);
    }

    /**
     * Decode base64
     *
     * @param input
     * @return
     */
    public static String base64Decode(byte[] bytes) {
        Base64 b64e = new Base64();
        byte[] valueText = b64e.decode(bytes);

        return new String(valueText);
    }

    /**
     * Decode base64
     *
     * @param input
     * @return
     */
    public static byte[] base64DecodeToArray(String input) {
        Base64 b64e = new Base64();
        byte[] valueText = b64e.decode(input.getBytes());

        return valueText;
    }

    /**
     * Decode base64
     *
     * @param input
     * @return
     */
    public static String base64(String input) {
        Base64 b64e = new Base64();
        byte[] valueText = b64e.encode(input.getBytes());
        return new String(valueText);
    }

    /**
     * Decode base64
     *
     * @param input
     * @return
     */
    public static String base64(byte[] input) {

        Base64 b64e = new Base64();
        byte[] valueText = b64e.encode(input);
        return new String(valueText);
    }
    
    public static String generateApplicationId() {
		SecureRandom random = new SecureRandom();
		long aid = random.nextLong();
		if (aid < 0) {
			aid = aid*-1;
		}
		return String.valueOf(aid);
	}
}
