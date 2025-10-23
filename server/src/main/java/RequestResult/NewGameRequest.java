package RequestResult;

public record NewGameRequest(
        String authToken,
        String gameName
) {}
