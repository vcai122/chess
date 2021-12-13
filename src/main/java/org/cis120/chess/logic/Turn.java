package org.cis120.chess.logic;

/**
 * Represents a turn that was made by storing information about the two tiles associated
 * with the turn. In other words stores information about a particular move on the board.
 * Used to implement undo.
 */
public class Turn {
    /**
     * The starting and ending tiles of the move. tile1 is starting tile which is the tile
     * the moving piece started on and tile2 is the ending tile, the tile that the moving piece
     * ended on
     */
    private Tile tile1, tile2;

    /**
     * The pieces that were on the corresponding tiles when this Turn object was instantiated.
     */
    private Piece piece1, piece2;

    /**
     * The number of moves associated with the pieces when the Turn object was instantiared.
     */
    private int numMoves1, numMoves2;

    /**
     * A boolean that represents if a Turn is linked to another Turn. This will be true for
     * turns in which castling or en Passant occurs.
     */
    private boolean isDoubleTurn;

    /**
     * Constructor, initializes the private fields
     * @param tile1
     * @param tile2
     */
    public Turn(Tile tile1, Tile tile2) {
        this.tile1 = tile1;
        this.tile2 = tile2;
        this.piece1 = tile1.getPiece();
        this.piece2 = tile2.getPiece();
        numMoves1 = piece1 == null ? 0 : piece1.getNumMoves();
        numMoves2 = piece2 == null ? 0 : piece2.getNumMoves();
        boolean castled = piece1 instanceof King && Math.abs(tile2.getY() - tile1.getY()) > 1;
        boolean enPassant = piece1 instanceof Pawn &&
                tile1.getY() != tile2.getY() && piece2 == null;
        isDoubleTurn = castled || enPassant;
    }

    /**
     * Returns if this Turn is a double turn
     * @return true if this Turn is a double turn, false otherwise
     */
    public boolean isDoubleTurn() {
        return isDoubleTurn;
    }

    /**
     * Sets the piece for each Tile, the number of moves for each Piece, and moves each piece
     * such that the Tiles and Piece on each tile now represent the state of the two tiles and
     * pieces before the turn. Essential "undoes" the turn.
     */
    public void goToThisTurn() {
        tile1.setPiece(piece1);
        tile2.setPiece(piece2);
        if (piece1 != null) {
            piece1.moveTo(tile1.getX(), tile1.getY());
            piece1.setNumMoves(numMoves1);
        }
        if (piece2 != null) {
            piece2.moveTo(tile2.getX(), tile2.getY());
            piece2.setNumMoves(numMoves2);
        }
    }

    /**
     * Gets the piece that moved for this Turn.
     * @return the piece that moved for this Turn.
     */
    public Piece getPieceMoved() {
        return piece1;
    }
}
