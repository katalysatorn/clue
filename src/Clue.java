import javax.swing.*;
import java.util.*;

public class Clue {
    /**
     * Cards
     */
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 6;
    private static Suggestion gameSolution;
    private static GUI ux;
    protected static final ArrayList<Weapon> weapons = new ArrayList<>();
    protected static final ArrayList<Room> rooms = new ArrayList<>();
    protected static final ArrayList<ClueCharacter> characters = new ArrayList<>();
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
    /**
     * PlayerInfo
     */
    private static final ArrayList<Player> players = new ArrayList<>();
    /**
     * Locations
     */
    private static final Map<Room, Pair<Integer, Integer>> roomLocations = new HashMap<>();
    private static final ArrayList<Pair<Integer, Integer>> entranceLocations = new ArrayList<>();
    private static final Queue<ClueCharacter> characterOrder = new ArrayDeque<>();
    private static final Queue<Player> playOrder = new ArrayDeque<>();
    protected static Player currentPlayer;
    static Card[][] board = new Card[24][25];
    private static GUI ux;
    private static ArrayList<ClueCharacter> allCharacters = new ArrayList<>();
    private static Suggestion gameSolution;

    /*      Gaming Order    */
    private static final Queue<ClueCharacter> characterOrder = new ArrayDeque<>();
    private static final Queue<Player> playOrder = new ArrayDeque<>()

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
        System.out.println("Placing cards on board");
        placeCards();
        printBoard();

        System.out.println("Game Starting");
        while (!round());
    }

    public static boolean round() {
        if (playOrder.isEmpty()) {
            return true;
        }

        currentPlayer = playOrder.poll();
        System.out.println("Current player is: " + currentPlayer.getName());
        GUI.suggestionBtn.setEnabled(currentPlayer.getCurrentRoom() != null);

        JOptionPane.showConfirmDialog(
                ux,
                "Get " + currentPlayer.getName() + " to the screen for their turn",
                "New Turn",
                0
        );


        // Pause until nextTurnBtn is clicked
        while (!ux.isNextTurn);

        // This happens after the next turn button is clicked
        ux.isNextTurn = false;

        if (currentPlayer.canStillPlay()) playOrder.offer(currentPlayer);

        return false;
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
            System.out.printf("\tPlayer %d (%s) %s\n", p.playerNumber, p.name, p.clueCharacter.getName());
        }

        System.out.print("First player to start is..\nRoll dice..\n");
        int start = order.get(Dice.r.nextInt(order.size()));  //Random number
        Player player = allCharacters.get(start).player;
        System.out.printf("~~Player %d (%s): %s~~\n", player.playerNumber, player.name, player.clueCharacter.getName());

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

    public static void placeCards() {
        placeRooms();
        setEntrances();
        placeCharacters();
    }

    /**
     * Places room on the board
     */
    public static void placeRooms() {
        for (Map.Entry<Room, Pair<Integer, Integer>> roomPairEntry : roomLocations.entrySet()) {
            Room room = roomPairEntry.getKey();
            Pair<Integer, Integer> dimension = roomPairEntry.getValue();

            int height = dimension.getOne();
            int width = dimension.getTwo();

            int nextRow = room.TLSquare.getOne();  // starting row
            int nextCol = room.TLSquare.getTwo();  // starting column

            if (room.getName().equals("Middle Room")) {  // Middle room can't be accessed
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        board[nextRow][nextCol] = new Impassable(true);
                        nextCol++;
                    }
                    nextRow++;
                    nextCol = room.TLSquare.getTwo();  // need to set back to starting column
                }
            } else {  // is a room that can be accessed
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        board[nextRow][nextCol] = room;
                        nextCol++;
                    }
                    nextRow++;
                    nextCol = room.TLSquare.getTwo();  // need to set back to starting column
                }
            }
        }
    }

    /**
     * Places character on board
     */
    public static void placeCharacters() {
        for (ClueCharacter c : allCharacters) {
            var loc = c.getLocation();

            board[loc.getOne()][loc.getTwo()] = c;
        }
    }

    /**
     * Loads the entrances to each room on the board
     */
    public static void loadEntrances() {
        entranceLocations.add(new Pair<>(6, 5));
        entranceLocations.add(new Pair<>(12, 8));
        entranceLocations.add(new Pair<>(15, 7));
        entranceLocations.add(new Pair<>(19, 7));
        entranceLocations.add(new Pair<>(18, 12));
        entranceLocations.add(new Pair<>(18, 13));
        entranceLocations.add(new Pair<>(20, 15));
        entranceLocations.add(new Pair<>(21, 18));
        entranceLocations.add(new Pair<>(16, 19));
        entranceLocations.add(new Pair<>(14, 21));
        entranceLocations.add(new Pair<>(9, 19));
        entranceLocations.add(new Pair<>(12, 23));
        entranceLocations.add(new Pair<>(4, 19));
        entranceLocations.add(new Pair<>(5, 16));
        entranceLocations.add(new Pair<>(7, 15));
        entranceLocations.add(new Pair<>(7, 10));
        entranceLocations.add(new Pair<>(5, 9));
    }

    /**
     * Sets the entrance as active on the board, creates a room as well
     */
    public static void setEntrances() {
        for (Pair<Integer, Integer> location : entranceLocations) {
            int row = location.getOne();
            int col = location.getTwo();
            board[row][col] = new Impassable();
        }

    }

    public static String printBoard() {
        StringBuilder output = new StringBuilder();
        for (int row = 0; row < 24; row++) {
            output.append("|");
            for (int col = 0; col < 25; col++) {
                Card cell = board[row][col];
                if (cell != null) {
                    output.append(cell.getCharRep()).append("|");
                } else {
                    output.append("_|");
                }
            }
            output.append("\n");
        }
        return output.toString();
    }

    /**
     * Creates game solution by randomly selecting a weapon, room and murderer
     */
    public void makeSolution() {
        // randomly choosing a murder weapon
        Weapon w = weapons.remove(Dice.r.nextInt(weapons.size()));

        // randomly choosing a murder room
        Room r = rooms.remove(Dice.r.nextInt(rooms.size()));

        // randomly choosing a murderer
        ClueCharacter c = characters.remove(Dice.r.nextInt(characters.size()));

        gameSolution = new Suggestion(w, c, r);
    }

    /**
     * Deals remaining cards (not including solution cards) to players hands
     * NOTE: this method must be called after makeSolution()
     */
    public void dealCards() {
        ArrayList<Card> toDeal = new ArrayList<>();

        //add all cards but solution cards to new deck
        toDeal.addAll(weapons);
        toDeal.addAll(characters);
        toDeal.addAll(rooms);

        //shuffle said deck
        Collections.shuffle(toDeal);

        //deal between players
        while (!toDeal.isEmpty()) {
            for (Player p : players) {
                p.addCard(toDeal.get(toDeal.size() - 1));
                toDeal.remove(toDeal.size() - 1);
            }
        }

    }

    /**
     * This loops over the players apart from the one that instantiated the suggestion
     *
     * @param player player making suggestion
     * @param other  player involved in suggestion
     * @param s      suggestion envelope
     */
    public void makeSuggestion(Player player, Player other, Suggestion s) {
        //Move other player to suggested room
        other.setCurrentRoom(s.getRoom());

        //Move weapon to suggested room
        s.getWeapon().setRoom(s.getRoom());


        for (Player p : playOrder) {
            if (!p.equals(player)) {
                ArrayList<Card> matchingCards = new ArrayList<>();

                for (Card c : p.hand) {
                    if (c == s.character || c == s.room || c == s.weapon) {
                        matchingCards.add(c);
                    }
                }

                getPlayerToScreen(p);

                if (!matchingCards.isEmpty()) {
                    System.out.println("You can refute with the following cards: ");
                    System.out.println("(0) - None");
                    for (int i = 0; i < matchingCards.size(); i++) {
                        System.out.printf("(%d) - %s\n", i + 1, matchingCards.get(i));
                    }

                    System.out.println("\nChoose a card to refute with:");
                    int refIndex = 0;

                    if (refIndex != 0) p.refuteCard = matchingCards.get(refIndex - 1);
                } else {
                    System.out.println("You have no cards to refute with");
                }
            }
        }

        // Now relay the refute cards
        for (Player p : playOrder) {
            if (p.refuteCard != null) {
                System.out.printf("%s has refuted with card \"%s\"\n", p.name, p.refuteCard);
            }

            p.refuteCard = null;
        }

        //Player can choose to make an accusation
        System.out.print("Enter 'Y' if you would like to make an accusation that " + s.getCharacter().toString()
                + " committed a murder using " + s.getWeapon().toString() + " in " + s.getRoom().toString());

        if (true) {
            //Make accusation
            makeAccusation(s);
        }

        //Game resumes
    }

    //Players accusation is incorrect and they get kicked out of the game

    public void makeAccusation(Suggestion s) {
        if (s == gameSolution) {
            //PLAYER WINS
        }
    }

    public void getPlayerToScreen(Player p) {
        System.out.println("\n\n\n\n");
        System.out.println("Player " + p.name + "'s turn.\n (Click to continue)");
    }
}
