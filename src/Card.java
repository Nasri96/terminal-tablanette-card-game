public class Card {
    private char suit;
    private String symbol;
    private int value;
    private int points;

    public Card(char suit, String symbol, int value, int points) {
        this.suit = suit;
        this.symbol = symbol;
        this.value = value;
        this.points = points;
    }

    public char getSuit() {
        return this.suit;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getValue() {
        return this.value;
    }

    public int getPoints() {
        return this.points;
    }


    public String toString() {
        String redColor = "\u001B[31m";
        String resetColor = "\u001B[0m";

        if(String.valueOf(this.suit).equals("d") || String.valueOf(this.suit).equals("h")) {
            return redColor + this.symbol + "-" +  this.suit + resetColor;
        }

        return this.symbol + "-" + this.suit;
    }
}
