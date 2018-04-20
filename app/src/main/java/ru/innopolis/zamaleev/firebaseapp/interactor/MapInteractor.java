package ru.innopolis.zamaleev.firebaseapp.interactor;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable ;
import io.reactivex.Observable;
import io.reactivex.Observer;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Event;
import ru.innopolis.zamaleev.firebaseapp.data.entity.EventMap;
import ru.innopolis.zamaleev.firebaseapp.data.entity.Filter;
import ru.innopolis.zamaleev.firebaseapp.data.entity.User;

/**
 * Created by Ilgiz on 6/16/2017.
 */

public class MapInteractor {
    private DatabaseReference myRef;
    private Observable<Map<String, EventMap>> observable;

    public MapInteractor() {
        this.myRef = FirebaseDatabase.getInstance().getReference("events");
    }

    public void getEventsByFilter(Filter filter, Observer<Map<String, EventMap>> subscriber) {

        observable = Observable.create(e -> {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI

                    GenericTypeIndicator<Map<String, EventMap>> t = new GenericTypeIndicator<Map<String, EventMap>>() {};
                    Map<String, EventMap> eventMaps = dataSnapshot.getValue(t);
                    e.onNext(eventMaps);
//                    e.onComplete();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                  e.onError(databaseError.toException());
                }
            });

        });
        observable.subscribe(subscriber);





    }

}
