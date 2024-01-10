package org.leanpoker.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Card(
        String rank,
        String suit
) {
}
