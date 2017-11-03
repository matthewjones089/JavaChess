import java.util.Collection;
import java.util.List;
import java.util.Stack;


public class FileIO {

        static void SaveCurrentGameMove(Board currentBoard, Board previousBoard, Collection<OpeningMove> gameBook, MoveContent bestMove)
        {
            try
            {
                OpeningMove move = new OpeningMove();

                move.StartingFEN = Board.Fen(true, previousBoard);
                move.EndingFEN = Board.Fen(true, currentBoard);
                move.Moves.add(bestMove);

                gameBook.add(move);

                for (OpeningMove move1 : gameBook)
                {
                    byte repeatedMoves = 0;

                    for (OpeningMove move2 : gameBook)
                    {
                        if (move1.EndingFEN == move2.EndingFEN)
                        {
                            repeatedMoves++;
                        }
                    }

                    if (previousBoard.repeatedMove < repeatedMoves)
                    {
                        previousBoard.repeatedMove = repeatedMoves;
                        currentBoard.repeatedMove = repeatedMoves;
                    }
                }
                if (currentBoard.repeatedMove >= 3)
                {
                    currentBoard.staleMate = true;
                }
            }
            catch (Exception e)
            {
                return;
            }

            return;
        }

        static boolean SaveGame(String filePath, Board chessBoard, chessPiece.ChessPieceColour whoseMove, Stack<MoveContent> moveHistory)
        {
            if (filePath == null)
                return false;

           /* var serializer = new XmlSerializer(typeof(XMLBoard));
            TextWriter writer = new StreamWriter(filePath);
            var xmlBoard = new XMLBoard();
            xmlBoard.Squares = new List<XMLBoard.XMLSquare>();
            xmlBoard.MoveHistory = new List<MoveContent>();
            xmlBoard.WhoseMove = whoseMove;
            xmlBoard.FiftyMoveCount = chessBoard.FiftyMove;
            for (byte x = 0; x < 64; x++)
            {
                var square = new XMLBoard.XMLSquare();
                if (chessBoard.Squares[x].Piece.PieceType != chessPiece.ChessPieceType.None)
                {
                    square.CurrentPiece = new XMLBoard.XMLChessPiece();
                    square.CurrentPiece.Moved = chessBoard.Squares[x].Piece.Moved;
                    square.CurrentPiece.PieceColor = chessBoard.Squares[x].Piece.PieceColor;
                    square.CurrentPiece.PieceType = chessBoard.Squares[x].Piece.PieceType;
                    square.BoardColumn = (byte)(x % 8);
                    square.BoardRow = (byte)(x / 8);
                }
                xmlBoard.Squares.Add(square);
            }
            foreach (MoveContent move in moveHistory)
            {
                xmlBoard.MoveHistory.Add(move);
            }
            serializer.Serialize(writer, xmlBoard);
            writer.Close();*/

            return true;
        }

        static boolean LoadGame(String filePath,Board chessBoard, chessPiece.ChessPieceColour whoseMove, Stack<MoveContent> moveHistory, List<OpeningMove> currentGameBook, List<OpeningMove> undoGameBook)
        {
            if (filePath == null)
            {
                return false;
            }

           /* chessBoard = new Board();
            moveHistory = new Stack<MoveContent>();
            currentGameBook = new List<OpeningMove>();
            undoGameBook = new List<OpeningMove>();
            for (byte x = 0; x < 64; x++)
            {
                var square = new Square();
                square.Piece = new chessPiece();
                chessBoard.Squares[x] = square;
            }
            var serializer = new XmlSerializer(typeof(XMLBoard));
            TextReader reader = new StreamReader(filePath);
            var xmlBoard = (XMLBoard)serializer.Deserialize(reader);
            reader.Close();
            foreach (XMLBoard.XMLSquare square in xmlBoard.Squares)
            {
                if (square.CurrentPiece != null)
                {
                    RegisterPiece(square.BoardColumn, square.BoardRow, square.CurrentPiece, chessBoard);
                }
            }
            chessBoard.WhoseMove = xmlBoard.WhoseMove;
            chessBoard.FiftyMove = xmlBoard.FiftyMoveCount;
            chessBoard.ZobristHash = Zobrist.CalculateZobristHash(chessBoard);         
            foreach (MoveContent move in xmlBoard.MoveHistory)
            {
                moveHistory.Push(move);
            }
            PieceValidMoves.GenerateValidMoves(chessBoard);
            Evaluation.EvaluateBoardScore(chessBoard);*/

            return true;
        }      

        public static boolean LoadOpeningBook(List<OpeningMove> openingBook)
        {
           /* if (File.Exists("OpeningBook\\OpeningBook.xml"))
            {
                var serializer = new XmlSerializer(typeof(List<OpeningMove>));
                TextReader reader =
                    new StreamReader("OpeningBook\\OpeningBook.xml");
                openingBook = (List<OpeningMove>)serializer.Deserialize(reader);
                reader.Close();
            }
            List<OpeningMove> newOpeningBook = new List<OpeningMove>();
            //Delete Duplicates
            foreach (OpeningMove mv1 in openingBook)
            {
                bool duplicate = false;
                foreach (OpeningMove mv2 in newOpeningBook)
                {
                    if (mv1.StartingFEN == mv2.StartingFEN)
                    {
                        if (mv1.EndingFEN == mv2.EndingFEN)
                        {
                            duplicate = true;
                        }
                    }
                }
                if (duplicate == false)
                {
                    newOpeningBook.Add(mv1);
                }
            }
            openingBook = newOpeningBook; */

            return true;
        }
		/* 
        private static void RegisterPiece(byte boardColumn, byte boardRow, XMLBoard.XMLChessPiece piece, Board chessBoard)
        {
          byte position = (byte)(boardColumn + (boardRow * 8));
            chessBoard.Squares[position].Piece = new Piece(piece.PieceType, piece.PieceColor);
            chessBoard.Squares[position].Piece.Moved = piece.Moved;
			
            return;
        }*/
    
    }

