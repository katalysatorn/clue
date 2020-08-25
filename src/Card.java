/**
 * Abstract container class for manipulation on all types of Card
 */
abstract class Card {
    protected String name;
    protected String symbol;

    public Card(String name, String symbol) {
        this.symbol = symbol;
        this.name = name;
    }

    protected Card() { }

    /**
     * Name of card entity
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Name of symbol for board
     * @return String symbol
     */
    public String getSymbol() { return symbol;}

   // /**
   // * @return a human-readable description of the Card
   //  */
   // abstract String getDescription();

}
