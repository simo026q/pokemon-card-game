package com.example.pokemoncardgame.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.concurrent.atomic.AtomicBoolean;

public class GameActivity extends AppCompatActivity implements OnCardClickListener {

    CardAdapter playersHandAdapter;

    CardAdapter opponentsHandAdapter;

    Game game;

    private Handler handler = new Handler();
    private Runnable setupRunnable;
    Button attackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int numberOfCards = getIntent().getIntExtra("numberOfCards", 0);
        game = new Game(numberOfCards, new PokemonCardService(this));

        attackButton = findViewById(R.id.player_attack_button);
        attackButton.setEnabled(false);
        attackButton.setOnClickListener(v -> {
            performAttack();
        });

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


    private void performAttack() {
        if (!game.getCurrentPlayer().isAi) {
            // Implement the logic to perform an attack with the player's active card
            // This might involve modifying the game state, updating the UI, etc.
            AiPlayer opponent = (AiPlayer) game.getPlayers().get(game.getCurrentPlayer() == game.getPlayers().get(1) ? 0 : 1);
            attackButton.setEnabled(false);
            Toast.makeText(this, game.getCurrentPlayer().attackWithCard(opponent.activeCard), Toast.LENGTH_LONG).show();

            // Update UI or game state as needed after the attack
            updateActiveCardViews();
            game.nextTurn();
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
                Toast.makeText(this, aiPlayer.attackWithCard(opponent.activeCard), Toast.LENGTH_LONG).show();
                updateActiveCardViews();
                try {
                    Thread.sleep(5000); // Pause to simulate turn duration
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interruption status
                }
                game.nextTurn();
            }
            if (aiPlayer.activeCard == null && !aiPlayer.hand.isEmpty()) {
                PokemonCardDetails card = aiPlayer.getRandomCardFromHand();
                int cardPosition = game.getCurrentPlayer().hand.indexOf(card);
                if (cardPosition != -1) {
                    opponentsHandAdapter.notifyItemRemoved(cardPosition);
                }
                Toast.makeText(this, aiPlayer.setActiveCard(card), Toast.LENGTH_LONG).show();
                updateActiveCardViews();
                try {
                    Thread.sleep(200); // Pause to simulate turn duration
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interruption status
                }
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
                    cardDeathHandler();
                    if (game.getCurrentPlayer().isAi) {
                        isGameOver.set(playAiTurn());
                    } else {
                        // For non-AI, check for game over condition
                        isGameOver.set(game.getCurrentPlayer().hasLost());
                    }
                }
            });
            try {
                Thread.sleep(1000); // Pause to simulate turn duration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interruption status
            }
        }
        finish();
    }


    public void updateActiveCardViews() {
        ImageView playerActiveCardView = findViewById(R.id.player_active_card);
        ImageView opponentActiveCardView = findViewById(R.id.opponent_active_card);
        TextView playerCardInfoView = findViewById(R.id.player_card_info);
        TextView opponentCardInfoView = findViewById(R.id.opponent_card_info);

        for (Player player : game.getPlayers()) {
            if (player.activeCard != null) { // Check if the player has an active card
                // Construct the card information string
                PokemonCardDetails activeCard = player.activeCard;
                //set the name, the attack with most damage and health values
                String cardInfo = activeCard.name + "\n" + "Attack: " + activeCard.getAttackDamage() + " damage" + "\n" + "Health: " + activeCard.hp + " HP";
                // Update the TextView with the card information
                if (!player.isAi) {
                    playerCardInfoView.setText(cardInfo);
                    Button attackButton = findViewById(R.id.player_attack_button);
                    // It's a human player
                    Picasso.get()
                            .load(player.activeCard.image + "/high.jpg")
                            .into(playerActiveCardView);
                    playerActiveCardView.setVisibility(ImageView.VISIBLE);
                } else {
                    opponentCardInfoView.setText(cardInfo);
                    // It's an AI player
                    Picasso.get()
                            .load(player.activeCard.image + "/high.jpg")
                            .into(opponentActiveCardView);
                    opponentActiveCardView.setVisibility(ImageView.VISIBLE);
                }
            }
            else {
                if (!player.isAi) {
                    playerCardInfoView.setText("No active card");
                    playerActiveCardView.setVisibility(ImageView.INVISIBLE);
                } else {
                    opponentCardInfoView.setText("No active card");
                    opponentActiveCardView.setVisibility(ImageView.INVISIBLE);
                }
            }
        }
    }

    private void cardDeathHandler() {
        if (game.getCurrentPlayer().activeCard != null && game.getCurrentPlayer().activeCard.hp <= 0) {
            game.getCurrentPlayer().activeCard = null;
            attackButton.setEnabled(false);
            updateActiveCardViews();
        }
        else if (game.getCurrentPlayer().activeCard != null) {
            attackButton.setEnabled(true);
        }
    }


    @Override
    public void onCardClick(PokemonCardDetails card) {
        if (!game.getCurrentPlayer().isAi && game.getCurrentPlayer().hand.contains(card) && game.getCurrentPlayer().activeCard == null) {
            int cardPosition = game.getCurrentPlayer().hand.indexOf(card);
            playersHandAdapter.notifyItemRemoved(cardPosition);
            playersHandAdapter.notifyItemRangeChanged(cardPosition, game.getCurrentPlayer().hand.size());
            Toast.makeText(this, game.getCurrentPlayer().setActiveCard(card), Toast.LENGTH_SHORT).show();
            attackButton.setEnabled(true);
            updateActiveCardViews();

            game.nextTurn(); // Move to the next turn
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
