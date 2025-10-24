package service;

import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import requestresult.GetGameListResult;
import requestresult.JoinGameRequest;
import requestresult.NewGameRequest;
import requestresult.NewGameResult;
import chess.ChessGame;
import dataaccess.DataAccess;
import model.GameData;

import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    private final DataAccess dataAccess;
    private final AtomicInteger gameIDCounter = new AtomicInteger(1);

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GetGameListResult getGameList(String authToken) throws UnauthorizedException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        var gList = dataAccess.listGames(authToken);
        var gLRes = new GetGameListResult(gList);
        return gLRes;
    }

    public NewGameResult newGame(NewGameRequest nGReq) throws UnauthorizedException, BadRequestException {
        if (nGReq.authToken() == null || nGReq.gameName() == null || nGReq.gameName().equals("{}")) {
            throw new BadRequestException("Bad Request");
        }
        if (dataAccess.getAuth(nGReq.authToken()) == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        var gameData = dataAccess.createGame(nGReq.gameName(), generateGameId());
        var nGRes = new NewGameResult(gameData.gameID());
        return nGRes;
    }

    public void joinGame(JoinGameRequest jGReq) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        if (jGReq.authToken() == null || jGReq.playerColor() == null || jGReq.gameID() == 0 || dataAccess.getGame(jGReq.gameID()) == null) {
            throw new BadRequestException("Bad Request");
        }
        if (dataAccess.getAuth(jGReq.authToken()) == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        if (jGReq.playerColor().equals(ChessGame.TeamColor.WHITE) && dataAccess.getGame(jGReq.gameID()).whiteUsername() != null) {
            throw new AlreadyTakenException("White Already Taken");
        } else if (jGReq.playerColor().equals(ChessGame.TeamColor.BLACK) && dataAccess.getGame(jGReq.gameID()).blackUsername() != null) {
            throw new AlreadyTakenException("White Already Taken");
        }
        var oldGame = dataAccess.getGame(jGReq.gameID());
        var newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        if(jGReq.playerColor().equals(ChessGame.TeamColor.WHITE)) {
            newGame = new GameData(oldGame.gameID(), dataAccess.getAuth(jGReq.authToken()).username(), oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        } else if(jGReq.playerColor().equals(ChessGame.TeamColor.BLACK)) {
            newGame = new GameData(oldGame.gameID(),oldGame.whiteUsername(), dataAccess.getAuth(jGReq.authToken()).username(), oldGame.gameName(), oldGame.game());
        }
        dataAccess.updateGame(newGame);

    }

    public int generateGameId() {
        return gameIDCounter.getAndIncrement();
    }
}
