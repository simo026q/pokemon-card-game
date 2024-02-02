package com.example.pokemoncardgame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokemoncardgame.R;

public class MainActivity extends AppCompatActivity {
    private EditText numCardsInput;
    private Button startGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numCardsInput = findViewById(R.id.numCardsInput);
        startGameButton = findViewById(R.id.startGameButton);

        startGameButton.setOnClickListener(view -> {
            String numCardsStr = numCardsInput.getText().toString();
            if (!numCardsStr.isEmpty()) {
                int numCards = Integer.parseInt(numCardsStr);
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("numberOfCards", numCards);
                startActivity(intent);
                startGameButton.setEnabled(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startGameButton.setEnabled(true); // Re-enable the button when the activity resumes
    }
}