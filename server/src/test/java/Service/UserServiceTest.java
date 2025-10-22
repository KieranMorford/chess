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
        assertEquals("link", userService.register(regReq).username());
    }

    @Test
    void registerNegativeTest() throws AlreadyTakenException, BadRequestException {}

    @Test
    void clear() {
    }
}