package ru.undframe.needle.utils;

import java.io.IOException;
import java.util.Optional;

public interface Properties {
    void setProperties(String key, String value);

    Optional<String> getValue(String key);

    void save() throws IOException;
}
