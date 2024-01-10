package org.leanpoker.player;

public record PlayerRequest(
        String name,
        int stack,
        String status,
        int bet,
        Card[] hole_cards,
        String version,
        int id
) {
}


