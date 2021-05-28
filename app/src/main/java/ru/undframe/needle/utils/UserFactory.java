package ru.undframe.needle.utils;

import android.util.Base64;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import ru.undframe.needle.encryption.SimpleCipher;
import ru.undframe.needle.model.User;
import ru.undframe.needle.tasks.AuthUserTask;
import ru.undframe.needle.tasks.RefreshTokenTask;


public class UserFactory {

    private final User currentUser;

    private UserFactory() {
        currentUser = User.Companion.getInstance();
    }

    private static UserFactory userFactory;

    public static UserFactory getInstance() {
        if (userFactory == null) userFactory = new UserFactory();
        return userFactory;
    }




    public void authCurrentUser(String user, String password, boolean savePassword, NConsumer<User> action) {
        try {
            SecretKeySpec aesKey = new SecretKeySpec(Base64.decode(SimpleCipher.PASSWORD_CIPHER_KEY.getBytes(),Base64.DEFAULT), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(password.getBytes());
            String s = DatatypeConverter.printBase64Binary(encrypted);

            new AuthUserTask(user, s, u -> {
                setCurrentUser(u);
                action.accept(currentUser);
                if (u.getAuthorization()) {
                    try {
                        FileProperties properties = GlobalProperties.INSTANCE.getFileProperties();
                        System.out.println("savePassword "+ properties.toString());
                        properties.setProperties("refresh_token", !savePassword ? null : new String(Base64.encode(SimpleCipher.encodePassword(u.getRefreshToken().getBytes()),Base64.DEFAULT)));
                        properties.setProperties("user_id", !savePassword ? null : Long.valueOf(u.getId()).toString());
                        properties.setProperties("save", Boolean.toString(savePassword));
                        properties.save();
                    } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void refreshCurrentUser(NConsumer<User> action) {
        if (currentUser != null && currentUser.getRefreshToken() != null) {
            new RefreshTokenTask(currentUser.getRefreshToken(), currentUser.getId(), u -> {
                setCurrentUser(u);
                action.accept(u);
                if (u.getAuthorization()) {
                    try {
                        FileProperties properties = GlobalProperties.INSTANCE.getFileProperties();
                        String saveProperty = properties.getValue("save");
                        boolean save = Boolean.parseBoolean(saveProperty==null ?"false": saveProperty);
                        if (save) {
                            properties.setProperties("refresh_token", new String(Base64.encode(SimpleCipher.encodePassword(u.getRefreshToken().getBytes()),Base64.DEFAULT)));
                            properties.setProperties("user_id", Long.valueOf(u.getId()).toString());
                            properties.setProperties("save", Boolean.toString(save));
                            properties.save();
                        }
                    } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }).execute();
        }
    }

    public void refreshSavedUser(NConsumer<User> action) {
        FileProperties properties = GlobalProperties.INSTANCE.getFileProperties();
        String refreshTokenOptional = properties.getValue("refresh_token");
        String userIdOptional = properties.getValue("user_id");

        System.out.println("AUTH: "+refreshTokenOptional+" : "+ userIdOptional);

        if (refreshTokenOptional!=null && userIdOptional!=null) {
            try {
                String refreshToken = refreshTokenOptional;
                long userId = Long.parseLong(userIdOptional);

                refreshToken = new String(SimpleCipher.decodePassword(Base64.decode(refreshToken.getBytes(),Base64.DEFAULT)));
                new RefreshTokenTask(refreshToken, userId, u -> {
                    setCurrentUser(u);
                    action.accept(u);
                    if (u.getAuthorization()) {
                        try {
                            properties.setProperties("refresh_token", new String(Base64.encode(SimpleCipher.encodePassword(u.getRefreshToken().getBytes()),Base64.DEFAULT)));
                            properties.setProperties("user_id", Long.valueOf(u.getId()).toString());
                            String saveProperty = properties.getValue("save");
                            properties.setProperties("save", saveProperty==null?"false":saveProperty);
                            properties.save();
                        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
                            e.printStackTrace();
                        }
                    }
                }).execute();
            } catch (BadPaddingException | IllegalBlockSizeException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void exit() {
        setCurrentUser(User.Companion.getInstance());
        FileProperties properties = GlobalProperties.INSTANCE.getFileProperties();
        try {
            properties.remove("refresh_token");
            properties.remove("user_id");
            properties.remove("save");

            properties.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCurrentUser(User user) {
        this.currentUser.fillData(user);
    }

}
