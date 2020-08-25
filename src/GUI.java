import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

public class GUI extends JFrame{
	
	public GUI() {
		createMenu();
		createBorder();
		setTitle("Cluedo");
		setSize(800, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void createMenu() {
		var menuBar = new JMenuBar();
		var fileMenu = new JMenu("File");
		var gameMenu = new JMenu("Game");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		var exit = new JMenuItem("Exit");
		var suggestion = new JMenuItem("Make Suggestion");
		var dice = new JMenuItem("Roll Dice");
		var nxtTurn = new JMenuItem("Next Turn");
		exit.setMnemonic(KeyEvent.VK_E);
		exit.setToolTipText("Exit");
		exit.addActionListener((event) -> System.exit(0));
		fileMenu.add(exit);
		gameMenu.add(dice);
		gameMenu.add(suggestion);
		gameMenu.add(nxtTurn);
		menuBar.add(fileMenu);
		menuBar.add(gameMenu);
		setJMenuBar(menuBar);
		
	}
	
	private void createBorder() {
		var bottomPanel = new JPanel(new BorderLayout());
		var topPanel = new JPanel();
		topPanel.setBackground(Color.gray);
		topPanel.setPreferredSize(new Dimension(400, 600));
		bottomPanel.add(topPanel);
		bottomPanel.setBorder(new EmptyBorder(0, 0, 200, 0));
		add(bottomPanel);
		pack();
	}
	
	
	public static void main(String a[]) {

		EventQueue.invokeLater(() -> {
			var gui = new GUI();
			gui.setVisible(true);
		});
	}
