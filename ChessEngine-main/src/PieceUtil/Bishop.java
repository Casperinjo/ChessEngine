package src.PieceUtil;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.ChessGame.Game;



public class Bishop extends Piece {

    public Bishop(ChessPiece type, boolean isWhite, int row, int col, Game game) {
        super(type, isWhite, row, col, game);
    }

    public void setImage() {

        try {

            if (isWhite) {
                File file = new File(
                        "./Images/piece_0_2.png");
                this.image = ImageIO.read(file);
            } else {
                File file = new File(
                        "./Images/piece_1_2.png");
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
        moves |= calculateRayMoves(position, whitePieces, blackPieces, 9);
        moves |= calculateRayMoves(position, whitePieces, blackPieces, 7);
        moves |= calculateRayMoves(position, whitePieces, blackPieces, -9);
        moves |= calculateRayMoves(position, whitePieces, blackPieces, -7);

        legalMoves = moves;
        

    }

    public long calculateRayMoves(long position, long whitePieces, long blackPieces, int direction) {
        long moves = 0L;
        long guardingMoves = 0L;

        long potentialMoves = position;
      
        long fileBoundaryMask = (direction == 7 || direction == -9) ? ~FILE_H : ~FILE_A;

        long occupancy = whitePieces | blackPieces;
        long enemypieces = isWhite == true ? blackPieces : whitePieces;

        while (true) {
            potentialMoves = shift(potentialMoves, direction) & fileBoundaryMask;
            if (potentialMoves == 0) { // Immediately exit if no valid moves are possible
                break;
            }

            if ((potentialMoves & occupancy) != 0L) {

                if (((potentialMoves & occupancy) & enemypieces) != 0L) {
                    moves |= potentialMoves;
                } else {

                    guardingMoves |= potentialMoves;
                }
                break;
            }
            moves |= potentialMoves;
        }
        attackingMoves |= moves | guardingMoves;

        return moves;
    }

    private static long shift(long bitboard, int shiftBy) {

        return shiftBy > 0 ? (bitboard << shiftBy) : (bitboard >>> -shiftBy);

    }
}
