import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class chessBoard extends JFrame implements MouseListener, MouseMotionListener {
	
	Engine engine;
	JLayeredPane layeredPane;
	JPanel chessBoard;
	JLabel chessPieceIcon;
	JTextArea outputArea;
	JScrollPane scroll;
	ImageIcon WhitePawn;
	ImageIcon BlackPawn;
	ImageIcon WhiteKnight;
	ImageIcon BlackKnight;
	ImageIcon WhiteBishop;
	ImageIcon BlackBishop;
	ImageIcon WhiteRook;
	ImageIcon BlackRook;
	ImageIcon WhiteQueen;
	ImageIcon BlackQueen;
	ImageIcon WhiteKing;
	ImageIcon BlackKing;
	int xAdjustment;
	int yAdjustment;
	int xOriginal;
	int yOriginal;
	String name;
		
	public chessBoard(){
		
		engine = new Engine();
		
		menu Menu = new menu();
		this.setJMenuBar(Menu.createMenuBar(this));
		
		// Create initial board
		Dimension boardSize = new Dimension(500, 500);
		Dimension layerSize = new Dimension(500, 600);
	 
		// Use a Layered Pane
		// A layered pane has a z-order, so components can overlap
		layeredPane = new JLayeredPane();
		
		// Add the layered pane to the content pane
		getContentPane().add(layeredPane);

		// Set the board size and add mouse listener events
		layeredPane.setPreferredSize(layerSize);
		layeredPane.addMouseListener(this);
		layeredPane.addMouseMotionListener(this);

		// Create the chess board from a JPanel
	 	chessBoard = new JPanel();

	 	// Add the chess board to the layered pane as the default layer
	 	layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
	 	
	 	// Divide the chess board into an 8 x 8 grid using the layout property
		chessBoard.setLayout(new GridLayout(8, 8));
		chessBoard.setPreferredSize(boardSize);
		chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);
		
		// Sets up and displays the games message box with a scroll bar when needed.
		outputArea = new JTextArea();
		scroll = new JScrollPane(outputArea);
		scroll.setBounds(0, 500, 500, 100);
		scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.GRAY, Color.DARK_GRAY), "Game Messages"));
		layeredPane.add(scroll, JLayeredPane.DEFAULT_LAYER);
		
		// Create the board by adding JPanels to each grid element
		for (int i = 0; i < 64; i++) {

			// Create a new board square and add it to the chess board
			JPanel square = new JPanel(new BorderLayout());
			square.setName(Integer.toString(i));
			chessBoard.add(square);

			// Set the colours of each board square
			int row = (i / 8) % 2;
			if (row == 0)
				square.setBackground( i % 2 == 0 ? Color.decode("#888888") : Color.decode("#FFFFFF") );
			else
				square.setBackground( i % 2 == 0 ? Color.decode("#FFFFFF") : Color.decode("#888888") );
			
		}
		
		
		WhitePawn = new ImageIcon(Main.class.getResource("images/whitePawn.png"));
		BlackPawn = new ImageIcon(Main.class.getResource("images/blackPawn.png"));
		WhiteKnight = new ImageIcon(Main.class.getResource("images/whiteKnight.png"));
		BlackKnight = new ImageIcon(Main.class.getResource("images/blackKnight.png"));
		WhiteBishop = new ImageIcon(Main.class.getResource("images/whiteBishop.png"));
		BlackBishop = new ImageIcon(Main.class.getResource("images/blackBishop.png"));
		WhiteRook = new ImageIcon(Main.class.getResource("images/whiteRook.png"));
		BlackRook = new ImageIcon(Main.class.getResource("images/blackRook.png"));
		WhiteQueen = new ImageIcon(Main.class.getResource("images/whiteQueen.png"));
		BlackQueen = new ImageIcon(Main.class.getResource("images/blackQueen.png"));
		WhiteKing = new ImageIcon(Main.class.getResource("images/whiteKing.png"));
		BlackKing = new ImageIcon(Main.class.getResource("images/blackKing.png"));
	}
	
	public void drawBoard(Boolean clearBorder) {
			
		JLabel[] piece;
		piece = new JLabel[32];
		
		int currPiece = 0;
		
		for (byte i = 0; i < 64; i++) {
			
			JPanel panel = (JPanel)chessBoard.getComponent(i);
			panel.removeAll();
			
			switch (engine.GetPieceColorAt(i)) {
			case White: 
				switch (engine.GetPieceTypeAt(i)) {
				case Pawn:
					piece[currPiece] = new JLabel("", WhitePawn, JLabel.CENTER);
                    break;
				case Knight:
					piece[currPiece] = new JLabel("", WhiteKnight, JLabel.CENTER);
                    break;
            	case Bishop:
            		piece[currPiece] = new JLabel("", WhiteBishop, JLabel.CENTER);
                    break;
            	case Rook:
            		piece[currPiece] = new JLabel("", WhiteRook, JLabel.CENTER);
                    break;
            	case Queen:
            		piece[currPiece] = new JLabel("", WhiteQueen, JLabel.CENTER);
                    break;
            	case King:
            		piece[currPiece] = new JLabel("", WhiteKing, JLabel.CENTER);
                    break;
            	case None:
            		continue;
				}
				break;
			case Black:
				switch (engine.GetPieceTypeAt(i)) {
				case Pawn:
					piece[currPiece] = new JLabel("", BlackPawn, JLabel.CENTER);
                    break;
				case Knight:
					piece[currPiece] = new JLabel("", BlackKnight, JLabel.CENTER);
                    break;
            	case Bishop:
					piece[currPiece] = new JLabel("", BlackBishop, JLabel.CENTER);
                    break;
            	case Rook:
					piece[currPiece] = new JLabel("", BlackRook, JLabel.CENTER);
                    break;
            	case Queen:
					piece[currPiece] = new JLabel("", BlackQueen, JLabel.CENTER);
                    break;
            	case King:
					piece[currPiece] = new JLabel("", BlackKing, JLabel.CENTER);
                    break;
            	case None:
            		continue;
				}
				break;
				
			}
			panel = (JPanel)chessBoard.getComponent(i);
			panel.add(piece[currPiece]);
			currPiece++;
				
			
		}
		
		if (clearBorder) {
			for (Component s : chessBoard.getComponents()) {
				if (s instanceof JPanel) {
					((JPanel) s).setBorder(null);
				}
			}
		}
		this.invalidate();
		this.validate();

	}

	// Handle mouse pressed event

	public void mousePressed(MouseEvent e){
		
		// Reset the chess piece that is being dragged
		chessPieceIcon = null;
		
		// Find the component at the cursor X/Y coordinates
		Component c =  chessBoard.findComponentAt(e.getX(), e.getY());

		// If the instance is a JPanel (chess board) then there is 
		// no piece here so exit
		if (c instanceof JPanel) return;
	 		
		// Get parent location
		Point parentLocation = c.getParent().getLocation();

		// Get square name
		name = c.getParent().getName();
	
		// If no valid moves then exit
		if (engine.GetChessBoard().Squares[Integer.parseInt(name)].Piece.getValidMovesCount(engine.GetChessBoard(), Integer.parseInt(name)) == 0) return;
		
		// If chess piece is not player colour then exit
		if (engine.GetChessBoard().Squares[Integer.parseInt(name)].Piece.PieceColour != engine.HumanPlayer) return;
		
		// Adjust mouse pointer relative to parent location
		xAdjustment = parentLocation.x - e.getX();
		yAdjustment = parentLocation.y - e.getY();

		xOriginal = parentLocation.x;
		yOriginal = parentLocation.y;
		
		// Get chess piece and move it into the drag layer
		chessPieceIcon = (JLabel)c;
		chessPieceIcon.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
		chessPieceIcon.setSize(chessPieceIcon.getWidth(), chessPieceIcon.getHeight());
		layeredPane.add(chessPieceIcon, JLayeredPane.DRAG_LAYER);
		
		for (Byte validDst : engine.GetChessBoard().Squares[Integer.parseInt(name)].Piece.validMoves) {
			if (validDst == null) continue;

			for (Component s : chessBoard.getComponents()) {
				if (s instanceof JPanel) {
					if ( Byte.parseByte(s.getName()) == validDst) {
						int col = Integer.parseInt(s.getName()) % 8;
						int row = Integer.parseInt(s.getName()) / 8;
						if (row % 2 == 0)
							s.setBackground( col % 2 == 0 ? Color.decode("#008800") : Color.decode("#88FF88") );
						else
							s.setBackground( col % 2 == 0 ? Color.decode("#88FF88") : Color.decode("#008800") );
					}
				}
			}
		}
		
		

	}
	 
	// Handle mouse dragged event
	  
	public void mouseDragged(MouseEvent e) {
		
		// If not dragging a chess piece then exit
		if (chessPieceIcon == null) return;

		// Move chess piece to current cursor position
		chessPieceIcon.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
		
	}
	 
	// Handle mouse released event
	 
	public void mouseReleased(MouseEvent e) {
		
		for (Component s : chessBoard.getComponents()) {
			if (s instanceof JPanel) {
				int col = Integer.parseInt(s.getName()) % 8;
				int row = Integer.parseInt(s.getName()) / 8;
				if (row % 2 == 0)
					s.setBackground( col % 2 == 0 ? Color.decode("#888888") : Color.decode("#FFFFFF") );
				else
					s.setBackground( col % 2 == 0 ? Color.decode("#FFFFFF") : Color.decode("#888888") );
				((JPanel) s).setBorder(null);
			
			}
		}
		
		// If not dragging a chess piece then exit
		if(chessPieceIcon == null) return;
	 
		// Remove chess piece from drag layer
		chessPieceIcon.setVisible(false);

		// Get component at cursor coordinates
		Component c =  chessBoard.findComponentAt(e.getX(), e.getY());

		// If the current location already contains a chess piece then
		// drop it on top, otherwise drop it into a board square
		if (c instanceof JLabel){
			Container parent = c.getParent();
			
			// Uncomment this to remove the piece underneath
			// parent.remove(0);

			parent.add( chessPieceIcon );
			
			if (parent.getName() != name) {
				makeMove(name, parent.getName());
			}
			
		} else {
			if (c instanceof JPanel){
				Container parent = (Container)c;
				parent.add(chessPieceIcon);

				if (parent.getName() != name) {
					makeMove(name, parent.getName());
				}
			
			} else {
				
				// If drop was onto something other than the board then
				// put the piece back where it started
				c =  chessBoard.findComponentAt(xOriginal, yOriginal);
				Container parent = (Container)c;
				parent.add(chessPieceIcon);
			}
		}
	 
		// Show the chess piece in the default layer
		chessPieceIcon.setVisible(true);
		
	}
	 
	private void makeMove(String from, String to) {
		
		int From, To;
		String move = "";
		
		From = Integer.parseInt(from);
		To = Integer.parseInt(to);
		
		move += (char)((From % 8) + 65) ;
		move += (char)(8 - (From / 8) + 48);
		move += (char)((To % 8) + 65);
		move += (char)(8 - (To / 8) + 48);
		
		
		if (!engine.IsValidMove((byte)(From % 8), (byte)(From / 8), (byte)(To % 8), (byte)(To / 8)))
		{
			drawBoard(false);
			return;
		}
		
		if (engine.MovePiece((byte)(From % 8), (byte)(From / 8), (byte)(To % 8), (byte)(To / 8))) {
			outputArea.append(move + "\n");
			MakeEngineMove(engine);
		}

		if (engine.GetStaleMate())
		{
			if (engine.GetInsufficientMaterial())
			{
				outputArea.append("Draw by insufficient material \n");
			}
			else if (engine.GetRepeatedMove())
			{
				outputArea.append("Draw by repetition \n");
			}
			else if (engine.GetFiftyMove())
			{
				outputArea.append("Draw by fifty move rule \n");
			}
			else
			{
				outputArea.append("Stalemate \n");
			}
			outputArea.cut();
			engine.NewGame();
			drawBoard(true);
		} else if (engine.GetWhiteMate())
		{
			outputArea.append("Black mates \n");
			outputArea.cut();
			engine.NewGame();
			drawBoard(true);
		}
		else if (engine.GetBlackMate())
		{
			outputArea.append("White mates \n");
			outputArea.cut();
			engine.NewGame();
			drawBoard(true);
		}
		
		drawBoard(false);
		
		return;
		
	}
		
	public void MakeEngineMove(Engine engine)
	{
		
		LocalDateTime start = LocalDateTime.now();

		engine.AiPonderMove();

		MoveContent lastMove = (MoveContent) engine.GetMoveHistory().toArray()[0];

		@SuppressWarnings("unused")
		String tmp = "";

		byte sourceColumn = (byte)(lastMove.MovingPiecePrimary.SrcPosition % 8);
		byte sourceRow = (byte)(8 - (lastMove.MovingPiecePrimary.SrcPosition / 8));
		byte destinationColumn = (byte)(lastMove.MovingPiecePrimary.DstPosition % 8);
		byte destinationRow = (byte)(8 - (lastMove.MovingPiecePrimary.DstPosition / 8));

		tmp += GetPgnMove(lastMove.MovingPiecePrimary.PieceType);

		if (lastMove.MovingPiecePrimary.PieceType == chessPiece.ChessPieceType.Knight)
		{
			tmp += GetColumnFromInt(sourceColumn + 1);
			tmp += sourceRow;
		}
		else if (lastMove.MovingPiecePrimary.PieceType == chessPiece.ChessPieceType.Rook)
		{
			tmp += GetColumnFromInt(sourceColumn + 1);
			tmp += sourceRow;
		}
		else if (lastMove.MovingPiecePrimary.PieceType == chessPiece.ChessPieceType.Pawn)
		{
			if (sourceColumn != destinationColumn)
			{
				tmp += GetColumnFromInt(sourceColumn + 1);
			}
		}

		if (lastMove.TakenPiece.PieceType != chessPiece.ChessPieceType.None)
		{
			tmp += "x";
		}

		tmp += GetColumnFromInt(destinationColumn + 1);

		tmp += destinationRow;

		if (lastMove.PawnPromotedTo == chessPiece.ChessPieceType.Queen)
		{
			tmp += "=Q";
		}
		else if (lastMove.PawnPromotedTo == chessPiece.ChessPieceType.Rook)
		{
			tmp += "=R";
		}
		else if (lastMove.PawnPromotedTo == chessPiece.ChessPieceType.Knight)
		{
			tmp += "=K";
		}
		else if (lastMove.PawnPromotedTo == chessPiece.ChessPieceType.Bishop)
		{
			tmp += "=B";
		}

		LocalDateTime end = LocalDateTime.now();

		@SuppressWarnings("unused")
		Duration ts = Duration.between(start, end);

		int score = engine.GetScore();

		if (score > 0)
		{
			score = score / 10;
		}
		for (Component s : chessBoard.getComponents()) {
			if (s instanceof JPanel) {
				if (engine.GetLastNonPlayerMove().MovingPiecePrimary.SrcPosition == Byte.parseByte(s.getName())) {
					((JPanel) s).setBorder(BorderFactory.createLineBorder(Color.RED, 2));
				}
				else if (engine.GetLastNonPlayerMove().MovingPiecePrimary.DstPosition == Byte.parseByte(s.getName())) {
					((JPanel) s).setBorder(BorderFactory.createLineBorder(Color.RED, 2));
				}
				else if (engine.GetLastNonPlayerMove().MovingPieceSecondary.PieceType != chessPiece.ChessPieceType.None) {
					if (engine.GetLastNonPlayerMove().MovingPieceSecondary.SrcPosition == Byte.parseByte(s.getName())) {
						((JPanel) s).setBorder(BorderFactory.createLineBorder(Color.RED, 2));
					}
					else if (engine.GetLastNonPlayerMove().MovingPieceSecondary.DstPosition == Byte.parseByte(s.getName())) {
						((JPanel) s).setBorder(BorderFactory.createLineBorder(Color.RED, 2));
					}
				}
			
				}
				else {
					((JPanel) s).setBorder(null);
				}
			}
		}

		
	
	
	public static String GetColumnFromInt(int column)
	{
		String returnColumnt;

		switch (column)
		{
			case 1:
				returnColumnt = "a";
				break;
			case 2:
				returnColumnt = "b";
				break;
			case 3:
				returnColumnt = "c";
				break;
			case 4:
				returnColumnt = "d";
				break;
			case 5:
				returnColumnt = "e";
				break;
			case 6:
				returnColumnt = "f";
				break;
			case 7:
				returnColumnt = "g";
				break;
			case 8:
				returnColumnt = "h";
				break;
			default:
				returnColumnt = "Unknown";
				break;
		}

		return returnColumnt;
	}
	
	private static String GetPgnMove(chessPiece.ChessPieceType pieceType)
	{
		String move = "";

		if (pieceType == chessPiece.ChessPieceType.Bishop)
		{
			move += "B";
		}
		else if (pieceType == chessPiece.ChessPieceType.King)
		{
			move += "K";
		}
		else if (pieceType == chessPiece.ChessPieceType.Knight)
		{
			move += "N";
		}
		else if (pieceType == chessPiece.ChessPieceType.Queen)
		{
			move += "Q";
		}
		else if (pieceType == chessPiece.ChessPieceType.Rook)
		{
			move += "R";
		}

		return move;
	}
	
	public void mouseClicked(MouseEvent e) {
	  
	}
	  
	public void mouseMoved(MouseEvent e) {

	}
	  
	public void mouseEntered(MouseEvent e){
	  
	}

	public void mouseExited(MouseEvent e) {
	  
	}
	
}
