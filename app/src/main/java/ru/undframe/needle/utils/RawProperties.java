package ru.undframe.needle.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RawProperties implements Properties{

    private final Map<String, String> properties = new HashMap<>();


    public RawProperties(InputStream inputStream) {



        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bReader.readLine()) != null) {

                Base64.Decoder decoder = Base64.getDecoder();
                String decodeString = new String(decoder.decode(line.getBytes()));
                String[] property = decodeString.split("=", 2);
                if (property.length == 2)
                    properties.put(property[0], property[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setProperties(String key, String value) {

    }

    @Override
    public Optional<String> getValue(String key) {
        return Optional.ofNullable(properties.getOrDefault(key,null));
    }

    @Override
    public void save() throws IOException {
    }

    @Override
    public String toString() {
        return "RawProperties{" +
                "properties=" + properties +
                '}';
    }
}
