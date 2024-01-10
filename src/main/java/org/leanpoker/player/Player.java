package org.leanpoker.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class Player {

    static final ObjectMapper mapper = new ObjectMapper();

    static final String VERSION = "Default Java folding player";
    static final List<String> HIGH_CARDS = List.of("A", "K", "Q", "J", "10");
    static boolean TWO_HIGH = false;
    static boolean ONE_HIGH = false;
    static boolean PAIR_RANK = false;
    static boolean PAIR_COLOR = false;

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

    public static int betRequest(JsonNode request) throws JsonProcessingException {
        var gameState = mapper.treeToValue(request, Request.class);
//        var cards = Arrays.stream(gameState.players()).filter(player -> player.name().equals("Cota")).findFirst().get().hole_cards();
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
        if (gameState.round() == 0) {
            if (TWO_HIGH || ONE_HIGH || PAIR_RANK || PAIR_COLOR) {
                if (gameState.current_buy_in() < 50) {
                    return call(gameState);
                } else {
                    return fold();
                }
            } else {
                return fold();
            }
        } else {
            return fold();
        }
    }

    private static int fold() {
        return 0;
    }

    private static int call(Request request) {
        var player = request.players()[request.in_action()];
        var bet = player.bet();
        var call = request.current_buy_in() - bet;
        System.out.println("************Call for " + call + "**************");
        return request.current_buy_in() - bet;
    }

//    private static int minimumRaise(Request request) {
//        var cards = request.players()[request.in_action()].hole_cards();
//        var communityCards = request.community_cards();
//        var currentBuyIn = request.current_buy_in();
//        var allCards = Arrays.asList(cards, communityCards);
//        if (singlePair(allCards)) {
//            return Math.max(0, (PAIR - currentBuyIn));
//        }
//        if (doublePair(allCards)) {
//            return Math.max(0, (DOUBLE_PAIR - currentBuyIn));
//        }
//        if (threeOfAKind(allCards)) {
//            return Math.max(0, (THREE_OF_A_KIND - currentBuyIn));
//        }
//        if (singlePair(allCards)) {
//            return Math.max(0, (FULL_HOUSE - currentBuyIn));
//        }
////        if (singlePair()) {
////            return Math.max(0, (HIGH - currentBuyIn));
////        }
////        if (singlePair()) {
////            return Math.max(0, (HIGH - currentBuyIn));
////        }
//
//        return 0;
//    }
//
//    private threeOfAKind(Card[] cards) {
//      HashMap<int, int>  = new HashMap<int, int>();
//    }

//    private frequencies(Card[] cards) {
    
//}
//
//    private boolean sameRank(Card[] card, Card[] communityCard) {
//        if (card[0].rank().equals(card[1].rank())) {
//            // pair
//            PAIR_RANK = true;
//        } else if (List.of(card[0], card[1]).contains(communityCard[0].rank())) {
//            // color pair
//            PAIR_COLOR = true;
//        } else {
//
//        }
//    }

    public static void showdown(JsonNode game) {
    }
}

