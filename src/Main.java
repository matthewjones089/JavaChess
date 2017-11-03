import javax.swing.WindowConstants;

public class Main {
	static chessBoard frame;
	static Engine engine;
	public static void main(String[] args) {
				
		frame = new chessBoard();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	
		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.drawBoard(true);

	}
	
}