package src.ChessEngine;

import src.ChessGame.Game;
import src.PieceUtil.Piece;

public class Evaluator {
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000;

    public static int evaluate(Game game) {
        int evaluation = 0;

        for (Piece piece : game.pieces) {
            if (piece.isWhite()) {
                evaluation += getPieceValue(piece);
            } else {
                evaluation -= getPieceValue(piece);
            }
        }

        return evaluation;
    }

    private static int getPieceValue(Piece piece) {
        switch (piece.getPiece()) {
            case PAWN:
                return PAWN_VALUE;
            case KNIGHT:
                return KNIGHT_VALUE;
            case BISHOP:
                return BISHOP_VALUE;
            case ROOK:
                return ROOK_VALUE;
            case QUEEN:
                return QUEEN_VALUE;
            case KING:
                return KING_VALUE;
            default:
                return 0;
        }
    }
}
