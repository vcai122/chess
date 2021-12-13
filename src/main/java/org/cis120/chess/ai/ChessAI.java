package org.cis120.chess.ai;

import org.cis120.chess.logic.Board;
import org.cis120.chess.logic.Piece;
import org.cis120.chess.logic.Tile;

import java.util.*;

/**
 * Implements the chess AI and contains all the logic for it.
 */
public class ChessAI {
    /**
     * A boolean representing if the ai is playing white or not
     */
    final boolean white;

    /**
     * The Board that the ai is playing on
     */
    private final Board board;

    /**
     * Constructor, initializes the private fields
     * @param white
     * @param board
     */
    public ChessAI(boolean white, Board board) {
        this.white = white;
        this.board = board;
    }

    /**
     * Easy mode, randomly ai randomly chooses a move to play (used for testing)
     */
    public void playTurnEasy() {
        int x = (int) (Math.random() * 8);
        int y = (int) (Math.random() * 8);
        while (true) {
            Piece piece = board.getPiece(x, y);
            if (piece != null && piece.getWhite() == white) {
                LinkedList<Tile> validMoves = piece.getValidMoves();
                if (validMoves.size() != 0) {
                    int choice = (int) (Math.random() * validMoves.size());
                    Tile target = validMoves.get(choice);
                    board.playTurn(x, y);
                    board.playTurn(target.getX(), target.getY());
                    return;
                }
            }
            x = (int) (Math.random() * 8);
            y = (int) (Math.random() * 8);
        }
    }

    /**
     * Plays a move based on the best result from the minimax algorithm with heuristic based on
     * pre-defined piece values, looking forward 3 steps. Uses another heuristic for tie breaking.
     */
    public void playTurn() {
        if (board.staleMate() || board.checkMate()) {
            return;
        }
        Piece pieceToMove = null; // the piece to be moved
        Tile target = null; // the target tile to move to

        // the variables to be used for comparing move options
        int best = Integer.MIN_VALUE; // the best result from the minimax algorithm
        int bestNumMoves = 0; // the largest number of moves achieved associated to the best value
        int bestPieceValue = Integer.MAX_VALUE; // the lowest piece value associated to best value

        LinkedList<Piece> pieces = getPieces(white);
        for (Piece piece : pieces) {
            LinkedList<Tile> possibleMoves = piece.getValidMoves();
            for (Tile nextTile : possibleMoves) {
                movePiece(piece.getX(), piece.getY(), nextTile.getX(), nextTile.getY());
                int val = minimax(2, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                int numMoves = board.calculateNumMoves(white);
                board.undo();
                if (Piece.isPawn(piece) && Math.abs(nextTile.getX() - piece.getX()) == 2) {
                    numMoves += 1;
                }
                if (val > best || (val == best && (piece.getValue() < bestPieceValue ||
                        (piece.getValue() == bestPieceValue && numMoves > bestNumMoves)))) {
                    pieceToMove = piece;
                    target = nextTile;
                    best = val;
                    bestPieceValue = piece.getValue();
                    bestNumMoves = numMoves;
                }
            }
        }
        movePiece(pieceToMove.getX(), pieceToMove.getY(), target.getX(), target.getY());

    }

    /**
     * Recursive minimax algorithm with pruning used for calculating the best possible outcome
     * after a certain number of steps. Goes through all possible moves at each step and tries to
     * at each level, depending on whose turn it is at that level, to either maximize or minimize
     * the score of the board. Uses pruning to reduce calculations.
     * @param level the level of the minimax algorithm we are at, reduces by 1 for each level, and
     *              when it reaches 0, the minimax algorithm will just calculate and return the
     *              score of the board
     * @param bestMin the largest score the AI has achieved so far
     * @param bestMax the smallest score the non-AI player has achieved so far
     * @param aiTurn a boolean representing if it is the AI's turn to make a move
     * @return the highest/lowest possible board value depending on whose turn it is
     */
    private int minimax(int level, int bestMin, int bestMax, boolean aiTurn) {
        if (board.staleMate()) {
            //checks for stalemate edge case
            return 0;
        }
        if (level == 0) {
            return board.calculateScoreDifference(white);
        }
        int ret = aiTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        LinkedList<Piece> pieces = getPieces(aiTurn == white);
        for (Piece piece : pieces) {
            LinkedList<Tile> possibleMoves = piece.getValidMoves();
            for (Tile nextTile : possibleMoves) {
                movePiece(piece.getX(), piece.getY(), nextTile.getX(), nextTile.getY());
                int val = minimax(level - 1, bestMin, bestMax, !aiTurn);
                board.undo();
                if (aiTurn) {
                    ret = Math.max(ret, val);
                    bestMin = Math.max(bestMin, val);
                } else {
                    ret = Math.min(ret, val);
                    bestMax = Math.min(bestMax, val);
                }
                if (bestMax <= bestMin) {
                    return ret;
                }
            }
        }
        //checks if it's the player's turn and the player has no moves
        return ret;
    }

    /**
     * Gets all the pieces that is associated with the input parameter
     * @param white a boolean that is true if we are getting all white pieces and false if we are
     *              getting all black pieces
     * @return a Linkedlist of all the pieces associated with the input parameter
     */
    private LinkedList<Piece> getPieces(boolean white) {
        LinkedList<Piece> ret = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            int x = white ? 7 - i : i;
            for (int j = 3; j >= 0; j--) {
                Piece piece = board.getPiece(x, j);
                if (piece != null && piece.getWhite() == white) {
                    ret.add(piece);
                }
                piece = board.getPiece(x, 7 - j);
                if (piece != null && piece.getWhite() == white) {
                    ret.add(piece);
                }
            }
        }
        return ret;
    }

    /**
     * Simulates moving the piece
     * @param x0 the x coordinate of where the piece starts
     * @param y0 the y coordinate of where the peice starts
     * @param x1 the x coordinate of where the piece is moving to
     * @param y1 the y coordinate of where the piece is moving to
     */
    private void movePiece(int x0, int y0, int x1, int y1) {
        board.playTurn(x0, y0);
        if (board.playTurn(x1, y1)) {
            board.promote(0);
        }
    }

    /**
     * @return a boolean value that is true if the ai is playing white and false otherwise
     */
    public boolean getWhite() {
        return white;
    }



}
