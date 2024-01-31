package com.example.pokemoncardgame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pokemoncardgame.CardAdapter;
import com.example.pokemoncardgame.R;
import com.example.pokemoncardgame.data.PokemonCard;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private RecyclerView playersHandRecyclerView;
    private RecyclerView opponentsHandRecyclerView;
    private CardAdapter playersHandAdapter;
    private CardAdapter opponentsHandAdapter;
    private List<PokemonCard> playersHand;
    private List<PokemonCard> opponentsHand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playersHandRecyclerView = findViewById(R.id.players_hand);
        opponentsHandRecyclerView = findViewById(R.id.opponents_hand);

        // Initialize the card lists (empty for now)
        playersHand = new ArrayList<PokemonCard>();
        opponentsHand = new ArrayList<PokemonCard>();

        // Set up the adapters
        playersHandAdapter = new CardAdapter(playersHand);
        opponentsHandAdapter = new CardAdapter(opponentsHand, false);

        playersHandRecyclerView.setAdapter(playersHandAdapter);
        playersHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        opponentsHandRecyclerView.setAdapter(opponentsHandAdapter);
        opponentsHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // You can add cards to the lists and notify the adapters as needed
    }
}


