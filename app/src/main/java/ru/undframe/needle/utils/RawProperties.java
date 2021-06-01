package ru.undframe.needle.utils;

import android.util.Base64;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import kotlin.Unit;
import kotlin.coroutines.Continuation;

public class RawProperties implements Properties{

    private final Map<String, String> properties = new HashMap<>();


    public RawProperties(InputStream inputStream) {



        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bReader.readLine()) != null) {

                String decodeString = new String(Base64.decode(line.getBytes(),Base64.DEFAULT));
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
    public String getValue(String key) {
        return properties.containsKey(key)?properties.get(key):null;
    }


    @Override
    public String toString() {
        return "RawProperties{" +
                "properties=" + properties +
                '}';
    }

    @Nullable
    @Override
    public Object save(@NotNull Continuation<? super Unit> $completion) throws IOException {
        return null;
    }
}
