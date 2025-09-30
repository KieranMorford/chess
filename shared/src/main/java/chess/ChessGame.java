package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor teamTurn;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var tempPiece = board.getPiece(startPosition);
        var moves = tempPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> invalidMoves = new HashSet<ChessMove>();
        for (ChessMove move : moves) {
            var tempBoard = board.clone();
            board.addPiece(move.getEndPosition(), tempPiece);
            board.removePiece(startPosition);
            if (isInCheck(tempPiece.getTeamColor())) {
                invalidMoves.add(move);
            }
            board = tempBoard.clone();
        }
        for (ChessMove move : invalidMoves) {
            moves.remove(move);
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    private ChessPosition findKing(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition tempPosition = new ChessPosition(i, j);
                if (board.getPiece(tempPosition) != null) {
                    ChessPiece tempPiece = board.getPiece(tempPosition);
                    if (tempPiece.getTeamColor() == teamColor && tempPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosition = tempPosition;
                    }
                }
            }
        }
        return kingPosition;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        TeamColor color = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE) {
            color = TeamColor.BLACK;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition tempPosition = new ChessPosition(i, j);
                if (board.getPiece(tempPosition) != null) {
                    ChessPiece tempPiece = board.getPiece(tempPosition);
                    if (tempPiece.getTeamColor() == color) {
                        Collection<ChessMove> moves = tempPiece.pieceMoves(board, tempPosition);
                        for (ChessMove move : moves) {
                            if (move.getEndPosition().equals(kingPosition)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        var kingPosition = findKing(teamColor);
        TeamColor color = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE) {
            color = TeamColor.BLACK;
        }
        var kingMoves = validMoves(kingPosition);
        if (kingMoves.isEmpty()) {
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition tempPosition = new ChessPosition(i, j);
                    if (board.getPiece(tempPosition) != null) {
                        ChessPiece tempPiece = board.getPiece(tempPosition);
                        if (tempPiece.getTeamColor() == color) {
                            Collection<ChessMove> moves = tempPiece.pieceMoves(board, tempPosition);
                            for (ChessMove move : moves) {
                                if (move.getEndPosition().equals(kingPosition)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        var kingPosition = findKing(teamColor);
        TeamColor color = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE) {
            color = TeamColor.BLACK;
        }
        var kingMoves = validMoves(kingPosition);
        if (kingMoves.isEmpty()) {
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition tempPosition = new ChessPosition(i, j);
                    if (board.getPiece(tempPosition) != null) {
                        ChessPiece tempPiece = board.getPiece(tempPosition);
                        if (tempPiece.getTeamColor() == color) {
                            Collection<ChessMove> moves = tempPiece.pieceMoves(board, tempPosition);
                            for (ChessMove move : moves) {
                                if (move.getEndPosition().equals(kingPosition)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition tempPosition = new ChessPosition(i, j);
                if (board.getPiece(tempPosition) != null) {
                    ChessPiece tempPiece = board.getPiece(tempPosition);
                    if (tempPiece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = tempPiece.pieceMoves(board, tempPosition);
                        if (!moves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                '}';
    }
}
