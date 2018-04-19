package ru.innopolis.zamaleev.firebaseapp.interactor;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventDecorator;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Filter;
import ru.innopolis.zamaleev.firebaseapp.enums.MarkerEnum;

/**
 * Created by Ilgiz on 6/16/2017.
 */

public class MapInteractor {
    private DatabaseReference myRef;
    private Observable<EventDecorator> observable;

    public MapInteractor() {
        this.myRef = FirebaseDatabase.getInstance().getReference("events");
    }

    public void getEventsByFilter(Filter filter, Observer<EventDecorator> subscriber, LatLng position) {

        observable = Observable.create(e -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
            GeoFire geoFire = new GeoFire(ref);
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(position.latitude, position.longitude), 1);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<EventMap> t = new GenericTypeIndicator<EventMap>() {};
                            EventMap event = dataSnapshot.getValue(t);
                            if (event != null)
                                e.onNext(new EventDecorator(event, MarkerEnum.ADD));
//                    e.onComplete();

                            // ...
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            e.onError(databaseError.toException());
                        }
                    });
                }

                @Override
                public void onKeyExited(String key) {
                    myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI

                            GenericTypeIndicator<EventMap> t = new GenericTypeIndicator<EventMap>() {};
                            EventMap event = dataSnapshot.getValue(t);
                            e.onNext(new EventDecorator(event, MarkerEnum.DELETE));
//                    e.onComplete();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            e.onError(databaseError.toException());
                        }
                    });
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    e.onError(error.toException());
                }
            });



        });
        observable.subscribe(subscriber);
    }

}
