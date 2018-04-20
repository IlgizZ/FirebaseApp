package ru.innopolis.zamaleev.firebaseapp.presentation.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;
import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Filter;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Requirement;
import ru.innopolis.zamaleev.firebaseapp.data.entity.User;
import ru.innopolis.zamaleev.firebaseapp.interactor.MapInteractor;

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
    private Observer<Map<String, EventMap>> eventsSubscriber;
    private List<Marker> markers;
    private StorageReference mStorageRef;
    private static final String TAG = "MapFragment";

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
            participants[0] = ((ImageView) view.findViewById(R.id.marker_participant1));
            participants[1] = ((ImageView) view.findViewById(R.id.marker_participant2));
            participants[2] = ((ImageView) view.findViewById(R.id.marker_participant3));

            EventMap event = (EventMap) marker.getTag();


            List<User> users = new ArrayList<>(event.getParticipants().values());

            int userCount = users.size() >= 3 ? 3 : users.size();

            for (int i = 0; i < userCount; i++) {
                byte[] bytes = markerImages.get(i);
                participants[i].setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }


            List<Requirement> requirements = event.getRequirements() != null ? new ArrayList<>(event.getRequirements().values()): new ArrayList<>();

            String currentParticipantsCount = users.size() + "/" + event.getRequired_people_count();
            StringBuilder requirementsText = new StringBuilder();

            requirements.forEach(requirement -> {
                requirementsText.append(requirement.getDescription() + "\n");
            });

            ((TextView) view.findViewById(R.id.title)).setText(event.getTitle());
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
                LatLng innopolis = new LatLng(55.7525657, 48.7423347);
                markers = new ArrayList();
                markerImages = new ArrayList();

                interactor.getEventsByFilter(new Filter(), eventsSubscriber);
                // For zooming automatically to the location of the marker
                googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
                googleMap.setOnMarkerClickListener(marker -> {
                    EventMap event = (EventMap) marker.getTag();
                    markerImages.clear();


                    List<User> users = new ArrayList<>(event.getParticipants().values());

                    int userCount = users.size() >= PARTICIPANT_COUNT ? PARTICIPANT_COUNT : users.size();

                    for (int i = 0; i < userCount; i++) {
                        User user = users.get(i);
                        String path = "user_images/" + user.getImg();
                        if (path != null) {
                            final long ONE_MEGABYTE = 1024 * 1024 * 2;
                            StorageReference spaceRef =  mStorageRef.child(path);
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

    private void updateMarkers(Map<String, EventMap> eventMaps) {
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
