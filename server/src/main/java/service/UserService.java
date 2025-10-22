package service;

import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest regReq) throws Exception {
        if (dataAccess.getUser(regReq.username()) != null) {
            throw new Exception("Username Already Taken");
        }
        dataAccess.createUser(new UserData(regReq.username(), regReq.password(), regReq.email()));
        var authToken = generateAuthToken();
        var authData = new AuthData(regReq.username(), authToken);
        dataAccess.createAuth(authData);
        var regRes = new RegisterResult(regReq.username(), authToken);
        return regRes;
    }

    public void clear() {
        dataAccess.clear();
    }

    private String generateAuthToken () {
        return UUID.randomUUID().toString();
    }
}
