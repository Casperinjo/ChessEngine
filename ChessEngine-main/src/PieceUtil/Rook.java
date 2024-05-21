package src.PieceUtil;


import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.ChessGame.Game;

public class Rook extends Piece {

    public Rook(ChessPiece piece, boolean isWhite, int row, int col, Game game) {
        super(piece, isWhite, row, col, game);
    }

    public void setImage() {

        try {

            if (isWhite) {
                File file = new File(
                        "./Images/piece_0_4.png");
                this.image = ImageIO.read(file);
            } else {
                File file = new File(
                        "./Images/piece_1_4.png");
                this.image = ImageIO.read(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateLegalMoves(long position, long whitePieces, long blackPieces) {
        long moves = 0L;
        attackingMoves = 0L;
        // Calculate diagonal moves
        moves |= calculateRayMoves(position, whitePieces, blackPieces, 1);
        moves |= calculateRayMoves(position, whitePieces, blackPieces, -1);
        moves |= calculateRayMoves(position, whitePieces, blackPieces, 8);
        moves |= calculateRayMoves(position, whitePieces, blackPieces, -8);

        legalMoves = moves;

    }

    public long calculateRayMoves(long position, long whitePieces, long blackPieces, int direction) {
        long moves = 0L;
        long guardingMoves = 0L;
        long potentialMoves = position;

        long occupancy = whitePieces | blackPieces;
        long enemypieces = isWhite == true ? blackPieces : whitePieces;


        long fileBoundaryMask = direction == 1 ? ~FILE_A : ~FILE_H;
        if (direction == 8 || direction == -8) {
            fileBoundaryMask = 0xFFFFFFFFFFFFFFFFL;
        }

        while (true) {
            potentialMoves = shift(potentialMoves, direction) & fileBoundaryMask;
            if (potentialMoves == 0) {
                break; // Exit if the move is off the board
            }
            if ((potentialMoves & occupancy) != 0L) {
                if (((potentialMoves & occupancy) & enemypieces) != 0L) {
                    moves |= potentialMoves;
                } else {

                    guardingMoves |= potentialMoves;
                }
                break;
            }

            moves |= potentialMoves; // Include this square as a legal move if it's free
        }
        attackingMoves = moves | guardingMoves;
        return moves;
    }

    private static long shift(long bitboard, int shiftBy) {
        return (shiftBy > 0) ? (bitboard << shiftBy)  : (bitboard >>> -shiftBy);
    }

}