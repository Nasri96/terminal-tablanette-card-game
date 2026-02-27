import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Card {
    private final char suit;
    private final String symbol;
    private final int value;
    private final int points;

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

    // new way to handle card value(s), it is always this.value unless the card is ACE, which can be 1 or 11
    public List<Integer> getPossibleValues() {
        if(this.symbol.equals("A")) {
            return Arrays.asList(1, 11);
        } else {
            return List.of(this.value);
        }
    }

    public int getPoints() {
        return this.points;
    }

    public int getToStringLength() {
        return this.symbol.length() + "-".length() + 1;
    }


    public String toString() {
        String redColor = "\u001B[31m";
        String resetColor = "\u001B[0m";

        if(String.valueOf(this.suit).equals("d") || String.valueOf(this.suit).equals("h")) {
            return redColor + this.symbol + "-" +  this.suit + resetColor;
        }

        return this.symbol + "-" + this.suit;
    }

    public boolean equals(Object compared) {
        if(this == compared) {
            return true;
        }

        if(!(compared instanceof Card)) {
            return false;
        }

        Card comparedCard = (Card) compared;

        return this.suit == comparedCard.suit && this.symbol.equals(comparedCard.symbol);

    }

    public int hashCode() {
        return Objects.hash(this.suit, this.symbol);
    }
}
