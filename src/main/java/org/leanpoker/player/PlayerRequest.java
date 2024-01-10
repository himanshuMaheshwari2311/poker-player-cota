package org.leanpoker.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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


