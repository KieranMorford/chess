package serverfacade;

import exceptions.RequestException;
import exceptions.ResponseException;
import com.google.gson.Gson;
import requestresult.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public RegisterResult register(RegisterRequest regReq) throws Exception {
        var request = buildRequestB("POST", "/user", regReq);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginReq) throws Exception {
        var request = buildRequestB("POST", "/session", loginReq);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutReq) throws Exception {
        var request = buildRequestH("DELETE", "/session", logoutReq.authToken());
        var response = sendRequest(request);
        return handleResponse(response, LogoutResult.class);
    }

    public NewGameResult createGame(NewGameRequest newGameReq) throws Exception {
        var request = buildRequestHB("POST", "/game", newGameReq.authToken(), newGameReq);
        var response = sendRequest(request);
        return handleResponse(response, NewGameResult.class);
    }

    public GetGameListResult listGames(String authToken) throws Exception {
        var request = buildRequestH("GET", "/game", authToken);
        var response = sendRequest(request);
        return handleResponse(response, GetGameListResult.class);
    }

    public JoinGameResult playGame(JoinGameRequest joinGameReq) throws Exception {
        var request = buildRequestHB("PUT", "/game", joinGameReq.authToken(), joinGameReq);
        var response = sendRequest(request);
        return handleResponse(response, JoinGameResult.class);
    }

    public JoinGameResult observeGame(JoinGameRequest joinGameReq) throws Exception {
        var request = buildRequestHB("GET", "/game/watch", joinGameReq.authToken(), joinGameReq);
        var response = sendRequest(request);
        return handleResponse(response, JoinGameResult.class);
    }

    private HttpRequest buildRequestH(String method, String path, String header) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, HttpRequest.BodyPublishers.noBody());
        if (header != null) {
            request.setHeader("authorization", header);
        }
        return request.build();
    }

    private HttpRequest buildRequestB(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest buildRequestHB(String method, String path, String header, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (header != null) {
            request.setHeader("authorization", header);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws RequestException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            throw new RequestException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        String body = response.body();
        if (!isSuccessful(status)) {
            throw new ResponseException("HTTP " + status + ": " + body);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
