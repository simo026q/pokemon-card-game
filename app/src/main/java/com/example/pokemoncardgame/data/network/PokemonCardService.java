package com.example.pokemoncardgame.data.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokemoncardgame.data.PokemonCard;
import com.example.pokemoncardgame.data.PokemonCardDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class PokemonCardService {
    private static final String BASE_URI = "https://api.tcgdex.net/v2/en";
    private static List<PokemonCard> pokemonCards = new ArrayList<PokemonCard>(0);

    private final RequestQueue requestQueue;

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

        StringRequest request = new StringRequest(uri,
            response -> {
                PokemonCardDetails card = null;
                try {
                    card = new Gson().fromJson(response, PokemonCardDetails.class);
                }
                catch (Exception err) {
                    System.out.println("Error: " + err);
                }
                finally {
                    callback.accept(card);
                }
            },
            err -> {
                System.out.println("Error: " + err.toString());
                callback.accept(null);
            }
        );

        requestQueue.add(request);
    }

    public void getRandomCards(int count, Consumer<List<PokemonCardDetails>> callback) {
        getCards(pokemonCards -> {
            Thread thread = new Thread(() -> {
                int safeCount = Math.min(Math.max(0, count), pokemonCards.size());

                List<PokemonCardDetails> randomCards = new ArrayList<PokemonCardDetails>(safeCount);

                AtomicBoolean isReady = new AtomicBoolean(true);
                while (randomCards.size() < safeCount) {
                    if (!isReady.get()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    isReady.set(false);

                    int randomIndex = (int)(Math.random() * pokemonCards.size());
                    PokemonCard card = pokemonCards.get(randomIndex);

                    getCardDetails(card.id, randomCard -> {
                        if (randomCard != null && !randomCards.contains(randomCard)) {
                            randomCards.add(randomCard);
                        }

                        isReady.set(true);
                    });
                }

                callback.accept(randomCards);
            });

            thread.start();
        });
    }
}