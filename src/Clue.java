import javax.swing.*;
import java.util.*;

public class Clue {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 6;
    private static Suggestion gameSolution;
    private static GUI ux;

    /*      Cards       */
    public static final ArrayList<Weapon> weapons = new ArrayList<>();
    public static final ArrayList<Room> rooms = new ArrayList<>();
    private static final ArrayList<ClueCharacter> characters = new ArrayList<>(); //Temporary
    public static ArrayList<ClueCharacter> allCharacters = new ArrayList<>();

    /*      PlayerInfo      */
    private static final ArrayList<Player> players = new ArrayList<>();

    /*      Gaming Order    */
    private static final Queue<ClueCharacter> characterOrder = new ArrayDeque<>();
    private static final Queue<Player> playOrder = new ArrayDeque<>();


    /**
     * TODO - Main Clue event loop
     * 1. Create Circumstance to be used as solution <character, weapon, room> ()
     * 2. Ask how many players (and their names?) ()
     * 3. Share out the remaining Cards ()
     * <p>
     * Loop through:
     * 1. Roll 2 dice and loop until all moves are over or they enter a room.
     * // TODO - Have to make sure player doesn't move into an occupied room or space or impassable space.
     * 2. If player enters room, can break out of move loop
     * 3. Player can make an suggestion.
     * 4. Loop again through all players:
     * 1. Show the suggestion and the players cards.
     * 2. Allow player to decide if they want to refute or not
     * 5. Give player opportunity to accuse or not.
     * <p>
     * <p>
     * That loop continues until all the players are gone or someone guesses correctly
     */
    public static void main(String[] a) {
        ux = new GUI();
        loadCharacters();
        loadWeapons();
        loadRooms();
        Board b = new Board(weapons,rooms,allCharacters);
        loadGameFormat();

        System.out.print(b.toString());
    }

    /**
     * Ask for the number of players and their selectable characters, which will then
     * create the playing order of the players. Also creates the solution envelope and
     * distributes the cards to the players
     */
    public static void loadGameFormat(){
        // Create temporary collections for Weapon, Room and Character cards
        ArrayList<Weapon> weaponCards = new ArrayList<>(weapons);
        ArrayList<Room> roomCards = new ArrayList<>(rooms);

        // 1. Create Circumstance to be used as solution <character, weapon, room>
        Collections.shuffle(characters);
        Collections.shuffle(weaponCards);
        Collections.shuffle(roomCards);

        // Deal out weapons to rooms
        for (int i = 0; i < weaponCards.size(); i++) {
            roomCards.get(i).addWeapon(weaponCards.get(i));
        }

        // 2. Ask how many players (and their names?)
        getPlayerInfo();

        gameSolution = new Suggestion(
                weaponCards.remove(0),
                characters.remove(0),
                roomCards.remove(0)
        );

        //3. Orders the players' playOrder
        setPlayOrder();

        // 4. Share out the remaining Cards ()
        ArrayList<Card> deck = new ArrayList<>();
        deck.addAll(weaponCards);
        deck.addAll(characters);
        deck.addAll(roomCards);
        Collections.shuffle(deck);
        distributeCards(deck);
    }

    /**
     * Uses user input to get how many players and player names
     **/
    public static void getPlayerInfo() {
        // Find out how many players
        int numPlayers;

        String[] choices = new String[(MAX_PLAYERS - MIN_PLAYERS) + 1];
        for (int i = MIN_PLAYERS; i <= MAX_PLAYERS; i++) choices[i - 3] = String.valueOf(i);

        numPlayers = Integer.parseInt(
                (String) JOptionPane.showInputDialog(ux, "", "Choose how many players",
                        JOptionPane.PLAIN_MESSAGE, null, choices, choices[0])
        );

        ArrayList<String> selectablePlayers = new ArrayList<>();
        for(ClueCharacter c : characters) selectablePlayers.add(c.getName());

        // Get player names and assign them a character
        for (int i = 0; i < numPlayers; i++) {
            String playerName = "";

            while (playerName.equals("")) {
                playerName = JOptionPane.showInputDialog("Enter name for player " + (i + 1) + ":");
            }

            String chosenCharacter = (String) JOptionPane.showInputDialog(ux, "Available:",
                    "Choose a Character", JOptionPane.QUESTION_MESSAGE,
                    null, selectablePlayers.toArray(), selectablePlayers.toArray()[0]);

            selectablePlayers.remove(chosenCharacter);
            ClueCharacter chosen = null;
            for(ClueCharacter c : characters) if(c.getName().equals(chosenCharacter)) chosen = c;
            players.add(new Player(playerName, chosen, (i + 1)));
            assert chosen != null;
            chosen.addPlayer(players.get(players.size() - 1));
        }
    }

    /**
     * Queue ordering of players through sorting out the playing and unused characters
     */
    public static void setPlayOrder() {
        //Collections that separates playing and unused characters
        Queue<ClueCharacter> playingCharacters = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        while (!characterOrder.isEmpty()) {
            if (characterOrder.peek().getPlayer() == null) characterOrder.poll();
            else {
                ClueCharacter c = characterOrder.poll();
                playingCharacters.offer(c);
                assert c != null;
                order.add(c.getOrder());
            }
        }

        // Clear Screen 20 times
        for (int i = 0; i < 20; i++) System.out.println();

        System.out.print("Characters playing are :\n");
        for (Player p : players) {
            System.out.printf("\tPlayer %d (%s) %s\n", p.playerNumber, p.name, p.clueCharacter.name);
        }

        System.out.print("First player to start is..\nRoll dice..\n");
        int start = order.get(new Random().nextInt(order.size()));  //Random number
        Player player = allCharacters.get(start).getPlayer();
        System.out.printf("~~Player %d (%s): %s~~\n", player.playerNumber, player.name, player.clueCharacter.name);

        //Sorts out the character order
        while (Objects.requireNonNull(playingCharacters.peek()).getOrder() != start) {
            ClueCharacter c = playingCharacters.poll();
            playingCharacters.offer(c);
        }

        //Sorts out the playing order
        while (!playingCharacters.isEmpty()) playOrder.offer(playingCharacters.poll().getPlayer());
    }

    /**
     * Adds all remaining cards into one deck, shuffles and distributes to each player
     */
    public static void distributeCards(List<Card> cards) {
        System.out.print("Shuffling cards..\nDistributing cards to players\n");
        int count = 0;
        for (Card card : cards) {
            players.get(count).addCard(card);
            count++;
            if (count >= players.size()) count = 0;
        }
    }

    /**
     * Loads room name and dimensions into map
     */
    public static void loadRooms() {
        rooms.add(new Room("Kitchen","a", 1));
        rooms.add(new Room("Ball Room","b",2));
        rooms.add(new Room("Conservatory","c",3));
        rooms.add(new Room("Billiard Room","d",4));
        rooms.add(new Room("Library","e",5));
        rooms.add(new Room("Study","f",6));
        rooms.add(new Room("Hall","g",7));
        rooms.add(new Room("Lounge","h",8));
        rooms.add(new Room("Dining Room","i",9));
    }

    /**
     * Loads Clue Character names and their starting positions into map
     */
    public static void loadCharacters() {
        characters.add(new ClueCharacter("Miss Scarlett", 0, "S", new Pair<>(24,7)));
        characters.add(new ClueCharacter("Col Mustard", 1,"M", new Pair<>(17,0)));
        characters.add(new ClueCharacter("Mrs White", 2,"W",new Pair<>(0,9)));
        characters.add(new ClueCharacter("Mr Green", 3,"G",new Pair<>(0,14)));
        characters.add(new ClueCharacter("Mrs Peacock", 4,"C",new Pair<>(6,23)));
        characters.add(new ClueCharacter("Prof Plum", 5,"P",new Pair<>(19,23)));
        for(ClueCharacter c :characters) characterOrder.offer(c);
        allCharacters = new ArrayList<>(characters);
    }

    /**
     * Loads weapons into list
     */
    public static void loadWeapons() {
        weapons.add(new Weapon("Candlestick", "!"));
        weapons.add(new Weapon("Dagger", "T"));
        weapons.add(new Weapon("Lead Pipe", "L"));
        weapons.add(new Weapon("Revolver", "R"));
        weapons.add(new Weapon("Rope", "&"));
        weapons.add(new Weapon("Spanner", "F"));
    }

}
