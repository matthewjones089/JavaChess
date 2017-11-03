
public class PieceMoving {

	public byte DstPosition , SrcPosition;
	public boolean Moved;
	public chessPiece.ChessPieceColour PieceColour;
	public chessPiece.ChessPieceType PieceType;
	
	public PieceMoving(chessPiece.ChessPieceColour pieceColour , chessPiece.ChessPieceType pieceType ,
			boolean moved , byte srcPosition , byte dstPosition) {
		PieceColour = pieceColour;
		PieceType = pieceType;
		Moved = moved;
		SrcPosition = srcPosition;
		DstPosition = dstPosition;
	}
	
	public PieceMoving(PieceMoving pieceMoving) {
		PieceColour = pieceMoving.PieceColour;
		PieceType = pieceMoving.PieceType;
		Moved = pieceMoving.Moved;
		SrcPosition = pieceMoving.SrcPosition;
		DstPosition = pieceMoving.DstPosition;
	}
	
	public PieceMoving(chessPiece.ChessPieceType pieceType) {
		PieceType = pieceType;
		PieceColour = chessPiece.ChessPieceColour.White;
		SrcPosition = 0;
		DstPosition = 0;
		Moved = false;
	}
	
}
