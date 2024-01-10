package org.leanpoker.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class Player {

    static final ObjectMapper mapper = new ObjectMapper();

    static final String VERSION = "Default Java folding player";
    static final List<String> HIGHEST_CARDS = List.of("A", "K", "Q");
    static final List<String> HIGH_CARDS = List.of("A", "K", "Q", "J", "10");

    boolean flush = false;
    boolean straight = false;

    // CONSECUTIVE ROUNDS

    static public final int HIGH = 50;

    static public final int PAIR = 75;

    static public final int DOUBLE_PAIR = 100;

    static public final int THREE_OF_A_KIND = 200;

    static public final int STRAIGHT = 300;

    static public final int FLUSH = 400;

    static public final int FULL_HOUSE = 500;

    static public final int FOUR_OF_A_KIND = 600;

    static public final int STRAIGHT_FLUSH = 800;

    static public final int ROYAL_FLUSH = 1000;

    static boolean startGambling = false;

    static boolean blueCounter = true;

    public static int betRequest(JsonNode request) {
        Request gameState;
        try {
            gameState = mapper.treeToValue(request, Request.class);
        } catch (JsonProcessingException e) {
            System.out.println("******************************");
            System.out.println("Error parsing request: " + e.getMessage());
            return fold();
        }
        if (startGambling) {
        }
        if (gameState.current_buy_in() == 1000 && gameState.community_cards().length == 0) {
            return fold();
        }
        if (gameState.community_cards().length == 0) {
            return firstRound(gameState);
        } else if (gameState.community_cards().length == 3) {
            return checkMyCards(gameState);
        } else if (gameState.community_cards().length == 4) {
            return checkMyCards(gameState);
        } else {
            return checkMyCards(gameState);
        }
    }

    private static int checkMyCards(Request request) {
        var blueCounterRaise = blueCounter ? 20 : 0;
        var allCards = getAllCards(request.players()[request.in_action()].hole_cards(), request.community_cards());
        var communityCards = Arrays.asList(request.community_cards());
        var twoOfAKind = countNOfAKind(allCards, 2);
        var threeOfKindInCommunity = countNOfAKind(communityCards, 3);
        var twoOfKindInCommunity = countNOfAKind(communityCards, 2);
        var threeOfAkind = countNOfAKind(allCards, 3);
        var fourOfAkind = countNOfAKind(allCards, 4);
        var haveHighCard = HIGHEST_CARDS.contains(request.players()[request.in_action()].hole_cards()[0].rank())
                || HIGHEST_CARDS.contains(request.players()[request.in_action()].hole_cards()[1].rank());
        if (isStraightFlush(allCards)) {
            return raise(request, STRAIGHT_FLUSH);
        } else if (isStraight(allCards)) {
            return raise(request, STRAIGHT);
        } else if (fourOfAkind == 1) {
            return request.players()[request.in_action()].stack();
        } else if (threeOfAkind == 1 && twoOfAKind == 1) {
            return raise(request, FULL_HOUSE);
        } else if (threeOfKindInCommunity == 1) {
            if (Objects.equals(request.players()[request.in_action()].hole_cards()[0].rank(), "A") || Objects.equals(request.players()[request.in_action()].hole_cards()[1].rank(), "A")) {
                return call(request);
            }
        } else if (threeOfAkind == 1 && threeOfKindInCommunity == 0) {
            return raise(request, THREE_OF_A_KIND);
        }
//        else if (isFlush(allCards)) {
//            return raise(request, FLUSH);
//        }
        else if (twoOfKindInCommunity == 2) {
            if (haveHighCard && currentBet(request) < 10 + blueCounterRaise)
                return call(request);
            else
                return fold();
        } else if (twoOfAKind == 2 && currentBet(request) < 10 + blueCounterRaise) {
            return raise(request, DOUBLE_PAIR);
        } else if (twoOfAKind == 1 && currentBet(request) < 20 + blueCounterRaise) {
            return call(request);
        }
        //here
        else if (getMaxConsecutive(allCards) == 4 && request.community_cards().length < 5) {
            if (currentBet(request) < 30)
                return call(request);
        }
//        } else if (maxSameSuitCards(allCards) == 4 && request.community_cards().length < 5) {
//            if (currentBet(request) < 20)
//                return call(request);
//        }
        //here
        else if (haveHighCard && currentBet(request) < 10 + blueCounterRaise) {
            return call(request);
        }
        return fold();
    }

    private static int raise(Request request, int raiseBy) {
        var currentBet = currentBet(request);
        var raise = currentBet + raiseBy;
        return Math.max(request.players()[request.in_action()].stack(), raise);
    }

    private static int fold() {
        return 0;
    }

    private static int call(Request request) {
        var call = Math.min(request.players()[request.in_action()].stack(), currentBet(request));
        System.out.println("************Call for " + call + "**************");
        return call;
    }

    private static int currentBet(Request request) {
        var player = request.players()[request.in_action()];
        var bet = player.bet();
        var currentBet = request.current_buy_in() - bet;
        System.out.println("************CurrentBet for " + currentBet + "**************");
        return currentBet;
    }

    private static int firstRound(Request gameState) {
        boolean TWO_HIGH = false;
        boolean ONE_HIGH = false;
        boolean PAIR_RANK = false;
        boolean PAIR_COLOR = false;
        var cards = gameState.players()[gameState.in_action()].hole_cards();
        if (HIGH_CARDS.contains(cards[0].rank()) && HIGH_CARDS.contains(cards[1].rank())) {
            TWO_HIGH = true;
        } else if (HIGH_CARDS.contains(cards[0].rank()) || HIGH_CARDS.contains(cards[1].rank())) {
            ONE_HIGH = true;
        }
        if (cards[0].rank().equals(cards[1].rank())) {
            // pair
            PAIR_RANK = true;
        } else if (cards[0].suit().equals(cards[1].suit())) {
            // color pair
            PAIR_COLOR = true;
        }
        if (TWO_HIGH && PAIR_RANK) {
            return raise(gameState, 100);
        }
        if (PAIR_RANK) {
            if (currentBet(gameState) < 100)
                return call(gameState);
            return fold();
        }
        if (TWO_HIGH) {
            if (currentBet(gameState) > 100) {
                return fold();
            } else {
                return call(gameState);
            }
        } else if (ONE_HIGH) {
            if (currentBet(gameState) > 50) {
                return fold();
            } else {
                return call(gameState);
            }
        } else if (PAIR_COLOR) {
            if (currentBet(gameState) > 10) {
                return fold();
            } else {
                return call(gameState);
            }
        } else if (currentBet(gameState) < 5) {
            return call(gameState);
        } else {
            return fold();
        }
    }

    static int countNOfAKind(List<Card> cards, int count) {
        var rankFrequency = new HashMap<String, Integer>();
        for (var card : cards) {
            rankFrequency.put(card.rank(), rankFrequency.getOrDefault(card.rank(), 0) + 1);
        }
        int pairsCount = 0;
        for (var rank : rankFrequency.keySet()) {
            if (rankFrequency.get(rank) == count) {
                pairsCount++;
            }
        }
        return pairsCount;
    }

    private static List<Card> getAllCards(Card[] myCards, Card[] communityCards) {
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(Arrays.asList(myCards));
        allCards.addAll(Arrays.asList(communityCards));
        return allCards;
    }

    public static void showdown(JsonNode game) {
    }

    public static int compare(String rank1, String rank2) {
        return rankToInt(rank1) - rankToInt(rank2);
    }

    private static int rankToInt(String rank) {
        return switch (rank) {
            case "J" -> 11;
            case "Q" -> 12;
            case "K" -> 13;
            case "A" -> 14;
            default -> Integer.parseInt(rank);
        };
    }

    private static boolean isFlush(List<Card> cards) {
        var suitCount = new HashMap<String, Integer>();
        for (var card : cards) {
            suitCount.put(card.suit(), suitCount.getOrDefault(card.suit(), 0) + 1);
        }
        return suitCount.values().stream().anyMatch(count -> count >= 5);
    }

    private static boolean isStraight(List<Card> cards) {
        return checkSequence(cards).size() == 5;
    }

    private static boolean isStraightFlush(List<Card> cards) {
        var set = checkSequence(cards);
        if (set.size() == 0) {
            return false;
        }
        var suit = set.stream().findAny().get().suit();
        var isSameColor = set.stream().allMatch(card -> card.suit().equals(suit));
//        isFlush = true
        return isSameColor && set.size() == 5;
    }

    private static HashSet<Card> checkSequence(List<Card> cards) {
        cards.sort((card1, card2) ->
                compare(card1.rank(), card2.rank()));

        var straightSet = new HashSet<Card>();
        for (int i = 1; i < cards.size(); i++) {
            if (compare(cards.get(i).rank(), cards.get(i - 1).rank()) == 1) {
                straightSet.add(cards.get(i - 1));
                straightSet.add(cards.get(i));
            } else {
                straightSet.clear();
            }
        }

        return straightSet;
    }

    private static int maxSameSuitCards(List<Card> cards) {
        var suitCount = new HashMap<String, Integer>();
        for (var card : cards) {
            suitCount.put(card.suit(), suitCount.getOrDefault(card.suit(), 0) + 1);
        }
        return suitCount.values().stream().mapToInt(count -> count).max().orElse(0);
    }

    private static int getMaxConsecutive(List<Card> cards) {
        cards.sort((card1, card2) ->
                compare(card1.rank(), card2.rank()));

        int maxConsecutive = 1;
        int consecutives = 1;
        for (int i = 1; i < cards.size(); i++) {
            if (compare(cards.get(i).rank(), cards.get(i - 1).rank()) == 1) {
                consecutives++;
            } else {
                maxConsecutive = Math.max(maxConsecutive, consecutives);
                consecutives = 0;
            }
        }
        return maxConsecutive;
    }

    public static void main(String[] args) {
        System.out.println(
                getMaxConsecutive(Arrays.asList(
                        new Card("A", "hearts"),
                        new Card("2", "hearts"),
                        new Card("3", "hearts"),
                        new Card("4", "hearts"),
                        new Card("5", "hearts")
                )));
    }
}
