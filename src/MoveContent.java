public class MoveContent {

	public PieceMoving MovingPiecePrimary;
	public PieceMoving MovingPieceSecondary;
	public PieceTaken TakenPiece;
	public chessPiece.ChessPieceType PawnPromotedTo;
	public boolean enPassantOccured;
	public boolean PawnPromoted;

	public boolean DoubleRowQueen;
	public boolean DoubleColQueen;

	public boolean DoubleRowRook;
	public boolean DoubleColRook;

	public boolean DoubleRowKnight;
	public boolean DoubleColKnight;

	public String PgnMove;

	public String ToString() {
		
		if (!(PgnMove == null)) return PgnMove;

		byte srcCol = (byte) (MovingPiecePrimary.SrcPosition % 8);
		byte srcRow = (byte) (8 - (MovingPiecePrimary.SrcPosition / 8));
		byte dstCol = (byte) (MovingPiecePrimary.DstPosition % 8);
		byte dstRow = (byte) (8 - (MovingPiecePrimary.DstPosition / 8));

		if (MovingPieceSecondary.PieceType == chessPiece.ChessPieceType.Rook) {
			if (MovingPieceSecondary.PieceColour == chessPiece.ChessPieceColour.Black) {
				if (MovingPieceSecondary.SrcPosition == 7) {
					PgnMove += "O-O";
				} else if (MovingPieceSecondary.SrcPosition == 0) {
					PgnMove += "O-O-O";
				}
			} else if (MovingPieceSecondary.PieceColour == chessPiece.ChessPieceColour.White) {
				if (MovingPieceSecondary.SrcPosition == 63) {
					PgnMove += "O-O";
				} else if (MovingPieceSecondary.SrcPosition == 56) {
					PgnMove += "O-O-O";
				}
			}
		} else {
			PgnMove += GetPgnMove(MovingPiecePrimary.PieceType);

			switch (MovingPiecePrimary.PieceType) {
			case Knight:
				PgnMove += GetColumnFromInt(srcCol + 1);
				PgnMove += srcRow;
				break;

			case Rook:
				PgnMove += GetColumnFromInt(srcCol + 1);
				PgnMove += srcRow;
				break;

			case Pawn:
				if (srcCol != dstCol) {
					PgnMove += GetColumnFromInt(srcCol + 1);
				}
				break;

			default:
				break;
			}

			if (TakenPiece.PieceType != chessPiece.ChessPieceType.None)
				PgnMove += "x";

			PgnMove += GetColumnFromInt(dstCol + 1);

			PgnMove += dstRow;

			if (PawnPromotedTo == chessPiece.ChessPieceType.Queen)
				PgnMove += "=Q";
			else if (PawnPromotedTo == chessPiece.ChessPieceType.Rook)
				PgnMove += "=R";
			else if (PawnPromotedTo == chessPiece.ChessPieceType.Bishop)
				PgnMove += "=B";
			else if (PawnPromotedTo == chessPiece.ChessPieceType.Knight)
				PgnMove += "=N";
		}

		return PgnMove;
	}
	
	public String GeneratePGNString(Board board)
    {
		if (!(PgnMove == null)) return PgnMove;

        boolean doubleColumn = false;
        boolean doubleRow = false;


        boolean doubleDestination = false;

        byte srcCol = (byte)(MovingPiecePrimary.SrcPosition % 8);
        byte srcRow = (byte)(8 - (MovingPiecePrimary.SrcPosition / 8));
        byte dstCol = (byte)(MovingPiecePrimary.DstPosition % 8);
        byte dstRow = (byte)(8 - (MovingPiecePrimary.DstPosition / 8));

        PgnMove = "";

        for (byte x = 0; x < 64; x++)
        {
            if (x == MovingPiecePrimary.DstPosition)
                continue;
            
            Square square = board.Squares[x];

            if (square.Piece.PieceType == chessPiece.ChessPieceType.None)
                continue;

            
            if (square.Piece.PieceType == MovingPiecePrimary.PieceType)
            {
                if (square.Piece.PieceColour == MovingPiecePrimary.PieceColour)
                {
                    for (Byte move : square.Piece.validMoves)
                    {
                    	if (move == null) continue;
                        if ((byte) move == MovingPiecePrimary.DstPosition)
                        {
                            doubleDestination = true;

                            byte col = (byte)(x % 8);
                            byte row = (byte)(8-(x / 8));
                           
                            if (col == srcCol)
                            {
                                doubleColumn = true;
                            }

                            if (row == srcRow)
                            {
                                doubleRow = true;
                            }
                           
                            break;
                        }
                    }

                    
                }
            }
        }


        if (MovingPieceSecondary.PieceType == chessPiece.ChessPieceType.Rook)
        {
            if (MovingPieceSecondary.PieceColour == chessPiece.ChessPieceColour.Black)
            {
                if (MovingPieceSecondary.SrcPosition == 7)
                {
                    PgnMove += "O-O";
                }
                else if (MovingPieceSecondary.SrcPosition == 0)
                {
                    PgnMove += "O-O-O";
                }
            }
            else if (MovingPieceSecondary.PieceColour == chessPiece.ChessPieceColour.White)
            {
                if (MovingPieceSecondary.SrcPosition == 63)
                {
                    PgnMove += "O-O";
                }
                else if (MovingPieceSecondary.SrcPosition == 56)
                {
                    PgnMove += "O-O-O";
                }
            }
        }
        else
        {
            PgnMove += GetPgnMove(MovingPiecePrimary.PieceType);

            switch (MovingPiecePrimary.PieceType)
            {
                case Knight:
                    {
                        if (doubleDestination)
                        {
                            if (!doubleColumn)
                            {
                                PgnMove += GetColumnFromInt(srcCol);
                            }
                            else
                            {
                                if (doubleRow)
                                {
                                    PgnMove += GetColumnFromInt(srcCol);
                                }

                                PgnMove += srcRow;
                            }
                        }
                        break;
                    }
                case Bishop:
                    {
                        if (doubleDestination)
                        {
                            if (!doubleColumn)
                            {
                                PgnMove += GetColumnFromInt(srcCol);
                            }
                            else
                            {
                                if (doubleRow)
                                {
                                    PgnMove += GetColumnFromInt(srcCol);
                                }

                                PgnMove += srcRow;
                            }
                        }
                        break;
                    }
                case Rook:
                    {
                        if (doubleDestination)
                        {
                            if (!doubleColumn)
                            {
                                PgnMove += GetColumnFromInt(srcCol);
                            }
                            else
                            {
                                if (doubleRow)
                                {
                                    PgnMove += GetColumnFromInt(srcCol);
                                }

                                PgnMove += srcRow;
                            }
                        }
                        break;
                    }
                case Queen:
                    {
                        if (doubleDestination)
                        {
                            if (!doubleColumn)
                            {
                                PgnMove += GetColumnFromInt(srcCol);
                            }
                            else
                            {
                                if (doubleRow)
                                {
                                    PgnMove += GetColumnFromInt(srcCol);
                                }

                                PgnMove += srcRow;
                            }
                        }
                        break;
                    }
                case Pawn:
                    {
                        if (doubleDestination && srcCol != dstCol)
                        {
                            PgnMove += GetColumnFromInt(srcCol);
                        }
                        else if (TakenPiece.PieceType != chessPiece.ChessPieceType.None)
                        {
                            PgnMove += GetColumnFromInt(srcCol);
                        }
                        break;
                    }
			default:
				break;
            }

            if (TakenPiece.PieceType != chessPiece.ChessPieceType.None)
            {
                
                PgnMove += "x";
            }

            PgnMove += GetColumnFromInt(dstCol);

            PgnMove += dstRow;

            if (PawnPromotedTo == chessPiece.ChessPieceType.Queen)
            {
                PgnMove += "=Q";
            }
            else if (PawnPromotedTo == chessPiece.ChessPieceType.Rook)
            {
                PgnMove += "=R";
            }
            else if (PawnPromotedTo == chessPiece.ChessPieceType.Bishop)
            {
                PgnMove += "=B";
            }
            else if (PawnPromotedTo == chessPiece.ChessPieceType.Knight)
            {
                PgnMove += "=N";
            }
        }

        return PgnMove;
    }

	public static byte GetBoardIndex(int col , int row) {
		return (byte) (col + (row * 8));
	}
	
	private static String GetColumnFromInt(int column) {

		switch (column) {
		case 1:
			return "a";

		case 2:
			return "b";

		case 3:
			return "c";

		case 4:
			return "d";

		case 5:
			return "e";

		case 6:
			return "f";

		case 7:
			return "g";

		case 8:
			return "h";

		default:
			return "Unknown";
		}
	}
	
	private static int GetIntFromColumn(char column) {
		switch (column) {
		case 'a':
			return 1;

		case 'b':
			return 2;

		case 'c':
			return 3;

		case 'd':
			return 4;

		case 'e':
			return 5;

		case 'f':
			return 6;

		case 'g':
			return 7;

		case 'h':
			return 8;

		default:
			return -1;
		}
	}
	
	private static chessPiece.ChessPieceType GetPieceType(char c) {
		switch (c) {
		case 'B':
            return chessPiece.ChessPieceType.Bishop;
        case 'K':
            return chessPiece.ChessPieceType.King;
        case 'N':
            return chessPiece.ChessPieceType.Knight;
        case 'Q':
            return chessPiece.ChessPieceType.Queen;
        case 'R':
            return chessPiece.ChessPieceType.Rook;
        default:
            return chessPiece.ChessPieceType.None;
		}
	}

	private static String GetPgnMove(chessPiece.ChessPieceType pieceType) {

		switch (pieceType) {
		case Bishop:
			return "B";

		case King:
			return "K";

		case Knight:
			return "N";

		case Queen:
			return "Q";

		case Rook:
			return "R";

		default:
			return "";
		}
	}

	MoveContent() {
		
		
		
		MovingPiecePrimary = new PieceMoving(chessPiece.ChessPieceType.None);
		MovingPieceSecondary = new PieceMoving(chessPiece.ChessPieceType.None);
		TakenPiece = new PieceTaken(chessPiece.ChessPieceType.None);
	}

	MoveContent(MoveContent moveContent) {
		
		MovingPiecePrimary = new PieceMoving(moveContent.MovingPiecePrimary);
		MovingPieceSecondary = new PieceMoving(moveContent.MovingPieceSecondary);

		TakenPiece = new PieceTaken(moveContent.TakenPiece.PieceColour,
				moveContent.TakenPiece.PieceType, moveContent.TakenPiece.Moved,
				moveContent.TakenPiece.Position);

		enPassantOccured = moveContent.enPassantOccured;
		PawnPromotedTo = moveContent.PawnPromotedTo;
	}
	
	 public MoveContent(String move)
     {
		 
		 MovingPiecePrimary = new PieceMoving(chessPiece.ChessPieceType.None);
         int srcCol =-1;
         
         boolean comment = false;
         boolean srcFound = false;

         if (move.contains("=Q"))
         {
             PawnPromotedTo = chessPiece.ChessPieceType.Queen;
         }
         else if (move.contains("=N"))
         {
             PawnPromotedTo = chessPiece.ChessPieceType.Knight;
         }
         else if (move.contains("=R"))
         {
             PawnPromotedTo = chessPiece.ChessPieceType.Rook;
         }
         else if (move.contains("=B"))
         {
             PawnPromotedTo = chessPiece.ChessPieceType.Bishop;
         }

         for (Character c : move.toCharArray())
         {
             if (c=='{')
             {
                 comment = true;
                 continue;
             }
             if (c == '}')
             {
                 comment = false;
                 continue;
             }

             if (comment)
             {
                 continue;
             }
    
             if (MovingPiecePrimary.PieceType == chessPiece.ChessPieceType.None)
             {
                 //Get Piece Type
                 MovingPiecePrimary.PieceType = GetPieceType(c);

                 if (MovingPiecePrimary.PieceType == chessPiece.ChessPieceType.None)
                 {
                     MovingPiecePrimary.PieceType = chessPiece.ChessPieceType.Pawn;

                     //This is a column character
                     srcCol= GetIntFromColumn(c);
                 }
                 continue;
             }
             if (srcCol < 0)
             {
                 srcCol = GetIntFromColumn(c);
                 continue;
             }
             if (srcCol >= 0)
             {
                 int srcRow = Integer.parseInt((c.toString()));

                 if (!srcFound)
                 {
                     MovingPiecePrimary.SrcPosition = GetBoardIndex(srcCol, 8 - srcRow);
                     srcFound = true;
                 }
                 else
                 {
                     MovingPiecePrimary.DstPosition = GetBoardIndex(srcCol, 8 - srcRow);
                 }

                 srcCol = -1;
                 continue;
             }
         }
     }
}
