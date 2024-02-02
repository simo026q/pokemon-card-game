package com.example.pokemoncardgame.data;

import java.util.ArrayList;

public class PokemonCardDetails extends PokemonCard {
    public String category;
    public String illustrator;
    public String rarity;
    public Set set;
    public Variants variants;
    public ArrayList<Integer> dexId;
    public int hp;
    public ArrayList<String> types;
    public String evolveFrom;
    public String stage;
    public ArrayList<Ability> abilities;
    public ArrayList<Attack> attacks;
    public ArrayList<Weakness> weaknesses;
    public Legal legal;

    public int getAttackDamage() {
        int damage = 0;
        if (attacks != null) {
            for (Attack attack : attacks) {
                damage = Math.max(damage, attack.damage);
            }
        }
        return damage;
    }

    public void dealDamage(int damage) {
        hp -= damage;
    }

    public boolean isAlive() {
        return hp > 0;
    }
}

class Ability{
    public String type;
    public String name;
    public String effect;
}

class Attack {
    public ArrayList<String> cost;
    public String name;
    public String effect;
    public int damage;
}

class CardCount {
    public int official;
    public int total;
}

class Legal {
    public boolean standard;
    public boolean expanded;
}

class Set {
    public CardCount cardCount;
    public String id;
    public String logo;
    public String name;
    public String symbol;
}

class Variants {
    public boolean firstEdition;
    public boolean holo;
    public boolean normal;
    public boolean reverse;
    public boolean wPromo;
}

class Weakness {
    public String type;
    public String value;
}