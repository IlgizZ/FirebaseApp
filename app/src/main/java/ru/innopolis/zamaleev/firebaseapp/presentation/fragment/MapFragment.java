package ru.innopolis.zamaleev.firebaseapp.presentation.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;
import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Filter;
import ru.innopolis.zamaleev.firebaseapp.interactor.MapInteractor;

//import com.google.android.gms.location.FusedLocationProviderClient;

/**
 * Created by Ilgiz on 6/8/2017.
 */

public class MapFragment extends Fragment {

    private static final int REQUEST_LOCATION = 777;
    MapView mMapView;
    private GoogleMap googleMap;
    private MapInteractor interactor;
    private Observer<Map<String, EventMap>> eventsSubscriber;
    private List<Marker> markers;

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }

        private void render(Marker marker, View view) {



            EventMap event = (EventMap) marker.getTag();

            ((TextView)view.findViewById(R.id.title)).setText(event.getTitle());
            ((TextView)view.findViewById(R.id.marker_date)).setText(event.getDate_begin());
            ((TextView)view.findViewById(R.id.marker_time)).setText(event.getTime_begin());
            ((TextView)view.findViewById(R.id.snippet)).setText(event.getDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        eventsSubscriber = new DisposableObserver<Map<String, EventMap>>() {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull Map<String, EventMap> eventMaps) {
                updateMarkers(eventMaps);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        interactor = new MapInteractor();


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //TODO location permission request

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getContext(),
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION);
                    } else {
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    //Not in api-23, no need to prompt
                    googleMap.setMyLocationEnabled(true);
                }

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(55.7525657, 48.7423347);
                markers = new ArrayList<Marker>();
                interactor.getEventsByFilter(new Filter(), eventsSubscriber);
                // For zooming automatically to the location of the marker
                googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //TODO needs better impl
        if (requestCode == REQUEST_LOCATION && grantResults.length > 0) {
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION))
            googleMap.setMyLocationEnabled(true);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updateMarkers(Map<String, EventMap> eventMaps){
        markers.forEach(Marker::remove);
        markers.clear();
        eventMaps.forEach((key, eventMap) -> {
            LatLng sydney = new LatLng(eventMap.getLocation().getLn(), eventMap.getLocation().getLg());
            Marker marker = googleMap.addMarker(new MarkerOptions().position(sydney).title(eventMap.getTitle()).snippet(eventMap.getDescription()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

            marker.setTag(eventMap);
            markers.add(marker);
        });
    }

}
