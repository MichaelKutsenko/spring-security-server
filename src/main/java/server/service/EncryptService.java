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

    public String encrypt(String id, String key) {

        try {
            if (!isKeyLenthValid(key)){
                throw new Exception("length of key must be 128, 192, 256 bits");
            }
            IvParameterSpec ivParameterSpec = new IvParameterSpec("1234567890123456".getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            return new String(Hex.encodeHex(cipher.doFinal(id.getBytes("UTF-8")), false));
        } catch (Exception e) {
            logger.error("Encryption failed: " + e.getMessage());
            return null;
        }
    }

    private boolean isKeyLenthValid(String key) {
        if (key.length() == 16 || key.length() == 24 ||key.length() == 32) return true;
        else return false;
    }

    public static void main(String[] args) {
        EncryptService service = new EncryptService();

        System.out.println(service.encrypt("9876543210", "1234567890123456"));
        System.out.println(service.encrypt("9876543210", "1234567890123456"));
        System.out.println(service.encrypt("user", "1234567890123456"));
        System.out.println(service.encrypt("manager", "1234567890123456"));
        System.out.println(service.encrypt("admin", "1234567890123456"));
    }

}
