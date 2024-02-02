package com.example.pokemoncardgame.data;

import java.util.List;
import java.util.Random;

public class AiPlayer extends Player {
    public AiPlayer(String name, List<PokemonCardDetails> playerHand) {
        super(name, playerHand, true);
    }

    public PokemonCardDetails getRandomCardFromHand() {
        Random random = new Random();
        int randomIndex = random.nextInt(hand.size());
        return hand.get(randomIndex);
    }
}
