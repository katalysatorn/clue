public class ClueCharacter extends Card {
    private Player player = null;
    private final Integer order;
    private Pair<Integer, Integer> location;
    private Room room;

    public ClueCharacter(String name, int number, String symbol, Pair<Integer,Integer> loc) {
        super(name,symbol);
        location = loc;
        order = number;
    }

    public void addPlayer(Player p) {
        player = p;
    }

    public Integer getOrder() { return order; }
    public Player getPlayer() { return player;}
    public Pair<Integer, Integer> getLocation(){ return location; }

    public void setLocation(Pair<Integer, Integer> location) { this.location = location; }
    public void setRoom(Room r) { room = r; }

    //@Override
    //String getDescription() {
    //    return "Hi. I am " + super.getName() + ".";
    //}

    //@Override
    //public String toString() {
    //    return name;
    //}
}
