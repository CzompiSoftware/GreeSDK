package eu.czsoft.greesdk;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Log4j2
public class Crypto {
    protected static String GENERIC_KEY = "a3K8Bx%2r8Y7#xDh";

    protected static String encrypt(String plainText, String key) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            c.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = c.doFinal(plainText.getBytes("UTF-8"));
            String encoded = new String(Base64.getEncoder().encode(encrypted));

            return encoded;
        } catch (Exception e) {
            LOGGER.error("Pack encryption failed. Error: " + e.getMessage());
        }

        return "";
    }

    protected static String decrypt(String encrypted, String key) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encrypted);

            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            c.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decrypted = c.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            LOGGER.error("Pack decryption failed. Error: " + e.getMessage());
        }

        return "";
    }
}
