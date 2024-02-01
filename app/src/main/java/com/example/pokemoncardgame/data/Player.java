package com.example.pokemoncardgame.data;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Player {
    public String id;
    public String name;
    public boolean AI;

    public AIProfile aiProfile = AIProfile.RANDOM;

    public List<PokemonCardDetails> hand;
    public PokemonCardDetails activePokemon;

    public Player(String name, boolean AI) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.AI = AI;
    }

    private String playCard(PokemonCardDetails card) {
        hand.remove(card);
        activePokemon = card;
        return name + " played " + card.name + "!";
    }

    private String attack(PokemonCardDetails attacker, PokemonCardDetails defender, Attack attack) {
        if (attacker.hp <= 0) {
            activePokemon = null;
            return attacker.name + " has fainted and cannot attack!";
        }
        if (attack.damage > 0) {
            defender.hp -= attack.damage;
        }
        if (attack.damage > 0 && defender.hp <= 0) {
            return attacker.name + " attacked " + defender.name + " for " + attack.damage + " damage and knocked it out!";
        }
        return attacker.name + " attacked " + defender.name + " for " + attack.damage + " damage!";
    }

    private boolean activeDead() {
        if (activePokemon != null && activePokemon.hp <= 0) {
            activePokemon = null;
            return true;
        }
        return false;
    }

    private boolean hasLost() {
        return hand.size() == 0 && activePokemon.hp <= 0;
    }

    public void playAICard(Player opponent) {
        Random rand = new Random();
        if (activeDead()) {
            if (AIProfile.RANDOM.equals(aiProfile)) {
                 playCard(hand.get(rand.nextInt(hand.size() - 1)));
            }
            else {
                for (PokemonCardDetails card : hand) {
                    if (AIProfile.PASSIVE.equals(aiProfile)) {
                        if (card.hp > 0 && card.hp < opponent.activePokemon.hp) {
                            playCard(card);
                            break;
                        }
                    } else if (AIProfile.AGGRESSIVE.equals(aiProfile)) {
                        if (card.hp > 0) {
                            for (Attack attack : card.attacks) {
                                if (attack.damage > 0 && attack.damage >= opponent.activePokemon.hp) {
                                    playCard(card);
                                    break;
                                }
                            }
                            if (activePokemon != null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
