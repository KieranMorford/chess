package service;

import Exceptions.BadRequestException;
import Exceptions.UnauthorizedException;
import RequestResult.NewGameRequest;
import RequestResult.NewGameResult;
import dataaccess.DataAccess;

import java.util.concurrent.atomic.AtomicInteger;

public class GameService {
    private final DataAccess dataAccess;
    private static final AtomicInteger gameIdCounter = new AtomicInteger(1);

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    //    public GameResult getGameList(LogoutRequest gListReq) throws UnauthorizedException {
//        if (gListReq == null || dataAccess.getAuth(gListReq.authToken()) == null) {
//            throw new UnauthorizedException("Unauthorized");
//        }
//        dataAccess.
//    }

    public NewGameResult newGame(NewGameRequest nGReq) throws UnauthorizedException, BadRequestException {
        var test = nGReq.gameName();
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

    public int generateGameId() {
        return gameIdCounter.getAndIncrement();
    }
}
