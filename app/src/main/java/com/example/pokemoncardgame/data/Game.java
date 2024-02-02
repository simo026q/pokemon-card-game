package com.example.pokemoncardgame.data;

import com.example.pokemoncardgame.data.network.PokemonCardService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private final List<Player> players = Collections.synchronizedList(new ArrayList<Player>(2));
    private int turnIdx = 0;

    public Game(int numberOfCards, PokemonCardService pokemonCardService) {
        pokemonCardService.getRandomCards(numberOfCards * 2, cards -> {
            synchronized (players) {
                players.add(new Player("Player", new ArrayList<>(cards.subList(0, numberOfCards)))); // Use new ArrayList for thread safety
                players.add(new AiPlayer("Bot", new ArrayList<>(cards.subList(numberOfCards, numberOfCards * 2)))); // Ditto
            }
        });
    }

    public synchronized List<Player> getPlayers() {
        return new ArrayList<>(players); // Return a copy to avoid ConcurrentModificationException
    }

    public synchronized Player getCurrentPlayer() {
        return players.get(turnIdx);
    }

    public synchronized Player nextTurn() {
        turnIdx = (turnIdx + 1) % players.size();
        return getCurrentPlayer();
    }
}
