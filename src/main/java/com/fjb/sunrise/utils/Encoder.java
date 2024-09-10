package com.fjb.sunrise.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Encoder {
    @Value("${default.encode-key}")
    private String encodeKey;

    @Value("${default.type-encode}")
    private String typeEncode;

    private SecretKeySpec secretKeySpec() {
        return new SecretKeySpec(encodeKey.getBytes(StandardCharsets.UTF_8), typeEncode);
    }

    public String encode(String string) throws Exception {
        Cipher cipher = Cipher.getInstance(typeEncode);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec());

        byte[] encryptedData = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder().encodeToString(encryptedData);
    }

    public String decode(String string) throws Exception {
        Cipher cipher = Cipher.getInstance(typeEncode);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec());

        string = string.replace('-', '+').replace('_', '/');

        byte[] decodeDate = Base64.getDecoder().decode(string);
        byte[] decryptedData = cipher.doFinal(decodeDate);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
