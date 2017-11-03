import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

public class menu implements ActionListener, ItemListener {

	chessBoard board;

	public JMenuBar createMenuBar(chessBoard Board) {

		board = Board;

		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;

		// Create the menu bar
		menuBar = new JMenuBar();

		// Create top-level menu item "Game"
		menu = new JMenu("Game");
		menu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(menu);

		// Create sub menu item "New Game" in menu "Game"
		menuItem = new JMenuItem("New Game", KeyEvent.VK_N);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// Create sub menu item "Undo" in menu "Game"
		menuItem = new JMenuItem("Undo", KeyEvent.VK_U);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		// Create sub menu item "Exit" in menu "Game"
		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0,
				ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Create top-level menu item "Settings"
		menu = new JMenu("Settings");
		menu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(menu);

		// Create sub menu item "Difficulty" in menu "Settings"
		submenu = new JMenu("Difficulty");
		submenu.setMnemonic(KeyEvent.VK_D);

		// Create a new sub menu item button group for "Easy", "Medium", and
		// "Hard"
		ButtonGroup group = new ButtonGroup();

		rbMenuItem = new JRadioButtonMenuItem("Easy");
		rbMenuItem.setMnemonic(KeyEvent.VK_E);
		group.add(rbMenuItem);
		rbMenuItem.addActionListener(this);
		submenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Medium");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_M);
		group.add(rbMenuItem);
		rbMenuItem.addActionListener(this);
		submenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Hard");
		rbMenuItem.setMnemonic(KeyEvent.VK_H);
		group.add(rbMenuItem);
		rbMenuItem.addActionListener(this);
		submenu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("Very Hard");
		rbMenuItem.setMnemonic(KeyEvent.VK_V);
		group.add(rbMenuItem);
		rbMenuItem.addActionListener(this);
		submenu.add(rbMenuItem);

		menu.add(submenu);

		// Create sub menu item "Difficulty" in menu "Settings"
		submenu = new JMenu("Player Colour");
		submenu.setMnemonic(KeyEvent.VK_P);

		// Create a new sub menu item button group for "Easy", "Medium", and
		// "Hard"
		group = new ButtonGroup();

		rbMenuItem = new JRadioButtonMenuItem("White");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_W);
		group.add(rbMenuItem);
		rbMenuItem.addActionListener(this);
		submenu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Black");
		rbMenuItem.setMnemonic(KeyEvent.VK_B);
		group.add(rbMenuItem);
		rbMenuItem.addActionListener(this);
		submenu.add(rbMenuItem);
	
		menu.add(submenu);
		
		submenu = new JMenu("End Game Rules");
		submenu.setMnemonic(KeyEvent.VK_R);
		
		group = new ButtonGroup();
		
		cbMenuItem = new JCheckBoxMenuItem("Enable Draw By Insufficient Material");
		cbMenuItem.setSelected(true);
		cbMenuItem.setMnemonic(KeyEvent.VK_I);
		group.add(cbMenuItem);
		cbMenuItem.addActionListener(this);
		submenu.add(cbMenuItem);
		
		cbMenuItem = new JCheckBoxMenuItem("Enable Draw By Repetition");
		cbMenuItem.setSelected(true);
		cbMenuItem.setMnemonic(KeyEvent.VK_T);
		group.add(cbMenuItem);
		cbMenuItem.addActionListener(this);
		submenu.add(cbMenuItem);
		
		cbMenuItem = new JCheckBoxMenuItem("Enable Draw By Fifty Move Rule");
		cbMenuItem.setSelected(true);
		cbMenuItem.setMnemonic(KeyEvent.VK_F);
		group.add(cbMenuItem);
		cbMenuItem.addActionListener(this);
		submenu.add(cbMenuItem);
		
		menu.add(submenu);

		return menuBar;

	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		if (source.getText() == "Exit") 
			board.dispatchEvent(new WindowEvent(board, WindowEvent.WINDOW_CLOSING));
		if (source.getText() == "New Game") {
			board.engine.NewGame();
			board.drawBoard(true);
		} else if (source.getText() == "Undo") {
			board.engine.Undo();
			board.drawBoard(true);
		} else if (source.getText() == "Easy") {
			board.engine.SetGameDifficulty(Engine.Difficulty.Easy);
		} else if (source.getText() == "Medium") {
			board.engine.SetGameDifficulty(Engine.Difficulty.Medium);
		} else if (source.getText() == "Hard") {
			board.engine.SetGameDifficulty(Engine.Difficulty.Hard);
		} else if (source.getText() == "Very Hard") {
			board.engine.SetGameDifficulty(Engine.Difficulty.VeryHard);
		} else if (source.getText() == "White") {
			board.engine.HumanPlayer = chessPiece.ChessPieceColour.White;
			if (board.engine.getWhosMove() != board.engine.HumanPlayer) {
				board.MakeEngineMove(board.engine);
				board.drawBoard(false);
			}
		} else if (source.getText() == "Black") {
			board.engine.HumanPlayer = chessPiece.ChessPieceColour.Black;
			if (board.engine.getWhosMove() != board.engine.HumanPlayer) {
				board.MakeEngineMove(board.engine);
				board.drawBoard(false);
			}
		} else if (source.getText() == "Enable Draw By Insufficient Material") {
			board.engine.SetInsufficientMaterialActive(!board.engine.GetInsufficientMaterialActive());
			
			if (board.outputArea.getText() == null) {
				if (!board.engine.GetInsufficientMaterialActive()) 
					board.outputArea.append("Draw by Insuffcient Material rule is enabled.");
				else
					board.outputArea.append("Draw by Insuffcient Material rule is disabled.");
			} else {
				board.outputArea.setText("");
				if (!board.engine.GetInsufficientMaterialActive()) 
					board.outputArea.append("Draw by Insuffcient Material rule is enabled.");
				else
					board.outputArea.append("Draw by Insuffcient Material rule is disabled.");
			}
			
		} else if (source.getText() == "Enable Draw By Repetition") {
			board.engine.SetRepeatedMoveActive(!board.engine.GetRepeatedMoveActive());
			
			if (board.outputArea.getText() == null) {
				if (!board.engine.GetRepeatedMoveActive()) 
					board.outputArea.append("Draw by Repetition rule is enabled.");
				else
					board.outputArea.append("Draw by Repetition rule is disabled.");
			} else {
				board.outputArea.setText("");
				if (!board.engine.GetRepeatedMoveActive()) 
					board.outputArea.append("Draw by Repetition rule is enabled.");
				else
					board.outputArea.append("Draw by Repetition rule is disabled.");
			}
			
		} else if (source.getText() == "Enable Draw By Fifty Move Rule") {
			board.engine.SetFiftyMoveActive(!board.engine.GetFiftyMoveActive());
			
			if (board.outputArea.getText() == null) {
				if (!board.engine.GetFiftyMoveActive()) 
					board.outputArea.append("Draw by Fifty Move rule is enabled.");
				else
					board.outputArea.append("Draw by Fifty Move rule is disabled.");
			} else {
				board.outputArea.setText("");
				if (!board.engine.GetFiftyMoveActive()) 
					board.outputArea.append("Draw by Fifty Move rule is enabled.");
				else
					board.outputArea.append("Draw by Fifty Move rule is disabled.");
			}
		}		
	}

	public void itemStateChanged(ItemEvent e) {
		@SuppressWarnings("unused")
		JMenuItem source = (JMenuItem) (e.getSource());
	}

	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}

}
