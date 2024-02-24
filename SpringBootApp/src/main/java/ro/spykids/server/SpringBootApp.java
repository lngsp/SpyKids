package ro.spykids.server;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@EnableJpaRepositories("ro.spykids.server.repository")
@ComponentScan(basePackages = {"ro.spykids"})
@EntityScan("ro.spykids.server.model")
@SpringBootApplication
@EnableEncryptableProperties
public class SpringBootApp {
//	private static final String KEY_FILE_PATH = "C:\\Users\\STEFANIA\\Desktop\\Facultate\\Licenta\\SpyKids\\SpringBootApp\\src\\main\\resources\\static\\key.txt";

	public static void main(String[] args) {
		SpringApplication.run(SpringBootApp.class, args);

		//if (Files.exists(Paths.get(KEY_FILE_PATH))) {
			String aesKey = loadKeyFromFile(); // Load key from file
			System.out.println("Key from file: " + aesKey);
		//} else {
		//	String aesKey = generateAndSaveKey();
		//	System.out.println("Key generate and save: " + aesKey);
		//}
	}

//	private static String generateAndSaveKey() {
//		try {
//			// Generating a random key with the AES algorithm
//			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//			SecureRandom secureRandom = new SecureRandom();
//			keyGenerator.init(256, secureRandom); // key length (256 recommended for AES)
//			SecretKey secretKey = keyGenerator.generateKey();
//
//			// Getting the key value as a string (hexadecimal representation)
//			String aesKey = bytesToHex(secretKey.getEncoded());
//
//			// Save in file
//			FileWriter writer = new FileWriter(KEY_FILE_PATH);
//			writer.write(aesKey);
//			writer.close();
//
//			return aesKey;
//		} catch (NoSuchAlgorithmException | IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

	private static String loadKeyFromFile() {
		try {
			byte[] keyBytes = new ClassPathResource("classpath:key.txt").getContentAsByteArray();//ClassLoader.getSystemResourceAsStream("BOOT-INF/clases/key.txt").readAllBytes();//Files.readAllBytes(ClassLoader.getSystemResourceAsStream("key.txt"));//Paths.get(KEY_FILE_PATH));
			return new String(keyBytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// from bytes to hex (octeti -> hexa)
	private static String bytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte b : bytes) {
			result.append(String.format("%02x", b));
		}
		return result.toString();
	}

}
