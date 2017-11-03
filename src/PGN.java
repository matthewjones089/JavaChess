import java.util.Calendar;
import java.util.Locale;
import java.util.Stack;

public class PGN {

	public enum Result {
		White, Black, Tie, Ongoing;
	}
	
	 public static String GeneratePGN(Stack<MoveContent> moveHistory, int round, String whitePlayer, String blackPlayer, Result result)
     {
         int count = 0;

         String pgn = "";

         /*
             [Event "F/S Return Match"]
             [Site "Belgrade, Serbia Yugoslavia|JUG"]
             [Date "1992.11.04"]
             [Round "29"]
             [White "Fischer, Robert J."]
             [Black "Spassky, Boris V."]
             [Result "1/2-1/2"]
         */

         Calendar mCalendar = Calendar.getInstance();
         int Year = Calendar.getInstance().get(Calendar.YEAR);
         String Month = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
         int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
         
         String DayOfMonth = String.valueOf(dayOfMonth);
         
         String pgnHeader = "";

         pgnHeader += "[Date \"" + Year + "." + Month + "." + DayOfMonth + "\"]\r\n";
         pgnHeader += "[White \"" + whitePlayer + "\"]\r\n";
         pgnHeader += "[Black \"" + blackPlayer + "\"]\r\n";

         if (result == Result.Ongoing)
         {
             pgnHeader += "[Result \"" + "*" + "\"]\r\n";
         }
         else if (result == Result.White)
         {
             pgnHeader += "[Result \"" + "1-0" + "\"]\r\n";
         }
         else if (result == Result.Black)
         {
             pgnHeader += "[Result \"" + "0-1" + "\"]\r\n";
         }
         else if (result == Result.Tie)
         {
             pgnHeader += "[Result \"" + "1/2-1/2" + "\"]\r\n";
         }

         for (MoveContent move : moveHistory)
         {
             String tmp = "";

             if (move.MovingPiecePrimary.PieceColour == chessPiece.ChessPieceColour.White)
             {
                 tmp += ((moveHistory.size() / 2) - count + 1) + ". ";
             }

             tmp += move.toString();
             tmp += " ";

             tmp += pgn;
             pgn = tmp;

             if (move.MovingPiecePrimary.PieceColour == chessPiece.ChessPieceColour.Black)
             {
                 count++;
             }
         }

         if (result == Result.White)
         {
             pgn += " 1-0";
         }
         else if (result == Result.Black)
         {
             pgn += " 0-1";
         }
         else if (result == Result.Tie)
         {
             pgn += " 1/2-1/2";
         }

         return pgnHeader + pgn;
     }


 }

