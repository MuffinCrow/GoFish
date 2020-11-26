import java.util.*;

public class Multi {

    private final String[] SUITS = {"C", "D", "H", "S"};
    private final String[] RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"};

    private char whoseTurn = 'P';
    private final Player player1 = new Player();
    private final Player player2 = new Player();
    private List<Card> deck;
    private final Scanner in = new Scanner(System.in);

    public void play() {
        shuffleAndDeal();

        // play the game until someone wins

        while (true) {
            if (whoseTurn == 'P') {
                whoseTurn = takeTurn(false);

                if (player1.findAndRemoveBooks()) {
                    System.out.println("PLAYER1: Oh, that's a book!");
                    showBooks(false);
                }
            } else if (whoseTurn == 'C') {
                whoseTurn = takeTurn(true);
                if (player2.findAndRemoveBooks()) {
                    System.out.println("PLAYER2: Oh, that's a book!");
                    showBooks(true);
                }
            }

            // the games doesn't end until all 13 books are completed, or there are
            // no more cards left in the deck. the player with the ,ost books at the
            // end of the game wins.

            int playerBooks = player1.getBooks().size();
            int computerBooks = player2.getBooks().size();

            String winMessage = "Congratulations Player1, you win! " + playerBooks + " books to " + computerBooks + ".";
            String loseMessage = "Congratulations Player2, you win! " + computerBooks + " books to " + playerBooks + ".";
            String tieMessage = "Looks like it's a tie, " + playerBooks + " to " + computerBooks + ".";

            if (playerBooks + computerBooks == 13) {
                if (player1.getBooks().size() > player2.getBooks().size()) {
                    System.out.println("\n" + winMessage);
                } else {
                    System.out.println("\n" + loseMessage);
                }
                break;
            } else if (deck.size() == 0) {
                System.out.println("\nOh no, there are no more cards in the deck!");

                if (playerBooks > computerBooks) {
                    System.out.println(winMessage);
                } else if (computerBooks > playerBooks) {
                    System.out.println(loseMessage);
                } else {
                    System.out.println(tieMessage);
                }
                break;
            }
        }
    }

    public void shuffleAndDeal() {
        if (deck == null) {
            initializeDeck();
        }
        Collections.shuffle(deck);  // shuffles the deck

        while (player1.getHand().size() < 7) {
            player1.takeCard(deck.remove(0));    // deal 7 cards to the
            player2.takeCard(deck.remove(0));  // player and the computer
        }
    }

    ////////// PRIVATE METHODS /////////////////////////////////////////////////////

    private void initializeDeck() {
        deck = new ArrayList<>(52);

        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(new Card(rank, suit));     // adds 52 cards to the deck (13 ranks, 4 suits)
            }
        }
    }

    private char takeTurn(boolean cpu) {
        showHand(cpu);
        showBooks(cpu);

        // if requestCard returns null, then the hand was empty and new card was drawn.
        // this restarts the turn, ensuring the updated hand is printed to the console.

        Card card = requestCard(cpu);
        if (card == null) {
            return cpu ? 'C' : 'P';     // restart this turn with updated hand
        }

        // check if your opponent has the card you requested. it will be automatically
        // relinquished if you do. otherwise, draw from the deck. return the character
        // code for whose turn it should be next.

        if (!cpu) {
            if (player2.hasCard(card)) {
                System.out.println("PLAYER2: Yup, here you go!");
                player2.relinquishCard(player1, card);
                    if (player2.hasCard(card)) {
                        takeTurn(false);
                    }
                return 'P';
            } else {
                System.out.println("PLAYER2: Nope, go fish!");
                player1.takeCard(deck.remove(0));

                return 'C';
            }
        } else {
            if (player1.hasCard(card)) {
                System.out.println("PLAYER1: Yup, here you go!");
                player1.relinquishCard(player2, card);
                    if (player1.hasCard(card)) {
                        takeTurn(true);
                    }
                return 'C';
            } else {
                System.out.println("PLAYER1: Nope, go fish!");
                player2.takeCard(deck.remove(0));


                return 'P';
            }
        }
    }

    private Card requestCard(boolean cpu) {
        Card card = null;

        // request a card from your opponent, ensuring that the request is valid.
        // if your hand is empty, we return null to signal the calling method to
        // restart the turn. otherwise, we return the requested card.

        while (card == null) {
            if (!cpu) {
                if (player1.getHand().size() == 0) {
                    player1.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.print("PLAYER1: Got any... ");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            } else {
                if (player2.getHand().size() == 0) {
                    player2.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.print("PLAYER2: Got any... ");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            }
        }

        return card;
    }

    private void showHand(boolean cpu) {
        if (!cpu) {
            System.out.println("\nPLAYER1 hand: " + player1.getHand());   // only show player's hand
        } else {
            System.out.println("\nPLAYER2 hand: " + player2.getHand());
        }
    }

    private void showBooks(boolean cpu) {
        if (!cpu) {
            System.out.println("PLAYER1 books: " + player1.getBooks());   // shows the player's books
        } else {
            System.out.println("\nPLAYER2 books: " + player2.getBooks());  // shows the computer's books
        }
    }
}