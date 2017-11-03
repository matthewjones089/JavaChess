import java.util.ArrayList;
import java.util.Stack;

public class Engine {

	
	public ArrayList<OpeningMove> CurrentGameBook;
	public ArrayList<OpeningMove> UndoGameBook;
	
	private Board ChessBoard;
	// Enables easy undo functionality.
	private Board PreviousChessBoard;
	private Board UndoChessBoard;
	
	//public chessPiece.ChessPieceColour WhosMove;
	
	public Stack<MoveContent> MoveHistory;
	private ArrayList<OpeningMove> OpeningBook;
	
	public String pvLine;
	
	public enum Difficulty {
		Easy, Medium, Hard, VeryHard;
	}
	
	public enum TimeSettings {
		Moves40In5Minutes, Moves40In10Minutes, Moves40In20Minutes, Moves40In0Minutes, Moves40In40Minutes,
		Moves40In60Minutes, Moves40In90Minutes;
	}
	
	public chessPiece.ChessPieceType PromoteToPieceType = chessPiece.ChessPieceType.Queen;
	public PiecesTaken PiecesTakenCount = new PiecesTaken();
	
	public chessPiece.ChessPieceColour HumanPlayer;
	public boolean Thinking;
	public boolean TrainingMode;
	
	public int NodesSearched;
	public int NodesQuiessence;
	public byte PlyDepthSearched;
	public byte PlyDepthReached;
	public byte RootMovesSearched;
	
	public TimeSettings GameTimeSettings;
	
	public String GetFEN() {
		return Board.Fen(false, ChessBoard);
	}
	
	public MoveContent GetLastMove() {
		return ChessBoard.lastMove;
	}
	
	public Difficulty GetGameDifficulty() {
		if (PlyDepthSearched == 3)
			return Difficulty.Easy;
		else if (PlyDepthSearched == 5)
			return Difficulty.Medium;
		else if (PlyDepthSearched == 6)
			return Difficulty.Hard;
		else if(PlyDepthSearched == 7)
			return Difficulty.VeryHard;
		
		return Difficulty.Medium;
	}
	
	public void SetGameDifficulty(Difficulty value) {
		if (value == Difficulty.Easy) {
			PlyDepthSearched = 3;
			GameTimeSettings = TimeSettings.Moves40In10Minutes;
		} else if (value == Difficulty.Medium) {
			PlyDepthSearched = 5;
			GameTimeSettings = TimeSettings.Moves40In20Minutes;
		} else if (value == Difficulty.Hard) {
			PlyDepthSearched = 6;
			GameTimeSettings = TimeSettings.Moves40In60Minutes;
		} else if (value == Difficulty.VeryHard) {
			PlyDepthSearched = 7;
			GameTimeSettings = TimeSettings.Moves40In90Minutes;
		}	
	}

	public chessPiece.ChessPieceColour getWhosMove() {
		return ChessBoard.WhosMove;
	}
	public void setWhosMove(chessPiece.ChessPieceColour value) {
		ChessBoard.WhosMove = value;
	}
	
	public boolean GetFiftyMoveActive() {
		return ChessBoard.fiftyMoveActive;
	}
	
	public void SetFiftyMoveActive(boolean value) {
		ChessBoard.fiftyMoveActive = value;
	}
	
	public boolean GetRepeatedMoveActive() {
		return ChessBoard.repeatedMoveActive;
	}
	
	public void SetRepeatedMoveActive(boolean value) {
		ChessBoard.repeatedMoveActive = value;
	}
	
	public boolean GetRepeatedMove() {
		if (ChessBoard.repeatedMove >= 3)
			return true;
		
		return false;
	}
	
	public boolean GetStaleMate() {
		return ChessBoard.staleMate;
	}
	
	public void SetStaleMate(boolean value) {
		ChessBoard.staleMate = value;
	}
	public String GetPvLine() {
		return pvLine;
	}
	
	public boolean GetFiftyMove() {
		if (ChessBoard.fiftyMove >= 50)
			return true;
		
		return false;
	}
	
	public boolean GetInsufficientMaterial() {
		return ChessBoard.InsufficientMaterial;
	}
	
	public void SetInsufficientMaterialActive(boolean value) {
		ChessBoard.InsufficientMaterialActive = value;
	}
	
	public boolean GetInsufficientMaterialActive() {
		return ChessBoard.InsufficientMaterialActive;
	}
	
	public Board GetChessBoard() {
		return ChessBoard;
	}
	
	public Engine() {
		InitiateEngine();
		InitiateBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}
	
	public Engine(String fen) {
		InitiateEngine();
		InitiateBoard(fen);
	}
	
	public void NewGame() {
		InitiateEngine();
		InitiateBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}
	
	public void InitiateBoard(String fen) {
		ChessBoard = new Board(fen);
		
		if (!(fen == null)) {
			PieceValidMoves.GenerateValidMoves(ChessBoard);
			Evaluation.EvaluateBoardScore(ChessBoard);
		}
	}
	
	private void InitiateEngine() {
		SetGameDifficulty(Difficulty.Medium);
		
		MoveHistory = new Stack<MoveContent>();
		HumanPlayer = chessPiece.ChessPieceColour.White;
		OpeningBook = new ArrayList<OpeningMove>();
		CurrentGameBook = new ArrayList<OpeningMove>();
		PieceMoves.InitiateChessPieceMotion();
		Book.LoadOpeningBook();
	}
	
	public void SetChessPieceSelection(byte boardColumn, byte boardRow,
            boolean selection)
	{
		byte index = GetBoardIndex(boardColumn, boardRow);
		
		if (ChessBoard.Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
		{
			return;
		}
		if (ChessBoard.Squares[index].Piece.PieceColour != HumanPlayer)
		{
			return;
		}
		if (ChessBoard.Squares[index].Piece.PieceColour != getWhosMove())
		{
			return;
		}
		ChessBoard.Squares[index].Piece.selected = selection;
	}
	
	public int ValidateOpeningBook()
    {
        return Book.ValidateOpeningBook(OpeningBook);
    }
	
	private static boolean CheckForMate(chessPiece.ChessPieceColour whosTurn, Board chessBoard)
    {
		
		//System.out.println("Engine: CheckForMate");
		
        Search.SearchForMate(whosTurn, chessBoard, chessBoard.blackMate,
                              chessBoard.whiteMate,  chessBoard.staleMate);

        if (chessBoard.blackMate || chessBoard.whiteMate || chessBoard.staleMate)
        {
            return true;
        }

        return false;
    }
	
	private static boolean FindPlayBookMove(MoveContent bestMove, Board chessBoard, Iterable<OpeningMove> openingBook)
    {
		
		//System.out.println("Engine: FindPlayBookMove");
		
        //Get the Hash for the current Board;
        String boardFen= Board.Fen(true, chessBoard);

        //Check the Opening Move Book
        for (OpeningMove move : openingBook)
        {
            if (move.StartingFEN.contains(boardFen))
            {
                int index = 0;
/*
                if (move.Moves.Count > 1)
                {
                    Random random = new Random(DateTime.Now.Second);
                    index = random.Next(move.Moves.Count - 1);
                }
*/
                bestMove = move.Moves.get(index);
                return true;
            }
        }

        return false;
    }
	
	public void Undo()
    {
		
		//System.out.println("Engine: Undo");
		
        if (UndoChessBoard != null)
        {
            PieceTakenRemove(ChessBoard.lastMove);
            PieceTakenRemove(PreviousChessBoard.lastMove);

            ChessBoard = new Board(UndoChessBoard);
            CurrentGameBook = new ArrayList<OpeningMove>(UndoGameBook);

            PieceValidMoves.GenerateValidMoves(ChessBoard);
            Evaluation.EvaluateBoardScore(ChessBoard);
        }
    }
	
	public static byte GetBoardIndex(byte boardColumn, byte boardRow)
    {
        return (byte)(boardColumn + (boardRow * 8));
    }
	
	public byte[] GetEnPassantMoves()
    {
        if (ChessBoard == null)
        {
            return null;
        }

        byte[] returnArray = new byte[2];

        returnArray[0] = (byte)(ChessBoard.enPassantPosition % 8);
        returnArray[1] = (byte)(ChessBoard.enPassantPosition / 8);

        return returnArray;
    }
	
	public boolean GetBlackMate()
    {
        if (ChessBoard == null)
        {
            return false;
        }

        return ChessBoard.blackMate;
    }
	
	public boolean GetWhiteMate()
    {
        return ChessBoard.whiteMate;
    }

    public boolean GetBlackCheck()
    {
        return ChessBoard.blackCheck;
    }

    public boolean GetWhiteCheck()
    {
        return ChessBoard.whiteCheck;
    }

    public byte GetrepeatedMove()
    {
        return ChessBoard.repeatedMove;
    }

    public byte GetFiftyMoveCount()
    {
        return ChessBoard.fiftyMove;
    }

    public Stack<MoveContent> GetMoveHistory()
    {
        return MoveHistory;
    }
    
    public MoveContent GetLastNonPlayerMove() {
    	for (int i = GetMoveHistory().size() - 1; i >= 0; i--) {
    			MoveContent move = GetMoveHistory().get(i);
    			if (move.MovingPiecePrimary.PieceColour != HumanPlayer) {
    				return move;
    			}
    	}
    	return null;
    }
	
	public chessPiece.ChessPieceType GetPieceTypeAt(byte boardColumn, byte boardRow)
    {
        byte index = GetBoardIndex(boardColumn, boardRow);

        if (ChessBoard.Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
        {
            return chessPiece.ChessPieceType.None;
        }

        return ChessBoard.Squares[index].Piece.PieceType;
    }
	
	public void SetPieceTypeAt(chessPiece.ChessPieceType pieceType, byte col, byte row) {
		byte index = GetBoardIndex(col , row);
		ChessBoard.Squares[index].Piece.PieceType = pieceType;
	}
	
	public void SetPieceAt(chessPiece piece, byte boardColumn, byte boardRow) {
		byte index = GetBoardIndex(boardColumn, boardRow);
		ChessBoard.Squares[index].Piece = piece;
	}
	
	public chessPiece.ChessPieceType GetPieceTypeAt(byte index)
    {
        if (ChessBoard.Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
        {
            return chessPiece.ChessPieceType.None;
        }

        return ChessBoard.Squares[index].Piece.PieceType;
    }
	
	public chessPiece.ChessPieceColour GetPieceColorAt(byte boardColumn, byte boardRow)
    {
        byte index = GetBoardIndex(boardColumn, boardRow);

        if (ChessBoard.Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
        {
            return chessPiece.ChessPieceColour.White;
        }
        return ChessBoard.Squares[index].Piece.PieceColour;
    }
	
	public chessPiece.ChessPieceColour GetPieceColorAt(byte index)
    {
        if (ChessBoard.Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
        {
            return chessPiece.ChessPieceColour.White;
        }
        return ChessBoard.Squares[index].Piece.PieceColour;
    }
	
	 public boolean GetChessPieceSelected(byte boardColumn, byte boardRow)
     {
         byte index = GetBoardIndex(boardColumn, boardRow);

         if (ChessBoard.Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
         {
             return false;
         }

         return ChessBoard.Squares[index].Piece.selected;
     }

     public void GenerateValidMoves()
     {
         PieceValidMoves.GenerateValidMoves(ChessBoard);
     }

     public int EvaluateBoardScore()
     {
         Evaluation.EvaluateBoardScore(ChessBoard);
         return ChessBoard.score;
     }

     public byte[][] GetValidMoves(byte boardColumn, byte boardRow)
     {
         byte index = GetBoardIndex(boardColumn, boardRow);

         if (ChessBoard.Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
         {
             return null;
         }

         byte[][] returnArray = new byte[ChessBoard.Squares[index].Piece.validMoves.size()][];
         int counter = 0;

         for (byte square : ChessBoard.Squares[index].Piece.validMoves)
         {
             returnArray[counter] = new byte[2];
             returnArray[counter][0] = (byte)(square % 8);
             returnArray[counter][1] = (byte)(square /8);
             counter++;
         }

         return returnArray;
     }

     public int GetScore()
     {
         return ChessBoard.score;
     }
     
     public byte FindSourcePositon(chessPiece.ChessPieceType chessPieceType, chessPiece.ChessPieceColour chessPieceColor, byte dstPosition, boolean capture, int forceCol, int forceRow)
     {
         Square square;

         if (dstPosition == ChessBoard.enPassantPosition && chessPieceType == chessPiece.ChessPieceType.Pawn)
         {
             if (chessPieceColor == chessPiece.ChessPieceColour.White)
             {
                 square = ChessBoard.Squares[dstPosition + 7];

                 if (square.Piece.PieceType != chessPiece.ChessPieceType.None)
                 {
                     if (square.Piece.PieceType == chessPiece.ChessPieceType.Pawn)
                     {
                         if (square.Piece.PieceColour == chessPieceColor)
                         {
                             if ((dstPosition + 7) % 8 == forceCol || forceCol == -1)
                             {
                                 return (byte)(dstPosition + 7);
                             }
                             
                         }
                     }
                 }

                 square = ChessBoard.Squares[dstPosition + 9];

                 if (square.Piece.PieceType != chessPiece.ChessPieceType.None)
                 {
                     if (square.Piece.PieceType == chessPiece.ChessPieceType.Pawn)
                     {
                         if (square.Piece.PieceColour == chessPieceColor)
                         {
                             if ((dstPosition + 9) % 8 == forceCol || forceCol == -1)
                             {
                                 return (byte) (dstPosition + 9);
                             }
                         }
                     }
                 }
             }
             else 
             {
                 if (dstPosition - 7 >= 0)
                 {
                     square = ChessBoard.Squares[dstPosition - 7];

                     if (square.Piece.PieceType != chessPiece.ChessPieceType.None)
                     {
                         if (square.Piece.PieceType == chessPiece.ChessPieceType.Pawn)
                         {
                             if (square.Piece.PieceColour == chessPieceColor)
                             {
                                 if ((dstPosition - 7)%8 == forceCol || forceCol == -1)
                                 {
                                     return (byte) (dstPosition - 7);
                                 }
                             }
                         }
                     }
                 }
                 if (dstPosition - 9 >= 0)
                 {
                     square = ChessBoard.Squares[dstPosition - 9];

                     if (square.Piece.PieceType != chessPiece.ChessPieceType.None)
                     {
                         if (square.Piece.PieceType == chessPiece.ChessPieceType.Pawn)
                         {
                             if (square.Piece.PieceColour == chessPieceColor)
                             {
                                 if ((dstPosition - 9)%8 == forceCol || forceCol == -1)
                                 {
                                     return (byte) (dstPosition - 9);
                                 }
                             }
                         }
                     }
                 }
             }
         }

         for (byte x = 0; x < 64; x++)
         {
             square = ChessBoard.Squares[x];

             if (square.Piece.PieceType == chessPiece.ChessPieceType.None)
                 continue;
             if (square.Piece.PieceType != chessPieceType)
                 continue;
             if (square.Piece.PieceColour != chessPieceColor)
                 continue;
            
             for (byte move : square.Piece.validMoves)
             {
                 if (move == dstPosition)
                 {
                     if (!capture)
                     {
                         if ((byte)(x / 8) == (forceRow) || forceRow == -1)
                         {
                             if (x%8 == forceCol || forceCol == -1)
                             {
                                 return x;
                             }
                         }
                     }
                             
                     //Capture
                     if (ChessBoard.Squares[dstPosition].Piece.PieceType != chessPiece.ChessPieceType.None)
                     {
                         if (ChessBoard.Squares[dstPosition].Piece.PieceColour != chessPieceColor)
                         {
                             if (x % 8 == forceCol || forceCol == -1)
                             {
                                 if ((byte)(x / 8) == (forceRow) || forceRow == -1)
                                 {
                                     return x;
                                 }
                             }
                         }
                     }
                 }
             }
         }

         return 0;
     }

     public boolean IsValidMove(byte srcPosition, byte dstPosition)
     {
         if (ChessBoard == null)
         {
             return false;
         }

         if (ChessBoard.Squares == null)
         {
             return false;
         }

         if (ChessBoard.Squares[srcPosition].Piece.PieceType == chessPiece.ChessPieceType.None)
         {
             return false;
         }

         for (Byte bs : ChessBoard.Squares[srcPosition].Piece.validMoves)
         {
        	 if (bs == null) continue;
             if (bs == dstPosition)
             {
                 return true;
             }
         }

         if (dstPosition == ChessBoard.enPassantPosition)
         {
             return true;
         }

         return false;
     }

     public boolean IsValidMove(byte sourceColumn, byte sourceRow, byte destinationColumn, byte destinationRow)
     {
         if (ChessBoard == null)
         {
             return false;
         }

         if (ChessBoard.Squares == null)
         {
             return false;
         }

         byte index = GetBoardIndex(sourceColumn, sourceRow);

         if (ChessBoard.Squares[index].Piece.PieceType == chessPiece.ChessPieceType.None)
         {
             return false;
         }

         if (ChessBoard.Squares[index].Piece.PieceColour != HumanPlayer)
         {
             return false;
         }
         
         
         for (Byte bs : ChessBoard.Squares[index].Piece.validMoves)
         {
        	if (bs == null)
        		continue;
        	 
             if ((byte)(bs % 8) == destinationColumn)
             {
                 if ((byte)(bs / 8) == destinationRow)
                 {
                     return true;
                 }
             }
         }

         index = GetBoardIndex(destinationColumn, destinationRow);

         if (index == ChessBoard.enPassantPosition && ChessBoard.enPassantPosition > 0)
         {
             return true;
         }

         return false;
     }

     public boolean IsGameOver()
     {
         if (ChessBoard.staleMate)
         {
             return true;
         }
         if (ChessBoard.whiteMate || ChessBoard.blackMate)
         {
             return true;
         }
         if (ChessBoard.fiftyMove >= 50)
         {
             return true;
         }
         if (ChessBoard.repeatedMove >= 3)
         {
             return true;
         }
         if (ChessBoard.InsufficientMaterialActive) {
	         if (ChessBoard.InsufficientMaterial)
	         {
	             return true;
	         }
         }
         return false;
     }

     public boolean IsTie()
     {
         if (ChessBoard.staleMate)
         {
             return true;
         }
         
         if (ChessBoard.fiftyMove >= 50)
         {
             return true;
         }
         if (ChessBoard.repeatedMove >= 3)
         {
             return true;
         }

         if (ChessBoard.InsufficientMaterialActive) {
	         if (ChessBoard.InsufficientMaterial)
	         {
	             return true;
	         }
         }

         return false;
     }
	
     public boolean MovePiece(byte srcPosition, byte dstPosition)
     {
         chessPiece piece = ChessBoard.Squares[srcPosition].Piece;

         PreviousChessBoard = new Board(ChessBoard);
         UndoChessBoard = new Board(ChessBoard);
         UndoGameBook = new ArrayList<OpeningMove>(CurrentGameBook);

         Board.MovePiece(ChessBoard, srcPosition, dstPosition, PromoteToPieceType);

         ChessBoard.lastMove.GeneratePGNString(ChessBoard);

         PieceValidMoves.GenerateValidMoves(ChessBoard);
         Evaluation.EvaluateBoardScore(ChessBoard);

         //If there is a check in place, check if this is still true;
         if (piece.PieceColour == chessPiece.ChessPieceColour.White)
         {
             if (ChessBoard.whiteCheck)
             {
                 //Invalid Move
                 ChessBoard = new Board(PreviousChessBoard);
                 PieceValidMoves.GenerateValidMoves(ChessBoard);
                 return false;
             }
         }
         else if (piece.PieceColour == chessPiece.ChessPieceColour.Black)
         {
             if (ChessBoard.blackCheck)
             {
                 //Invalid Move
                 ChessBoard = new Board(PreviousChessBoard);
                 PieceValidMoves.GenerateValidMoves(ChessBoard);
                 return false;
             }
         }

         MoveHistory.push(ChessBoard.lastMove);
         FileIO.SaveCurrentGameMove(ChessBoard, PreviousChessBoard, CurrentGameBook, ChessBoard.lastMove);

         CheckForMate(getWhosMove(), ChessBoard);
         PieceTakenAdd(ChessBoard.lastMove);

         if (ChessBoard.whiteMate || ChessBoard.blackMate)
         {
             ChessBoard.lastMove.PgnMove += "#";
         }
         else if (ChessBoard.whiteCheck || ChessBoard.blackCheck)
         {
             ChessBoard.lastMove.PgnMove += "+";
         }

         return true;
     }

     private void PieceTakenAdd(MoveContent lastMove)
     {
         if (lastMove.TakenPiece.PieceType != chessPiece.ChessPieceType.None)
         {
             if (lastMove.TakenPiece.PieceColour == chessPiece.ChessPieceColour.White)
             {
                 if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Queen)
                 {
                     PiecesTakenCount.WhiteQueen++;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Rook)
                 {
                     PiecesTakenCount.WhiteRook++;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Bishop)
                 {
                     PiecesTakenCount.WhiteBishop++;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Knight)
                 {
                     PiecesTakenCount.WhiteKnight++;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Pawn)
                 {
                     PiecesTakenCount.WhitePawn++;
                 }
             }
             if (ChessBoard.lastMove.TakenPiece.PieceColour == chessPiece.ChessPieceColour.Black)
             {
                 if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Queen)
                 {
                     PiecesTakenCount.BlackQueen++;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Rook)
                 {
                     PiecesTakenCount.BlackRook++;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Bishop)
                 {
                     PiecesTakenCount.BlackBishop++;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Knight)
                 {
                     PiecesTakenCount.BlackKnight++;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Pawn)
                 {
                     PiecesTakenCount.BlackPawn++;
                 }
             }
         }
     }

     private void PieceTakenRemove(MoveContent lastMove)
     {
         if (lastMove.TakenPiece.PieceType != chessPiece.ChessPieceType.None)
         {
             if (lastMove.TakenPiece.PieceColour == chessPiece.ChessPieceColour.White)
             {
                 if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Queen)
                 {
                     PiecesTakenCount.WhiteQueen--;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Rook)
                 {
                     PiecesTakenCount.WhiteRook--;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Bishop)
                 {
                     PiecesTakenCount.WhiteBishop--;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Knight)
                 {
                     PiecesTakenCount.WhiteKnight--;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Pawn)
                 {
                     PiecesTakenCount.WhitePawn--;
                 }
             }
             if (lastMove.TakenPiece.PieceColour == chessPiece.ChessPieceColour.Black)
             {
                 if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Queen)
                 {
                     PiecesTakenCount.BlackQueen--;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Rook)
                 {
                     PiecesTakenCount.BlackRook--;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Bishop)
                 {
                     PiecesTakenCount.BlackBishop--;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Knight)
                 {
                     PiecesTakenCount.BlackKnight--;
                 }
                 else if (lastMove.TakenPiece.PieceType == chessPiece.ChessPieceType.Pawn)
                 {
                     PiecesTakenCount.BlackPawn--;
                 }
             }
         }
     }

     public boolean MovePiece(byte sourceColumn, byte sourceRow, byte destinationColumn, byte destinationRow)
     {
         byte srcPosition = (byte)(sourceColumn + (sourceRow * 8));
         byte dstPosition = (byte)(destinationColumn + (destinationRow * 8));

         return MovePiece(srcPosition, dstPosition);
     }

      void SetChessPiece(chessPiece piece, byte index)
     {
         ChessBoard.Squares[index].Piece = new chessPiece(piece);

     }

     public void AiPonderMove()
     {
         Thinking = true;
         NodesSearched = 0;
			
		ResultBoards resultBoards = new ResultBoards();
        resultBoards.Positions = new ArrayList<Board>();

         if (CheckForMate(getWhosMove(), ChessBoard))
         {
             Thinking = false;
				return;
         }

         MoveContent bestMove = new MoveContent();
        
         //If there is no play book move search for the best move
         if (FindPlayBookMove( bestMove, ChessBoard, OpeningBook) == false
             || ChessBoard.fiftyMove > 45 || ChessBoard.repeatedMove >= 2)
         {
             if (FindPlayBookMove(bestMove, ChessBoard, CurrentGameBook) == false ||
                 ChessBoard.fiftyMove > 45 || ChessBoard.repeatedMove >= 2)
             {
					bestMove = Search.IterativeSearch(ChessBoard, PlyDepthSearched, NodesSearched, NodesQuiessence, pvLine, PlyDepthReached, RootMovesSearched, CurrentGameBook);
             }
         }

         //Make the move 
         PreviousChessBoard = new Board(ChessBoard);

         RootMovesSearched = (byte)resultBoards.Positions.size();

         Board.MovePiece(ChessBoard, bestMove.MovingPiecePrimary.SrcPosition, bestMove.MovingPiecePrimary.DstPosition, chessPiece.ChessPieceType.Queen);

         ChessBoard.lastMove.GeneratePGNString(ChessBoard);

         FileIO.SaveCurrentGameMove(ChessBoard, PreviousChessBoard, CurrentGameBook, bestMove);

         for (byte x = 0; x < 64; x++)
         {
             Square sqr = ChessBoard.Squares[x];

             if (sqr.Piece.PieceType == chessPiece.ChessPieceType.None)
                 continue;

             sqr.Piece.defendedValue = 0;
             sqr.Piece.attackedValue = 0;
         }

         PieceValidMoves.GenerateValidMoves(ChessBoard);
         Evaluation.EvaluateBoardScore(ChessBoard);

         PieceTakenAdd(ChessBoard.lastMove);

         MoveHistory.push(ChessBoard.lastMove);

         if (CheckForMate(getWhosMove(), ChessBoard))
         {
             Thinking = false;

             if (ChessBoard.whiteMate || ChessBoard.blackMate)
             {
                 	ChessBoard.lastMove.PgnMove += "#";
             }
				
             return;
         }

         if (ChessBoard.whiteCheck || ChessBoard.blackCheck)
         {
             ChessBoard.lastMove.PgnMove += "+";
         }

         Thinking = false;
     }

     public boolean SaveGame(String filePath)
     {
         return FileIO.SaveGame(filePath, ChessBoard, getWhosMove(), MoveHistory);
     }

     public boolean LoadGame(String filePath)
     {
    	 return FileIO.LoadGame(filePath, ChessBoard, getWhosMove(), MoveHistory, CurrentGameBook, UndoGameBook);
     }

     public boolean LoadOpeningBook()
     {
         OpeningBook = Book.LoadOpeningBook();

         return true;
     }
	
}
