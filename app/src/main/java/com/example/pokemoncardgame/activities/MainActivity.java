package com.example.pokemoncardgame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.pokemoncardgame.R;
import com.example.pokemoncardgame.data.network.PokemonCardService;

public class MainActivity extends AppCompatActivity {

    private PokemonCardService pokemonCardService;

    private TextView debugTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugTextView = findViewById(R.id.tv_debug);

        pokemonCardService = new PokemonCardService(this);
        pokemonCardService.getRandomCards(10, cards -> {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < cards.size(); i++) {
                sb.append(cards.get(i).name);
                sb.append("\n");
            }

            String debugText = sb.toString();

            this.runOnUiThread(() -> {
                debugTextView.setText(debugText);
            });
        });
    }
}