public class PieceTaken {

	public boolean Moved;
	public chessPiece.ChessPieceColour PieceColour;
	public chessPiece.ChessPieceType PieceType;
	public byte Position;
	
	public PieceTaken(chessPiece.ChessPieceColour pieceColor, chessPiece.ChessPieceType pieceType, 
			boolean moved,byte position)
		 {
		  PieceColour = pieceColor;
		  PieceType = pieceType;
		  Position = position;
		  Moved = moved;
		 }

		 public PieceTaken(chessPiece.ChessPieceType pieceType)
		 {
		  PieceColour = chessPiece.ChessPieceColour.White;
		  PieceType = pieceType;
		  Position = 0;
		  Moved = false;
		 }
}
