import java.util.ArrayList;


    public class OpeningMove
    {
        public String EndingFEN;
        public String StartingFEN;
        public ArrayList<MoveContent> Moves;

        public OpeningMove()
        {
            StartingFEN = "";
            EndingFEN = "";
            Moves = new ArrayList<MoveContent>();
        }
    }
