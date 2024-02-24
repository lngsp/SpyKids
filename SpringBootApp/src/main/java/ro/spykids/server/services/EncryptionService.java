package ro.spykids.server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EncryptionService {
    //encryption descryption using secret key cipher
//    private static final String KEY_FILE_PATH = "C:\\Users\\STEFANIA\\Desktop\\Facultate\\Licenta\\SpyKids\\SpringBootApp\\src\\main\\resources\\static\\key.txt";

    private static String loadKeyFromFile() {
        try {
            byte[] keyBytes = new ClassPathResource("classpath:key.txt").getContentAsByteArray();//ClassLoader.getSystemResourceAsStream("BOOT-INF/clases/key.txt").readAllBytes();//Files.readAllBytes(ClassLoader.getSystemResourceAsStream("key.txt"));//Paths.get(KEY_FILE_PATH));
            return new String(keyBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static final String AES_KEY;

    static {
        AES_KEY = loadKeyFromFile();
    }

    public String encrypt(String data){
        AES aes = new AES(AES_KEY);
        return aes.encrypt(data);
    }
    public String decrypt(String data){
        AES aes = new AES(AES_KEY);
        return aes.decrypt(data);
    }

    private class AES{
        private SecretKeySpec secretKeySpec;
        private byte[] key;

        AES(String secret){
            MessageDigest messageDigest = null;
            try {
                //Convert the secret string to a byte array using the specified character set
                key = secret.getBytes(StandardCharsets.ISO_8859_1);
                //MessageDigest object for the SHA-1 hashing algorithm
                messageDigest = MessageDigest.getInstance("SHA-1");
                key = messageDigest.digest(key); // hash value of the key
                key = Arrays.copyOf(key,16); // Truncate the key to 16 bytes
                //SecretKeySpec object using the derived key and algorithm AES
                secretKeySpec = new SecretKeySpec(key, "AES");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }


        String encrypt(String data){
            try {
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                return Base64.getUrlEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.ISO_8859_1)));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }

        String decrypt(String data){
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                return new String(cipher.doFinal(Base64.getUrlDecoder().decode(data)));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
