=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: 27390325
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays
  I used an 8x8 2D array of Tiles (a class I created) to represent the chess board since the chessboard is essentially an 8x8 grid of tiles. The Tile class, like a Tile on a chess board, can either contain a piece or not.

  2. Inheritance/Subtyping
  I used a Piece abstract class with an abstract method and a subclass that extends this abstract class for each piece. The Piece class has an abstract method findPossibleMoves that needs to be implemented by the subclasses since the way each piece moves is different so it needs to be specifically implemented. The Piece class also defines variables such as white, value, x, y, as well as certain methods common to all pieces. These methods include getter/setter methods as well as methods like addPossibleMoves, and moveTo (although moveTo is overridden in certain subclasses where they have special cases for movements such as in the Pawn class since pawns can en Passant and the King class since the king needs to be able to castle). (See javadocs for details). Pieces are statically typed as a Piece in almost every case they are used which allows us to re-use code when dealing with how the users interact with the piece or how the ai interacts with it. However, when calculating the possible moves the dynamic method is always called as well as for certain overridden methods for certain classes such as moveTo.

  3. Complex Game Logic
  I implemented the complex game logic of chess. Checking is implemented by disallowing moves that would lead to a checked board state which implements straightforward checking, disallows Kings to move into check, disallows pieces to move in a way that puts the King into check, and forces players to resolve check. Checkmate is implemented by checking if the player is in check and has no moves. Castling and en Passant were treated as special cases to deal with.

  4. AI
  I created an ai that uses minimax algorithm using a heuristic based on the value of each piece that looks forward 3 turns and tries to maximize the board value it can guarantee after the 3 turns. It uses pruning to minimize the amount of calculations. After that, it breaks ties by applying another heuristic based on piece value and number of moves the move opens up.

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

  RunChess - The top level frame and widget for the GUI, initializes/displays the buttons and the GameBoard JPanel.

  GameBoard - JPanel that displays the board state of the game. Handles user interactions (clicks) and acts as the interface between the chess logic and the user. Also handles button clicks like undo and reset as well as the interface for pawn promotion. Contains Board object that represents the board the game is being played on which has all the Tiles and Pieces.

  Board - A class that represents board. Has an array of Tiles which contains pieces and has methods associated with the board. Serves as a collection of all the chess logic and components. It stores information like the current player, if they are checked, and if they have moves. It has methods like playTurn to handle the user inputs and methods to move pieces, undo, and calculate valid moves for pieces.

  Tile - A class that represents a tile on the board. It stores information on the piece that is at that tile, and whether or not that tile is a valid move based on the selected piece. It just has getter and setter methods, since it sort of just behaves as a wrapper class for information associated to the tile.

  Turn - A class that represents a turn that was made by storing information about the two tiles associated with the turn. In other words it stores information about a particular move that was made. The most significant method here is the goToThisTurn method which is used for undoing since it essentially undoes the move with respect to the two tiles.

  Piece - An abstract class that represents a chess piece. It has variables and methods that are shared among all subclasses like the board instance the game is being played on, as well as an abstract method findPossibleMoves since each piece has different possible moves so this method needs to be implemented differently. It also contains methods to filter out possible moves into only valid moves and contains two lists storing the possible and valid moves. Piece subclasses have constructor methods that fill out the fields specific to that piece, and certain subclasses override methods such as Pawn and King class overriding moveTo.

  ChessAI - A class that handles the logic for the ai. Its primary method is playTurn which uses the minimax algorithm to determine which of its possible moves will yield the best outcome. It also has the board instance that the chess game is being played on so it can make moves.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

  No, there wasn't anything particularly challenging, it was just a lot of work in terms of implementation.

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

  I think generally the logic is separated pretty well and encapsulation is done well. There's not really anything that I would refactor, since I have been doing refactoring throughout the project to not re-write/have repeating code.

========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

  Minimax tutorial: https://towardsdatascience.com/how-a-chess-playing-computer-thinks-about-its-next-move-8f028bd0e7b1