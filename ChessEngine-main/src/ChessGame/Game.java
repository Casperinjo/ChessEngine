package src.ChessGame;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import src.PieceUtil.*;
import src.PieceUtil.Piece.ChessPiece;
import src.ChessEngine.AI;


public class Game extends JPanel implements Runnable {

    public boolean currentColor;

    public long wPawns = 0x000000000000ff00L;
    public long bPawns = 0x00ff000000000000L;
    public long wKnights = 0x000000000000042L;
    public long bKnights = 0x4200000000000000L;
    public long wBishops = 0x000000000000024L;
    public long bBishops = 0x2400000000000000L;
    public long wRooks = 0x000000000000081L;
    public long bRooks = 0x8100000000000000L;
    public long wQueens = 0x0000000000000008L;
    public long bQueens = 0x0800000000000000L;
    public long wKing = 0x0000000000000010L;
    public long bKing = 0x1000000000000000L;

    public long whitePieces = wPawns | wKnights | wBishops | wRooks | wQueens | wKing;
    public long blackPieces = bPawns | bKnights | bBishops | bRooks | bQueens | bKing;

    private static final int HEIGHT = 1100;
    private static final int WIDTH = 800;
    private final int FPS = 60;
    Thread thread;

    private ChessBoard board;
    private Mouse mouse;
    public BitBoardUtil bitBoardUtil;
    private RuleUtils ruleUtils;
    private AI engine;

    public long allPieces;

    public ArrayList<Piece> pieces = new ArrayList<>();
    public Piece activeP, checkingP;
    public int prevCol = -1;
    public int prevRow = -1;

    public Game() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        mouse = new Mouse();
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        currentColor = true;

        board = new ChessBoard();
        bitBoardUtil = new BitBoardUtil(this);
        this.ruleUtils = new RuleUtils(this);
        this.engine = new AI();

        setPieces();

    }

    public void startGame() {
        thread = new Thread(this);
        thread.start();
    }

    // Getters
    public synchronized ArrayList<Piece> getPieces() {
        return pieces;
    }

    public synchronized long getBitBoardWhite() {
        return wPawns | wKnights | wBishops | wRooks | wQueens | wKing;
    }

    public synchronized long getBitBoardBlack() {
        return bPawns | bKnights | bBishops | bRooks | bQueens | bKing;
    }

    public synchronized long getBitBoardAllPieces() {
        return allPieces;
    }

    public King getCurrentKing() {
        King king = null;
        for (Piece piece : pieces) {
            if (currentColor == piece.isWhite() && piece.getPiece() == ChessPiece.KING) {
                king = (King) piece;
            }
        }
        return king;
    }

    public King getOpposingKing() {
        King king = null;
        for (Piece piece : pieces) {
            if (currentColor != piece.isWhite() && piece.getPiece() == ChessPiece.KING) {
                king = (King) piece;
            }
        }
        return king;
    }

    public long getPiecePosition(Piece piece) {
        int index = piece.row * 8 + piece.col; // Assuming 0-based indexing
        return 1L << index; // Returns a bitboard with a single bit set at the piece's position
    }

    public void setPieces() {
        addPiecesFromBitBoard(wPawns, true, ChessPiece.PAWN);
        addPiecesFromBitBoard(bPawns, false, ChessPiece.PAWN);
        addPiecesFromBitBoard(wKnights, true, ChessPiece.KNIGHT);
        addPiecesFromBitBoard(bKnights, false, ChessPiece.KNIGHT);
        addPiecesFromBitBoard(wBishops, true, ChessPiece.BISHOP);
        addPiecesFromBitBoard(bBishops, false, ChessPiece.BISHOP);
        addPiecesFromBitBoard(wRooks, true, ChessPiece.ROOK);
        addPiecesFromBitBoard(bRooks, false, ChessPiece.ROOK);
        addPiecesFromBitBoard(wQueens, true, ChessPiece.QUEEN);
        addPiecesFromBitBoard(bQueens, false, ChessPiece.QUEEN);
        addPiecesFromBitBoard(wKing, true, ChessPiece.KING);
        addPiecesFromBitBoard(bKing, false, ChessPiece.KING);
    }

    public void addPiecesFromBitBoard(long bitBoard, boolean isWhite, ChessPiece type) {

        for (int i = 0; i < 64; i++) {
            if (bitBoardUtil.isPieceOnSquare(bitBoard, i)) {

                int col = i % 8;
                int row = i / 8;
                pieces.add(getPieceObject(type, isWhite, row, col));
            }

        }
    }

    public void setBitBoardForPieces() {

        for (Piece piece : pieces) {
            long position = 1L << (piece.row * 8 + piece.col); // Calculate the position bitboard
            if (piece.isWhite()) {
                whitePieces |= position; // Add to white pieces bitboard
            } else {
                blackPieces |= position; // Add to black pieces bitboard
            }
        }

        allPieces = whitePieces | blackPieces; // Combine both to form the allPieces bitboard
    }

    public Piece getPieceObject(ChessPiece type, boolean isWhite, int row, int col) {
        switch (type) {
            case PAWN:
                return new Pawn(type, isWhite, row, col, this);
            case BISHOP:
                return new Bishop(type, isWhite, row, col, this);
            case KNIGHT:
                return new Knight(type, isWhite, row, col, this);
            case ROOK:
                return new Rook(type, isWhite, row, col, this);
            case QUEEN:
                return new Queen(type, isWhite, row, col, this);
            case KING:
                return new King(type, isWhite, row, col, this);

            default:
                break;
        }
        return null;
    }

    /**
     * Remove a position from a bitboard
     * 
     * @param bitBoard Current state of the game
     * @param position The position that needs to be removed
     * @return New state of the game
     */
    public static long removePiece(long bitBoard, long position) {
        long mask = ~(1L << position);
        return bitBoard & mask;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (thread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }

    }

    public void update() {
        if (currentColor == false) {
            Move move = engine.findBestMove(this, ruleUtils, bitBoardUtil);
            int startX = move.getStartX();
            int startY = move.getStartY();
            int endX = move.getEndX();
            int endY = move.getEndY();

            activeP = move.getMovedPiece();

            prevCol = startX;
            prevRow = startY;
            activeP.col = endX;
            activeP.row = endY;
           
            movePiece();

            if (checkingP != null && ruleUtils.isCheckMate()) {
                System.exit(0);
            } else if (checkingP == null && !ruleUtils.kingCanMove(getCurrentKing())) {
                System.out.println("STALEMATE");
            }
            activeP = null;

        }
        else if (mouse.pressed) {

            if (activeP == null) {
                activeP = findCorrectPiece();
                if (activeP != null) {
                    prevCol = activeP.col;
                    prevRow = activeP.row;
                }
            } else {
                simulate();
            }
        } else if (mouse.pressed == false && activeP != null) {
            movePiece();
            if (checkingP != null && ruleUtils.isCheckMate()) {
                System.exit(0);
            } else if (checkingP == null && !ruleUtils.kingCanMove(getCurrentKing())) {
                System.out.println("STALEMATE");
            }
            activeP = null;
        }
    }

    

    public Piece findPieceAt(int col, int row) {
        for (Piece piece : pieces) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public void simulate() {

        activeP.setPos(mouse.getX() - ChessBoard.SQUARE_SIZE / 2, mouse.getY() - ChessBoard.SQUARE_SIZE / 2);
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        board.draw(g2);

        for (Piece piece : pieces) {
            piece.drawPiece(g2);
        }

        if (activeP != null) {
            g2.setColor(Color.WHITE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.fillRect(activeP.col * ChessBoard.SQUARE_SIZE, (7 - activeP.row) * ChessBoard.SQUARE_SIZE,
                    ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE);

            activeP.drawPiece(g2);

        }

    }

    public void movePiece() {
        long legalMovesForPiece = getLegalMovesFromPiece(activeP, false);
        if (Math.abs(prevCol - activeP.col) == 2 && activeP.getPiece() == ChessPiece.KING
                && ruleUtils.isCastlingMove()) {
            boolean kingside = prevCol < activeP.col;

            ((King) activeP).castle(kingside, this);
            switchTeams();
        } else if (bitBoardUtil.isLegal(legalMovesForPiece, activeP.col, activeP.row)
                && simulateIfValidMove(activeP.col, activeP.row , null)) {

            activeP.setPos(activeP.col * ChessBoard.SQUARE_SIZE, (7 - activeP.row) * ChessBoard.SQUARE_SIZE);

            if (activeP.getPiece() == ChessPiece.PAWN && Math.abs(activeP.row - prevRow) == 2) {
                Pawn pawn = (Pawn) activeP;
                pawn.hasDoubleMoved = true;
            }
            int prevSquare = prevRow * 8 + prevCol;
            int toSquare = activeP.row * 8 + activeP.col;

            bitBoardUtil.changeBitBoardAfterMove(activeP.getPiece(), activeP.isWhite(), prevSquare, toSquare);

            if (ruleUtils.isCheckingKing()) {
                checkingP = activeP;
            }
            switchTeams();

        } else {
            if (ruleUtils.isEnPassant()) {
                ruleUtils.captureWithEnPassant();
                activeP.setPos(activeP.col * ChessBoard.SQUARE_SIZE, (7 - activeP.row) * ChessBoard.SQUARE_SIZE);
                switchTeams();
                return;
            }
            activeP.col = prevCol;
            activeP.row = prevRow;
            activeP.setPos(activeP.col * ChessBoard.SQUARE_SIZE, (7 - activeP.row) * ChessBoard.SQUARE_SIZE);

            prevCol = -1;
            prevRow = -1;
        }

    }

    public boolean simulateIfValidMove(int col, int row , Piece piece) {
        if (piece != null) {
            activeP = piece;
            prevCol = piece.col;
            prevRow = piece.row;
        }
        long simBitBoard = currentColor == true ? getBitBoardWhite() : getBitBoardBlack();
        long newPos = bitBoardUtil.getBitBoardFromPos(col, row);

        simBitBoard &= ~bitBoardUtil.getBitBoardFromPos(prevCol, prevRow);
        simBitBoard |= newPos;

        Piece pinningPiece = ruleUtils.getPinningPiece(simBitBoard);
        if (pinningPiece != null) {
            if (activeP.col == pinningPiece.col && activeP.row == pinningPiece.row) {
                return true;
            }
            return false;
        }
        if (checkingP == null && activeP.getPiece() != ChessPiece.KING) {

            return true;
        } else if (checkingP != null) {

            Piece simCheckingPiece = getPieceObject(checkingP.getPiece(), checkingP.isWhite(), checkingP.row,
                    checkingP.col);

            if (activeP.isWhite()) {
                simCheckingPiece.calculateLegalMoves(bitBoardUtil.getBitBoardFromPos(checkingP.col, checkingP.row),
                        simBitBoard, getBitBoardBlack());
            } else {
                simCheckingPiece.calculateLegalMoves(bitBoardUtil.getBitBoardFromPos(checkingP.col, checkingP.row),
                        getBitBoardWhite(), simBitBoard);
            }
            King currentKing = getCurrentKing();
            if (simCheckingPiece.canMove(currentKing.col, currentKing.row)) {
                return false;
            }
            checkingP = null;
        }
        if (piece != null) {
            
            activeP = null;
            prevCol = -1;
            prevRow = -1;
        }
        return true;

    }

    public boolean simulateIfValidCastleMove() {
        Piece simKing = getPieceObject(activeP.getPiece(), activeP.isWhite(), prevRow, prevCol);
        simKing.legalMoves = activeP.legalMoves;
        if (simKing.canMove(prevCol + 1, prevRow)) {
            long simBitBoard = currentColor == true ? getBitBoardWhite() : getBitBoardBlack();
            long newPos = bitBoardUtil.getBitBoardFromPos(prevCol + 1, prevRow);

            simBitBoard = ~bitBoardUtil.getBitBoardFromPos(prevCol, prevRow);
            simBitBoard &= newPos;
            if (activeP.isWhite()) {

                simKing.calculateLegalMoves(newPos, simBitBoard, squaresAttackedByOpposingTeam(simKing.isWhite()));
                if (simKing.canMove(prevCol + 2, prevRow)) {
                    return true;
                }
            }
            if (!activeP.isWhite()) {

                simKing.calculateLegalMoves(newPos, simBitBoard, squaresAttackedByOpposingTeam(simKing.isWhite()));
                if (simKing.canMove(prevCol + 2, prevRow)) {
                    return true;
                }
            }

        }
        return false;
    }

    public long getLegalMovesFromPiece(Piece piece, boolean fromEngine) {
        if(!fromEngine){

            if (piece.getPiece() == ChessPiece.KING && currentColor == true) {
                piece.calculateLegalMoves(bitBoardUtil.getBitBoardFromPos(prevCol, prevRow), getBitBoardWhite(),
                        squaresAttackedByOpposingTeam(currentColor));
            } else if (piece.getPiece() == ChessPiece.KING && currentColor == false) {
                piece.calculateLegalMoves(bitBoardUtil.getBitBoardFromPos(prevCol, prevRow), getBitBoardBlack(),
                        squaresAttackedByOpposingTeam(currentColor));
            } else {
                piece.calculateLegalMoves(bitBoardUtil.getBitBoardFromPos(prevCol, prevRow), getBitBoardWhite(),
                        getBitBoardBlack());
            }
        }
        else{
            if (piece.getPiece() == ChessPiece.KING && currentColor == false) {
                piece.calculateLegalMoves(bitBoardUtil.getBitBoardFromPos(piece.col, piece.row), getBitBoardBlack(),
                        squaresAttackedByOpposingTeam(currentColor));
            } else {
                piece.calculateLegalMoves(bitBoardUtil.getBitBoardFromPos(piece.col, piece.row), getBitBoardWhite(),
                        getBitBoardBlack());
            }
        }
        return piece.legalMoves;
    }

    public long squaresAttackedByOpposingTeam(boolean team) {
        long attackedSquares = 0L;
        for (Piece piece : pieces) {
            if (piece.isWhite() != team && piece.getPiece() != ChessPiece.KING) {

                piece.calculateLegalMoves(bitBoardUtil.getBitBoardFromPos(piece.col, piece.row), getBitBoardWhite(),
                        getBitBoardBlack());

                attackedSquares |= piece.attackingMoves;
            }
        }

        return attackedSquares;
    }

    public Piece findCorrectPiece() {

        for (Piece piece : pieces) {
            if (currentColor == piece.isWhite() && piece.col == mouse.getX() / ChessBoard.SQUARE_SIZE
                    && piece.row == 7 - (mouse.getY() / ChessBoard.SQUARE_SIZE)) {
                return piece;
            }
        }
        return null;
    }

    public Piece getPiece(int piecePosition) {
        for (Piece piece : pieces) {
            if (piece.col == piecePosition % 8 && piece.row == piecePosition / 8) {
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) {
        // Update board state with the move
        Piece piece = getPiece(move.getStartX() + move.getStartY() * 8);
        piece = move.getMovedPiece();

        switchTeams();
    }

    public void undoMove(Move move) {
        // Revert board state to before the move
        Piece piece = getPiece(move.getEndX() + move.getEndY() * 8);
        piece = move.getMovedPiece();

        switchTeams();

    }

    public boolean switchTeams() {
        currentColor = !currentColor;
        ruleUtils.removeDoubleMovedForCurrentColor();
        return currentColor;
    }

    public boolean getCurrentColor() {
        return currentColor;
    }
}
