package ru.undframe.needle.utils;

public class ResponseStatus {

    public static final int NOT_AUTHORIZED = -1;
    public static final int SUCCESSFUL_AUTHORIZATION = 1;
    public static final int ERROR = 0;
    public static final int INVALID_PASSWORD = 2;
    public static final int INVALID_TOKEN = 3;
    public static final int EXPIRED_TOKEN = 4;
    public static final int INVALID_INPUT_DATA = 5;
    public static final int INVALID_INPUT_DATA_JSON = 6;
    public static final int SERVICE_NOT_SUPPORT = 7;
    public static final int LOGOUT_SUCCESSFUL = 8;
    public static final int TOKEN_IS_ALIVE = 9;
    public static final int TOKEN_NOT_ALIVE = 10;

}
