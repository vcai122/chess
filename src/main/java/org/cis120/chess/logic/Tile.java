package org.cis120.chess.logic;

/**
 * A class that represents a tile on the board
 */
public class Tile {
    /**
     * The piece at this tile position
     */
    private Piece piece;
    /**
     * If this tile is a valid move for the selected piece to move to
     */
    private boolean validMove;
    /**
     * the x y coordinates of the Tile
     */
    private int x, y;

    /**
     * Constructor, initializes validMoves to be false and sets the x y coordinates
     * @param x the x coordinate of the Tile
     * @param y the y coordinate of the Tile
     */
    public Tile(int x, int y) {
        validMove = false;
        this.x = x;
        this.y = y;
    }

    /**
     * @return the x coordinate of this Tile
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y coordinate of this Tile
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the Piece at this tile
     * @param piece the new Piece to set at this tile
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * @return the Piece at this Tile
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets validMove to be val
     * @param val the new value for validMove
     */
    public void setValidMove(boolean val) {
        validMove = val;
    }

    /**
     * @return if this tile has a piece or not
     */
    public boolean containsPiece() {
        return piece != null;
    }

    /**
     * @return if this tile is a validMove for the selected piece
     */
    public boolean isValidMove() {
        return validMove;
    }

}
