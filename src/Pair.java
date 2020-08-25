/**
 * A Pair is treated as a cell in a board and a container of coordinates
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {
    private final K y;  //Rows
    private final V x;  //Columns
    private Impassable card;
    private Room room;
    private Player currentPlayer;
    private ClueCharacter character;
    private Weapon weapon;

    public Pair(K y, V x) {
        this.y = y;
        this.x = x;
    }

    /**
     * setCharacter and setWeapon is used for allocating position inside the room
     * Only used when character or weapon goes in and out of room
     *
     * @param c
     */
    public void setCharacter(ClueCharacter c) {character=c;}
    public void setWeapon(Weapon w) {weapon=w;}

    /**
     * setImpassable and setRoom separates the board functionality
     * 4 conditions -Impassable and hasRoom -> This is a room
     *              -Impassable and noRoom -> This is a wall
     *              -Passable and hasRoom -> This is a room door
     *              -Passable and noRoom -> This is a hallway
     *
     * @param i
     */
    public void setImpassable(Impassable i) {card = i;}
    public void setRoom(Room r) { room = r; }

    /**
     *  This method should only be used when the player moves in a passable cell
     *  It records the characters current location and player's current and previous location
     *  while moving
     *
     *  Player doesn't need to know the tile they are standing in the room
     * @param p
     */
    public void setPlayer(Player p) {
        Pair<Integer,Integer> coords;
        coords = new Pair<>((Integer)this.getY(),(Integer)this.getX());
        currentPlayer = p;
        if(currentPlayer != null) {
            currentPlayer.setPreviousLoc(currentPlayer.getCurrentLoc());
            currentPlayer.setCurrentLoc(coords);
            currentPlayer.getClueCharacter().setLocation(coords);
            setCharacter(currentPlayer.getClueCharacter());
        }
    }

    /**
     * A boolean function that checks if the player is allowed to move to the next cell
     * Conditions to check are: -If there is a player next to you
     *                          -If the cell is impassable or passable
     *
     * @param currentLoc
     * @return
     */
    public Boolean isMovementAllowed(Pair<Integer,Integer> currentLoc) {
        boolean status = card.getPassable();
        //First check if "Passable", then check if there is "Room", then check if
        //that is the "entrance" to the Room
        if(status){
            if(room != null) status = room.checkEntrance(currentLoc);
            if(currentPlayer != null) status = false;
        }
        return status;
    }

    public K getY() { return this.y; }

    public V getX() { return this.x; }

    @Override
    public String toString() {
        String s = "";
        if ((card.getPassable() && room == null) || (!card.getPassable() && room == null)) {
            if(character != null) s = character.getSymbol();
            else s = card.toString();
        }
        else if(card.getPassable() && room != null) s = room.getDoorNumber().toString();
        else if(!card.getPassable() && room != null) {
            if(character != null) s = character.getSymbol();
            else if(weapon != null) s = weapon.getSymbol();
            else s = room.getSymbol();
        }
        return s;
    }
}
