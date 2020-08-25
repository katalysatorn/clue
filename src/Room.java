import java.util.*;

public class Room extends Card {
    private Integer doorNumber = null;
    private List<ClueCharacter> characters = new ArrayList<>();
    private List<Weapon> weapons = new ArrayList<>();
    //Squares are coordinates that the room has taken
    private List<Pair<Integer, Integer>> squares = new ArrayList<>();
    //GUI graphics purposes, to put cards in whatever position in the room
    private Map<Pair<Integer, Integer>,Card> roomSquares = new HashMap<>();
    //Contains the doors and their entrances
    private Map<Pair<Integer, Integer>,Pair<Integer, Integer>> doors = new HashMap<>();

    public Room(String roomName, String symbol, int d) {
        super(roomName,symbol);
        doorNumber = d;
    }

    /**
     * Adds weapon to room when game is made, may not have a weapon
     */
    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);    //Room knows how many weapons in room
        weapon.setRoom(this);   //Weapon knows they are in room
        boolean taken = false;
        while(!taken) {
           Pair<Integer, Integer> tile = randomSquare();
            if (roomSquares.get(tile) == null) {
                roomSquares.replace(tile, weapon);  //Room knows where weapon is placed in room
                tile.setWeapon(weapon);             //Tile only knows weapon is TAKING that tile in room
                taken = true;
            }
        }
    }

    public Pair<Integer, Integer> randomSquare(){ return squares.get(new Random().nextInt(squares.size())); }

    /**
     * Adds character and player (if playing the character) to the room
     */
    public void addCharacter(ClueCharacter character) {
        characters.add(character);  //Room knows who is in the room
        character.setRoom(this);    //Characters knows they are in room
        boolean taken = false;      //Check if tile in room not taken
        while(!taken) {
            Pair<Integer, Integer> tile = randomSquare();
            if (roomSquares.get(tile) == null) {
                roomSquares.replace(tile, character);   //Room knows where character is standing in room
                if(character.getPlayer() != null){
                    character.getPlayer().setCurrentRoom(this); //Player knows they are in room

                    //Player doesn't move when inside the room, so set the locations to null
                    character.getPlayer().setPreviousLoc(null);
                    character.getPlayer().setCurrentLoc(null);
                }

                //Tile (Space inside room) knows that character is taking that TILE in room, but
                //character doesn't know that they are standing there. (Not necessity for character)
                tile.setCharacter(character);
                taken = true;
            }
        }
    }

    /**
     * Adds area to room
     */
    public void addSquare(Pair<Integer, Integer> s) {
        squares.add(s);
        roomSquares.put(s,null);
    }

    /**
     * Adds doors to the room
     * @param door
     * @param entry
     */
    public void addDoor(Pair<Integer, Integer> door, Pair<Integer, Integer> entry) { doors.put(door,entry); }

    /**
     * Removes weapon out of room
     */
    public void removeWeapon(Weapon wp){
        weapons.removeIf(w -> w.equals(wp));    //Room removes the weapon
        wp.setRoom(null);                       //Weapon removes the room
        for(Map.Entry<Pair<Integer, Integer>,Card> c : roomSquares.entrySet()){
            if(c.getValue().equals(wp)){
                c.setValue(null);               //Room removes weapon position
                c.getKey().setWeapon(null);     //Tile removes weapon position
            }
        }
    }

    /**
     * Removes character out of room
     */
    public void removeCharacter(ClueCharacter ch){
        characters.removeIf(c -> c.equals(ch));                     //Room removes the character

        //Player goes out of room, Player records room as the "Previous Room"
        if(ch.getPlayer() != null)  ch.getPlayer().setPreviousRoom(ch.getPlayer().getCurrentRoom());
        for(Map.Entry<Pair<Integer, Integer>,Card> c : roomSquares.entrySet()){
            if(c.getValue().equals(ch)){
                c.setValue(null);                                   //Room removes character position
                c.getKey().setCharacter(null);                      //Tile removes character position
            }
        }
        ch.setRoom(null);                                           //Character removes the room
    }

    /**
     * Checks if the current location is an entrance to room, (location is facing door)
     *
     * @param e
     * @return if this is an entrance to a room
     */
    public boolean checkEntrance(Pair<Integer, Integer> e) {
        boolean statement = false;
        for(Map.Entry<Pair<Integer, Integer>,Pair<Integer, Integer>> door : doors.entrySet()){
            if (door.getValue().equals(e)) {
                statement = true;
                break;
            }
        }
        return statement;
    }

    /**
     * Checks if a player is blocking the door
     * @param door
     * @return boolean
     */
    public boolean isEntranceBlocked(Pair<Integer, Integer> door){ return getEntrance(door).isMovementAllowed(door); }

    /**
     * Returns the entrance location to an allocated door
     * @param door
     * @return entrance
     */
    public Pair<Integer, Integer> getEntrance(Pair<Integer, Integer> door){ return doors.get(door); }

    public Integer getDoorNumber() { return doorNumber;}
}