package src.PieceUtil;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.ChessGame.Game;

public class Knight extends Piece {

    

    public Knight(ChessPiece piece, boolean isWhite, int row, int col, Game game) {
        super(piece, isWhite, row, col, game);
    }

    public void setImage() {

        try {

            if (isWhite) {
                File file = new File(
                        "./Images/piece_0_3.png");
                this.image = ImageIO.read(file);
            } else {
                File file = new File(
                        "./Images/piece_1_3.png");
                this.image = ImageIO.read(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateLegalMoves(long position, long whitePieces, long blackPieces) {
        long moves = 0L;
        long occupancy = isWhite == true ? whitePieces : blackPieces;
        long emptySquares = ~occupancy;

        final long notABFile = ~(FILE_A | FILE_B);
        final long notHGFile = ~(FILE_G | FILE_H);

        long moveNoNoWe = (position << 15) & ~FILE_H & emptySquares;
        long guardNoNoWe = (position << 15) & ~FILE_H & occupancy;

        long moveNoWeWe = (position << 6) & notHGFile & emptySquares;
        long guardNoWeWe = (position << 6) & notHGFile & occupancy;
        // correct shift and mask
        long moveNoNoEa = (position << 17) & ~FILE_A & emptySquares;
        long guardNoNoEa = (position << 17) & ~FILE_A & occupancy;
        // correct shift and mask
        long moveNoEaEa = (position << 10) & notABFile & emptySquares;
        long guardNoEaEa = (position << 10) & notABFile & occupancy;

        long moveSoEaEa = (position >> 10) & notHGFile & emptySquares;
        long guardSoEaEa = (position >> 10) & notHGFile & occupancy;

        long moveSoSoEa = (position >> 15) & ~FILE_A & emptySquares;
        long guardSoSoEa = (position >> 15) & ~FILE_A & occupancy;

        long moveSoSoWe = (position >> 17) & ~FILE_H & emptySquares;
        long guardSoSoWe = (position >> 17) & ~FILE_H & occupancy;

        long moveSoWeWe = (position >> 6) & notABFile & emptySquares;
        long guardSoWeWe = (position >> 6) & notABFile & occupancy;

        moves = moveNoWeWe | moveNoNoWe | moveNoNoEa | moveNoEaEa | moveSoEaEa | moveSoSoEa | moveSoSoWe | moveSoWeWe;
        attackingMoves = moves | guardNoWeWe | guardNoNoWe | guardNoNoEa | guardNoEaEa | guardSoEaEa | guardSoSoEa | guardSoSoWe | guardSoWeWe;
        // Return the final bitboard of legal moves
        legalMoves = moves;
       

    }
}