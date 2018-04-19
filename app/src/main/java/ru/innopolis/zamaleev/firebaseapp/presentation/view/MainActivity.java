package ru.innopolis.zamaleev.firebaseapp.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.presentation.fragment.MapFragment;
import ru.innopolis.zamaleev.firebaseapp.presentation.fragment.MyEventsFragment;
import ru.innopolis.zamaleev.firebaseapp.presentation.fragment.Notification;

public class MainActivity extends AppCompatActivity {

    private Fragment myEventsFragment;
    private Fragment notificationFragment;
    private MapFragment mapFragment;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notification:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, notificationFragment).commit();
                    return true;
                case R.id.navigation_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, myEventsFragment).commit();
                    return true;
                case R.id.navigation_search:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, mapFragment).commit();
                    return true;
                case R.id.navigation_new_event:
                    Intent intent = new Intent(MainActivity.this, EventCreator.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myEventsFragment = new MyEventsFragment();
        mapFragment = new MapFragment();
        notificationFragment = new Notification();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth ->  {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Toast.makeText(MainActivity.this, "user already have signed in", Toast.LENGTH_SHORT).show();
//                Log.beginDatePicker(ON_AUTH, "onAuthStateChanged:signed_in:" + user.getUid());


            } else {
                // User is signed out
                Toast.makeText(MainActivity.this, "user signed out", Toast.LENGTH_SHORT).show();
//                Log.beginDatePicker(ON_AUTH, "onAuthStateChanged:signed_out");
                Intent intent = new Intent(MainActivity.this, SingInActivity.class);
                startActivity(intent);
                finish();
            }
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent startingIntent = getIntent();
        String event = startingIntent.getStringExtra("newEvent");
        if (event != null) {
            navigation.setSelectedItemId(R.id.navigation_notification);
        } else {
            navigation.setSelectedItemId(R.id.navigation_search);
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
