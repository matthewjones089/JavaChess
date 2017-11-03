import java.math.BigInteger;

final class Board {

	public Square[] Squares;
	
	public boolean InsufficientMaterialActive = false;
	public boolean InsufficientMaterial;

	/*
	 * To work out if an index is a row or a column:
	 * 
	 * Rows --- integer ( position / 8 )
	 * 
	 * Columns --- position / 8
	 */

	public Integer score;

	public boolean blackCheck, whiteCheck, whiteMate, blackMate, staleMate;

	// Counters for the tie scenarios of the 50-move rule and the 3-move rule.
	public byte fiftyMove, repeatedMove;
	
	public boolean fiftyMoveActive, repeatedMoveActive;

	// Tracks if either side has been castled.
	// So that later bonus points can be awarded.
	// And to allow castling to occur in the circumstance.
	public boolean whiteCastled, blackCastled;

	// Tracks the state of the game. Flags at end game state.
	// In end game state certain behaviours are altered to increase king safety
	// and mate opportunities.
	public boolean endGamePhase;

	// Keeps track of the last move that occurred.
	public MoveContent lastMove = new MoveContent();

	// Holds which colour last made an EnPassant move ( Which side moved a pawn
	// 2 spots ).
	public chessPiece.ChessPieceColour enPassantColour;

	// Holds a position one space behind the pawn which made a EnPassant move.
	public byte enPassantPosition;

	// Holds the current player.
	public chessPiece.ChessPieceColour WhosMove;

	public boolean BlackCanCastle;
	public boolean WhiteCanCastle;

	public boolean[] BlackAttackBoard;
	public boolean[] WhiteAttackBoard;
	
	public byte WhiteKingPosition;
	public byte BlackKingPosition;

	public BigInteger ZobrtisHash;

	// Holds the number of moves which have occurred.
	private int moveCount;

	public boolean started = true;

	// Default Constructor
	Board(String fen) {
		
		Squares = new Square[64];
		
		for (int i = 0; i < 64; i++)
			Squares[i] = new Square();

		byte index = 0;
		byte spc = 0;

		whiteCastled = true;
		blackCastled = true;

		byte spacers = 0;

		WhosMove = chessPiece.ChessPieceColour.White;

		if (fen.contains("a3")) {
			enPassantColour = chessPiece.ChessPieceColour.White;
			enPassantPosition = 40;
		} else if (fen.contains("b3")) {
			enPassantColour = chessPiece.ChessPieceColour.White;
			enPassantPosition = 41;
		} else if (fen.contains("c3")) {
			enPassantColour = chessPiece.ChessPieceColour.White;
			enPassantPosition = 42;
		} else if (fen.contains("d3")) {
			enPassantColour = chessPiece.ChessPieceColour.White;
			enPassantPosition = 43;
		} else if (fen.contains("e3")) {
			enPassantColour = chessPiece.ChessPieceColour.White;
			enPassantPosition = 44;
		} else if (fen.contains("f3")) {
			enPassantColour = chessPiece.ChessPieceColour.White;
			enPassantPosition = 45;
		} else if (fen.contains("g3")) {
			enPassantColour = chessPiece.ChessPieceColour.White;
			enPassantPosition = 46;
		} else if (fen.contains("h3")) {
			enPassantColour = chessPiece.ChessPieceColour.White;
			enPassantPosition = 47;
		}

		if (fen.contains("a6")) {
			enPassantColour = chessPiece.ChessPieceColour.Black;
			enPassantPosition = 16;
		} else if (fen.contains("b6")) {
			enPassantColour = chessPiece.ChessPieceColour.Black;
			enPassantPosition = 17;
		} else if (fen.contains("c6")) {
			enPassantColour = chessPiece.ChessPieceColour.Black;
			enPassantPosition = 18;
		} else if (fen.contains("d6")) {
			enPassantColour = chessPiece.ChessPieceColour.Black;
			enPassantPosition = 19;
		} else if (fen.contains("e6")) {
			enPassantColour = chessPiece.ChessPieceColour.Black;
			enPassantPosition = 20;
		} else if (fen.contains("f6")) {
			enPassantColour = chessPiece.ChessPieceColour.Black;
			enPassantPosition = 21;
		} else if (fen.contains("g6")) {
			enPassantColour = chessPiece.ChessPieceColour.Black;
			enPassantPosition = 22;
		} else if (fen.contains("h6")) {
			enPassantColour = chessPiece.ChessPieceColour.Black;
			enPassantPosition = 23;
		}

		if (fen.contains(" w "))
			WhosMove = chessPiece.ChessPieceColour.White;

		if (fen.contains(" b "))
			WhosMove = chessPiece.ChessPieceColour.Black;

		for (char c : fen.toCharArray()) {
			if (index < 64 && spc == 0) {
				if (Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
					Squares[index].Piece = new chessPiece();
				if (c == '1' && index < 63)
					index++;
				else if (c == '2' && index < 62)
					index += 2;
				else if (c == '3' && index < 61)
					index += 3;
				else if (c == '4' && index < 60)
					index += 4;
				else if (c == '5' && index < 59)
					index += 5;
				else if (c == '6' && index < 58)
					index += 6;
				else if (c == '7' && index < 57)
					index += 7;
				else if (c == '8' && index < 56) {
					index += 8;
				} else if (c == 'P') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Pawn,
							chessPiece.ChessPieceColour.White);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'N') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Knight,
							chessPiece.ChessPieceColour.White);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'B') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Bishop,
							chessPiece.ChessPieceColour.White);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'R') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Rook,
							chessPiece.ChessPieceColour.White);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'Q') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Queen,
							chessPiece.ChessPieceColour.White);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'K') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.King,
							chessPiece.ChessPieceColour.White);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'p') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Pawn,
							chessPiece.ChessPieceColour.Black);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'n') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Knight,
							chessPiece.ChessPieceColour.Black);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'b') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Bishop,
							chessPiece.ChessPieceColour.Black);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'r') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Rook,
							chessPiece.ChessPieceColour.Black);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'q') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.Queen,
							chessPiece.ChessPieceColour.Black);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == 'k') {
					Squares[index].Piece = new chessPiece(
							chessPiece.ChessPieceType.King,
							chessPiece.ChessPieceColour.Black);
					Squares[index].Piece.moved = true;
					index++;
				} else if (c == '/') 
					continue;
				else if (c == ' ')
					spc++;
			} else {
				if (c == 'K') {
					if (Squares[60].Piece.PieceType != chessPiece.ChessPieceType.None) {
						if (Squares[60].Piece.PieceType == chessPiece.ChessPieceType.King)
							Squares[60].Piece.moved = false;
					}
					if (Squares[63].Piece.PieceType != chessPiece.ChessPieceType.None) {
						if (Squares[63].Piece.PieceType == chessPiece.ChessPieceType.Rook)
							Squares[63].Piece.moved = false;
					}
					whiteCastled = false;
				} else if (c == 'Q') {
					if (Squares[60].Piece.PieceType != chessPiece.ChessPieceType.None) {
						if (Squares[60].Piece.PieceType == chessPiece.ChessPieceType.King)
							Squares[60].Piece.moved = false;
					}
					if (Squares[56].Piece.PieceType != chessPiece.ChessPieceType.None) {
						if (Squares[56].Piece.PieceType == chessPiece.ChessPieceType.Rook)
							Squares[56].Piece.moved = false;
					}
					whiteCastled = false;
				} else if (c == 'k') {
					if (Squares[4].Piece.PieceType != chessPiece.ChessPieceType.None) {
						if (Squares[4].Piece.PieceType == chessPiece.ChessPieceType.King)
							Squares[4].Piece.moved = false;
					}
					if (Squares[7].Piece.PieceType != chessPiece.ChessPieceType.None) {
						if (Squares[7].Piece.PieceType == chessPiece.ChessPieceType.Rook)
							Squares[7].Piece.moved = false;
					}
					blackCastled = false;
				} else if (c == 'q') {
					if (Squares[4].Piece.PieceType != chessPiece.ChessPieceType.None) {
						if (Squares[4].Piece.PieceType == chessPiece.ChessPieceType.King)
							Squares[4].Piece.moved = false;
					}
					if (Squares[0].Piece.PieceType != chessPiece.ChessPieceType.None) {
						if (Squares[0].Piece.PieceType == chessPiece.ChessPieceType.Rook)
							Squares[0].Piece.moved = false;
					}
					blackCastled = false;
				} else if (c == ' ')
					spacers++;
				else if (c == '1' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 1);
				else if (c == '2' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 2);
				else if (c == '3' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 3);
				else if (c == '4' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 4);
				else if (c == '5' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 5);
				else if (c == '6' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 6);
				else if (c == '7' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 7);
				else if (c == '8' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 8);
				else if (c == '9' && spacers == 4)
					fiftyMove = (byte) ((fiftyMove * 10) + 9);
				else if (c == '0' && spacers == 4)
					moveCount = (byte) ((moveCount * 10) + 0);
				else if (c == '1' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 1);
				else if (c == '2' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 2);
				else if (c == '3' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 3);
				else if (c == '4' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 4);
				else if (c == '5' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 5);
				else if (c == '6' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 6);
				else if (c == '7' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 7);
				else if (c == '8' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 8);
				else if (c == '9' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 9);
				else if (c == '0' && spacers == 5)
					moveCount = (byte) ((moveCount * 10) + 0);
			} 
		}
		
		WhiteAttackBoard = new boolean[64];
		BlackAttackBoard = new boolean[64];
	}

	/*public void InitiateBoard() {

		for (int c = 0; c < 16; c++) {
			Squares[c].Piece.PieceColour = chessPiece.ChessPieceColour.Black;
		}
		Squares[0].Piece.PieceType = chessPiece.ChessPieceType.Rook;
		Squares[1].Piece.PieceType = chessPiece.ChessPieceType.Knight;
		Squares[2].Piece.PieceType = chessPiece.ChessPieceType.Bishop;
		Squares[3].Piece.PieceType = chessPiece.ChessPieceType.Queen;
		Squares[4].Piece.PieceType = chessPiece.ChessPieceType.King;
		Squares[5].Piece.PieceType = chessPiece.ChessPieceType.Bishop;
		Squares[6].Piece.PieceType = chessPiece.ChessPieceType.Knight;
		Squares[7].Piece.PieceType = chessPiece.ChessPieceType.Rook;
		for (int i = 8; i < 16; i++) {
			Squares[i].Piece.PieceType = chessPiece.ChessPieceType.Pawn;
		}

		for (int c = 48; c < 64; c++) {
			if (c < 56)
				Squares[c].Piece.PieceType = chessPiece.ChessPieceType.Pawn;

			Squares[c].Piece.PieceColour = chessPiece.ChessPieceColour.White;
		}
		Squares[56].Piece.PieceType = chessPiece.ChessPieceType.Rook;
		Squares[57].Piece.PieceType = chessPiece.ChessPieceType.Knight;
		Squares[58].Piece.PieceType = chessPiece.ChessPieceType.Bishop;
		Squares[59].Piece.PieceType = chessPiece.ChessPieceType.Queen;
		Squares[60].Piece.PieceType = chessPiece.ChessPieceType.King;
		Squares[61].Piece.PieceType = chessPiece.ChessPieceType.Bishop;
		Squares[62].Piece.PieceType = chessPiece.ChessPieceType.Knight;
		Squares[63].Piece.PieceType = chessPiece.ChessPieceType.Rook;

		started = false;

	}*/

	Board() {

		Squares = new Square[64];

		for (byte i = 0; i < 64; i++) {
			Squares[i] = new Square();
		}

		lastMove = new MoveContent();

		BlackCanCastle = true;
		WhiteCanCastle = true;

		WhiteAttackBoard = new boolean[64];
		BlackAttackBoard = new boolean[64];
		
		fiftyMoveActive = true;
		repeatedMoveActive = true;

	}

	// Copy constructor.
	Board(Board board) {
		Squares = new Square[64];

		for (byte x = 0; x < 64; x++) {
			
				Squares[x] = new Square(board.Squares[x].Piece);
			
		}

		WhiteAttackBoard = new boolean[64];
		BlackAttackBoard = new boolean[64];

		for (byte x = 0; x < 64; x++) {
			WhiteAttackBoard[x] = board.WhiteAttackBoard[x];
			BlackAttackBoard[x] = board.BlackAttackBoard[x];
		}

		endGamePhase = board.endGamePhase;

		if (fiftyMoveActive)
			fiftyMove = board.fiftyMove;
		if (repeatedMoveActive)
			repeatedMove = board.repeatedMove;

		whiteCastled = board.whiteCastled;
		blackCastled = board.blackCastled;

		WhiteCanCastle = board.WhiteCanCastle;
		BlackCanCastle = board.BlackCanCastle;
		
		WhiteKingPosition = board.WhiteKingPosition;
		BlackKingPosition = board.BlackKingPosition;

		blackCheck = board.blackCheck;
		whiteCheck = board.whiteCheck;
		staleMate = board.staleMate;
		whiteMate = board.whiteMate;
		blackMate = board.blackMate;
		WhosMove = board.WhosMove;
		enPassantPosition = board.enPassantPosition;
		enPassantColour = board.enPassantColour;

		ZobrtisHash = board.ZobrtisHash;

		score = board.score;

		lastMove = new MoveContent(board.lastMove);

		moveCount = board.moveCount;
	}

	// Constructor to pass in default score.
	Board(int Score) {
		this.score = Score;

		WhiteAttackBoard = new boolean[64];
		BlackAttackBoard = new boolean[64];
	}

	// Constructor which will accept an array of board squares.
	private Board(Square[] squares) {
		Squares = new Square[64];

		for (byte x = 0; x < 64; x++) {
			//if (squares[x].Piece.PieceType != chessPiece.ChessPieceType.None) {
				Squares[x] = new Square();
				Squares[x].Piece = new chessPiece(squares[x].Piece);
			//}
		}

		WhiteAttackBoard = new boolean[64];
		BlackAttackBoard = new boolean[64];
	}

	// Copies values which must persist from one board to another during move
	// generation.
	Board FastCopy() {
		Board clonedBoard = new Board(Squares);

		clonedBoard.endGamePhase = endGamePhase;
		clonedBoard.WhosMove = WhosMove;
		clonedBoard.moveCount = moveCount;
		clonedBoard.fiftyMove = fiftyMove;
		clonedBoard.ZobrtisHash = ZobrtisHash;
		clonedBoard.blackCastled = blackCastled;
		clonedBoard.whiteCastled = whiteCastled;

		clonedBoard.WhiteCanCastle = WhiteCanCastle;
		clonedBoard.BlackCanCastle = BlackCanCastle;

		WhiteAttackBoard = new boolean[64];
		BlackAttackBoard = new boolean[64];

		return clonedBoard;
	}

	// Handles pawn promotion.
	private static boolean PromotePawn(Board board, chessPiece piece,
			byte dstPosition, chessPiece.ChessPieceType promoteToPiece) {

		if (piece.PieceType == chessPiece.ChessPieceType.Pawn) {
			if (dstPosition < 8) {
				board.Squares[dstPosition].Piece.PieceType = promoteToPiece;
				board.Squares[dstPosition].Piece.pieceValue = chessPiece
						.calculatePieceValue(promoteToPiece);
				board.Squares[dstPosition].Piece.pieceActionValue = chessPiece
						.calculatePieceActionValue(promoteToPiece);
				return true;
			}
			if (dstPosition > 55) {
				board.Squares[dstPosition].Piece.PieceType = promoteToPiece;
				board.Squares[dstPosition].Piece.pieceValue = chessPiece
						.calculatePieceValue(promoteToPiece);
				board.Squares[dstPosition].Piece.pieceActionValue = chessPiece
						.calculatePieceActionValue(promoteToPiece);
				return true;
			}
		}

		return false;
	}

	// EnPassant -------------
	// Sets the enPassant flag if the piece currently moving is a pawn that
	// moves two squares.
	private static void recordEnPassant(chessPiece.ChessPieceColour pcColour,
			chessPiece.ChessPieceType pcType, Board board, byte srcPosition,
			byte dstPosition) {

		if (pcType == chessPiece.ChessPieceType.Pawn) {
			board.fiftyMove = 0;

			int difference = srcPosition - dstPosition;

			if (difference == 16 || difference == -16) {
				board.enPassantPosition = (byte) (dstPosition + (difference / 2));
				board.enPassantColour = pcColour;
			}
		}
	}

	// Will move the enPassant piece and kill the advanced pawn based on the
	// enPassant flags on the board.
	private static boolean setEnPassantMove(Board board, byte srcPosition,
			byte dstPosition, chessPiece.ChessPieceColour pcColour) {

		if (board.enPassantPosition != dstPosition)
			return false;

		if (pcColour == board.enPassantColour)
			return false;

		if (board.Squares[srcPosition].Piece.PieceType != chessPiece.ChessPieceType.Pawn)
			return false;

		int pieceLocationOffset = 8;

		if (board.enPassantColour == chessPiece.ChessPieceColour.White)
			pieceLocationOffset = -8;

		dstPosition = (byte) (dstPosition + pieceLocationOffset);

		Square sqr = board.Squares[dstPosition];

		board.lastMove.TakenPiece = new PieceTaken(sqr.Piece.PieceColour,
				sqr.Piece.PieceType, sqr.Piece.moved, dstPosition);

		board.Squares[dstPosition].Piece = new chessPiece();

		board.fiftyMove = 0;

		return true;
	}

	// Moves the Rook to the correct position if castling is requested.
	private static void KingCastle(Board board, chessPiece piece,
			byte srcPosition, byte dstPosition) {

		if (piece.PieceType != chessPiece.ChessPieceType.King)
			return;

		// Check if this is a Castling move.
		if (piece.PieceColour == chessPiece.ChessPieceColour.White
				&& srcPosition == 60) {
			// Castle Right.
			if (dstPosition == 62) {
				// We are Castling, we need to move the Rook.
				if (board.Squares[63].Piece.PieceType == chessPiece.ChessPieceType.Rook) {
					board.Squares[61].Piece = board.Squares[63].Piece;
					board.Squares[63].Piece = new chessPiece();
					board.whiteCastled = true;
					board.lastMove.MovingPieceSecondary = new PieceMoving(
							board.Squares[61].Piece.PieceColour,
							board.Squares[61].Piece.PieceType,
							board.Squares[61].Piece.moved, (byte) 63, (byte) 61);
					board.Squares[61].Piece.moved = true;
					return;
				}
			}
			// Castle left.
			else if (dstPosition == 58) {
				// We are Castling, we need to move the Rook.
				if (board.Squares[56].Piece.PieceType != chessPiece.ChessPieceType.None) {
					board.Squares[59].Piece = board.Squares[56].Piece;
					board.Squares[56].Piece = new chessPiece();
					board.whiteCastled = true;
					board.lastMove.MovingPieceSecondary = new PieceMoving(
							board.Squares[59].Piece.PieceColour,
							board.Squares[59].Piece.PieceType,
							board.Squares[59].Piece.moved, (byte) 56, (byte) 59);
					board.Squares[59].Piece.moved = true;
					return;
				}
			}
		} else if (piece.PieceColour == chessPiece.ChessPieceColour.Black
				&& srcPosition == 4) {
			// Castle Right.
			if (dstPosition == 6) {
				// We are Castling, we need to move the Rook.
				if (board.Squares[7].Piece.PieceType != chessPiece.ChessPieceType.None) {
					board.Squares[5].Piece = board.Squares[7].Piece;
					board.Squares[7].Piece = new chessPiece();
					board.whiteCastled = true;
					board.lastMove.MovingPieceSecondary = new PieceMoving(
							board.Squares[5].Piece.PieceColour,
							board.Squares[5].Piece.PieceType,
							board.Squares[5].Piece.moved, (byte) 7, (byte) 5);
					board.Squares[5].Piece.moved = true;
					return;
				}
			}
			// Castle left.
			else if (dstPosition == 2) {
				// We are Castling, we need to move the Rook.
				if (board.Squares[0].Piece.PieceType != chessPiece.ChessPieceType.None) {
					board.Squares[3].Piece = board.Squares[0].Piece;
					board.Squares[0].Piece = new chessPiece();
					board.whiteCastled = true;
					board.lastMove.MovingPieceSecondary = new PieceMoving(
							board.Squares[3].Piece.PieceColour,
							board.Squares[3].Piece.PieceType,
							board.Squares[3].Piece.moved, (byte) 0, (byte) 3);
					board.Squares[3].Piece.moved = true;
					return;
				}
			}
		}
		return;
	}

	final static MoveContent MovePiece(Board board, byte srcPosition,
			byte dstPosition, chessPiece.ChessPieceType promoteToPiece) {

		chessPiece piece = board.Squares[srcPosition].Piece;

		// Record my last move.
		board.lastMove = new MoveContent();

		if (piece.PieceColour == chessPiece.ChessPieceColour.Black)
			board.moveCount++;
		// Add one to fiftyMoveCount to check for tie.
		board.fiftyMove++;

		// enPassant.
		if (board.enPassantPosition > 0)
			board.lastMove.enPassantOccured = setEnPassantMove(board,
					srcPosition, dstPosition, piece.PieceColour);

		if (!board.lastMove.enPassantOccured) {
			Square sqr = board.Squares[dstPosition];
			if (sqr.Piece.PieceType != chessPiece.ChessPieceType.None) {
				board.lastMove.TakenPiece = new PieceTaken(
						sqr.Piece.PieceColour, sqr.Piece.PieceType,
						sqr.Piece.moved, dstPosition);
				board.fiftyMove = 0;
			} else {
				board.lastMove.TakenPiece = new PieceTaken(
						chessPiece.ChessPieceColour.White,
						chessPiece.ChessPieceType.None, false, dstPosition);
			}
		}

		board.lastMove.MovingPiecePrimary = new PieceMoving(piece.PieceColour,
				piece.PieceType, piece.moved, srcPosition, dstPosition);

		// Delete the piece in its source position.
		board.Squares[srcPosition].Piece = new chessPiece();

		// Add the piece to its new position.
		piece.moved = true;
		piece.selected = false;
		board.Squares[dstPosition].Piece = piece;

		// Reset enPassantPosition.
		board.enPassantPosition = 0;

		// Record enPassant if pawn moving.
		if (piece.PieceType == chessPiece.ChessPieceType.Pawn) {
			board.fiftyMove = 0;
			recordEnPassant(piece.PieceColour, piece.PieceType, board,
					srcPosition, dstPosition);
		}

		board.WhosMove = board.WhosMove == chessPiece.ChessPieceColour.White ? chessPiece.ChessPieceColour.Black
				: chessPiece.ChessPieceColour.White;

		KingCastle(board, piece, srcPosition, dstPosition);

		// Promote Pawns
		if (PromotePawn(board, piece, dstPosition, promoteToPiece))
			board.lastMove.PawnPromotedTo = promoteToPiece;
		else
			board.lastMove.PawnPromotedTo = chessPiece.ChessPieceType.None;

		if (board.fiftyMove > 50)
			board.staleMate = true;

		return board.lastMove;
	}
	
	private static String GetColumnFromByte(byte column)
    {
        switch (column)
        {
            case 0:
                return "a";
            case 1:
                return "b";
            case 2:
                return "c";
            case 3:
                return "d";
            case 4:
                return "e";
            case 5:
                return "f";
            case 6:
                return "g";
            case 7:
                return "h";
            default:
                return "a";
        }
    }
	
	public String ToString() {
		return Fen(false , this);
	}
	
	public static String Fen(boolean boardOnly , Board board) {
		String output = "";
		Byte blankSquares = 0;
		
		for (byte x = 0; x < 64; x++) {
			byte index = x;
			
			if (board.Squares[index].Piece.PieceType != chessPiece.ChessPieceType.None) {
				if (blankSquares > 0) {
					output += blankSquares.toString();
					blankSquares = 0;
				}
				if (board.Squares[index].Piece.PieceColour == chessPiece.ChessPieceColour.Black) 
					output += chessPiece.GetPieceTypeShort(board.Squares[index].Piece.PieceType).toLowerCase();
				else 
					output += chessPiece.GetPieceTypeShort(board.Squares[index].Piece.PieceType);
			} else
				blankSquares++;
			if (x % 8 == 7) {
				if (blankSquares > 0) {
					output += blankSquares.toString();
					output += "/";
					blankSquares = 0;
				} else {
					if (x > 0 && x != 63) 
						output += "/";
				}
			}
		}
		
		if (board.WhosMove == chessPiece.ChessPieceColour.White) 
			output += " w ";
		else
			output += " b ";
		
		String spacer = "";
		
		if (board.whiteCastled == false) {
            if (board.Squares[60].Piece.PieceType != chessPiece.ChessPieceType.None) {
                if (board.Squares[60].Piece.moved == false) {
                    if (board.Squares[63].Piece.PieceType != chessPiece.ChessPieceType.None) {
                        if (board.Squares[63].Piece.moved == false) {
                            output += "K";
                            spacer = " ";
                        }
                    }
                    if (board.Squares[56].Piece.PieceType != chessPiece.ChessPieceType.None) {
                        if (board.Squares[56].Piece.moved == false) {
                            output += "Q";
                            spacer = " ";
                        }
                    }
                }
            }
        }
		
		if (board.blackCastled == false) {
            if (board.Squares[4].Piece.PieceType != chessPiece.ChessPieceType.None) {
                if (board.Squares[4].Piece.moved == false) {
                    if (board.Squares[7].Piece.PieceType != chessPiece.ChessPieceType.None) {
                        if (board.Squares[7].Piece.moved == false) {
                            output += "k";
                            spacer = " ";
                        }
                    }
                    if (board.Squares[0].Piece.PieceType != chessPiece.ChessPieceType.None) {
                        if (board.Squares[0].Piece.moved == false) {
                            output += "q";
                            spacer = " ";
                        }
                    }
                }
            }
        }
		
		if (output.endsWith("/"))
			output = output.substring(0, output.length() - 1);
		
		if (board.enPassantPosition != 0) 
			output += spacer + GetColumnFromByte((byte) (board.enPassantPosition % 8)) + "" + (byte) (8 - (byte) (board.enPassantPosition / 8)) + " ";
		else
			output += spacer + "- ";
		
		if (!boardOnly) {
			output += board.fiftyMove + " ";
			output += board.moveCount + 1;
		}
		return output.trim();
		
	}
}
