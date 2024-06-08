package hu.czsoft.greesdk;

import lombok.extern.java.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Log
public class Crypto {
    protected static String GENERIC_KEY = "a3K8Bx%2r8Y7#xDh";

    protected static String encrypt(String plainText, String key) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.US_ASCII), "AES");
            c.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = c.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            String encoded = new String(Base64.getEncoder().encode(encrypted));

            return encoded;
        } catch (Exception e) {
            LOGGER.severe("Pack encryption failed. Error: " + e.getMessage());
        }

        return "";
    }

    protected static String decrypt(String encrypted, String key) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encrypted);

            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.US_ASCII), "AES");
            c.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decrypted = c.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            LOGGER.severe("Pack decryption failed. Error: " + e.getMessage());
        }

        return "";
    }
}
