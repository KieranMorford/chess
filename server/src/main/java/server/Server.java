package server;

import Exceptions.AlreadyTakenException;
import Exceptions.BadRequestException;
import RequestResult.RegisterRequest;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .delete("/db", this::delete);
        var memDA = new MemoryDataAccess();
        userService = new UserService(memDA);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var regReq = serializer.fromJson(reqJson, RegisterRequest.class);

            var registerResult = userService.register(regReq);

            ctx.result(serializer.toJson(registerResult));
        } catch (AlreadyTakenException ex) {
//            var msg = String.format("\"message\": \"Error: %s\"", ex.getMessage());
            ctx.status(403);
        } catch (BadRequestException ex) {
            var msg = String.format("\"message\": \"Error: %s\"", ex.getMessage());
            ctx.status(400).result(msg);
        }
    }

    private void delete(Context ctx) {
        userService.clear();
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
