package org.cis120.chess.game;

import org.cis120.chess.ai.ChessAI;
import org.cis120.chess.logic.Board;
import org.cis120.chess.logic.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class instantiates a Board object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private Board chessBoard; // model for the game
    private JLabel status; // current status text
    private ChessAI ai; // the ai that plays chess

    /**
     * Game constants
     */
    public static final int BOARD_SIZE = 744;
    public static final int SQUARE_SIZE = BOARD_SIZE / 8;

    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel statusInit) {
        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        chessBoard = new Board(); // initializes model for the game
        status = statusInit; // initializes the status JLabel

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();

                // updates the model given the coordinates of the mouseclick
                boolean promoted = chessBoard.playTurn(p.y / SQUARE_SIZE, p.x / SQUARE_SIZE);
                if (promoted) {
                    promotePawn();
                }

                updateBoard();
                SwingUtilities.invokeLater(() -> aiMove());
            }
        });
    }

    /**
     * The AI makes a move
     */
    private void aiMove() {
        if (ai != null && ai.getWhite() == chessBoard.getTurn()) {
            ai.playTurn();
            updateBoard();
        }
    }

    /**
     * Shows the user the option dialog to choose what to promote their pawn to and
     * then promotes the pawn
     */
    private void promotePawn() {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int result = -1;
        while (result == -1) {
            result = JOptionPane.showOptionDialog(
                    this,
                    "Select Which Piece To Promote To",
                    "Promotion",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    null
            );
        }
        chessBoard.promote(result);
    }

    /**
     * Shows the game menu with the instructions and what mode of the game to play as.
     * Initializes the ai if the user selects to play against the ai.
     */
    private void showMenu() {
        String message = "Welcome to Chess!" +
                "\n\nThe game follows the normal chess rules. Try to" +
                "\ncheckmate your opponent before they checkmate you." +
                "\nUse the mouse to select which piece you want to move" +
                "\nand where to move the piece." +
                "\nWhite goes first.";
        String[] options = {"Play As Black", "Play as White", "2 Players"};
        int result = JOptionPane.showOptionDialog(this, message, "Chess",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
        if (result == 0) {
            ai = new ChessAI(true, chessBoard);
            aiMove();
        } else if (result == 1) {
            ai = new ChessAI(false, chessBoard);
        } else {
            ai = null;
        }
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        chessBoard.reset();
        updateBoard();
        showMenu();
        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Undoes the last move that was made by the user
     */
    public void undo() {
        chessBoard.undo();
        if (ai != null) {
            chessBoard.undo(); //undo twice if playing against ai (undoes ai move as well)
        }
        updateBoard();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        String text = "";
        if (chessBoard.staleMate()) {
            text = "Stalemate";
        } else {
            if (chessBoard.checkMate()) {
                text += "Checkmate! ";
            } else if (chessBoard.isChecked()) {
                text += "Check! ";
            }
            text += chessBoard.getTurn() ? "White" : "Black";
            text += chessBoard.checkMate() ? " Loses" : "'s turn";
        }
        status.setText(text);
    }

    /**
     * updates the board
     */
    private void updateBoard() {
        updateStatus(); // updates the status JLabel
        repaint(); // repaints the game board
    }

    /**
     * Draws the game board.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(255, 252, 232)); //light color
                } else {
                    g.setColor(new Color(255, 129, 129)); //dark color
                }
                g.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                Tile tile = chessBoard.getTile(j, i);
                if (tile.isValidMove()) {
                    g.setColor(new Color(227, 0, 0, 203)); // shade in if valid move
                    g.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                } else if (chessBoard.isSelected(tile.getPiece())) {
                    g.setColor(new Color(128, 0, 0, 180)); //shade in if selected
                    g.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }

                g.setColor(Color.black);
                g.drawRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                if (tile.containsPiece()) {
                    g.setColor(Color.black);
                    g.setFont(new Font("", Font.PLAIN, SQUARE_SIZE));
                    g.drawString(
                            tile.getPiece().getImage(),
                            i * SQUARE_SIZE + SQUARE_SIZE / 10,
                            j * SQUARE_SIZE + 3 * SQUARE_SIZE / 4
                    );
                }
            }
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_SIZE, BOARD_SIZE);
    }
}
