package ru.innopolis.zamaleev.firebaseapp.presentation.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;
import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventDecorator;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Filter;
import ru.innopolis.zamaleev.firebaseapp.data.entity.User;
import ru.innopolis.zamaleev.firebaseapp.interactor.MapInteractor;
import ru.innopolis.zamaleev.firebaseapp.presentation.view.EventInfo;

//import com.google.android.gms.location.FusedLocationProviderClient;

/**
 * Created by Ilgiz on 6/8/2017.
 */

public class MapFragment extends Fragment {

    private static final int REQUEST_LOCATION = 777;
    private final int PARTICIPANT_COUNT = 3;
    MapView mMapView;
    private List<byte[]> markerImages;
    private GoogleMap googleMap;
    private MapInteractor interactor;
    private Observer<EventDecorator> eventsSubscriber;
    private List<Marker> markers;
    private List<Marker> events;
    private StorageReference mStorageRef;
    private static final String TAG = "MapFragment";
    private Button btn;


    private void createSnackbar(LayoutInflater inflater, ViewGroup container){
//        String text = "dsfasdf";


        Snackbar snackbar = Snackbar.make(container, "", Snackbar.LENGTH_INDEFINITE);
// Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
// Hide the text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

// Inflate our custom view
        View snackView = inflater.inflate(R.layout.my_snakbar, null);
// Configure the view

        TextView textViewTop = (TextView) snackView.findViewById(R.id.text);
//        textViewTop.setText(text);
        textViewTop.setTextColor(Color.WHITE);

// Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
// Show the Snackbar
        View view = snackbar.getView();
        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;

        view.setLayoutParams(params);
        snackbar.show();

    }

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

            ImageView[] participants = new ImageView[PARTICIPANT_COUNT];
            participants[0] = ((ImageView) view.findViewById(R.id.participant2));
            participants[1] = ((ImageView) view.findViewById(R.id.marker_participant2));
            participants[2] = ((ImageView) view.findViewById(R.id.marker_participant3));

            EventMap event = (EventMap) marker.getTag();


            List<User> users = new ArrayList<>(event.getParticipants().values());

            int userCount = users.size() >= 3 ? 3 : users.size();

            for (int i = 0; i < userCount; i++) {
                byte[] bytes = markerImages.get(i);
                participants[i].setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }


            List<String> requirements = event.getRequirements() != null ? new ArrayList<>(event.getRequirements().values()): new ArrayList<>();

            String currentParticipantsCount = users.size() + "/" + event.getPeople_count();
            StringBuilder requirementsText = new StringBuilder();

            requirements.forEach(requirement -> {
                requirementsText.append(requirement + "\n");
            });

            ((TextView) view.findViewById(R.id.title)).setText(event.getName());
            ((TextView) view.findViewById(R.id.marker_date)).setText(event.getDate_begin());
            ((TextView) view.findViewById(R.id.marker_time)).setText(event.getTime_begin());
            ((TextView) view.findViewById(R.id.marker_price)).setText(requirementsText.toString());
            ((TextView) view.findViewById(R.id.participants)).setText(currentParticipantsCount);
            ((TextView) view.findViewById(R.id.snippet)).setText(event.getDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mMapView.onResume(); // needed to get the map to display immediately
        btn = (Button) rootView.findViewById(R.id.button2);
        btn.setOnClickListener(v -> {
            createSnackbar(inflater, (ViewGroup)rootView);
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        eventsSubscriber = new DisposableObserver<EventDecorator>() {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull EventDecorator event) {
                updateMarkers(event);
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
                LatLng innopolis = new LatLng(55.7525657, 48.7423347);
                markers = new ArrayList();
                events = new ArrayList();


                markerImages = new ArrayList();

                interactor.getEventsByFilter(new Filter(), eventsSubscriber, innopolis);
                // For zooming automatically to the location of the marker
                googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
                googleMap.setOnMarkerClickListener(marker -> {
                    EventMap event = (EventMap) marker.getTag();
                    markerImages.clear();


                    List<User> users = new ArrayList<>(event.getParticipants().values());

                    int userCount = users.size() >= PARTICIPANT_COUNT ? PARTICIPANT_COUNT : users.size();

                    for (int i = 0; i < userCount; i++) {
                        User user = users.get(i);
                        String path = user.getImg();
                        if (path != null) {
                            final long ONE_MEGABYTE = 1024 * 1024 * 2;
                            StorageReference spaceRef =  FirebaseStorage.getInstance().getReferenceFromUrl(path);
                            spaceRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                markerImages.add(bytes);
                                if (isImagesDownload(userCount))
                                    marker.showInfoWindow();
                            }).addOnFailureListener(e -> {

                                //TODO
                                Log.e(TAG, e.getMessage());
                            })
                            ;

                        } else {
                            //TODO
                        }

                    }
                    return true;
                });
                googleMap.setOnInfoWindowClickListener(marker -> {
                    EventMap event = (EventMap) marker.getTag();
                    Intent intent = new Intent(getContext(), EventInfo.class);
                    intent.putExtra("event", event.getEvent_id());
                    getContext().startActivity(intent);
                });
                CameraPosition cameraPosition = new CameraPosition.Builder().target(innopolis).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        return rootView;
    }

    private boolean isImagesDownload(int userCount) {
        return markerImages.size() == userCount;
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

    private void updateMarkers(EventDecorator event) {
        switch (event.getLogic()){
            case ADD:
                LatLng newLocation = new LatLng( event.getLat(), event.getLng());
                Marker marker = googleMap.addMarker(new MarkerOptions().position(newLocation).title(event.getName()).snippet(event.getDescription()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                marker.setTag(event);
                markers.add(marker);
                break;
            case DELETE:
                EventMap e = event;
                int index = events.indexOf(e);
                markers.get(index).remove();
                markers.remove(index);
                events.remove(index);
                break;
            default:

        }
    }

}
