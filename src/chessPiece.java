import java.util.Stack;

public class chessPiece {

	public enum ChessPieceColour {
		White, Black;
	}

	public enum ChessPieceType {
		King, Queen, Rook, Bishop, Knight, Pawn, None;
	}

	public ChessPieceColour PieceColour;
	public ChessPieceType PieceType;

	public short pieceValue;
	public short attackedValue, defendedValue, pieceActionValue;

	public int LastValidMoveCount;

	public boolean selected;
	public boolean moved;

	Stack<Byte> validMoves;

	chessPiece() {
		
		PieceColour = ChessPieceColour.White;
		PieceType = ChessPieceType.None;
		moved = false;
				
	}
	
	// Constructor for move generation.
	chessPiece(chessPiece piece) {

		PieceColour = piece.PieceColour;
		PieceType = piece.PieceType;
		moved = piece.moved;
		pieceValue = piece.pieceValue;

		if (piece.validMoves != null)
			LastValidMoveCount = piece.validMoves.size();
		
	}

	// Constructor to initiate the chess board with chess pieces.
	chessPiece(ChessPieceType chessPieceType, ChessPieceColour chessPieceColour) {

		PieceType = chessPieceType;
		PieceColour = chessPieceColour;
		
		if (PieceType == ChessPieceType.Pawn || PieceType == ChessPieceType.Knight) 
			LastValidMoveCount = 2;
		else 
			LastValidMoveCount = 0;

		validMoves = new Stack<Byte>();
		validMoves.setSize(LastValidMoveCount);

		pieceValue = calculatePieceValue(PieceType);
		pieceActionValue = calculatePieceActionValue(PieceType);
	}
	
	 static String GetPieceTypeShort (ChessPieceType pieceType)
    {
        switch (pieceType)
        {
            case Pawn:
                {
                    return "P";
                }
            case Knight:
                {
                    return "N";
                }
            case Bishop:
                {
                    return "B";
                }
            case Rook:
                {
                    return "R";
                }

            case Queen:
                {
                    return "Q";
                }

            case King:
                {
                    return "K";
                }
            default:
                {
                    return "P";
                }
        }
    }
	 
	 public static chessPiece GetChessPiece(char type) {
		 
		 chessPiece piece = new chessPiece();
		 
		 switch (type) {
		 case 'P':
			 piece.PieceType = ChessPieceType.Pawn;
			 piece.PieceColour = ChessPieceColour.White;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 piece.validMoves = new Stack<Byte>();
			 break;
		 case 'N':
			 piece.PieceType = ChessPieceType.Knight;
			 piece.PieceColour = ChessPieceColour.White;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'B':
			 piece.PieceType = ChessPieceType.Bishop;
			 piece.PieceColour = ChessPieceColour.White;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 piece.moved = true;
			 break;
		 case 'R':
			 piece.PieceType = ChessPieceType.Rook;
			 piece.PieceColour = ChessPieceColour.White;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'Q':
			 piece.PieceType = ChessPieceType.Queen;
			 piece.PieceColour = ChessPieceColour.White;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'K':
			 piece.PieceType = ChessPieceType.Knight;
			 piece.PieceColour = ChessPieceColour.White;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'p':
			 piece.PieceType = ChessPieceType.Pawn;
			 piece.PieceColour = ChessPieceColour.Black;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'n':
			 piece.PieceType = ChessPieceType.Knight;
			 piece.PieceColour = ChessPieceColour.Black;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'b':
			 piece.PieceType = ChessPieceType.Bishop;
			 piece.PieceColour = ChessPieceColour.Black;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'r':
			 piece.PieceType = ChessPieceType.Rook;
			 piece.PieceColour = ChessPieceColour.Black;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'q':
			 piece.PieceType = ChessPieceType.Queen;
			 piece.PieceColour = ChessPieceColour.Black;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 case 'k':
			 piece.PieceType = ChessPieceType.Knight;
			 piece.PieceColour = ChessPieceColour.Black;
			 piece.pieceActionValue = calculatePieceActionValue(piece.PieceType);
			 break;
		 }
		 return piece;
	 }

	// Sets each pieces value.
	public static short calculatePieceValue(ChessPieceType pieceType) {

		switch (pieceType) {
		case Pawn:
			return 100;
		case Knight:
			return 320;
		case Bishop:
			return 325;
		case Rook:
			return 500;
		case Queen:
			return 975;
		case King:
			return 32767;
		default:
			return 0;
		}
	}

	// Sets each pieces action value.
	public static short calculatePieceActionValue(ChessPieceType pieceType) {

		switch (pieceType) {
		case Pawn:
			return 6;
		case Knight:
			return 3;
		case Bishop:
			return 3;
		case Rook:
			return 2;
		case Queen:
			return 1;
		case King:
			return 1;
		default:
			return 0;
		}
	}
	
	public boolean isPawn(ChessPieceType type) {
		if (type == ChessPieceType.Pawn)
			return true;
		return false;
	}
	
	public static boolean isKnight(ChessPieceType type) {
		if (type == ChessPieceType.Knight)
			return true;
		return false;
	}
	
	public boolean isBishop(ChessPieceType type) {
		if (type == ChessPieceType.Bishop)
			return true;
		return false;
	}
	
	public boolean isRook(ChessPieceType type) {
		if (type == ChessPieceType.Rook)
			return true;
		return false;
	}
	
	public boolean isQueen(ChessPieceType type) {
		if (type == ChessPieceType.Queen)
			return true;
		return false;
	}
	
	public boolean isKing(ChessPieceType type) {
		if (type == ChessPieceType.King)
			return true;
		return false;
	}
	
	public int getValidMovesCount(Board board, int srcPosition) {
		int count = 0;
		for (Byte bs : board.Squares[srcPosition].Piece.validMoves) {
			if (bs != null)count++;
		}
		return count;
	}
	public String ToString() {
		return PieceType + " " + PieceColour + " " + pieceValue + " " + validMoves.size() + " " + attackedValue + " " + defendedValue;
	}
}