package com.example.pokemoncardgame.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemoncardgame.CardAdapter;
import com.example.pokemoncardgame.OnCardClickListener;
import com.example.pokemoncardgame.R;
import com.example.pokemoncardgame.data.Player;
import com.example.pokemoncardgame.data.PokemonCardDetails;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements OnCardClickListener {

    CardAdapter playersHandAdapter;

    CardAdapter opponentsHandAdapter;

    private List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get the players from the intent
        players = (List<Player>) getIntent().getSerializableExtra("players");

        // Initialize the card lists
        assert players != null;
        for (Player player : players) {
            player.hand = new ArrayList<PokemonCardDetails>();
        }

        // Set up the adapters and recycler views
        for (Player player : players) {
            if (!player.isAi) {
                RecyclerView playersHandRecyclerView = findViewById(R.id.players_hand);
                playersHandAdapter = new CardAdapter(player.hand, this);
                playersHandRecyclerView.setAdapter(playersHandAdapter);
                playersHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
            else {
                RecyclerView opponentsHandRecyclerView = findViewById(R.id.opponents_hand);
                opponentsHandAdapter = new CardAdapter(player.hand,this, false);
                opponentsHandRecyclerView.setAdapter(opponentsHandAdapter);
                opponentsHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
        }

        // You can add cards to the lists and notify the adapters as needed
    }

    @Override
    public void onCardClick(PokemonCardDetails card) {
        // Handle the card click event here
        // For example, you can start a new activity or fragment with the card details
        Toast.makeText(this, "Card clicked: " + card.name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}


