package ru.undframe.needle.view;

import android.content.Context;

public interface BaseView {

    void openAuthorizationView();
    Context getContext();
    void openNoAccessActivity();
    void closeActivity();

}
