package service;

import dataaccess.DataAccess;
import datamodel.*;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register (UserData user) {
        if (dataAccess) {

        }
        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateAuthToken());
        return authData;
    }

    private String generateAuthToken () {
//        return UUID.randomUUID().toString();
        return "xyz";
    }
}
