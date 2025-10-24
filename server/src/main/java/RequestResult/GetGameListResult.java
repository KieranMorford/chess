package RequestResult;

import model.GameData;

import java.util.List;

public record GetGameListResult(
        List<GameData> games
) {}
