package com.example.pokemoncardgame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemoncardgame.data.PokemonCardDetails;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<PokemonCardDetails> cardList;
    private final OnCardClickListener listener;
    private boolean playerhand;

    // Constructor with listener
    public CardAdapter(List<PokemonCardDetails> cardList, OnCardClickListener listener, boolean... isPlayersHand) {
        this.cardList = cardList;
        this.listener = listener;
        playerhand = isPlayersHand != null && isPlayersHand.length > 0 && isPlayersHand[0];
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        PokemonCardDetails card = cardList.get(position);
        if (playerhand) {
            holder.cardNameTextView.setText(card.name);
            Picasso.get()
                    .load(card.image + "/high.jpg")
                    .into(holder.cardImage);
        } else {
            holder.cardNameTextView.setText("Opponent's Card");
            holder.cardImage.setImageResource(R.drawable.card_back);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardClick(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView cardNameTextView;

        public ImageView cardImage;

        public CardViewHolder(View view) {
            super(view);
            cardNameTextView = view.findViewById(R.id.cardName); // Assuming you have a TextView in your card item layout with this ID
            cardImage = view.findViewById(R.id.cardImage);
        }
    }
}
