package com.sex8.sinchat.utils;

import android.util.Base64;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {

    static private Cipher sLoginCipher;
    static private Cipher sIMKeyCipher;
    static private SecretKeySpec key;
    static private AlgorithmParameterSpec spec;

    static {
        //TODO : need refine
        try {
            sLoginCipher = Cipher.getInstance("AES/CBC/NoPadding");
            sIMKeyCipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            key = new SecretKeySpec("Af234dfdf0io@#$*".getBytes(), "AES");
            spec = new IvParameterSpec("1234567890123456".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public String imkeyEncrypt(String plainText) throws Exception
    {
        sIMKeyCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = sIMKeyCipher.doFinal(plainText.getBytes("UTF-8"));
        String encryptedText = new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8");

        return encryptedText;
    }

    public static String imkeyDecrypt(String plainText) throws Exception
    {
        sIMKeyCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(plainText, Base64.DEFAULT);
        byte[] decrypted = sIMKeyCipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");
        return decryptedText;
    }

    static public String loginEncrypt(String plainText) throws Exception
    {
        sLoginCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        while (plainText.getBytes().length % 16 != 0) {
            plainText += '\u0020';
        }
        byte[] encrypted = sLoginCipher.doFinal(plainText.getBytes("UTF-8"));
        String encryptedText = new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8");

        return encryptedText;
    }

    static public String decrypt(String cryptedText) throws Exception
    {
        sLoginCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = sLoginCipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");

        return decryptedText;
    }

    static public String getIMKey(String uid, String nickName, String imageUrl) {
        try {
            return imkeyEncrypt(WebApiUtils.cmdIMKey(uid, nickName, imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public String getLoginEncrypt(String account, String pwd, String verify) {
        try {
            return loginEncrypt(WebApiUtils.cmdLogin(account, pwd, verify));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
