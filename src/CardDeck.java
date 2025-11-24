import java.util.ArrayList;
import java.util.Collections;

public class CardDeck {
    private char[] suits = { 'c', 'd', 'h', 's' };
    private String[] symbols = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "A", "J", "Q", "K" };
    private ArrayList<Card> deck;


    public CardDeck() {
        this.deck = this.createDeck();
    }

    private ArrayList<Card> createDeck() {
        ArrayList<Card> newDeck = new ArrayList<>();
        // create cards from 2 - K symbols for all suits
        for(int i = 0; i < suits.length - 2; i++) {
            // cards 2-9 give 0 points, exception is 2 club which awards one point, rest 10-K are one point with exception of 10 diamond which is two points
            for(int j = 0; j < symbols.length; j++) {
                if(j < 8) {
                    // check for 2 club card, this one awards one point
                    if(symbols[j] == "2" && suits[i] == 'c') {
                        Card card = new Card(suits[i], symbols[j], Integer.valueOf(j + 2), 1);
                        newDeck.add(card);
                    } else {
                        Card card = new Card(suits[i], symbols[j], Integer.valueOf(j + 2), 0);
                        newDeck.add(card);
                    }
                } else {
                    if(symbols[j] == "10" && suits[i] == 'd') {
                        Card card = new Card(suits[i], symbols[j], Integer.valueOf(j + 2), 2);
                        newDeck.add(card);
                    } else {
                        Card card = new Card(suits[i], symbols[j], Integer.valueOf(j + 2), 1);   
                        newDeck.add(card);
                    }
                }
            }
        }

        return newDeck;
    }

    public ArrayList<Card> getDeck() {
        return this.deck;
    }

    public void shuffleDeck() {
        Collections.shuffle(this.deck);
    }

    public Card dealCard(int i) {
        return this.deck.remove(i);
    }

    public void printDeck() {
        System.out.println("DECK SIZE: " + this.deck.size());
        for(Card card: this.deck) {
            // System.out.println("Card suit: " + card.getSuit() + ", card symbol: " + card.getSymbol()+ ", card value: " + card.getValue() + ", card points: " + card.getPoints());
            // System.out.println(card);
        }
    }
}
