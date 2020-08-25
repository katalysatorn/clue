import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Player
 */
public class Player {
    final ClueCharacter clueCharacter;
    final String name;
    final Integer playerNumber;
    private List<Card> hand = new ArrayList<>();
    private Room currentRoom = null;
    private Room previousRoom = null;
    private boolean canPlay = true;

    /*Moving on board*/
    private Pair<Integer, Integer> previousLoc;
    private Pair<Integer, Integer> currentLoc;
    private ArrayList<Pair<Integer, Integer>> availablePathways = new ArrayList<>();

    public Player(String name, ClueCharacter character, Integer number) {
        clueCharacter = character;
        this.name = name;
        playerNumber = number;
    }

    /**
     * Adds card to hand
     * @param card to be added
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    // Getters
    public String getHand() {
        StringBuilder cards = new StringBuilder("Your cards:\n");
        for (Card c : hand) cards.append("[").append(c.name).append("] ");
        return cards.toString();
    }

    public ClueCharacter getClueCharacter() { return clueCharacter; }
    public Integer getPlayerNumber() {return playerNumber;}
    public String getName() {return name;}


    public Room getCurrentRoom() { return currentRoom; }
    public Room getPreviousRoom() { return previousRoom; }
    public Pair<Integer, Integer> getPreviousLoc() { return previousLoc; }
    public Pair<Integer, Integer> getCurrentLoc() { return currentLoc; }

    // Setters
    public void setCurrentRoom(Room r) { currentRoom = r; }
    public void setPreviousRoom(Room r){ previousRoom = r; }
    public void setPreviousLoc(Pair<Integer, Integer> p) { previousLoc = p; }
    public void setCurrentLoc(Pair<Integer, Integer> p) { currentLoc = p; }

    public boolean canStillPlay() { return canPlay; }

    public void setPlayStatus(boolean b) { canPlay = b; }

}
