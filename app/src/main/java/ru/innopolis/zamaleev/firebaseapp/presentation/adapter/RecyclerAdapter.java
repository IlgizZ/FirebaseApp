package ru.innopolis.zamaleev.firebaseapp.presentation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Event;

/**
 * Created by Ilgiz on 02.06.2017.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private List<Event> events;

    public RecyclerAdapter(List<Event> events) {
        this.events = events;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_card, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Event event = events.get(position);
        holder.cardTitle.setText(event.getName());
        holder.cardCity.setText(event.getCity());
        holder.cardDate.setText(event.getDate());
        holder.cardDescription.setText(event.getDescription());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView cardTitle;
        private TextView cardCity;
        private TextView cardDate;
        private TextView cardDescription;
        private Button eventBotton;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
            cardCity = (TextView) itemView.findViewById(R.id.card_city);
            cardDate = (TextView) itemView.findViewById(R.id.card_date);
            cardDescription = (TextView) itemView.findViewById(R.id.card_description);
        }
    }
}
