package com.example.pokemoncardgame.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemoncardgame.CardAdapter;
import com.example.pokemoncardgame.R;
import com.example.pokemoncardgame.data.Player;
import com.example.pokemoncardgame.data.PokemonCardDetails;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private RecyclerView playersHandRecyclerView;
    private RecyclerView opponentsHandRecyclerView;
    private CardAdapter playersHandAdapter;
    private CardAdapter opponentsHandAdapter;
    private Player player1;
    private Player player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get the players from the intent
        player1 = (Player) getIntent().getSerializableExtra("player1");
        player2 = (Player) getIntent().getSerializableExtra("player2");

        playersHandRecyclerView = findViewById(R.id.players_hand);
        opponentsHandRecyclerView = findViewById(R.id.opponents_hand);

        // Initialize the card lists (empty for now)
        player1.hand = new ArrayList<PokemonCardDetails>();
        player2.hand = new ArrayList<PokemonCardDetails>();

        // Set up the adapters
        playersHandAdapter = new CardAdapter(player1.hand);
        opponentsHandAdapter = new CardAdapter(player2.hand, false);

        playersHandRecyclerView.setAdapter(playersHandAdapter);
        playersHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        opponentsHandRecyclerView.setAdapter(opponentsHandAdapter);
        opponentsHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // You can add cards to the lists and notify the adapters as needed
    }
}


