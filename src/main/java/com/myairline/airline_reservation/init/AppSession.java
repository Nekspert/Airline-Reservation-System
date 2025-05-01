package com.myairline.airline_reservation.init;

import com.myairline.airline_reservation.model.user.User;

public class AppSession {
    private static final AppSession INST = new AppSession();
    private User currentUser;

    private AppSession() {
    }

    public static AppSession get() {
        return INST;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
