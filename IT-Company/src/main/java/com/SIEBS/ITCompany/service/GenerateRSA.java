package com.SIEBS.ITCompany.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

@Service
public class GenerateRSA {
    private final String KEYSTORE_FILE;
    private final String KEYSTORE_PASSWORD ;
    private final String KEY_ALIAS ;

    @Autowired
    public GenerateRSA(@Value("${server.ssl.key-store}") String keyStoreFile,
                              @Value("${server.ssl.key-store-password}") String keyStorePassword,
                              @Value("${server.ssl.key-alias}") String keyAlias) {
        this.KEYSTORE_FILE  = keyStoreFile;
        this.KEYSTORE_PASSWORD  = keyStorePassword;
        this.KEY_ALIAS  = keyAlias;
    }



    byte[] encryptedAesKey;
    public void encryptCVDocument(String cvFilePath) {
        try {
            // Učitavanje CV dokumenta
            Path cvPath = Paths.get(cvFilePath);
            byte[] cvContent = Files.readAllBytes(cvPath);

            // Generisanje AES ključa
            SecretKey aesKey = generateAESKey();

            // Učitavanje javnog ključa iz KeyStore-a
            PublicKey publicKey = loadPublicKeyFromKeyStore();

            // Šifrovanje AES ključa javnim RSA ključem
            encryptedAesKey = encryptAESKeyWithPublicKey(aesKey, publicKey);

            // Šifrovanje CV dokumenta AES ključem
            byte[] encryptedCVContent = encryptDocumentWithAES(cvContent, aesKey);

            // Čuvanje šifrovanog CV dokumenta i šifrovanog AES ključa
            saveEncryptedCVDocument(cvPath, encryptedCVContent);
            saveEncryptedAESKey(encryptedAesKey);

        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 KeyStoreException | CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decryptCVDocument(String cvFilePath) {
        try {
            // Učitavanje šifrovanog CV dokumenta
            Path cvPath = Paths.get(cvFilePath);
            byte[] encryptedCVContent = Files.readAllBytes(cvPath);

            // Učitavanje privatnog ključa iz KeyStore-a
            PrivateKey privateKey = loadPrivateKeyFromKeyStore();

            // Učitavanje šifrovanog AES ključa
//            byte[] encryptedAesKey = loadEncryptedAESKey();
//
            // Dekriptovanje AES ključa privatnim RSA ključem
            SecretKey aesKey = decryptAESKeyWithPrivateKey(encryptedAesKey, privateKey);

            // Dekriptovanje CV dokumenta AES ključem
            byte[] decryptedCVContent = decryptDocumentWithAES(encryptedCVContent, aesKey);
            saveDecryptedCVDocument(cvPath, decryptedCVContent);
            return decryptedCVContent;
            // Čuvanje dešifrovanog CV dokumenta


        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                 KeyStoreException | CertificateException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    private PublicKey loadPublicKeyFromKeyStore() throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException, IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream fileInputStream = new FileInputStream(KEYSTORE_FILE);
        keyStore.load(fileInputStream, KEYSTORE_PASSWORD.toCharArray());

        Certificate certificate = keyStore.getCertificate(KEY_ALIAS);
        return certificate.getPublicKey();
    }

    private PrivateKey loadPrivateKeyFromKeyStore() throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException, IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream fileInputStream = new FileInputStream(KEYSTORE_FILE);
        keyStore.load(fileInputStream, KEYSTORE_PASSWORD.toCharArray());

        Key key = keyStore.getKey(KEY_ALIAS, KEYSTORE_PASSWORD.toCharArray());
        if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        }
        return null;
    }

    private byte[] encryptAESKeyWithPublicKey(SecretKey aesKey, PublicKey publicKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return rsaCipher.doFinal(aesKey.getEncoded());
    }

    private byte[] encryptDocumentWithAES(byte[] document, SecretKey aesKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return aesCipher.doFinal(document);
    }

    private void saveEncryptedCVDocument(Path cvPath, byte[] encryptedCVContent) throws IOException {
        Path encryptedCVPath = cvPath.resolveSibling(cvPath.getFileName() + ".encrypted");
        Files.write(encryptedCVPath, encryptedCVContent);
    }

    private void saveEncryptedAESKey(byte[] encryptedAesKey) throws IOException {
        Path encryptedAESKeyPath = Paths.get(KEYSTORE_FILE + ".encrypted");
        Files.write(encryptedAESKeyPath, encryptedAesKey);
    }

    private byte[] loadEncryptedAESKey() throws IOException {
        Path encryptedAESKeyPath = Paths.get(KEYSTORE_FILE + ".encrypted");
        return Files.readAllBytes(encryptedAESKeyPath);
    }

    private SecretKey decryptAESKeyWithPrivateKey(byte[] encryptedAesKey, PrivateKey privateKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedAesKey = rsaCipher.doFinal(encryptedAesKey);
        return new SecretKeySpec(decryptedAesKey, "AES");
    }

    private byte[] decryptDocumentWithAES(byte[] encryptedData, SecretKey aesKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        return aesCipher.doFinal(encryptedData);
    }

    private void saveDecryptedCVDocument(Path cvPath, byte[] decryptedCVContent) throws IOException {
        Path decryptedCVPath = cvPath.resolveSibling(cvPath.getFileName() + ".decrypted");
        Files.write(decryptedCVPath, decryptedCVContent);
    }

}
