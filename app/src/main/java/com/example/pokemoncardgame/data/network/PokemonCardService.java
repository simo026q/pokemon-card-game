package com.example.pokemoncardgame.data.network;

import android.content.Context;

import com.android.volley.AsyncNetwork;
import com.android.volley.AsyncRequestQueue;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.AsyncHttpStack;
import com.android.volley.toolbox.BasicAsyncNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokemoncardgame.data.PokemonCard;
import com.example.pokemoncardgame.data.PokemonCardDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PokemonCardService {
    private static final String BASE_URI = "https://api.tcgdex.net/v2/en";
    private static List<PokemonCard> pokemonCards = new ArrayList<PokemonCard>(0);

    private RequestQueue requestQueue;

    public PokemonCardService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
    }

    private void getCards(Consumer<List<PokemonCard>> callback) {
        if (pokemonCards.size() == 0) {
            StringRequest request = new StringRequest(
                Request.Method.GET,
                BASE_URI + "/cards",
                response -> {
                    pokemonCards = new Gson().fromJson(response, new TypeToken<List<PokemonCard>>(){}.getType());
                    callback.accept(pokemonCards);
                },
                error -> {
                    System.out.println("Error: " + error.toString());
                    callback.accept(pokemonCards);
                }
            );

            requestQueue.add(request);
        }
        else {
            callback.accept(pokemonCards);
        }
    }

    private void getCardDetails(String id, Consumer<PokemonCardDetails> callback) {
        String uri = BASE_URI + "/cards/" + id;

        StringRequest request = new StringRequest(
                uri,
            response -> {
                PokemonCardDetails card = new Gson().fromJson(response, PokemonCardDetails.class);
                callback.accept(card);
            },
            error -> {
                System.out.println("Error: " + error.toString());
                callback.accept(null);
            }
        );

        requestQueue.add(request);
    }

    public synchronized void getRandomCards(int count, Consumer<List<PokemonCardDetails>> callback) {
        getCards(cards -> {
            int safeCount = Math.min(Math.max(0, count), pokemonCards.size());

            List<PokemonCardDetails> randomCards = new ArrayList<PokemonCardDetails>(safeCount);

            Wrapper<Boolean> ready = new Wrapper<Boolean>(true);
            while (randomCards.size() < safeCount) {
                if (!ready.value) {
                    continue;
                }

                System.out.println("getRandomCards :: Ready - " + randomCards.size() + " / " + safeCount);

                int randomIndex = (int)(Math.random() * pokemonCards.size());
                PokemonCard card = pokemonCards.get(randomIndex);

                ready.value = false;
                getCardDetails(card.id, randomCard -> {
                    if (randomCard == null || randomCards.contains(randomCard) || randomCard.category != "Pokemon") {
                        ready.value = true;
                        return;
                    }

                    randomCards.add(randomCard);
                    ready.value = true;

                    if (randomCards.size() == safeCount) {
                        callback.accept(randomCards);
                    }
                });
            }

            /*for (int i = 0; i < safeCount; i++) {
                int randomIndex = (int)(Math.random() * pokemonCards.size());
                PokemonCard card = pokemonCards.get(randomIndex);

                int finalI = i;

                getCardDetails(card.id, randomCard -> {
                    randomCards.add(randomCard);

                    if (finalI + 1 == safeCount) {
                        callback.accept(randomCards);
                    }
                });
            }*/
        });
    }
}

class Wrapper<T> {
    public T value;

    public Wrapper(T value) {
        this.value = value;
    }
}