import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Board {

    private String board =  "# # # # # # # # # _ b b b b _ # # # # # # # # #\n" +
                            "a a a a a a # _ _ _ b b b b _ _ _ # c c c c c c\n" +
                            "a a a a a a _ _ b b b b b b b b _ _ c c c c c c\n" +
                            "a a a a a a _ _ b b b b b b b b _ _ c c c c c c\n" +
                            "a a a a a a _ _ b b b b b b b b _ _ 3s c c c c c\n" +
                            "a a a a a a _ _ 2l b b b b b b 2r _ _ _ c c c c c\n" +
                            "a a a a 1s a _ _ b b b b b b b b _ _ _ _ _ _ _ _\n" +
                            "_ _ _ _ _ _ _ _ b 2s b b b b 2s b _ _ _ _ _ _ _ #\n" +
                            "# _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ d d d d d d\n" +
                            "e e e e e _ _ _ _ _ _ _ _ _ _ _ _ _ 4l d d d d d\n" +
                            "e e e e e e e e _ _ # # # # # _ _ _ d d d d d d\n" +
                            "e e e e e e e e _ _ # # # # # _ _ _ d d d d d d\n" +
                            "e e e e e e e 5r _ _ # # # # # _ _ _ d d d d 4s d\n" +
                            "e e e e e e e e _ _ # # # # # _ _ _ _ _ _ _ _ #\n" +
                            "e e e e e e e e _ _ # # # # # _ _ _ f f 6n f f #\n" +
                            "e e e e e e 5s e _ _ # # # # # _ _ f f f f f f f\n" +
                            "# _ _ _ _ _ _ _ _ _ # # # # # _ _ 6l f f f f f f\n" +
                            "_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ f f f f f f f\n" +
                            "# _ _ _ _ _ _ _ _ h h 8n 8n h h _ _ _ f f f f f #\n" +
                            "g g g g g g 7n _ _ h h h h h h _ _ _ _ _ _ _ _ _\n" +
                            "g g g g g g g _ _ h h h h h 8r _ _ _ _ _ _ _ _ #\n" +
                            "g g g g g g g _ _ h h h h h h _ _ 9n i i i i i i\n" +
                            "g g g g g g g _ _ h h h h h h _ _ i i i i i i i\n" +
                            "g g g g g g g _ _ h h h h h h _ _ i i i i i i i\n" +
                            "g g g g g g # _ # h h h h h h # _ # i i i i i i";

    private ArrayList<Room> allRooms = new ArrayList<>();
    private ArrayList<Weapon> allWeapons = new ArrayList<>();
    private ArrayList<ClueCharacter> allCharacters = new ArrayList<>();

    //GUI display
    static Pattern rooms = Pattern.compile("[a-i]");
    static Pattern doors = Pattern.compile("[1-9][nslr]");
    static Pattern characters = Pattern.compile("[SMWGCP]");
    static Pattern weapons = Pattern.compile("[!TLR&F]");

    //Player Movement
    static Pattern impassable = Pattern.compile("[a-i]|#");
    static Pattern passable = Pattern.compile("[1-9][nslr]|_");

    private Pair[][] actualBoard = new Pair[25][24];

    public Board(ArrayList<Weapon> w, ArrayList<Room> r, ArrayList<ClueCharacter> c){
        allWeapons.addAll(w);
        allRooms.addAll(r);
        allCharacters.addAll(c);
        loadBoard();
    }

    public void loadBoard(){
        Scanner scan = new Scanner(board);
        int row = 0, col = 0;
        while(scan.hasNextLine()) {
            Scanner s = new Scanner(scan.nextLine());
            while (s.hasNext()) {
                if (s.hasNext(impassable) || s.hasNext(passable)) {
                    Pair<Integer, Integer> location = new Pair<>(row, col);
                    actualBoard[row][col] = location;
                    if (s.hasNext(impassable)) s = loadImpassable(s, location);
                    else if (s.hasNext(passable)) s = loadPassable(s, location, row, col);
                    col++;
                }
            }
            col = 0;
            row++;
        }
        //Load character's starting point
        for(ClueCharacter c : allCharacters){
            actualBoard[c.getLocation().getY()][c.getLocation().getX()].setCharacter(c);
        }
    }

    public Scanner loadImpassable(Scanner s, Pair<Integer,Integer> location){
        location.setImpassable(new Impassable(false));
        String string;
        if(s.hasNext(rooms)){
            string = s.next();
            for(Room r : allRooms){
                if(r.getSymbol().equals(string)){
                    location.setRoom(r);
                    r.addSquare(location);
                }
            }
        }
        else if(!s.hasNext(rooms) && s.hasNext(impassable)) s.next();
        return s;
    }

    public Scanner loadPassable(Scanner s, Pair<Integer,Integer> location, int row, int col){
        location.setImpassable(new Impassable(true));
        String string;
        if(s.hasNext(doors)){
            string = s.next();
            for(Room r : allRooms){
                if(string.contains(r.getDoorNumber().toString())){
                    location.setRoom(r);
                    if(string.contains("n")) r.addDoor(location,new Pair<>(row -1, col));
                    else if(string.contains("s")) r.addDoor(location,new Pair<>(row +1, col));
                    else if(string.contains("l")) r.addDoor(location,new Pair<>(row, col -1));
                    else if(string.contains("r")) r.addDoor(location,new Pair<>(row, col +1));
                }
            }
        }
        else if(!s.hasNext(doors) && s.hasNext(passable)) s.next();
        return s;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int y = 0; y < 25; y++){
            for (int x = 0; x<24; x++) {
                if (x < 23) {
                    s.append(actualBoard[y][x].toString());
                    s.append(" ");
                } else {
                    s.append(actualBoard[y][x].toString());
                    s.append("\n");
                }
            }
        }
        return s.toString();
    }
}
