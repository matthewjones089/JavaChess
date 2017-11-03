
public class Square {
		public  chessPiece Piece;

		public Square() {
			Piece = new chessPiece();
		}
		
		// Copy chess piece from copied chess board or sets chess piece to null.
		public Square(chessPiece piece) {
			
				Piece = new chessPiece(piece);
		}

}
