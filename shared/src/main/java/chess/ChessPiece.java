package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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
            int x = 1;
            while (myPosition.getRow() + x <= 8 && myPosition.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() - x > 0 && myPosition.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() + x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() + x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() + x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() + x <= 8 && myPosition.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() - x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() - x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() - x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() - x > 0 && myPosition.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() - x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() - x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() - x)) != null) {
                    break;
                }
                x++;
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
        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                if (myPosition.getRow() + 1 <= 8
                        && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null) {
                    if (myPosition.getRow() + 1 == 8) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.ROOK));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
                    }
                    if (myPosition.getRow() == 2
                            && board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())) == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()), null));
                    }
                }
                if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 1 <= 8
                        && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null
                        && board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor()) {
                    if (myPosition.getRow() + 1 == 8) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), PieceType.ROOK));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null));
                    }
                }
                if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 1 > 0
                        && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null
                        && board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor()) {
                    if (myPosition.getRow() + 1 == 8) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), PieceType.ROOK));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null));
                    }
                }
            }
            if (piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                if (myPosition.getRow() - 1 > 0
                        && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null) {
                    if (myPosition.getRow() - 1 == 1) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), PieceType.ROOK));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
                    }
                    if (myPosition.getRow() == 7
                            && board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())) == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()), null));
                    }
                }
                if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() + 1 <= 8
                        && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null
                        && board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor()) {
                    if (myPosition.getRow() - 1 == 1) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), PieceType.ROOK));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null));
                    }
                }
                if (myPosition.getRow() - 1 > 0 && myPosition.getColumn() - 1 > 0
                        && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null
                        && board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor()) {
                    if (myPosition.getRow() - 1 == 1) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), PieceType.ROOK));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null));
                    }
                }
            }
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            int x = 1;
            while (myPosition.getRow() + x <= 8 && myPosition.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() + x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() - x > 0 && myPosition.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() + x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() + x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() + x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() + x <= 8 && myPosition.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() - x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() - x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn() - x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() - x > 0 && myPosition.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() - x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() - x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn() - x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() + x <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn())) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn())).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + x, myPosition.getColumn()), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn())) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() - x > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn())) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn())).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - x, myPosition.getColumn()), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn())) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - x)) != null) {
                    break;
                }
                x++;
            }
        }
        else if (piece.getPieceType() == PieceType.ROOK) {
            int x = 1;
            while (myPosition.getRow() + x <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn())) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn())).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + x, myPosition.getColumn()), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + x, myPosition.getColumn())) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getRow() - x > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn())) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn())).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - x, myPosition.getColumn()), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - x, myPosition.getColumn())) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + x)) != null) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPosition.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - x)) == null
                    || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - x)).getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - x)) != null) {
                    break;
                }
                x++;
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        return "[" + pieceColor +
                ", " + type +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
