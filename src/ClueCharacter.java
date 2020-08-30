public class ClueCharacter extends Card {
    protected Player player;
    private final Integer order;
    private Pair<Integer, Integer> location;

    public ClueCharacter(String name, int number, Pair<Integer, Integer> location) {
        super(name);
        order = number;
        this.location = location;
    }

    public Integer getOrder() { return order; }

    public void addPlayer(Player p) {
        player = p;
    }

    public Player getPlayer() { return player;}
    
    public void setLocation(Pair<Integer, Integer> location) {
    	this.location = location;
    }
    
    public Pair<Integer, Integer> getLocation(){
    	return location;
    }

    @Override
    String getDescription() {
        return "Hi. I am " + super.getName() + ".";
    }

    public boolean playerNotNull() {
        return player == null;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
