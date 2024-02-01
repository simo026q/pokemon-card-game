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
import java.util.List;

public class GameActivity extends AppCompatActivity {

    CardAdapter playersHandAdapter;

    CardAdapter opponentsHandAdapter;

    private List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get the players from the intent
        players = (List<Player>) getIntent().getSerializableExtra("players");

        // Initialize the card lists (empty for now)
        assert players != null;
        for (Player player : players) {
            player.hand = new ArrayList<PokemonCardDetails>();
        }

        // Set up the adapters and recycler views
        for (Player player : players) {
            if (!player.AI) {
                RecyclerView playersHandRecyclerView = findViewById(R.id.players_hand);
                playersHandAdapter = new CardAdapter(player.hand);
                playersHandRecyclerView.setAdapter(playersHandAdapter);
                playersHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
            else {
                RecyclerView opponentsHandRecyclerView = findViewById(R.id.opponents_hand);
                opponentsHandAdapter = new CardAdapter(player.hand, false);
                opponentsHandRecyclerView.setAdapter(opponentsHandAdapter);
                opponentsHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
        }

        // You can add cards to the lists and notify the adapters as needed
    }
}


