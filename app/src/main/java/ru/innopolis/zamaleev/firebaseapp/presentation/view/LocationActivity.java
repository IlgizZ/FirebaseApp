package ru.innopolis.zamaleev.firebaseapp.presentation.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Location;
import ru.innopolis.zamaleev.firebaseapp.util.Extras;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private EventMap event;
    private Marker marker;
    public final int REQUEST = 777;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        event = (EventMap) getIntent().getSerializableExtra(Extras.EVENT.toString());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng innopolis = new LatLng(55.7525657, 48.7423347);
        marker = mMap.addMarker(new MarkerOptions()
                .position(innopolis)
                .draggable(true));
        mMap.setOnMapClickListener(latLng -> {
            marker.setPosition(latLng);
        });

        CameraPosition cameraPosition = new CameraPosition.Builder().target(innopolis).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Called when the snapshot button is clicked.
     */
    public void onScreenshot(View view) {
        takeSnapshot();
    }

    private void takeSnapshot() {
        if (mMap == null) {
            return;
        }

        final ImageView snapshotHolder =  new ImageView(this);

        final GoogleMap.SnapshotReadyCallback callback = bitmap -> {
            Intent intent = new Intent(LocationActivity.this, EventCreator.class);

            bitmap = Bitmap.createScaledBitmap(bitmap, 320, 240, true);
            intent.putExtra(Extras.BITMAP.toString(), bitmap);

            intent.putExtra(Extras.LOCATION.toString(), marker.getPosition());
            intent.putExtra(Extras.EVENT.toString(), event);
            setResult(REQUEST, intent);
            finish();
        };


        mMap.setOnMapLoadedCallback(() -> {
            mMap.snapshot(callback);
        });

    }

}

