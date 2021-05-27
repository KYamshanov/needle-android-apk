package ru.undframe.needle.encryption;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SignatureException;
import java.util.Objects;

public class JWT {
    public static boolean isAlive(String token) {
        Objects.requireNonNull(token);

        String[] split = token.split("\\.");
        if (split.length == 3) {
            try {
                String header = split[0];
                String payload = split[1];
                String signatureExpected = split[2];
                long time = new JSONObject(new String(android.util.Base64.decode(payload, Base64.DEFAULT))).getLong("exp");
                return SimpleCipher.verifySignature(
                        (header + "." + payload).getBytes(), android.util.Base64.decode(signatureExpected.getBytes(),Base64.DEFAULT)) && (time >= (System.currentTimeMillis() / 1000L));

            } catch (SignatureException | JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }




}
