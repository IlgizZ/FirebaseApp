package ru.innopolis.zamaleev.firebaseapp.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.MyEvent;
import ru.innopolis.zamaleev.firebaseapp.presentation.view.EventCreator;
import ru.innopolis.zamaleev.firebaseapp.presentation.view.EventInfo;

/**
 * Created by Ilgiz on 02.06.2017.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private List<MyEvent> myEvents;

    public RecyclerAdapter(List<MyEvent> myEvents) {
        this.myEvents = myEvents;
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
        MyEvent myEvent = myEvents.get(position);
        holder.cardTitle.setText(myEvent.getName());
        holder.cardCity.setText(myEvent.getCity());
        holder.cardDate.setText(myEvent.getDate_begin());
        holder.cardDescription.setText(myEvent.getDescription());
        holder.eventButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventInfo.class);
            intent.putExtra("event", myEvent.getEvent_id());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return myEvents.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView cardTitle;
        private TextView cardCity;
        private TextView cardDate;
        private TextView cardDescription;
        private Button eventButton;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
            cardCity = (TextView) itemView.findViewById(R.id.card_city);
            cardDate = (TextView) itemView.findViewById(R.id.card_date);
            cardDescription = (TextView) itemView.findViewById(R.id.card_description);
            eventButton = (Button) itemView.findViewById(R.id.event_btn);
        }
    }
}
