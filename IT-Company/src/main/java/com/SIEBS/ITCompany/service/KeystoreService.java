package com.SIEBS.ITCompany.service;
import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.Address;
import com.SIEBS.ITCompany.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

import java.security.cert.CertificateException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


@Service
@RequiredArgsConstructor
@Slf4j
public class KeystoreService {

    @Value("${keystore.path}")
    private String KEYSTORE_PATH;

    @Value("${keystore.password}")
    private String KEYSTORE_PASSWORD;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] IV_BYTES = {
            (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
            (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
            (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C,
            (byte) 0x0D, (byte) 0x0E, (byte) 0x0F, (byte) 0x10
    };
    private static final IvParameterSpec IV = new IvParameterSpec(IV_BYTES);

    public String removeDangerousCharacters(String input) {
        // Lista potencijalno opasnih karaktera
        String[] dangerousCharacters = {"'", "\"", "/", "\\", "<", ">", "|"};

    //private static final IvParameterSpec IV = new IvParameterSpec("[B@17c3e33".getBytes());
    //private static final IvParameterSpec IV = generateIv();

        // Uklanjanje opasnih karaktera iz stringa
        for (String character : dangerousCharacters) {
            input = input.replace(character, "");
        }

        return input;
    }
    public void addKey(String alias, String keyPassword, SecretKey secretKey) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            File keystoreFile = new File(KEYSTORE_PATH);

            if (keystoreFile.exists()) {
                FileInputStream fis = new FileInputStream(keystoreFile);
                keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
                fis.close();
            } else {
                createNewKeystore();
                FileInputStream fis = new FileInputStream(keystoreFile);
                keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
                fis.close();
            }

            KeyStore.SecretKeyEntry secret= new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection(keyPassword.toCharArray());
            keyStore.setEntry(alias, secret, password);

            FileOutputStream fos = new FileOutputStream(KEYSTORE_PATH);
            keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray());
            fos.close();
            log.info("Secret key successfully added to keystore. Alias: " + removeDangerousCharacters(alias));
            System.out.println("Tajni ključ je uspješno dodan u keystore.");
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            log.error("Secret key successfully added to keystore. " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createNewKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");

            keyStore.load(null, KEYSTORE_PASSWORD.toCharArray());

            FileOutputStream fos = new FileOutputStream(KEYSTORE_PATH);
            keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray());
            fos.close();
            log.info("New key store created");
            System.out.println("Kreiran novi keystore.");
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            log.info("Failed to create neq key store" + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        return secretKey;
    }

    public String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IV);

        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IV);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        System.out.println("--------------------------------------");
        System.out.println(iv.toString());
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public SecretKey getKey(String alias, String keyPassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            File keystoreFile = new File(KEYSTORE_PATH);

            if (keystoreFile.exists()) {
                FileInputStream fis = new FileInputStream(keystoreFile);
                keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
                fis.close();
            } else {
                System.out.println("Keystore file does not exist.");
                log.error("Keystore file does not exist.");
                return null;
            }

            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyPassword.toCharArray());
            KeyStore.Entry entry = keyStore.getEntry(alias, entryPassword);

            if (entry == null) {
                log.error("Entry with alias " + removeDangerousCharacters(alias) + " does not exist in the keystore.");
                System.out.println("Entry with alias '" + alias + "' does not exist in the keystore.");
                return null;
            }

            if (!(entry instanceof KeyStore.SecretKeyEntry)) {
                log.error("Entry with alias " + removeDangerousCharacters(alias) + " is not a SecretKey entry.");
                System.out.println("Entry with alias '" + alias + "' is not a SecretKey entry.");
                return null;
            }

            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) entry;
            return secretKeyEntry.getSecretKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException |
                 UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public UserEncoded encryptUser(User user) throws Exception {
        SecretKey key = generateKey();
        addKey(user.getEmail(), user.getPhoneNumber(), key);

        String encryptedTitle = encrypt(user.getTitle(), key);
        String encryptedCountry = encrypt(user.getAddress().getCountry(), key);
        String encryptedCity = encrypt(user.getAddress().getCity(), key);
        String encryptedStreet = encrypt(user.getAddress().getStreet(), key);
        String encryptedNumber = encrypt(user.getAddress().getNumber(), key);

        Address encryptedAddress = Address.builder()
                .id(user.getAddress().getId())
                .country(encryptedCountry)
                .city(encryptedCity)
                .street(encryptedStreet)
                .number(encryptedNumber)
                .build();
        UserEncoded encryptedUser = UserEncoded.builder()
                .title(encryptedTitle)
                .adress(encryptedAddress)
                .build();

        return encryptedUser;
    }

    public UserEncoded encryptUser(RegisterRequest user) throws Exception {
        SecretKey key = generateKey();
        addKey(user.getEmail(), user.getPhoneNumber(), key);

        String encryptedTitle = encrypt(user.getTitle(), key);
        String encryptedCountry = encrypt(user.getAddress().getCountry(), key);
        String encryptedCity = encrypt(user.getAddress().getCity(), key);
        String encryptedStreet = encrypt(user.getAddress().getStreet(), key);
        String encryptedNumber = encrypt(user.getAddress().getNumber(), key);

        Address encryptedAddress = Address.builder()
                .id(user.getAddress().getId())
                .country(encryptedCountry)
                .city(encryptedCity)
                .street(encryptedStreet)
                .number(encryptedNumber)
                .build();
        UserEncoded encryptedUser = UserEncoded.builder()
                .title(encryptedTitle)
                .adress(encryptedAddress)
                .build();

        return encryptedUser;
    }

    public UserDecoded decryptUser(User user) throws Exception {
        SecretKey secretKey = getKey(user.getEmail(),user.getPhoneNumber());

        String decryptedTitle = decrypt(user.getTitle(), secretKey);
        String decryptedCountry = decrypt(user.getAddress().getCountry(), secretKey);
        String decryptedCity = decrypt(user.getAddress().getCity(), secretKey);
        String decryptedStreet = decrypt(user.getAddress().getStreet(), secretKey);
        String decryptedNumber = decrypt(user.getAddress().getNumber(), secretKey);

        Address decryptedAddress = Address.builder()
                .id(user.getAddress().getId())
                .country(decryptedCountry)
                .city(decryptedCity)
                .street(decryptedStreet)
                .number(decryptedNumber)
                .build();

        UserDecoded decryptedUser = UserDecoded.builder()
                .title(decryptedTitle)
                .adress(decryptedAddress)
                .build();

        return decryptedUser;
    }

}

