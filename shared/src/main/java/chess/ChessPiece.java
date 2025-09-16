package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        var moves = new HashSet<ChessMove>();
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            int x1 = 1;
            while (myPosition.getRow() + x1 <= 8 && myPosition.getColumn() + x1 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + x1, myPosition.getColumn() + x1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + x1, myPosition.getColumn() + x1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + x1, myPosition.getColumn() + x1), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + x1, myPosition.getColumn() + x1)) != null) {
                    break;
                }
                x1++;
            }
            int x2 = 1;
            while (myPosition.getRow() - x2 > 0 && myPosition.getColumn() + x2 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - x2, myPosition.getColumn() + x2)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - x2, myPosition.getColumn() + x2)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - x2, myPosition.getColumn() + x2), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - x2, myPosition.getColumn() + x2)) != null) {
                    break;
                }
                x2++;
            }
            int x3 = 1;
            while (myPosition.getRow() + x3 <= 8 && myPosition.getColumn() - x3 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + x3, myPosition.getColumn() - x3)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + x3, myPosition.getColumn() - x3)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + x3, myPosition.getColumn() - x3), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + x3, myPosition.getColumn() - x3)) != null) {
                    break;
                }
                x3++;
            }
            int x4 = 1;
            while (myPosition.getRow() - x4 > 0 && myPosition.getColumn() - x4 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - x4, myPosition.getColumn() - x4)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - x4, myPosition.getColumn() - x4)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - x4, myPosition.getColumn() - x4), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - x4, myPosition.getColumn() - x4)) != null) {
                    break;
                }
                x4++;
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null));
            }
            if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null));
            }
            if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null));
            }
            if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null));
            }
            if (myPosition.getRow() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
            }
            if (myPosition.getRow() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
            }
            if (myPosition.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), null));
            }
            if (myPosition.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), null));
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1), null));
            }
            if (myPosition.getRow() - 2 > 0 && myPosition.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1), null));
            }
            if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1), null));
            }
            if (myPosition.getRow() - 2 > 0 && myPosition.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1), null));
            }
            if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 2 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2), null));
            }
            if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() + 2 <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2), null));
            }
            if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 2 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2), null));
            }
            if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() - 2 > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2), null));
            }
        }

        return moves;
    }
}
