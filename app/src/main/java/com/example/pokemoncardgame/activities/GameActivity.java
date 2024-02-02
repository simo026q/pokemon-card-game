package com.example.pokemoncardgame.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemoncardgame.CardAdapter;
import com.example.pokemoncardgame.OnCardClickListener;
import com.example.pokemoncardgame.R;
import com.example.pokemoncardgame.data.AiPlayer;
import com.example.pokemoncardgame.data.Game;
import com.example.pokemoncardgame.data.Player;
import com.example.pokemoncardgame.data.PokemonCardDetails;
import com.example.pokemoncardgame.data.network.PokemonCardService;
import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameActivity extends AppCompatActivity implements OnCardClickListener {

    CardAdapter playersHandAdapter;

    CardAdapter opponentsHandAdapter;

    Game game;

    private Handler handler = new Handler();
    private Runnable setupRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int numberOfCards = getIntent().getIntExtra("numberOfCards", 0);
        game = new Game(numberOfCards, new PokemonCardService(this));

        setupRunnable = new Runnable() {
            @Override
            public void run() {
                if (!game.getPlayers().isEmpty()) {
                    setupRecyclerViews();
                } else {
                    handler.postDelayed(this, 500); // Check again after 500ms
                }
            }
        };
        handler.post(setupRunnable);
        Thread gameLoopThread = new Thread(this::gameLoop);
        gameLoopThread.start();
    }

    private void setupRecyclerViews() {
        for (Player player : game.getPlayers()) {
            if (!player.isAi) {
                RecyclerView playersHandRecyclerView = findViewById(R.id.players_hand);
                playersHandAdapter = new CardAdapter(player.hand, this, true);
                playersHandRecyclerView.setAdapter(playersHandAdapter);
                playersHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            } else {
                RecyclerView opponentsHandRecyclerView = findViewById(R.id.opponents_hand);
                opponentsHandAdapter = new CardAdapter(player.hand, this);
                opponentsHandRecyclerView.setAdapter(opponentsHandAdapter);
                opponentsHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
        }
    }

    private boolean playAiTurn() {
        if (game.getPlayers().size() < 2) return true; // Safety check

        AiPlayer aiPlayer = (AiPlayer) game.getCurrentPlayer();
        synchronized (aiPlayer.hand) { // Synchronize on the aiPlayer's hand
            // Ensure there's an opponent and active card selection is safe
            Player opponent = game.getPlayers().get(aiPlayer == game.getPlayers().get(0) ? 1 : 0);
            if (aiPlayer.hasLost() || opponent == null) {
                return true;
            }
            if (aiPlayer.activeCard != null && opponent.activeCard != null) {
                aiPlayer.attackWithCard(opponent.activeCard);
                game.nextTurn();
            }
            if (aiPlayer.activeCard == null && !aiPlayer.hand.isEmpty()) {
                PokemonCardDetails card = aiPlayer.getRandomCardFromHand();
                int cardPosition = game.getCurrentPlayer().hand.indexOf(card);
                if (cardPosition != -1) {
                    opponentsHandAdapter.notifyItemRemoved(cardPosition);
                }
                aiPlayer.setActiveCard(card);
                updateActiveCardViews();
                game.nextTurn();
            }
        }
        return false;
    }

    private void gameLoop() {
        AtomicBoolean isGameOver = new AtomicBoolean(false);
        while (!isGameOver.get()) {
            // Ensure game loop runs on the main thread when updating UI components
            runOnUiThread(() -> {
                if (!game.getPlayers().isEmpty() && game.getCurrentPlayer() != null) {
                    if (game.getCurrentPlayer().isAi) {
                        isGameOver.set(playAiTurn());
                    } else {
                        // For non-AI, check for game over condition
                        isGameOver.set(game.getCurrentPlayer().hasLost());
                    }
                }
            });
            try {
                Thread.sleep(5000); // Pause to simulate turn duration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interruption status
            }
        }
    }


    public void updateActiveCardViews() {
        ImageView playerActiveCardView = findViewById(R.id.player_active_card);
        ImageView opponentActiveCardView = findViewById(R.id.opponent_active_card);

        for (Player player : game.getPlayers()) {
            if (player.activeCard != null) { // Check if the player has an active card
                if (!player.isAi) {
                    // It's a human player
                    Picasso.get()
                            .load(player.activeCard.image + "/high.jpg")
                            .into(playerActiveCardView);
                } else {
                    // It's an AI player
                    Picasso.get()
                            .load(player.activeCard.image + "/high.jpg")
                            .into(opponentActiveCardView);
                }
            }
        }
    }

    @Override
    public void onCardClick(PokemonCardDetails card) {
        if (!game.getCurrentPlayer().isAi) {
            int cardPosition = game.getCurrentPlayer().hand.indexOf(card);
            if (cardPosition != -1) {
                playersHandAdapter.notifyItemRemoved(cardPosition);
            }
            game.getCurrentPlayer().setActiveCard(card);
            updateActiveCardViews();
            Toast.makeText(this, "Card clicked: " + card.name, Toast.LENGTH_SHORT).show();
            game.nextTurn();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
