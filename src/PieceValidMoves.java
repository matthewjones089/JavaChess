import java.util.List;
import java.util.Stack;

final class PieceValidMoves {

	public static boolean AnalyseMove(Board board, byte dstPos,
			chessPiece pcMoving) {

		if (pcMoving.PieceColour == chessPiece.ChessPieceColour.White)
			board.WhiteAttackBoard[dstPos] = true;
		else
			board.BlackAttackBoard[dstPos] = true;

		// If there is no piece in destination position, then simply just move.
		if (board.Squares[dstPos].Piece.PieceType == chessPiece.ChessPieceType.None) {

			pcMoving.validMoves.push(dstPos);

			return true;
		}

		// Retrieve the piece to be attacked.
		chessPiece pcAttacked = board.Squares[dstPos].Piece;

		// Checks if the collision would be between two of the same colour.
		if (pcAttacked.PieceColour != pcMoving.PieceColour) {

			pcAttacked.attackedValue += pcMoving.pieceActionValue;

			if (pcAttacked.PieceType == chessPiece.ChessPieceType.King) {
				if (pcAttacked.PieceColour == chessPiece.ChessPieceColour.Black)
					board.blackCheck = true;
				else
					board.whiteCheck = true;
			} else
				// Add this as a valid move.
				pcMoving.validMoves.push(dstPos);

			// We don't continue movement past this piece.
			return false;
		}

		// Same colour, then defending.
		pcAttacked.defendedValue += pcMoving.pieceActionValue;

		// Since this piece is of my kind, i can't move there.
		return false;
	}

	private static void CheckValidMovesPawn(List<Byte> moves,
			chessPiece pcMoving, byte srcPosition, Board board, byte count) {

		for (byte i = 0; i < count; i++) {
			byte dstPos = moves.get(i);

			if (dstPos % 8 != srcPosition % 8) {
				// If there is a piece there, there is potential for a kill.
				AnalyseMovePawn(board, dstPos, pcMoving);

				if (pcMoving.PieceColour == chessPiece.ChessPieceColour.White)
					board.WhiteAttackBoard[dstPos] = true;
				else
					board.BlackAttackBoard[dstPos] = true;

				// If there is a piece in front, the pawn cannot move there.
			} else if (board.Squares[dstPos].Piece.PieceType != chessPiece.ChessPieceType.None)
				return;
			else
				pcMoving.validMoves.push(dstPos);
		}
	}

	private static void AnalyseMovePawn(Board board, byte dstPos,
			chessPiece pcMoving) {

		// Pawns only kill diagonally, so we handle the enPassant scenario
		// specifically.
		if (board.enPassantPosition > 0) {
			if (pcMoving.PieceColour != board.enPassantColour) {
				if (board.enPassantPosition == dstPos) {
					// A enPassant move is possible.
					pcMoving.validMoves.push(dstPos);

					if (pcMoving.PieceColour == chessPiece.ChessPieceColour.White)
						board.WhiteAttackBoard[dstPos] = true;
					else
						board.BlackAttackBoard[dstPos] = true;
				}
			}
		}

		chessPiece pcAttacked = board.Squares[dstPos].Piece;

		// If no piece there to kill.
		if (pcAttacked.PieceType == chessPiece.ChessPieceType.None)
			return;

		// Regardless of the piece or colour, the position is attacked.
		if (pcMoving.PieceColour == chessPiece.ChessPieceColour.White) {
			board.WhiteAttackBoard[dstPos] = true;

			// If the piece is the same colour.
			if (pcAttacked.PieceColour == pcMoving.PieceColour) {
				pcAttacked.defendedValue += pcMoving.pieceActionValue;
				return;
			}
			// Else piece is different so we are attacking.
			pcAttacked.attackedValue += pcMoving.pieceActionValue;

			// If the piece is a King, set it in check.
			if (pcAttacked.PieceType == chessPiece.ChessPieceType.King)
				board.blackCheck = true;
			else
				// Else add this as a valid move.
				pcMoving.validMoves.push(dstPos);
		} else {
			board.BlackAttackBoard[dstPos] = true;

			// If the piece is the same colour.
			if (pcAttacked.PieceColour == pcMoving.PieceColour) {
				pcAttacked.defendedValue += pcMoving.pieceActionValue;
				return;
			}
			// If the piece is a King, set it in check.
			if (pcAttacked.PieceType == chessPiece.ChessPieceType.King)
				board.whiteCheck = true;
			else
				// Else add this as a valid move.
				pcMoving.validMoves.push(dstPos);
		}
		return;

	}
	
	private static void GenerateValidMovesKing(chessPiece piece, Board board , byte srcPosition) {
		
		if (piece.PieceType != chessPiece.ChessPieceType.King)
			return;
		
		for (byte x = 0; x < MoveArrays.KingTotalMoves[srcPosition]; x++) {
			
			byte dstPos = MoveArrays.KingMoves[srcPosition].Moves.get(x);
			
			if (piece.PieceColour == chessPiece.ChessPieceColour.White) {
				if (board.BlackAttackBoard[dstPos]) {
					board.WhiteAttackBoard[dstPos] = true;
					continue;
				} 
			}else {
					if (board.WhiteAttackBoard[dstPos]) {
						board.BlackAttackBoard[dstPos] = true;
						continue;
					}
				}
				AnalyseMove(board , dstPos , piece);
		}
	}

	private static void GenerateValidMovesKingCastle(Board board,
			chessPiece king) {

		if (king.PieceType == chessPiece.ChessPieceType.None)
			return;

		if (king.moved)
			return;

		if (king.PieceColour == chessPiece.ChessPieceColour.White
				&& board.whiteCastled)
			return;

		if (king.PieceColour == chessPiece.ChessPieceColour.Black
				&& board.blackCastled)
			return;

		if (king.PieceColour == chessPiece.ChessPieceColour.Black
				&& board.blackCheck)
			return;

		if (king.PieceColour == chessPiece.ChessPieceColour.White
				&& board.whiteCheck)
			return;

		// Add the castling move to the pieces available moves.
		if (king.PieceColour == chessPiece.ChessPieceColour.White) {
			if (board.whiteCheck)
				return;

			if (board.Squares[63].Piece.PieceType != chessPiece.ChessPieceType.None) {
				// Check if the right rook is still in the correct position.
				if (board.Squares[63].Piece.PieceType == chessPiece.ChessPieceType.Rook) {
					if (board.Squares[63].Piece.PieceColour == king.PieceColour) {
						// Move one column to the right , see if it is empty.
						if (board.Squares[62].Piece.PieceType == chessPiece.ChessPieceType.None) {
							// And again.
							if (board.Squares[61].Piece.PieceType == chessPiece.ChessPieceType.None) {
								if (board.BlackAttackBoard[61] == false
										&& board.BlackAttackBoard[62] == false) {
									// Okay, move is valid, add it to stack.
									king.validMoves.push((byte) 62);
									board.WhiteAttackBoard[62] = true;
								}
							}
						}
					}
				}
			}

			if (board.Squares[56].Piece.PieceType != chessPiece.ChessPieceType.None) {
				// Check if the right rook is still in the correct position.
				if (board.Squares[56].Piece.PieceType == chessPiece.ChessPieceType.Rook) {
					if (board.Squares[56].Piece.PieceColour == king.PieceColour) {
						// Move one column to the right , see if it is empty.
						if (board.Squares[57].Piece.PieceType == chessPiece.ChessPieceType.None) {
							// And again.
							if (board.Squares[58].Piece.PieceType == chessPiece.ChessPieceType.None) {
								if (board.Squares[59].Piece.PieceType == chessPiece.ChessPieceType.None) {
									if (board.BlackAttackBoard[58] == false
											&& board.BlackAttackBoard[59] == false) {
										// Okay, move is valid, add it to stack.
										king.validMoves.push((byte) 58);
										board.WhiteAttackBoard[58] = true;
									}
								}
							}
						}
					}
				}
			}
		} else if (king.PieceColour == chessPiece.ChessPieceColour.Black) {
			if (board.blackCheck)
				return;

			if (board.Squares[7].Piece.PieceType != chessPiece.ChessPieceType.None) {
				// Check if the right rook is still in the correct position.
				if (board.Squares[7].Piece.PieceType == chessPiece.ChessPieceType.Rook
						&& !board.Squares[7].Piece.moved) {
					if (board.Squares[7].Piece.PieceColour == king.PieceColour) {
						// Move one column to the right , see if it is empty.
						if (board.Squares[6].Piece.PieceType == chessPiece.ChessPieceType.None) {
							// And again.
							if (board.Squares[5].Piece.PieceType == chessPiece.ChessPieceType.None) {
								if (board.WhiteAttackBoard[5] == false
										&& board.WhiteAttackBoard[6] == false) {
									// Okay, move is valid, add it to stack.
									king.validMoves.push((byte) 6);
									board.BlackAttackBoard[6] = true;
								}
							}
						}
					}
				}
			}

			if (board.Squares[0].Piece.PieceType != chessPiece.ChessPieceType.None) {
				// Check if the right rook is still in the correct position.
				if (board.Squares[0].Piece.PieceType == chessPiece.ChessPieceType.Rook
						&& !board.Squares[0].Piece.moved) {
					if (board.Squares[0].Piece.PieceColour == king.PieceColour) {
						// Move one column to the right , see if it is empty.
						if (board.Squares[1].Piece.PieceType == chessPiece.ChessPieceType.None) {
							// And again.
							if (board.Squares[2].Piece.PieceType == chessPiece.ChessPieceType.None) {
								if (board.Squares[3].Piece.PieceType == chessPiece.ChessPieceType.None) {
									if (board.WhiteAttackBoard[2] == false
											&& board.WhiteAttackBoard[3] == false) {
										// Okay, move is valid, add it to stack.
										king.validMoves.push((byte) 2);
										board.BlackAttackBoard[2] = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	static void GenerateValidMoves(Board board) {

		board.blackCheck = false;
		board.whiteCheck = false;
		
		byte blackRooksMoved = 0;
		byte whiteRooksMoved = 0;
		
		int remainingPiece = 0;

		for (byte x = 0; x < 64; x++) {
			Square sqr = board.Squares[x];

			if (sqr.Piece.PieceType == chessPiece.ChessPieceType.None)
				continue;
			
			sqr.Piece.validMoves = new Stack<Byte>();
			sqr.Piece.validMoves.setSize(sqr.Piece.LastValidMoveCount);
			
			remainingPiece++;
			
			switch (sqr.Piece.PieceType) {
			case Pawn:
				
				if (sqr.Piece.PieceColour == chessPiece.ChessPieceColour.White) {
					CheckValidMovesPawn(MoveArrays.WhitePawnMoves[x].Moves,
							sqr.Piece, x, board,
							MoveArrays.WhitePawnTotalMoves[x]);
					break;
				}
				if (sqr.Piece.PieceColour == chessPiece.ChessPieceColour.Black) {
					CheckValidMovesPawn(MoveArrays.BlackPawnMoves[x].Moves,
							sqr.Piece, x, board,
							MoveArrays.BlackPawnTotalMoves[x]);
					break;
				}
				break;

			case Knight:
				
				for (byte i = 0; i < MoveArrays.KnightTotalMoves[x]; i++) {
					AnalyseMove(board, MoveArrays.KnightMoves[x].Moves.get(i),
							sqr.Piece);
				}
				break;

			case Bishop:
				
				for (byte i = 0; i < MoveArrays.BishopTotalMoves1[x]; i++) {
					if (AnalyseMove(board, MoveArrays.BishopMoves1[x].Moves.get(i),
							sqr.Piece) == false)
						break;
				}
				for (byte i = 0; i < MoveArrays.BishopTotalMoves2[x]; i++) {
					if (AnalyseMove(board, MoveArrays.BishopMoves2[x].Moves.get(i),
							sqr.Piece) == false)
						break;
				}
				for (byte i = 0; i < MoveArrays.BishopTotalMoves3[x]; i++) {
					if (AnalyseMove(board, MoveArrays.BishopMoves3[x].Moves.get(i),
							sqr.Piece) == false)
						break;
				}
				for (byte i = 0; i < MoveArrays.BishopTotalMoves4[x]; i++) {
					if (AnalyseMove(board, MoveArrays.BishopMoves4[x].Moves.get(i),
							sqr.Piece) == false)
						break;
				}
				break;

			case Rook:
				
				if (sqr.Piece.moved) {
					if (sqr.Piece.PieceColour == chessPiece.ChessPieceColour.Black) 
						blackRooksMoved++;
					else 
						whiteRooksMoved++;
				}
				
				for (byte i = 0; i < MoveArrays.RookTotalMoves1[x]; i++) {
					if (AnalyseMove(board, MoveArrays.RookMoves1[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.RookTotalMoves2[x]; i++) {
					if (AnalyseMove(board, MoveArrays.RookMoves2[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.RookTotalMoves3[x]; i++) {
					if (AnalyseMove(board, MoveArrays.RookMoves3[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.RookTotalMoves4[x]; i++) {
					if (AnalyseMove(board, MoveArrays.RookMoves4[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}

				break;

			case Queen:
				
				for (byte i = 0; i < MoveArrays.QueenTotalMoves1[x]; i++) {
					if (AnalyseMove(board, MoveArrays.QueenMoves1[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.QueenTotalMoves2[x]; i++) {
					if (AnalyseMove(board, MoveArrays.QueenMoves2[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.QueenTotalMoves3[x]; i++) {
					if (AnalyseMove(board, MoveArrays.QueenMoves3[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.QueenTotalMoves4[x]; i++) {
					if (AnalyseMove(board, MoveArrays.QueenMoves4[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}

				for (byte i = 0; i < MoveArrays.QueenTotalMoves5[x]; i++) {
					if (AnalyseMove(board, MoveArrays.QueenMoves5[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.QueenTotalMoves6[x]; i++) {
					if (AnalyseMove(board, MoveArrays.QueenMoves6[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.QueenTotalMoves7[x]; i++) {
					if (AnalyseMove(board, MoveArrays.QueenMoves7[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}
				for (byte i = 0; i < MoveArrays.QueenTotalMoves8[x]; i++) {
					if (AnalyseMove(board, MoveArrays.QueenMoves8[x].Moves.get(i),
							sqr.Piece) == false) {
						break;
					}
				}

				break;

			case King:
				
				if (sqr.Piece.PieceColour == chessPiece.ChessPieceColour.White) {
					if (sqr.Piece.moved)  
						board.WhiteCanCastle = false;
					else if (!sqr.Piece.moved)
						board.WhiteCanCastle = true;
					board.WhiteKingPosition = x;
					
					
				} else {
					if (sqr.Piece.moved) 
						board.BlackCanCastle = false;
					else if (!sqr.Piece.moved)
						board.BlackCanCastle = true;
					board.BlackKingPosition = x;
					
				}

				break;

			case None:
				break;
			}
		}
		
		if (blackRooksMoved > 1) 
			board.BlackCanCastle = false;
		if (whiteRooksMoved > 1)
			board.WhiteCanCastle = false;
		
		if (remainingPiece < 10)
			board.endGamePhase = true;
		
		
		if (board.WhosMove == chessPiece.ChessPieceColour.White) {
			GenerateValidMovesKing(board.Squares[board.BlackKingPosition].Piece , board , board.BlackKingPosition);
			GenerateValidMovesKing(board.Squares[board.WhiteKingPosition].Piece , board , board.WhiteKingPosition);
		} else {
			GenerateValidMovesKing(board.Squares[board.WhiteKingPosition].Piece , board , board.WhiteKingPosition);
			GenerateValidMovesKing(board.Squares[board.BlackKingPosition].Piece , board , board.BlackKingPosition);
		}
		
		if (!board.whiteCastled && board.WhiteCanCastle && !board.whiteCheck)
			GenerateValidMovesKingCastle(board , board.Squares[board.WhiteKingPosition].Piece);
		if (!board.blackCastled && board.BlackCanCastle && !board.blackCheck)
			GenerateValidMovesKingCastle(board , board.Squares[board.BlackKingPosition].Piece);
	}
}
