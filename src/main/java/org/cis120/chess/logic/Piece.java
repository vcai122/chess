package org.cis120.chess.logic;

import java.util.*;


/**
 * Represents a chess piece for the game. The {@code Piece} abstract class has a concrete
 * class for each of the possible chess pieces.
 */
public abstract class Piece {

    public static boolean isPawn(Piece piece) {
        return piece instanceof Pawn;
    }

    /**
     * boolean representing if the piece is white or not
     */
    final boolean white;

    /**
     * int representing the value of each piece (used for minimax algorithm)
     */
    final int value;

    /**
     * the id associated with each piece (used to determine unicode value for printing
     */
    private final int id;

    /**
     * the board instance that the Piece is playing on
     */
    final Board board;

    /**
     * the x y position of the piece on the board
     */
    int x, y;

    /**
     * the list of possible moves for the piece (all moves the piece can go to that
     * does not take into account checking)
     */
    LinkedList<Tile> possibleMoves;

    /**
     * the list of legal moves (possible moves taking into account checking)
     */
    LinkedList<Tile> validMoves;

    /**
     * the number of moves the piece has moved
     */
    int numMoves;

    /**
     * Constructor method for piece, initializes the piece and its fields
     * @param white a boolean representing the color of the piece
     * @param board the board instance the piece is on
     * @param x the integer x coordinate
     * @param y the integer y coordinate
     * @param value the value associated with the piece
     * @param id the id of the piece
     */
    Piece(boolean white, Board board, int x, int y, int value, int id) {
        this.white = white;
        this.value = value;
        this.board = board;
        this.x = x;
        this.y = y;
        numMoves = 0;
        this.id = id;
    }

    /**
     * Sets the nummber of moves the piece has moved
     * @param numMoves the new value of the number of moves the piece has moved
     */
    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    /**
     * @return the number of moves the piece has moved
     */
    public int getNumMoves() {
        return numMoves;
    }

    /**
     * @return the boolean value representing if the piece is white
     */
    public boolean getWhite() {
        return white;
    }

    /**
     * @return the value of the piece
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a LinkedList copy of the valid moves for the piece
     * @return the LinkedList of valid moves
     */
    public LinkedList<Tile> getValidMoves() {
        return (LinkedList) validMoves.clone();
    }

    /**
     * Gets the number of valid moves that the piece has
     * @return the number of valid moves the piece has
     */
    public int getNumValidMoveOptions() {
        return validMoves == null ? 0 : validMoves.size();
    }

    /**
     * Gets the unicode associated with the piece based on the color and id of the piece
     * @return the unicode value corresponding to the piece
     */
    public String getImage() {
        char c = '\u2654';
        c += id + (white ? 0 : 6);
        return Character.toString(c);
    }

    /**
     * Moves the piece to the input position, updates the number of moves the piece has made
     * @param x the new x coordinate
     * @param y the new y coordinate
     */
    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
        numMoves++;
    }

    /**
     * Gets the x coordinate of the piece
     * @return the x coordinate of the piece
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coodinate of the peice
     * @return the y coordinates of the piece
     */
    public int getY() {
        return y;
    }

    /**
     * Finds the possible Tiles on the board the piece can move to without
     * taking into account checking and adds the moves to the possibleMoves LinkedList
     */
    public abstract void findPossibleMoves();

    /**
     * Sets all the Tiles on the Board instance that is a valid move for
     * this piece to be a valid move
     */
    public void setValidMoves() {
        for (Tile tile : validMoves) {
            tile.setValidMove(true);
        }
    }

    /**
     * Finds the valid moves associated with the piece and adds it to the validMoves LinkedList
     * Updates the hasMove variable for the board to be true if the piece has more than 1 valid move
     */
    public void findValidMoves() {
        validMoves = (LinkedList<Tile>) possibleMoves.clone();
        ListIterator<Tile> itr = validMoves.listIterator();
        while (itr.hasNext()) {
            Tile tile = itr.next();
            board.movePiece(this, tile); // simulates moving the piece forward
            board.stepForward(); // calculates the new possible moves for each piece
            if (board.isInCheck()) {
                itr.remove(); // removes the move if it puts the board in check (illegal move)
            }
            board.stepBackwards(); // undoes the step forward
        }
        if (validMoves.size() != 0) {
            board.setHasMove();
        }
    }

    /**
     * Attempts to add the Tile associated with the x y coordinates to possibleMoves.
     * Adds the move if there is no piece on that Tile or the piece is the opposite
     * color to this piece (capture).
     * Returns true if the piece can move past this tile; i.e. returns true if the Tile does
     * not contain a piece
     * @param x the x coordinate of the target Tile
     * @param y the y coordinate of the target Tile
     * @return true if the Tile associated with the x y coordinate does not contain a piece,
     * false otherwise
     */
    boolean addPossibleMove(int x, int y) {
        Piece otherPiece = board.getPiece(x, y);
        // if no pieces here or different color here, can move here
        if (otherPiece == null || otherPiece.white != white) {
            possibleMoves.add(board.getTile(x, y));
            if (otherPiece instanceof King) {
                board.handleCheck(white);
            }
        }
        return otherPiece == null;
    }

    /**
     * Finds the possible moves moving sideways (horizontal or vertical) and adds it to the
     * possibleMoves list. (To be used by Queen and Rook pieces).
     */
    void findPossibleMovesSideways() {
        for (int i = x + 1; i < 8 && addPossibleMove(i, y); i++) {
            continue;
        }
        for (int i = x - 1; i >= 0 && addPossibleMove(i, y); i--) {
            continue;
        }
        for (int i = y + 1; i < 8 && addPossibleMove(x, i); i++) {
            continue;
        }
        for (int i = y - 1; i >= 0 && addPossibleMove(x, i); i--) {
            continue;
        }
    }

    /**
     * Finds the possible moves moving diagonal and adds it to the possibleMoves list.
     * (To be used by Queen and Bishop pieces).
     */
    void findPossibleMovesDiagonal() {
        for (int i = 1; x + i < 8 && y + i < 8 && addPossibleMove(x + i, y + i); i++) {
            continue;
        }
        for (int i = 1; x + i < 8 && y - i >= 0 && addPossibleMove(x + i, y - i); i++) {
            continue;
        }
        for (int i = 1; x - i >= 0 && y + i < 8 && addPossibleMove(x - i, y + i); i++) {
            continue;
        }
        for (int i = 1; x - i >= 0 && y - i >= 0 && addPossibleMove(x - i, y - i); i++) {
            continue;
        }
    }
}

// ==============================================================================
// Piece subclasses
// ==============================================================================

/**
 * Represents the King piece. Handles castling.
 */
class King extends Piece {
    public King(boolean white, Board board, int x, int y) {
        super(white, board, x, y, 1000, 0);
    }

    /**
     * Overrides moveTo to take into account castling. Checks if King is castling and moves
     * the associated Rook to the appropriate position if it is
     * @param x the new x coordinate
     * @param y the new y coordinate
     */
    public void moveTo(int x, int y) {
        if (numMoves == 0) {
            if (y - this.y > 1) {
                //castle on king side, move king side rook
                board.movePiece(board.getPiece(x, 7), board.getTile(x, 5));
            } else if (this.y - y > 1) {
                //castle on queen side, move queen side rook
                board.movePiece(board.getPiece(x, 0), board.getTile(x, 3));
            }
        }
        super.moveTo(x, y);
    }

    public void findPossibleMoves() {
        possibleMoves = new LinkedList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nx = x + i;
                int ny = y + j;
                if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                    addPossibleMove(nx, ny);
                }
            }
        }
        // check if we can castle and adds to possibleMoves if we can
        if (numMoves == 0) {
            if (!board.containsPiece(x, 5) && !board.containsPiece(x, 6) &&
                    board.getPiece(x, 7) instanceof Rook && board.getPiece(x, 7).numMoves == 0) {
                addPossibleMove(x, 6);
            }
            if (!board.containsPiece(x, 3) && !board.containsPiece(x, 2) &&
                    !board.containsPiece(x, 1) && board.getPiece(x, 0) instanceof Rook &&
                    board.getPiece(x, 0).numMoves == 0) {
                addPossibleMove(x, 2);
            }
        }

    }
}

/**
 * Represents the Queen piece.
 */
class Queen extends Piece {
    public Queen(boolean white, Board board, int x, int y) {
        super(white, board, x, y, 90, 1);
    }

    public void findPossibleMoves() {
        possibleMoves = new LinkedList<>();
        findPossibleMovesDiagonal();
        findPossibleMovesSideways();
    }
}

/**
 * Represents the Rook piece.
 */
class Rook extends Piece {
    public Rook(boolean white, Board board, int x, int y) {
        super(white, board, x, y, 50, 2);
    }

    public void findPossibleMoves() {
        possibleMoves = new LinkedList<>();
        findPossibleMovesSideways();
    }
}

/**
 * Represents the Bishop piece
 */
class Bishop extends Piece {
    public Bishop(boolean white, Board board, int x, int y) {
        super(white, board, x, y, 30, 3);
    }

    public void findPossibleMoves() {
        possibleMoves = new LinkedList<>();
        findPossibleMovesDiagonal();
    }
}

/**
 * Represents the Knight piece
 */
class Knight extends Piece {
    public Knight(boolean white, Board board, int x, int y) {
        super(white, board, x, y, 30, 4);
    }

    public void findPossibleMoves() {
        possibleMoves = new LinkedList<>();
        int[] dx = {2, 2, -2, -2, 1, -1, 1, -1};
        int[] dy = {1, -1, 1, -1, 2, 2, -2, -2};
        for (int i = 0; i < 8; i++) {
            int nx = dx[i] + x;
            int ny = dy[i] + y;
            if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                addPossibleMove(nx, ny);
            }
        }
    }
}

/**
 * Represents the Pawn piece. Handles en Passant.
 */
class Pawn extends Piece {
    public Pawn(boolean white, Board board, int x, int y) {
        super(white, board, x, y, 10, 5);
    }

    /**
     * Overrides moveTo to handle en Passant case.
     * @param x the new x coordinate
     * @param y the new y coordinate
     */
    public void moveTo(int x, int y) {
        if (y != this.y && !board.containsPiece(x, y)) {
            board.removePiece(board.getTile(this.x, y));
        }
        super.moveTo(x, y);
    }

    public void findPossibleMoves() {
        possibleMoves = new LinkedList<>();
        int dx = white ? -1 : 1;
        int nx = x + dx;
        if (nx >= 8 || nx < 0) {
            return;
        }
        //diagonal capture
        if (y + 1 < 8 && board.containsPiece(nx, y + 1)) {
            addPossibleMove(nx, y + 1);
        }
        if (y - 1 >= 0 && board.containsPiece(nx, y - 1)) {
            addPossibleMove(nx, y - 1);
        }
        //forward one
        if (!board.containsPiece(nx, y)) {
            addPossibleMove(nx, y);
        }
        //forward two if first move
        if (numMoves == 0 && !board.containsPiece(nx, y)
                && !board.containsPiece(x + 2 * dx, y)) {
            addPossibleMove(x + 2 * dx, y);
        }
        //en passant
        if (y + 1 < 8) {
            Piece piece = board.getPiece(x, y + 1);
            if (piece instanceof Pawn &&
                    piece == board.getLastPieceMoved() && piece.numMoves == 1) {
                addPossibleMove(nx, y + 1);
            }
        }
        if (y - 1 >= 0) {
            Piece piece = board.getPiece(x, y - 1);
            if (piece instanceof Pawn &&
                    piece == board.getLastPieceMoved() && piece.numMoves == 1) {
                addPossibleMove(nx, y - 1);
            }
        }
    }
}