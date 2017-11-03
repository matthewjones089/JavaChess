import java.util.List;

public class PieceMoveSet {

	public List<Byte> Moves;
	
	//Moves = Collections.unmodifiableList(Moves);
	
	PieceMoveSet(List<Byte> moves) {
		Moves = moves;
	}
}
