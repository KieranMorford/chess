package Service;

import Exceptions.AlreadyTakenException;
import Exceptions.BadRequestException;
import RequestResult.RegisterRequest;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerPositiveTest() throws AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(new MemoryDataAccess());
        var regRes = userService.register(regReq);
        assertEquals("link", regRes.username());
        assertNotNull(regRes.authToken());
    }

    @Test
    void registerRepeatTest() throws AlreadyTakenException, BadRequestException {
        RegisterRequest regReq1 = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        RegisterRequest regReq2 = new RegisterRequest("link","zelda","kord@gmail.com");
        UserService userService = new UserService(new MemoryDataAccess());
        var regRes1 = userService.register(regReq1);
        assertThrows(AlreadyTakenException.class, () -> {userService.register(regReq2);});
    }

    @Test
    void registerBadRequestTest() throws AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos",null);
        UserService userService = new UserService(new MemoryDataAccess());
        assertThrows(BadRequestException.class, () -> {userService.register(regReq);});
    }

    @Test
    void clear() {
    }
}