package com.example.pokemoncardgame.data;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Player {
    public String id;
    public String name;
    public final boolean isAi;

    public List<PokemonCardDetails> hand;

    @Nullable
    public PokemonCardDetails activeCard;

    public Player(String name, List<PokemonCardDetails> playerHand) {
        this(name, playerHand, false);
    }

    protected Player(String name, List<PokemonCardDetails> playerHand, boolean isAi) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.hand = playerHand;
        this.isAi = isAi;
    }

    public synchronized boolean setActiveCard(PokemonCardDetails card) {
        if (hand.contains(card)) {
            hand.remove(card);
            activeCard = card;
            return true;
        }
        return false;
    }

    public synchronized void attackWithCard(PokemonCardDetails defenderCard) {
        if (activeCard != null) {
            defenderCard.dealDamage(activeCard.getAttackDamage());

            if (!activeCard.isAlive()) {
                activeCard = null;
            }
        }
    }

    public synchronized boolean hasLost() {
        return activeCard == null && hand.isEmpty();
    }


    /*private String attack(PokemonCardDetails attacker, PokemonCardDetails defender, Attack attack) {
        if (attacker.hp <= 0) {
            activeCard = null;
            return attacker.name + " has fainted and cannot attack!";
        }
        if (attack.damage > 0) {
            defender.hp -= attack.damage;
        }
        if (attack.damage > 0 && defender.hp <= 0) {
            return attacker.name + " attacked " + defender.name + " for " + attack.damage + " damage and knocked it out!";
        }
        return attacker.name + " attacked " + defender.name + " for " + attack.damage + " damage!";
    }*/
}
