package ru.undframe.needle.model;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface ServerRepository {

    void sendFileToServer(@NotNull String token, File file);

    static ServerRepository getBaseInstance(){
        return BaseServerRepository.Companion.getInstance();
    }

}
