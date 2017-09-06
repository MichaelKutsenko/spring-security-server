package server.service;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Mocart on 05-Sep-17.
 */
@Service
public class EncryptService {

    Logger logger = LoggerFactory.getLogger(getClass());

    public String encrypt(String textToEncr, String key) {

        try {
            if (!isKeyLengthValid(key)){
                throw new Exception("length of key must be 128, 192, 256 bits");
            }
            IvParameterSpec ivParameterSpec = new IvParameterSpec("1234567890123456".getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            return new String(Hex.encodeHex(cipher.doFinal(textToEncr.getBytes("UTF-8")), false));
        } catch (Exception e) {
            logger.error("Encryption failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String encodedText, String key) {

        try {
            if (!isKeyLengthValid(key)){
                throw new Exception("length of key must be 128, 192, 256 bits");
            }
            IvParameterSpec ivParameterSpec = new IvParameterSpec("1234567890123456".getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            return new String(cipher.doFinal(Hex.decodeHex(encodedText.toCharArray())));
        } catch (Exception e) {
            logger.error("Decryption failed: " + e.getMessage());
            return null;
        }
    }

    private boolean isKeyLengthValid(String key) {
        if (key.length() == 16 || key.length() == 24 ||key.length() == 32) return true;
        else return false;
    }
}
