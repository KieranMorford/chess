package server;

import Exceptions.AlreadyTakenException;
import Exceptions.BadRequestException;
import Exceptions.UnauthorizedException;
import RequestResult.LoginRequest;
import RequestResult.RegisterRequest;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.UserService;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/db", this::delete);
        var memDA = new MemoryDataAccess();
        userService = new UserService(memDA);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var regReq = serializer.fromJson(reqJson, RegisterRequest.class);

            var registerResult = userService.register(regReq);

            ctx.result(serializer.toJson(registerResult));
        } catch (BadRequestException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(400).result(serializer.toJson(exJson));
        } catch (AlreadyTakenException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(403).result(serializer.toJson(exJson));
        }
    }

    private void login(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var logReq = serializer.fromJson(reqJson, LoginRequest.class);

            var loginResult = userService.login(logReq);

            ctx.result(serializer.toJson(loginResult));
        } catch (BadRequestException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(400).result(serializer.toJson(exJson));
        } catch (UnauthorizedException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(401).result(serializer.toJson(exJson));
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
