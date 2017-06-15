package ru.innopolis.zamaleev.firebaseapp.presentation.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.util.Validator;

public class EventCreator extends AppCompatActivity {

    private EditText eventCreatorName, eventCreatorCity, eventCreatorDescription;
    private TextInputLayout eventCreatorLayoutName, eventCreatorLayoutCity, eventCreatorLayoutDescription;
    private Button btnSignUp, btn_date, btn_time;
    private DateFormat formatDateTime;
    private Calendar dateTime;
    private TextView text;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator);

        text = (TextView) findViewById(R.id.tv_date);
        btn_date = (Button) findViewById(R.id.btn_datePicker);
        btn_time = (Button) findViewById(R.id.btn_timePicker);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate();
            }
        });

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTime();
            }
        });
        dateTime = Calendar.getInstance();
        formatDateTime = new SimpleDateFormat("EEE, d MMM  HH:mm");
        updateTextLabel();

        eventCreatorLayoutName = (TextInputLayout) findViewById(R.id.event_creator_layout_name);
        eventCreatorLayoutCity = (TextInputLayout) findViewById(R.id.event_creator_layout_city);
        eventCreatorLayoutDescription = (TextInputLayout) findViewById(R.id.event_creator_layout_description);

        eventCreatorName = (EditText) findViewById(R.id.event_creator_name);
        eventCreatorCity = (EditText) findViewById(R.id.event_creator_city);
        eventCreatorDescription = (EditText) findViewById(R.id.event_creator_description);
        btnSignUp = (Button) findViewById(R.id.btn_create);

        validator = new Validator(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {

        if (!validator.checkToEmpty(eventCreatorName, eventCreatorLayoutName, R.string.err_msg_name, R.string.err_msg_required)) {
            return;
        }
        if (!validator.checkToEmpty(eventCreatorName, eventCreatorLayoutName, R.string.err_msg_city, R.string.err_msg_required)) {
            return;
        }
        if (!validator.checkToEmpty(eventCreatorName, eventCreatorLayoutName, R.string.err_msg_description, R.string.err_msg_required)) {
            return;
        }

        Toast.makeText(getApplicationContext(), "Event created!!", Toast.LENGTH_SHORT).show();
    }

    private void updateDate() {
        DatePickerDialog dialog = new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void updateTime() {
        new TimePickerDialog(this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel();
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            updateTextLabel();
        }
    };

    private void updateTextLabel() {
        text.setText(formatDateTime.format(dateTime.getTime()));
    }

}
