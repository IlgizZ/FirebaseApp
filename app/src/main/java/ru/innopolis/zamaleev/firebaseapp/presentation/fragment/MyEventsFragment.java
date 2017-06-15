package ru.innopolis.zamaleev.firebaseapp.presentation.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.innopolis.zamaleev.firebaseapp.presentation.view.EventCreator;
import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.presentation.adapter.RecyclerAdapter;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Event;

/**
 * Created by Ilgiz on 6/8/2017.
 */

public class MyEventsFragment extends Fragment {
    private DatabaseReference myRef;
    private List<Event> events;
    private RecyclerView recycler;
    private RecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private TextView tVNoData;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recycler = (RecyclerView) getView().findViewById(R.id.event_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        events = new ArrayList();

        adapter = new RecyclerAdapter(events);

        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        recycler.setNestedScrollingEnabled(false);
        recycler.setHasFixedSize(true);


        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recycler.setItemAnimator(itemAnimator);

        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Events");

        tVNoData = (TextView) getView().findViewById(R.id.text_no_data);

        fab = (FloatingActionButton) getView().findViewById(R.id.event_activity_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EventCreator.class);
                startActivity(intent);
            }
        });

        updateListener();

        checkIfDataEmpty();
    }

    private void updateListener() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                event.setId(Long.valueOf(dataSnapshot.getKey()));

                events.add(event);

                Collections.sort(events);

                adapter.notifyDataSetChanged();
                checkIfDataEmpty();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                event.setId(Long.valueOf(dataSnapshot.getKey()));

                int i = events.indexOf(event);

                events.set(i, event);
                Collections.sort(events);

                adapter.notifyItemChanged(i);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                event.setId(Long.valueOf(dataSnapshot.getKey()));

                int position = events.indexOf(event);

                events.remove(position);
                Collections.sort(events);

                adapter.notifyItemRemoved(position);
                recycler.removeViewAt(position);

                checkIfDataEmpty();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfDataEmpty(){
        if (events.isEmpty()){
            recycler.setVisibility(View.INVISIBLE);
            tVNoData.setVisibility(View.VISIBLE);
        } else {
            tVNoData.setVisibility(View.INVISIBLE);
            recycler.setVisibility(View.VISIBLE);
        }
    }
}
