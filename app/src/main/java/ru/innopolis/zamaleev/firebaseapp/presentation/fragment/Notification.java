package ru.innopolis.zamaleev.firebaseapp.presentation.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventDecorator;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.MyEvent;
import ru.innopolis.zamaleev.firebaseapp.enums.MarkerEnum;
import ru.innopolis.zamaleev.firebaseapp.presentation.adapter.RecyclerAdapter;


public class Notification extends Fragment {

    private DatabaseReference myRef;
    private List<MyEvent> myEvents;
    private RecyclerView recycler;
    private RecyclerAdapter adapter;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        Intent startingIntent = getActivity().getIntent();
        String event = startingIntent.getStringExtra("newEvent");

        if (startingIntent != null && event != null) {
            recycler = (RecyclerView) rootView.findViewById(R.id.event_recycler);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            myEvents = new ArrayList();

            adapter = new RecyclerAdapter(myEvents);

            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(adapter);
            recycler.setNestedScrollingEnabled(false);
            recycler.setHasFixedSize(true);


            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            recycler.setItemAnimator(itemAnimator);

            mAuth = FirebaseAuth.getInstance();

            FirebaseUser user = mAuth.getCurrentUser();

            myRef = FirebaseDatabase.getInstance().getReference().child("events").child(event);
            updateListener();
        }



        return rootView;
    }


    private void updateListener() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyEvent myEvent = dataSnapshot.getValue(MyEvent.class);
                myEvent.setEvent_id((dataSnapshot.getKey()));

                myEvents.add(myEvent);

                Collections.sort(myEvents);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                (databaseError.toException()).printStackTrace();
            }
        });
    }

}
