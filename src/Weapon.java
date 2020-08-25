public class Weapon extends Card {
    public Room room;
    private Pair<Integer, Integer> location;

    public Weapon(String name, String symbol) { super(name,symbol); }

    public void setLocation(Pair<Integer, Integer> l) { location = l; }
    public void setRoom(Room r) {
        room = r;
    }

}
