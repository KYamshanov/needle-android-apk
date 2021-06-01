package ru.undframe.needle.utils;

import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileProperties implements Properties{

    private Map<String, String> properties;
    private File file;

    public FileProperties(File file) {

        Map<String, String> propertyMap = new HashMap<>();
        this.file = file;
        this.properties = propertyMap;

        try {

            if (!file.exists())
                Files.createFile(file.toPath());


            try {
                List<String> strings = Files.readAllLines(file.toPath(), Charset.defaultCharset());
                for (String string : strings) {

                    String decodeString = new String(Base64.decode(string.getBytes(),Base64.DEFAULT));
                    String[] property = decodeString.split("=", 2);
                    if (property.length == 2)
                        propertyMap.put(property[0], property[1]);
                }
            }catch (IllegalArgumentException e){
                e.printStackTrace();
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void setProperties(String key, String value) {
        if (value != null)
            properties.put(key, value);
        else
            properties.remove(key);
    }

    @Override
    public String getValue(@NotNull String key) {
        return properties.containsKey("key")? properties.get(key):null;
    }

    @Override
    public void save() throws IOException {
        FileWriter fwOb = new FileWriter(file, false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            pwOb.println(new String(Base64.encode((entry.getKey() + "=" + entry.getValue()).getBytes(),Base64.DEFAULT)));
        }
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }

    @Override
    public String toString() {
        return "FileProperties{" +
                "properties=" + properties +
                '}';
    }

    public void remove(String key) {
        properties.remove(key);
    }
}
