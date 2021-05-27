package ru.undframe.needle.model;

import java.io.File;

public interface ServerRepository {

    void sendFileToServer(File file);

    static ServerRepository getBaseInstance(){
        return BaseServerRepository.Companion.getInstance();
    }

}
