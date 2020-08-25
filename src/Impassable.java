/**
 * An Impassable Card for use on the map for the back of rooms and such
 */
public class Impassable extends Card {
    final boolean showPassable;

    public Impassable(boolean showPassable) {
        this.showPassable = showPassable;
    }

    public boolean getPassable() { return showPassable;}

    @Override
    public String toString() {
        String s;
        if(showPassable) s = "_";
        else s = "#";

        return s;
    }
}
