package ru.innopolis.zamaleev.firebaseapp.presentation.view;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.User;

public class EventInfo extends AppCompatActivity {
    private DatabaseReference myRef;
    private EventMap event;
    private FirebaseUser user;
    private TextView address, country, dateBegin, timeBegin, dateEnd, timeEnd, name, creatorName, rating, description, cost, peopleCount;
    private ImageView creatorImg;
    private ImageView[] participantsImg;
    private Button join;
    private boolean joined;
    private User currentUser;
    private int completed;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Intent intent = getIntent();
        key = intent.getStringExtra("event");
        address = (TextView) findViewById(R.id.address);
        country = (TextView) findViewById(R.id.country);
        dateBegin = (TextView) findViewById(R.id.date_begin);
        timeBegin = (TextView) findViewById(R.id.time_begin);
        dateEnd = (TextView) findViewById(R.id.date_end);
        timeEnd = (TextView) findViewById(R.id.time_end);
        name = (TextView) findViewById(R.id.name);
        creatorName = (TextView) findViewById(R.id.creator_name);
        rating = (TextView) findViewById(R.id.rating);
        description = (TextView) findViewById(R.id.description);
        cost = (TextView) findViewById(R.id.cost);
        peopleCount = (TextView) findViewById(R.id.people_count);

        creatorImg = (ImageView) findViewById(R.id.creator_img);

        participantsImg = new ImageView[4];
        participantsImg[0] = (ImageView) findViewById(R.id.participant1);
        participantsImg[1] = (ImageView) findViewById(R.id.participant2);
        participantsImg[2] = (ImageView) findViewById(R.id.participant3);
        participantsImg[3] = (ImageView) findViewById(R.id.participant4);
        user = FirebaseAuth.getInstance().getCurrentUser();
        loadEvent();

        join = (Button) findViewById(R.id.join);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                join.setOnClickListener(v -> {
                    User creator = (User) ((event.getCreator().values()).toArray())[0];
                    if (currentUser.equals(creator)) {
                        Toast.makeText(EventInfo.this, "You are creater!", Toast.LENGTH_SHORT).show();
                        join.setText("Deny");
                        return;
                    }
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("events").child(event.getEvent_id()).child("participants").child(currentUser.getId());
                    DatabaseReference newRef = null;

                    if (joined) {
                        join.setText("Join");
                        mDatabase.removeValue();
                    } else {
                        mDatabase.setValue(currentUser);
                        join.setText("Deny");
                    }
                    joined = !joined;
                    name.setText("Loading ...");
                    loadEvent();
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                (databaseError.toException()).printStackTrace();
            }
        });


    }

    private void loadEvent() {


        myRef = FirebaseDatabase.getInstance().getReference().child("events").child(key);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(EventMap.class);
                loadPictures();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                (databaseError.toException()).printStackTrace();
            }
        });
    }

    private void loadPictures() {
        completed = 0;
        event.getParticipants().values().forEach(user1 -> {
            String path = user1.getImg();
            if (path != null) {
                final long ONE_MEGABYTE = 1024 * 1024 * 4;
                StorageReference spaceRef = FirebaseStorage.getInstance().getReferenceFromUrl(path);
                spaceRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    user1.setImage(bytes);
                }).addOnFailureListener(e -> {
                    System.out.println(1);
                }).addOnCompleteListener(task -> {
                    completed++;
                    if (completed == event.getParticipants().values().size())
                        fillFields();
                });
                ;

            } else {
                //TODO
            }
        });

//       ;
    }

    private void fillFields() {
        User creator = (User) ((event.getCreator().values()).toArray())[0];
        List<User> participants = new ArrayList(event.getParticipants().values());
        int index = participants.indexOf(creator);
        creator = participants.get(index);
        participants.remove(index);

        for (int i = 0; i < participantsImg.length; i++) {
            participantsImg[i].setVisibility(View.INVISIBLE);
        }
        name.setText(event.getName());
        if (address.getText().toString().isEmpty()) {
            address.setText(event.getAddress());
            country.setText(event.getCountry());
            dateBegin.setText(event.getDate_begin());
            timeBegin.setText(event.getTime_begin());
            dateEnd.setText(event.getDate_end());
            timeEnd.setText(event.getTime_end());


            creatorName.setText(creator.getName());
            rating.setText(event.getCreator_rating().toString());
            description.setText(event.getDescription());
            cost.setText(event.getCost() + "$");

            String pc = participants.size() + 1 + " / " + event.getPeople_count();
            peopleCount.setText(pc);
        }

        creatorImg.setImageBitmap(BitmapFactory.decodeByteArray(creator.getImage(), 0, creator.getImage().length));

        for (int i = 0; i < participants.size(); i++) {
            participantsImg[i].setImageBitmap(BitmapFactory.decodeByteArray(participants.get(i).getImage(), 0, participants.get(i).getImage().length));
            participantsImg[i].setVisibility(View.VISIBLE);
        }

        joined = false;

        event.getParticipants().values().forEach(user1 -> {
            if (user.getUid().equals(user1.getId())) {
                joined = true;
                return;
            }
        });

        if (joined) {
            join.setText("Deny");
        } else {
            join.setText("Join");
        }

    }
}
