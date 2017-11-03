import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Search {
	
	public static int progress;
	
	private static int piecesRemaining;
	
	private static Position[][] KillerMove = new Position[3][20];
    private static int kIndex;

    private static int SideToMoveScore(int score, chessPiece.ChessPieceColour color)
    {
        if (color == chessPiece.ChessPieceColour.Black)
            return -score;

        return score;
    }
    
    private static ResultBoards GetSortValidMoves(Board examineBoard)
    {
    	//System.out.println("Search: GetSortValidMoves");
    	
        ResultBoards succ = new ResultBoards();
                               
        succ.Positions = new ArrayList<Board>(30);
                                

        piecesRemaining = 0;

        for (byte x = 0; x < 64; x++)
        {
            Square sqr = examineBoard.Squares[x];

            //Make sure there is a piece on the square
            if (sqr.Piece.PieceType == chessPiece.ChessPieceType.None)
                continue;

            piecesRemaining++;

            //Make sure the color is the same color as the one we are moving.
            if (sqr.Piece.PieceColour != examineBoard.WhosMove)
                continue;

            //For each valid move for this piece
            for (Byte dst : sqr.Piece.validMoves)
            {
            	if (dst == null) continue;
                //We make copies of the board and move so that we can move it without effecting the parent board
                Board board = examineBoard.FastCopy();

                //Make move so we can examine it
                Board.MovePiece(board, x, dst, chessPiece.ChessPieceType.Queen);

                //We Generate Valid Moves for Board
                PieceValidMoves.GenerateValidMoves(board);

                //Invalid Move
                if (board.whiteCheck && examineBoard.WhosMove == chessPiece.ChessPieceColour.White)
                {
                    continue;
                }

                //Invalid Move
                if (board.blackCheck && examineBoard.WhosMove == chessPiece.ChessPieceColour.Black)
                {
                    continue;
                }

                //We calculate the board score
                Evaluation.EvaluateBoardScore(board);

                //Invert Score to support Negamax
                board.score = SideToMoveScore(board.score, board.WhosMove);

                succ.Positions.add(board);
            }
        }
        
        Collections.sort(succ.Positions, (Board a1, Board a2) -> a2.score - a1.score);
        //succ.Positions.sort(Sort);
        return succ;
    }

    private static int AlphaBeta(Board examineBoard, byte depth, int alpha, int beta, int nodesSearched, int nodesQuiessence, ArrayList<Position> pvLine, boolean extended)
    {
    	
    	//System.out.println("Search: AlphaBeta");
    	
        nodesSearched++;

        if (examineBoard.fiftyMove >= 50 || examineBoard.repeatedMove >= 3)
            return 0;

        //End Main Search with Quiescence
        if (depth == 0)
        {
            if (!extended && examineBoard.blackCheck || examineBoard.whiteCheck)
            {
                depth++;
                extended = true;
            }
            else
            {
                //Perform a Quiessence Search
                return Quiescence(examineBoard, alpha, beta, nodesQuiessence);
            }
        }

        List<Position> positions = EvaluateMoves(examineBoard, depth);

        if (examineBoard.whiteCheck || examineBoard.blackCheck || positions.size() == 0)
        {
            if (SearchForMate(examineBoard.WhosMove, examineBoard, examineBoard.blackMate, examineBoard.whiteMate, examineBoard.staleMate))
            {
                if (examineBoard.blackMate)
                {
                    if (examineBoard.WhosMove == chessPiece.ChessPieceColour.Black)
                        return -32767-depth;

                    return 32767 + depth;
                }
                if (examineBoard.whiteMate)
                {
                    if (examineBoard.WhosMove == chessPiece.ChessPieceColour.Black)
                        return 32767 + depth;

                    return -32767 - depth;
                }

                //If Not Mate then StaleMate
                return 0;
            }
        }

        Collections.sort(positions, (Position a1, Position a2) -> a2.Score - a1.Score);
        //positions.Sort(Sort);

        for (Position move : positions)
        {
            ArrayList<Position> pvChild = new ArrayList<Position>();

            //Make a copy
            Board board = examineBoard.FastCopy();

            //Move Piece
            Board.MovePiece(board, move.SrcPosition, move.DstPosition, chessPiece.ChessPieceType.Queen);

            //We Generate Valid Moves for Board
            PieceValidMoves.GenerateValidMoves(board);

            if (board.blackCheck)
            {
                if (examineBoard.WhosMove == chessPiece.ChessPieceColour.Black)
                {
                    //Invalid Move
                    continue;
                }
            }

            if (board.whiteCheck)
            {
                if (examineBoard.WhosMove == chessPiece.ChessPieceColour.White)
                {
                    //Invalid Move
                    continue;
                }
            }

            int value = -AlphaBeta(board, (byte)(depth - 1), -beta, -alpha, nodesSearched, nodesQuiessence, pvChild, extended);

            if (value >= beta)
            {
                KillerMove[kIndex][depth].SrcPosition = move.SrcPosition;
                KillerMove[kIndex][depth].DstPosition = move.DstPosition;

                kIndex = ((kIndex + 1) % 2);

                
                return beta;
            }
            if (value > alpha)
            {
                Position pvPos = new Position();

                pvPos.SrcPosition = board.lastMove.MovingPiecePrimary.SrcPosition;
                pvPos.DstPosition = board.lastMove.MovingPiecePrimary.DstPosition;
                pvPos.Move = board.lastMove.ToString();

                pvChild.add(0, pvPos);

                pvLine = pvChild;

                alpha = (int)value;
            }
        }

        return alpha;
    }

    private static int Quiescence(Board examineBoard, int alpha, int beta, int nodesSearched)
    {
    	//System.out.println("Search: Quiescence");
    	
        nodesSearched++;

        //Evaluate Score
        Evaluation.EvaluateBoardScore(examineBoard);

        //Invert Score to support Negamax
        examineBoard.score = SideToMoveScore(examineBoard.score, examineBoard.WhosMove);

        if (examineBoard.score >= beta)
            return beta;

        if (examineBoard.score > alpha)
            alpha = examineBoard.score;

        
        List<Position> positions;
      

        if (examineBoard.whiteCheck || examineBoard.blackCheck)
        {
            positions = EvaluateMoves(examineBoard,(byte) 0);
        }
        else
        {
            positions = EvaluateMovesQ(examineBoard);    
        }

        if (positions.size() == 0)
        {
            return examineBoard.score;
        }
        
        Collections.sort(positions, (Position a1, Position a2) -> a2.Score - a1.Score);
        //positions.Sort(Sort);

        for (Position move : positions)
        {
            if (StaticExchangeEvaluation(examineBoard.Squares[move.DstPosition]) >= 0)
            {
                continue;
            }

            //Make a copy
            Board board = examineBoard.FastCopy();

            //Move Piece
            Board.MovePiece(board, move.SrcPosition, move.DstPosition, chessPiece.ChessPieceType.Queen);

            //We Generate Valid Moves for Board
            PieceValidMoves.GenerateValidMoves(board);

            if (board.blackCheck)
            {
                if (examineBoard.WhosMove == chessPiece.ChessPieceColour.Black)
                {
                    //Invalid Move
                    continue;
                }
            }

            if (board.whiteCheck)
            {
                if (examineBoard.WhosMove == chessPiece.ChessPieceColour.White)
                {
                    //Invalid Move
                    continue;
                }
            }

            int value = -Quiescence(board, - beta, -alpha, nodesSearched);

            if (value >= beta)
            {
                KillerMove[2][0].SrcPosition = move.SrcPosition;
                KillerMove[2][0].DstPosition = move.DstPosition;

                return beta;
            }
            if (value > alpha)
            {
                alpha = value;
            }
        }

        return alpha;
    }

    private static List<Position> EvaluateMoves(Board examineBoard, byte depth)
    {
    	
    	//System.out.println("Search: EvaluateMoves");

        //We are going to store our result boards here           
        List<Position> positions = new ArrayList<Position>();

        //bool foundPV = false;


        for (byte x = 0; x < 64; x++)
        {
            chessPiece piece = examineBoard.Squares[x].Piece;

            //Make sure there is a piece on the square
            if (piece.PieceType == chessPiece.ChessPieceType.None)
                continue;

            //Make sure the color is the same color as the one we are moving.
            if (piece.PieceColour != examineBoard.WhosMove)
                continue;

            //For each valid move for this piece
            for (Byte dst : piece.validMoves)
            {
            	if (dst == null) continue;
                Position move = new Position();

                move.SrcPosition = x;
                move.DstPosition = dst;
			///
                if (move.SrcPosition == KillerMove[0][depth].SrcPosition && move.DstPosition == KillerMove[0][depth].DstPosition)
                {
                    //move.TopSort = true;
                    move.Score += 5000;
                    positions.add(move);
                    continue;
                }
                if (move.SrcPosition == KillerMove[1][depth].SrcPosition && move.DstPosition == KillerMove[1][depth].DstPosition)
                {
                    //move.TopSort = true;
                    move.Score += 5000;
                    positions.add(move);
                    continue;
                }

                chessPiece pieceAttacked = examineBoard.Squares[move.DstPosition].Piece;

                //If the move is a capture add it's value to the score
                if (pieceAttacked.PieceType != chessPiece.ChessPieceType.None)
                {
                    move.Score += pieceAttacked.pieceValue;

                    if (piece.pieceValue < pieceAttacked.pieceValue)
                    {
                        move.Score += pieceAttacked.pieceValue - piece.pieceValue;
                    }
                }

                if (!piece.moved)
                {
                    move.Score += 10;
                }

                move.Score += piece.pieceActionValue;

                //Add Score for Castling
                if (!examineBoard.whiteCastled && examineBoard.WhosMove == chessPiece.ChessPieceColour.White)
                {

                    if (piece.PieceType == chessPiece.ChessPieceType.King)
                    {
                        if (move.DstPosition != 62 && move.DstPosition != 58)
                        {
                            move.Score -= 40;
                        }
                        else
                        {
                            move.Score += 40;
                        }
                    }
                    if (piece.PieceType == chessPiece.ChessPieceType.Rook)
                    {
                        move.Score -= 40;
                    }
                }

                if (!examineBoard.blackCastled && examineBoard.WhosMove == chessPiece.ChessPieceColour.Black)
                {
                    if (piece.PieceType == chessPiece.ChessPieceType.King)
                    {
                        if (move.DstPosition != 6 && move.DstPosition != 2)
                        {
                            move.Score -= 40;
                        }
                        else
                        {
                            move.Score += 40;
                        }
                    }
                    if (piece.PieceType == chessPiece.ChessPieceType.Rook)
                    {
                        move.Score -= 40;
                    }
                }

                positions.add(move);
            }
        }

        return positions;
    }

    private static List<Position> EvaluateMovesQ(Board examineBoard)
    {
    	
    	//System.out.println("Search: EvaluateMovesQ");
    	
        //We are going to store our result boards here           
        List<Position> positions = new ArrayList<Position>();

        for (byte x = 0; x < 64; x++)
        {
            chessPiece piece = examineBoard.Squares[x].Piece;

            //Make sure there is a piece on the square
            if (piece.PieceType == chessPiece.ChessPieceType.None)
                continue;

            //Make sure the color is the same color as the one we are moving.
            if (piece.PieceColour != examineBoard.WhosMove)
                continue;

            //For each valid move for this piece
            for (Byte dst : piece.validMoves)
            {
            	if (dst == null) continue;
                if (examineBoard.Squares[dst].Piece.PieceType == chessPiece.ChessPieceType.None)
                {
                    continue;
                }

                Position move = new Position();

                move.SrcPosition = x;
                move.DstPosition = dst;

                if (move.SrcPosition == KillerMove[2][0].SrcPosition && move.DstPosition == KillerMove[2][0].DstPosition)
                {
                    //move.TopSort = true;
                    move.Score += 5000;
                    positions.add(move);
                    continue;
                }

                chessPiece pieceAttacked = examineBoard.Squares[move.DstPosition].Piece;

                move.Score += pieceAttacked.pieceValue;

                if (piece.pieceValue < pieceAttacked.pieceValue)
                {
                    move.Score += pieceAttacked.pieceValue - piece.pieceValue;
                }

                move.Score += piece.pieceActionValue;


                positions.add(move);
            }
        }

        return positions;
    }

	static boolean SearchForMate(chessPiece.ChessPieceColour movingSide,
			Board examineBoard, boolean blackMate, boolean whiteMate,
			boolean staleMate) {
		
		//System.out.println("Search: SearchForMate");
		
		boolean foundNonCheckBlack = false;
		boolean foundNonCheckWhite = false;

		for (byte x = 0; x < 64; x++) {
			Square sqr = examineBoard.Squares[x];

			// Make sure there is a piece on the square
			if (sqr.Piece.PieceType == chessPiece.ChessPieceType.None)
				continue;

			// Make sure the color is the same color as the one we are moving.
			if (sqr.Piece.PieceColour != movingSide)
				continue;

			// For each valid move for this piece
			for (Byte dst : sqr.Piece.validMoves) {

				if (dst == null) continue;
				// We make copies of the board and move so that we can move it
				// without effecting the parent board
				Board board = examineBoard.FastCopy();
				
				
				// Make move so we can examine it
				Board.MovePiece(board, x, dst, chessPiece.ChessPieceType.Queen);

				// We Generate Valid Moves for Board
				PieceValidMoves.GenerateValidMoves(board);

				if (board.blackCheck == false) {
					foundNonCheckBlack = true;
				} else if (movingSide == chessPiece.ChessPieceColour.Black) {
					continue;
				}

				if (board.whiteCheck == false) {
					foundNonCheckWhite = true;
				} else if (movingSide == chessPiece.ChessPieceColour.White) {
					continue;
				}
			}
		}

		if (foundNonCheckBlack == false) {
			if (examineBoard.blackCheck) {
				blackMate = true;
				return true;
			}
			if (!examineBoard.whiteMate
					&& movingSide != chessPiece.ChessPieceColour.White) {
				staleMate = true;
				return true;
			}
		}

		if (foundNonCheckWhite == false) {
			if (examineBoard.whiteCheck) {
				whiteMate = true;
				return true;
			}
			if (!examineBoard.blackMate
					&& movingSide != chessPiece.ChessPieceColour.Black) {
				staleMate = true;
				return true;
			}
		}

		return false;

	}
	
	static MoveContent IterativeSearch(Board examineBoard, byte depth, int nodesSearched, int nodesQuiessence, String pvLine, byte plyDepthReached, byte rootMovesSearched, ArrayList<OpeningMove> currentGameBook)
    {
		
		//System.out.println("Search: IterativeSearch");
        
		//cb
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 3; x++) {
				KillerMove[x][y] = new Position();
			}
		}
		
		ArrayList<Position> pvChild = new ArrayList<Position>();
        int alpha = -400000000;
        int beta = 400000000;
		
        
        MoveContent bestMove = new MoveContent();

        //We are going to store our result boards here           
        ResultBoards succ = GetSortValidMoves(examineBoard);

        rootMovesSearched = (byte)succ.Positions.size();

        if (rootMovesSearched == 1)
        {
            //I only have one move
           return succ.Positions.get(0).lastMove;
        }

        //Can I make an instant mate?
        for (Board pos : succ.Positions)
        {
            int value = -AlphaBeta(pos,(byte) 1, -beta, -alpha, nodesSearched, nodesQuiessence, pvChild, true);

            if (value >= 32767)
            {
                return pos.lastMove;
            }
        }

        int currentBoard = 0;

        alpha = -400000000;

        Collections.sort(succ.Positions, (Board a1, Board a2) -> a2.score - a1.score);
        //succ.Positions.Sort(Sort);

        depth--;

        plyDepthReached = ModifyDepth(depth, succ.Positions.size());

        for (Board pos : succ.Positions)
        {
            currentBoard++;

			progress = (int)((currentBoard / succ.Positions.size()) * 100);

            pvChild = new ArrayList<Position>();

            int value = -AlphaBeta(pos, depth, -beta, -alpha, nodesSearched, nodesQuiessence, pvChild, false);

            if (value >= 32767)
            {
                return pos.lastMove;
            }

            if (examineBoard.repeatedMove == 2)
            {
                String fen = Board.Fen(true, pos);

                for (OpeningMove move : currentGameBook)
                {
                    if (move.EndingFEN == fen)
                    {
                        value = 0;
                        break;
                    }
                }
            }

            pos.score = value;

            //If value is greater then alpha this is the best board
            if (value > alpha || alpha == -400000000)
            {
                pvLine = pos.lastMove.ToString();

                for (Position pvPos : pvChild)
                {
                    pvLine += " " + pvPos.ToString();
                }

                alpha = value;
                bestMove = pos.lastMove;
            }
        }

        plyDepthReached++;
		progress=100;
	
        return bestMove;
    }
	
	private static byte ModifyDepth(byte depth, int possibleMoves)
    {
		
		//System.out.println("Search: ModifyDepth");
        if (possibleMoves <= 20 || piecesRemaining < 14)
        {
            if (possibleMoves <= 10 || piecesRemaining < 6)
            {
                depth += 1;
            }

            depth += 1;
        }

        return depth;
    }
	
	private static int StaticExchangeEvaluation(Square examineSquare)
    {
		
		//System.out.println("Search: StaticExchangeEvaluation");
		
        if (examineSquare.Piece.PieceType == chessPiece.ChessPieceType.None)
        {
            return 0;
        }
        if (examineSquare.Piece.attackedValue == 0)
        {
            return 0;
        }

        return examineSquare.Piece.pieceActionValue - examineSquare.Piece.attackedValue + examineSquare.Piece.defendedValue;
    }

}
