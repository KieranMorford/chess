package service;

import Exceptions.AlreadyTakenException;
import Exceptions.BadRequestException;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
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

    public RegisterResult register(RegisterRequest regReq) throws AlreadyTakenException, BadRequestException {
        if (dataAccess.getUser(regReq.username()) != null) {
            throw new AlreadyTakenException("Username Already Taken");
        }
        if (regReq.username() == null || regReq.password() == null || regReq.email() == null) {
            throw new BadRequestException("Bad Request");
        }
        dataAccess.createUser(new UserData(regReq.username(), regReq.password(), regReq.email()));
        var authToken = generateAuthToken();
        var authData = new AuthData(regReq.username(), authToken);
        dataAccess.createAuth(authData);
        var regRes = new RegisterResult(regReq.username(), authToken);
        return regRes;
    }

    public LoginResult login(LoginRequest logReq) throws AlreadyTakenException, BadRequestException {
        if (logReq.username() == null || logReq.password() == null) {
            throw new BadRequestException("Bad Request");
        }
        if (!dataAccess.getUser(logReq.username()).password().equals(logReq.password())) {
            throw new UnauthorizedException("Unauthorized");
        }
        var authToken = generateAuthToken();
        var authData = new AuthData(logReq.username(), authToken);
        dataAccess.createAuth(authData);
        var logRes = new LoginResult(logReq.username(), authToken);
        return logRes;
    }

    public void clear() {
        dataAccess.clear();
    }

    private String generateAuthToken () {
        return UUID.randomUUID().toString();
    }
}
