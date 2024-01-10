package org.leanpoker.player;

public record PlayerRequest(
        int id,
        String name,
        String status,
        String version,
        int stack,
        int bet,
        Card[] hole_cards
) {
}


