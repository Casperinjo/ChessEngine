package src.ChessEngine;

import java.util.ArrayList;
import java.util.List;

import src.ChessGame.BitBoardUtil;
import src.ChessGame.Game;
import src.ChessGame.Move;
import src.ChessGame.RuleUtils;
import src.PieceUtil.Pawn;
import src.PieceUtil.Piece;
import src.PieceUtil.Piece.ChessPiece;

public class AI {
    private static final int MAX_DEPTH = 4;

    public Move findBestMove(Game game, RuleUtils ruleUtils, BitBoardUtil bitBoardUtil) {
        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;

        for (int piecePosition = 0; piecePosition < 64; piecePosition++) {
            Piece piece = game.getPiece(piecePosition);
            if (piece != null && !piece.isWhite()) { // Only consider current player's pieces
                long legalMovesBitboard = game.getLegalMovesFromPiece(piece, true);
                List<Move> legalMoves = generateLegalMoves(game, legalMovesBitboard, piecePosition);

                for (Move move : legalMoves) {
                    boolean validMove = game.simulateIfValidMove(move.getEndX(), move.getEndY(), piece);
                    if (validMove) {

                        game.makeMove(move);
                        int boardValue = minimax(game, MAX_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false,
                                ruleUtils, legalMovesBitboard, piecePosition);
                        game.undoMove(move);

                        if (boardValue > bestValue) {
                            bestValue = boardValue;
                            bestMove = move;
                        }
                    }
                }
            }
        }

        return bestMove;
    }

    public static List<Move> generateLegalMoves(Game game, long legalMovesBitboard, int piecePosition) {
        List<Move> moves = new ArrayList<>();
        Piece movedPiece = game.getPiece(piecePosition); // Get the piece from the board

        while (legalMovesBitboard != 0) {
            long lsb = legalMovesBitboard & -legalMovesBitboard; // Isolate the least significant bit that is set to 1
            int targetPosition = Long.numberOfTrailingZeros(lsb); // Get the index of the LSB

            // Calculate start and end positions (assuming an 8x8 board for this example)
            int startX = piecePosition % 8;
            int startY = piecePosition / 8;
            int endX = targetPosition % 8;
            int endY = targetPosition / 8;

            // Create a move (assuming no capture, castling, en passant, or promotion for
            // simplicity)
            Move move = new Move(startX, startY, endX, endY, movedPiece, null, false, false, null);
            moves.add(move);

            legalMovesBitboard &= legalMovesBitboard - 1; // Clear the least significant bit
        }

        return moves;
    }

    private int minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer, RuleUtils ruleUtils,
            long legalMovesBitboard, int piecePosition) {
        if (depth == 0) {
            return Evaluator.evaluate(game);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : generateLegalMoves(game, legalMovesBitboard, piecePosition)) {
                game.makeMove(move);
                int eval = minimax(game, depth - 1, alpha, beta, false, ruleUtils, legalMovesBitboard, piecePosition);
                game.undoMove(move);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : generateLegalMoves(game, legalMovesBitboard, piecePosition)) {
                game.makeMove(move);
                int eval = minimax(game, depth - 1, alpha, beta, true, ruleUtils, legalMovesBitboard, piecePosition);
                game.undoMove(move);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

}
