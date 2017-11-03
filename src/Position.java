
public class Position {

	Byte SrcPosition;
	Byte DstPosition;
	Integer Score;
	// internal boolean TopSort;
	String Move;
	
	public Position() {
		SrcPosition = -1;
		DstPosition = -1;
		Score = -1;
		Move = "";
	}

	public String ToString() {
		return Move;
	}
}
