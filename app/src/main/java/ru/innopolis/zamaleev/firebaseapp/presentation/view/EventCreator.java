package ru.innopolis.zamaleev.firebaseapp.presentation.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.User;
import ru.innopolis.zamaleev.firebaseapp.enums.Extras;
import ru.innopolis.zamaleev.firebaseapp.util.Validator;

public class EventCreator extends AppCompatActivity {

    private EditText name, description, location, peopleCount, cost;
    private TextInputLayout eventCreatorLayoutName, eventCreatorLayoutDescription, eventCreatorLayoutLocation, peopleCountLayout, costLayout;
    private Button btnSignUp, btnLocation;
    private DateFormat formatDateTime, dateFormat, timeFormat;
    private Calendar beginDate, endDate, currentDate;
    private TextView beginDateTV, beginTimeTV, current_tv, endDateTV, endTimeTV;
    private ImageView snapshot;
    private Validator validator;
    private EventMap newEvent;
    private DatabaseReference myRef;
    private RadioGroup tags;
    public final int REQUEST = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator);

        btnLocation = (Button) findViewById(R.id.event_creator_location_btn);
        btnLocation.setClickable(false);
        btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(EventCreator.this, LocationActivity.class);
            intent.putExtra(Extras.EVENT.toString(), newEvent);
            startActivityForResult(intent, REQUEST);
        });


        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("users").child(fbUser.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                HashMap<String, User> userMap = new HashMap();
                userMap.put(dataSnapshot.getKey(), user);
                newEvent = new EventMap(userMap);
                btnLocation.setClickable(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });

        beginDateTV = (TextView) findViewById(R.id.btn_datePicker);
        beginTimeTV = (TextView) findViewById(R.id.btn_timePicker);
        endDateTV = (TextView) findViewById(R.id.tv_end_date);
        endTimeTV = (TextView) findViewById(R.id.tv_end_time);

        dateFormat = new SimpleDateFormat("EEE, d MMM");
        timeFormat = new SimpleDateFormat("HH:mm");

        beginDateTV.setOnClickListener(v -> updateDate(beginDateTV, dateFormat, true));
        beginTimeTV.setOnClickListener(v -> updateTime(beginTimeTV, timeFormat, true));
        endDateTV.setOnClickListener(v -> updateDate(endDateTV, dateFormat, false));
        endTimeTV.setOnClickListener(v -> updateTime(endTimeTV, timeFormat, false));

        beginDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_YEAR, 1);

        initDates();

        eventCreatorLayoutName = (TextInputLayout) findViewById(R.id.event_creator_layout_name);
        eventCreatorLayoutLocation = (TextInputLayout) findViewById(R.id.event_creator_layout_location);
        eventCreatorLayoutDescription = (TextInputLayout) findViewById(R.id.event_creator_layout_description);
        peopleCountLayout = (TextInputLayout) findViewById(R.id.event_creator_layout_people_count);
        costLayout = (TextInputLayout) findViewById(R.id.event_creator_layout_cost);
        tags = (RadioGroup) findViewById(R.id.tags);

        name = (EditText) findViewById(R.id.event_creator_name);
        description = (EditText) findViewById(R.id.event_creator_description);
        peopleCount = (EditText) findViewById(R.id.people_count);
        cost = (EditText) findViewById(R.id.cost);
        btnSignUp = (Button) findViewById(R.id.btn_create);

        validator = new Validator(this);

        snapshot = (ImageView) findViewById(R.id.map_snapshot);

        btnSignUp.setOnClickListener(v -> submitForm());

        location = (EditText) findViewById(R.id.event_creator_location_tv);

        if (newEvent != null)
            initFields();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LatLng position = data.getParcelableExtra(Extras.LOCATION.toString());

        try {

            newEvent.addLocation(position, this);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
        initFields();
        Bitmap snapshot = data.getParcelableExtra(Extras.BITMAP.toString());
        this.snapshot.setImageBitmap(snapshot);
    }

    private void initFields() {
//        name.setText(newEvent.getTitle());
        location.setText(newEvent.getAddress());
//        description.setText(newEvent.getDescription());
//        beginDateTV.setText(newEvent.getDate_begin());
//        beginTimeTV.setText(newEvent.getTime_begin());
//        endDateTV.setText(newEvent.getDate_end());
//        endTimeTV.setText(newEvent.getTime_end());
    }

    private void initDates() {
        beginDateTV.setText(dateFormat.format(beginDate.getTime()));
        beginTimeTV.setText(timeFormat.format(beginDate.getTime()));
        endDateTV.setText(dateFormat.format(endDate.getTime()));
        endTimeTV.setText(timeFormat.format(endDate.getTime()));
    }

    private void submitForm() {

        if (!validator.checkToEmpty(name, eventCreatorLayoutName, R.string.err_msg_name, R.string.err_msg_required)) {
            return;
        }
        if (!validator.checkToEmpty(location, eventCreatorLayoutLocation, R.string.err_msg_location, R.string.err_msg_required)) {
            return;
        }
        if (!validator.checkToEmpty(description, eventCreatorLayoutDescription, R.string.err_msg_description, R.string.err_msg_required)) {
            return;
        }
        if (!validator.checkToEmpty(peopleCount, peopleCountLayout, R.string.err_msg_people_count, R.string.err_msg_required)) {
            return;
        }
        if (!validator.checkToEmpty(cost, costLayout, R.string.err_msg_cost, R.string.err_msg_required)) {
            return;
        }

        newEvent.setDate_begin(beginDateTV.getText().toString());
        newEvent.setTime_begin(beginTimeTV.getText().toString());
        newEvent.setDate_end(endDateTV.getText().toString());
        newEvent.setTime_end(endTimeTV.getText().toString());
        newEvent.setDescription(description.getText().toString());
        newEvent.setName(name.getText().toString());
        newEvent.setCost(Double.valueOf(cost.getText().toString()));
        newEvent.setPeople_count(Integer.valueOf(peopleCount.getText().toString()));

        RadioButton btn = (RadioButton) findViewById(tags.getCheckedRadioButtonId());

        newEvent.setType(btn.getText().toString());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newRef = mDatabase.child("events").push();
        String key = newRef.getKey();
        newEvent.setEvent_id(key);
        newRef.setValue(newEvent);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(user.getUid()).child("events").child(key).setValue(newEvent);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(newEvent.getEvent_id(), new GeoLocation(newEvent.getLat(), newEvent.getLng()));

        Toast.makeText(getApplicationContext(), "MyEvent created!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EventCreator.this, MainActivity.class);

//            intent.putExtra(Extras.BITMAP.toString(), bitmap);
        intent.putExtra(Extras.TAB.toString(), Extras.MAP);

        startActivity(intent);
        finish();
    }

    private void updateDate(TextView current_tv, DateFormat formatDateTime, boolean begin) {
        this.current_tv = current_tv;
        this.formatDateTime = formatDateTime;
        this.currentDate = begin ? beginDate : endDate;
        DatePickerDialog dialog = new DatePickerDialog(this, beginDatePicker, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        if (begin) {
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.getDatePicker().setMaxDate(endDate.getTimeInMillis() + 1000);
        } else {
            dialog.getDatePicker().setMinDate(beginDate.getTimeInMillis() - 1000);
        }
        dialog.show();
    }

    private void updateTime(TextView current_tv, DateFormat formatDateTime, boolean begin) {
        this.current_tv = current_tv;
        this.formatDateTime = formatDateTime;
        this.currentDate = begin ? beginDate : endDate;
        new TimePickerDialog(this, t, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener beginDatePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            currentDate.set(Calendar.YEAR, year);
            currentDate.set(Calendar.MONTH, monthOfYear);
            currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel();
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            currentDate.set(Calendar.MINUTE, minute);
            updateTextLabel();
        }
    };

    private void updateTextLabel() {
        current_tv.setText(formatDateTime.format(currentDate.getTime()));
    }


}
