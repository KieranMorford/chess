package requestresult;

public record NewGameRequest(
        String authToken,
        String gameName
) {}
