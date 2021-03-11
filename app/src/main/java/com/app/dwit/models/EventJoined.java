package com.app.dwit.models;

import java.util.List;

public class EventJoined {

    List<User> usersJoined;

    public EventJoined() {
    }

    public EventJoined(List<User> usersJoined) {
        this.usersJoined = usersJoined;
    }

    public List<User> getUsersJoined() {
        return usersJoined;
    }

    public void setUsersJoined(List<User> usersJoined) {
        this.usersJoined = usersJoined;
    }
}
