package org.cis120.chess.logic;

import java.util.LinkedList;

/**
 * Represents the chess board. Has an array of Tiles which contains the pieces and contains methods
 * associated with the board.
 */
public class Board {

    /**
     * A boolean variable keeping track of if it is white's turn or not
     */
    private boolean whiteTurn;

    /**
     * The currently selected piece by the user
     */
    private Piece currentPiece;

    /**
     * A LinkedList of Turns representing all the moves that have been made so far in
     * the order they were made
     */
    private LinkedList<Turn> turns;

    /**
     * A boolean that represents if the currently moving player is checking the opponent
     */
    private boolean inCheck;

    /**
     * A boolean that represents if the currently moving player is checked
     */
    private boolean checked;

    /**
     * A boolean that represents if the currently moving player has a valid move they can make
     */
    private boolean hasMove;

    /**
     * A 2d array of Tiles that represents the board
     */
    private Tile[][] board;

    /**
     * Constructor, initializes the board and calls the reset method to reset the board to be
     * ready to be played
     */
    public Board() {
        board = new Tile[8][8];
        reset();
    }

    /**
     * Handles user interactions with the board. Selects the piece on the tile associated with the
     * x y coodinate if possible and sets all the valid Tiles the piece can move to be a valid move.
     * Otherwise, moves the piece if the target tile is valid and a piece has already been selected.
     * Returns true if the piece moved needs to be promoted.
     * @param x the x coordinate of the target tile
     * @param y the y coordinate of the target tile
     * @return true if the piece moved was a pawn and needs to be promoted
     */
    public boolean playTurn(int x, int y) {
        Tile selectedTile = board[x][y];
        Piece selectedPiece = selectedTile.getPiece();
        if (selectedPiece != null && selectedPiece.white == whiteTurn) {
            //selects piece if piece is the color of the current player
            currentPiece = selectedPiece;
//            selectedPiece.removeMovesThatCheckKing();
            resetBoardValidMoves();
            selectedPiece.setValidMoves();
        } else if (currentPiece != null && selectedTile.isValidMove()) {
            //move the piece to the valid spot
            movePiece(currentPiece, board[x][y]);
            if (currentPiece instanceof Pawn && (x == 7 || x == 0)) {
                return true; //promotion
            }
            nextTurn();
        }
        return false;
    }

    /**
     * Simulates moving forward a turn but does not calculate valid moves. Calculates possible
     * moves. Used for checking for checks.
     */
    public void stepForward() {
        whiteTurn = !whiteTurn;
        resetBoardValidMoves();
        findPotentialMoves();
    }

    /**
     * Simulates undoing a turn but does not calculate valid moves. Calculates possilbe moves.
     * Used for undoing {@link #stepForward()}.
     */
    public void stepBackwards() {
        goToLastTurn();
        whiteTurn = !whiteTurn;
        findPotentialMoves();
    }

    /**
     * Goes to the next turn. Toggles the player that's currently playing, and re-calculates all
     * valid moves for each piece.
     */
    public void nextTurn() {
        whiteTurn = !whiteTurn;
        resetBoardValidMoves();
        updateMovesForPieces();
    }

    /**
     * Undoes a turn. Goes to the previous turn using {@link #goToLastTurn()} and calls
     * {@link #nextTurn()} to set up the board and re-calculate the valid moves at the
     * previous turn
     */
    public void undo() {
        if (turns.isEmpty()) {
            return;
        }
        goToLastTurn();
        nextTurn();
    }

    /**
     * Goes to the previous turn by polling from the end of the turns LinkedList and calling the
     * {@link #goToLastTurn()} method with the turn. Checks if the turn before that turn is a
     * "double turn" (a castling or en Passant turn), and undoes again if it is.
     */
    private void goToLastTurn() {
        if (turns.isEmpty()) {
            return;
        }
        turns.pollLast().goToThisTurn();
        if (!turns.isEmpty() && turns.peekLast().isDoubleTurn()) {
            turns.pollLast().goToThisTurn();
        }
    }

    /**
     * Gets the last piece moved
     * @return
     */
    public Piece getLastPieceMoved() {
        if (turns.isEmpty()) {
            return null;
        }
        return turns.peekLast().getPieceMoved();
    }

    /**
     * Simulates moving the piece, instantiates a new Turn object to store the board state of the
     * two relevant tiles before the move
     * @param piece the piece to be moved
     * @param target the target tile to move the piece to
     */
    public void movePiece(Piece piece, Tile target) {
        Tile prevTile = board[piece.getX()][piece.getY()];
        turns.add(new Turn(prevTile, target)); //save the current move and the pieces associated
        piece.moveTo(target.getX(), target.getY());
        prevTile.setPiece(null);
        target.setPiece(piece);
    }

    /**
     * Removes the piece at the target Tile.
     * @param target the tile to remove the piece from.
     */
    public void removePiece(Tile target) {
        turns.add(new Turn(new Tile(-1, -1), target));
        target.setPiece(null);
    }

    /**
     * Resets the board. Initializes the required variables and sets the pieces in the
     * appropraiate starting positions.
     */
    public void reset() {
        whiteTurn = true;
        currentPiece = null;
        turns = new LinkedList<>();
        hasMove = true;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Tile(i, j);
            }
        }
        for (int i = 0; i < 8; i++) {
            board[1][i].setPiece(new Pawn(false, this, 1, i));
            board[6][i].setPiece(new Pawn(true, this, 6, i));
        }
        for (int i = 0; i < 8; i += 7) {
            boolean isWhite = i == 7;
            board[i][0].setPiece(new Rook(isWhite, this, i, 0));
            board[i][1].setPiece(new Knight(isWhite, this, i , 1));
            board[i][2].setPiece(new Bishop(isWhite, this, i, 2));
            board[i][3].setPiece(new Queen(isWhite, this, i, 3));
            board[i][4].setPiece(new King(isWhite, this, i, 4));
            board[i][5].setPiece(new Bishop(isWhite, this, i, 5));
            board[i][6].setPiece(new Knight(isWhite, this, i, 6));
            board[i][7].setPiece(new Rook(isWhite, this, i, 7));
        }
        updateMovesForPieces();
    }

    /**
     * Gets the current player who is moving
     * @return a boolean representing if white is moving
     */
    public boolean getTurn() {
        return whiteTurn;
    }

    /**
     * Handles a check (used when detecting checks) based on who's turn it is and the color of
     * the piece doing the checking.
     * @param isWhite a boolean representing the color of the piece that is doing the checking
     */
    public void handleCheck(boolean isWhite) {
        if (isWhite == whiteTurn) {
            inCheck = true;
        } else {
            checked = true;
        }
    }

    /**
     * Sets has move to be true
     */
    public void setHasMove() {
        hasMove = true;
    }

    /**
     * @return if the current player is checking the opponent
     */
    public boolean isInCheck() {
        return inCheck;
    }

    /**
     * @return if the current player is being checked by the opponent
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @return if the current player has been checkmated
     */
    public boolean checkMate() {
        return checked && !hasMove;
    }

    /**
     * @return if the current player has been stalemated
     */
    public boolean staleMate() {
        return !checked && !hasMove;
    }

    /**
     * Updates the valid moves for each piece on the board for the current player
     */
    public void updateMovesForPieces() {
        hasMove = false;
        findPotentialMoves();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j].getPiece();
                if (piece != null && whiteTurn == piece.white) {
                    piece.findValidMoves();
                }
            }
        }
    }

    /**
     * Finds the possilbe moves for each piece on the board for the current player. Used to
     * calculate valid moves when stepping forward to check for checks.
     */
    public void findPotentialMoves() {
        checked = false;
        inCheck = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j].getPiece();
                if (piece != null) {
                    piece.findPossibleMoves();
                }
            }
        }
    }


    /**
     * Checks if there is a piece at the associated x y coordinate
     * @param x the x coordinate of the Tile to check
     * @param y the y coordinate of the Tile to check
     * @return a boolean representing if there is a piece there or not
     */
    public boolean containsPiece(int x, int y) {
        return board[x][y].containsPiece();
    }

    /**
     * Gets the piece at the associated x y coordinate
     * @param x the x coordinate of the Tile
     * @param y the y coordinate of the Tile
     * @return the piece at the Tile
     */
    public Piece getPiece(int x, int y) {
        return board[x][y].getPiece();
    }

    /**
     * Gets the Tile at the associated x y coordinate
     * @param x the x coordinate of the associated Tile
     * @param y the y coordinate of the associated Tile
     * @return the Tile
     */
    public Tile getTile(int x, int y) {
        return board[x][y];
    }

    /**
     * Resets the board so that there are no valid moves. Goes through each tile and sets
     * validMove to be false for each tile.
     */
    public void resetBoardValidMoves() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].setValidMove(false);
            }
        }
    }

    /**
     * Promotes the last moved pawn
     * @param newPiece an integer corresponding to what to promote the pawn to.
     */
    public void promote(int newPiece) {
        Piece pawn = getLastPieceMoved();
        int x = pawn.getX();
        int y = pawn.getY();
        if (newPiece == 0) {
            board[x][y].setPiece(new Queen(pawn.white, this, x, y));
        } else if (newPiece == 1) {
            board[x][y].setPiece(new Rook(pawn.white, this, x, y));
        } else if (newPiece == 2) {
            board[x][y].setPiece(new Bishop(pawn.white, this, x, y));
        } else if (newPiece == 3) {
            board[x][y].setPiece(new Knight(pawn.white, this, x, y));
        }
        nextTurn();
    }

    /**
     * Checks it the piece is the currently selected piece
     * @param piece the piece to check if selected
     * @return true if the piece is selcted, false otherwise
     */
    public boolean isSelected(Piece piece) {
        return piece != null && piece == currentPiece;
    }

    /**
     * Calculates the score of the board by summing the values of the pieces for both players,
     * and subtracting depending on for which player we are calculating the score for.
     * @param white the player we are calculating the score for
     * @return the score that was calculated
     */
    public int calculateScoreDifference(boolean white) {
        if (staleMate()) {
            return 0;
        }
        int whiteScore = 0;
        int blackScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int  j = 0; j < 8; j++) {
                Piece piece = board[i][j].getPiece();
                if (piece != null) {
                    if (piece.getWhite()) {
                        whiteScore += piece.value;
                    } else {
                        blackScore += piece.value;
                    }
                }
            }
        }
        if (white) {
            return whiteScore - blackScore;
        } else {
            return blackScore - whiteScore;
        }
    }

    /**
     * Calculates the number of moves a particular player has with a particular board position
     * @param white the player we want to calculate the number of moves for
     * @return the number of moves the player has
     */
    public int calculateNumMoves(boolean white) {
        int ret = 0;
        for (int i = 0 ; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j].getPiece();
                if (piece != null && piece.white == white) {
                    ret += piece.getNumValidMoveOptions();
                }
            }
        }
        return ret;
    }

}
