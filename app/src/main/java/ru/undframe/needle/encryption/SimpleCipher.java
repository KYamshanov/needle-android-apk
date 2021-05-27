package ru.undframe.needle.encryption;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class SimpleCipher {

    public static final String PASSWORD_CIPHER_KEY = "E1G6VO26lKO642udnUegyjLRyFjsNONiG9/ZSvXVltg=";
    private static final String PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2M35EphKi1AcNbRGtT/IbQdk7Pj1t5+uqhCxAj7owZkvdNPoRHCPIg3i6ZlIZzdlTl7zgccwqqjyQhhrnBpVAlPPLSQgnUqscUc2cXX4oFOkat8DhxAMnWIFW/Xh8QPTWucsRtF9uwId/BZGgJUhj58b8dvRLqAhmJIAS3uzFZF6HTLW+D2Fkc0IBLhUG3mHmze9mlfiioqUxc8oKpMIdtoU+67BcqqiQElCZTOje8h2XL7xM2aBi1EZ39KXs65bIBVEehF8U1o9SYIxv6R7NleQggjxgoJyEFjcKVKVj6ZOxiuVE/xi9OFrnl2dJ9ZSafjNfu1SF0KNukKWjgN6vwIDAQAB";
    private static Cipher cipher;
    private static Signature verifySignature;

    private static final String PASSWORD_KEY = "XeRzJ2BE7NYNat72TpHru44Auu5KHLAw";
    private static Cipher encoderPassword;
    private static Cipher decoderPassword;


    static{
        try {
            cipher = Cipher.getInstance("RSA");
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(android.util.Base64.decode(PUBLIC_KEY_BASE64.getBytes(), Base64.DEFAULT)));
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            verifySignature = Signature.getInstance("SHA256withRSA");
            verifySignature.initVerify(publicKey);



            SecretKeySpec aesKey = new SecretKeySpec(PASSWORD_KEY.getBytes(), "AES");
            encoderPassword = Cipher.getInstance("AES");
            encoderPassword.init(Cipher.ENCRYPT_MODE, aesKey);


            decoderPassword = Cipher.getInstance("AES");
            decoderPassword.init(Cipher.DECRYPT_MODE, aesKey);


        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encode(byte[] data) throws BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(data);
    }

    public static boolean verifySignature(byte[] signature, byte[] expectedSignature) throws SignatureException {
        verifySignature.update(signature);
        return verifySignature.verify(expectedSignature);
    }


    public static byte[] encodePassword(byte[] data) throws BadPaddingException, IllegalBlockSizeException {
        return encoderPassword.doFinal(data);
    }

    public static byte[] decodePassword(byte[] data) throws BadPaddingException, IllegalBlockSizeException {
        return decoderPassword.doFinal(data);
    }


}
