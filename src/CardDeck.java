import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardDeck {
    private static final char[] SUITS = { 'c', 'd', 'h', 's' };
    private static final String[] SYMBOLS = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "A", "J", "Q", "K" };
    private final List<Card> deck;


    public CardDeck(List<Card> deck) {
        this.deck = List.copyOf(deck);
    }

    public static CardDeck initial() {
        return new CardDeck(createDeck());
    }

    public CardDeck withRemovedCards(int numOfCards) {
        List<Card> withRemovedCards = new ArrayList<>(deck.subList(numOfCards, deck.size()));

        return new CardDeck(withRemovedCards);
    }

    public CardDeck withDeck(List<Card> updatedDeck) {
        return new CardDeck(updatedDeck);
    }

    private static List<Card> createDeck() {
        List<Card> newDeck = new ArrayList<>();
        // create cards from 2 - K symbols for all suits
        for(int i = 0; i < SUITS.length - 3; i++) {
            // cards 2-9 give 0 points, exception is 2 club which awards one point, rest 10-K are one point with exception of 10 diamond which is two points
            for(int j = 0; j < SYMBOLS.length; j++) {
                if(j < 8) {
                    // check for 2 club card, this one awards one point
                    if(SYMBOLS[j] == "2" && SUITS[i] == 'c') {
                        Card card = new Card(SUITS[i], SYMBOLS[j], Integer.valueOf(j + 2), 1);
                        newDeck.add(card);
                    } else {
                        Card card = new Card(SUITS[i], SYMBOLS[j], Integer.valueOf(j + 2), 0);
                        newDeck.add(card);
                    }
                } else {
                    if(SYMBOLS[j] == "10" && SUITS[i] == 'd') {
                        Card card = new Card(SUITS[i], SYMBOLS[j], Integer.valueOf(j + 2), 2);
                        newDeck.add(card);
                    } else {
                        Card card = new Card(SUITS[i], SYMBOLS[j], Integer.valueOf(j + 2), 1);   
                        newDeck.add(card);
                    }
                }
            }
        }

        return newDeck;
    }

    public void resetDeck() {
        // this.deck = createDeck();
    }

    public List<Card> getDeck() {
        return this.deck;
    }

    public CardDeck shuffled() {
        List<Card> shuffled = new ArrayList<>(this.deck);
        Collections.shuffle(shuffled);

        return new CardDeck(shuffled);
    }


    // force dealing card of given symbol
    public Card dealCard(String symbol) {
        for(Card card: this.deck) {
            if(card.getSymbol().equals(symbol)) {
                this.deck.remove(card);
                return card;
            }
        }

        return this.deck.remove(0);
    }

    public void printDeck() {
        System.out.println("deck size: " + this.deck.size());
        System.out.println("deck: " + this.deck);
        for(Card card: this.deck) {
            // System.out.println("Card suit: " + card.getSuit() + ", card symbol: " + card.getSymbol()+ ", card value: " + card.getValue() + ", card points: " + card.getPoints());
            // System.out.println(card);
        }
    }

    public String toString() {
        String output = 
                """
                CardDeck {
                    deck: %s
                }
                """.formatted(deck);

        return output;
    }
}
