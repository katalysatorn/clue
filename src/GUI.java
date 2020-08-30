import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

public class GUI extends JFrame {
    protected static boolean isNextTurn = false;
	protected static JMenuItem suggestionBtn = new JMenuItem("Make Suggestion");

    public GUI() {
        createMenu();
        createBorder();
        setTitle("Cluedo");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setVisible(true);
    }

    private void suggestionBtn(Player currentPlayer) {
        Weapon weapon = (Weapon) JOptionPane.showInputDialog(
                this,
                "",
                "What is the murder weapon?: ",
                0,
                null,
                Clue.weapons.toArray(),
                Clue.weapons.get(0)
        );

        ClueCharacter character = (ClueCharacter) JOptionPane.showInputDialog(
                this,
                "",
                "Who committed the murder?: ",
                0,
                null,
                Clue.characters.toArray(),
                Clue.characters.get(0)
        );

        Suggestion suggestion = new Suggestion(weapon, character, currentPlayer.getCurrentRoom());

        JOptionPane.showConfirmDialog(
                this,
                "You want to make the following suggestion:\n" + suggestion.getDescription(),
                "Do you want to make this suggestion?", JOptionPane.YES_NO_OPTION);
    }

    private void createMenu() {
        var menuBar = new JMenuBar();
        var fileMenu = new JMenu("File");
        var gameMenu = new JMenu("Game");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        var exit = new JMenuItem("Exit");
        suggestionBtn.addActionListener(e -> {
            suggestionBtn(Clue.currentPlayer);
        });
        suggestionBtn.setEnabled(false);
        var dice = new JMenuItem("Roll Dice");
        dice.addActionListener(e -> {
            int roll = Dice.roll();
            // TODO - Print the dice amount.
        });
        var nxtTurn = new JMenuItem("Next Turn");
        nxtTurn.addActionListener(e -> {
            isNextTurn = true;
        });
        exit.setMnemonic(KeyEvent.VK_E);
        exit.setToolTipText("Exit");
        exit.addActionListener((event) -> System.exit(0));
        fileMenu.add(exit);
        gameMenu.add(dice);
        gameMenu.add(suggestionBtn);
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
}

