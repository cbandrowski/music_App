package service;

import datab.DataBase;

import java.util.prefs.Preferences;

public class UserSession {
    private static UserSession instance;

    private static int userId; // Add user ID for database interactions
    private String email;
    private String userName;
    private String privileges;

    private String filePath;

    private UserSession(int userId, String email, String userName) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.privileges = privileges;
        saveCredentials(userId, email, userName);
    }

    private void saveCredentials(int userId, String email, String userName) {
        Preferences userPreferences = Preferences.userRoot();
        userPreferences.putInt("USER_ID", userId);
        userPreferences.put("EMAIL", email);
        userPreferences.put("USERNAME", userName);

    }

    public static synchronized UserSession getInstance(int userId, String email, String userName) {
        if (instance == null) {
            instance = new UserSession(userId, email, userName);
            // Fetch file path from database
            String localStoragePath = new DataBase().getUserStoragePath(email);
            if (localStoragePath != null) {
                instance.setLocalStoragePath(localStoragePath);
            }
        }
        return instance;
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            Preferences userPreferences = Preferences.userRoot();
            int userId = userPreferences.getInt("USER_ID", 0);
            String email = userPreferences.get("EMAIL", "");
            String userName = userPreferences.get("USERNAME", "");

            instance = new UserSession(userId, email, userName);
        }
        return instance;
    }

    public static int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }


    public void clearSession() {
        this.userId = 0;
        this.email = null;
        this.userName = null;
        this.privileges = null;

        Preferences userPreferences = Preferences.userRoot();
        userPreferences.remove("USER_ID");
        userPreferences.remove("EMAIL");
        userPreferences.remove("USERNAME");

    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }


    public void setLocalStoragePath(String localStoragePath) {
        filePath = localStoragePath;
    }

    public String getLocalStoragePath() {
        return filePath;
    }
}
