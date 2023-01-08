import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Crypto {
    static Cipher cipher;
    static String key = "aesEncryptionKey";

    public static SecretKey createAESKey(String mobile)
            throws Exception {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

        KeySpec passwordBasedEncryptionKeySpec = new PBEKeySpec(mobile.toCharArray() , key.getBytes() ,
                12288 , 256);
        SecretKey secretKeyFromPBKDF2 = secretKeyFactory.generateSecret(passwordBasedEncryptionKeySpec);
        return  new SecretKeySpec(secretKeyFromPBKDF2.getEncoded(), "AES");
    }

    public static String encrypt(String plainText , SecretKey KEY , String IV  )
            throws Exception {
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        byte[] plainTextByte = plainText.getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, KEY , ivParameterSpec);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }
    public static String decrypt(String encryptedText,SecretKey KEY , String IV)
            throws Exception {
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        Base64.Decoder decoder = Base64.getDecoder();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, KEY , ivParameterSpec);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }



}
