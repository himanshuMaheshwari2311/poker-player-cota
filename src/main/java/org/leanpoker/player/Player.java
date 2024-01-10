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
        if (TWO_HIGH || ONE_HIGH || PAIR_RANK || PAIR_COLOR) {
            return call(gameState);
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
        return request.current_buy_in() - bet;
    }

//    private static int minimumRaise(Request request) {
//        var cards = Arrays.stream(request.players()).filter(player -> player.name().equals("Cota")).findFirst().get().hole_cards();
//        var communityCards = request.community_cards();
//
//        if (singlePair() || twoPair()) {
            
//        }
//        return 0;
//    }

    public static void showdown(JsonNode game) {
    }
}

