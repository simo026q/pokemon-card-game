package com.example.pokemoncardgame.data;

import com.example.pokemoncardgame.data.network.PokemonCardService;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public final List<Player> players;

    private int turnIdx = 0;

    public Game(int numberOfCards, PokemonCardService pokemonCardService) {
        this.players = new ArrayList<Player>(2);

        pokemonCardService.getRandomCards(numberOfCards * 2, cards -> {
            players.add(new Player("Player", cards.subList(0, numberOfCards)));
            players.add(new AiPlayer("Bot", cards.subList(numberOfCards, numberOfCards * 2)));
        });
    }

    public Player getCurrentPlayer() {
        return players.get(turnIdx);
    }

    public Player nextTurn() {
        turnIdx = (turnIdx + 1) % players.size();
        return getCurrentPlayer();
    }
}
