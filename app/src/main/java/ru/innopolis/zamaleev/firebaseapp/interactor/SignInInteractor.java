package ru.innopolis.zamaleev.firebaseapp.interactor;

import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.innopolis.zamaleev.firebaseapp.data.entity.User;

/**
 * Created by Ilgiz on 6/14/2017.
 */

public class SignInInteractor {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


    public SignInInteractor(FirebaseAuth.AuthStateListener mAuthListener) {
        this.mAuth = FirebaseAuth.getInstance();
        this.myRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {

        if (!isValidEmail(email)) {
            myRef.orderByChild("username").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String emailFromDB = (String) dataSnapshot.child("email").getValue();
                    mAuth.signInWithEmailAndPassword(emailFromDB, password).addOnCompleteListener(listener);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TODO
                }
            });

        } else
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void signOut() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mAuth.signOut();
        } else {
            // TODO
        }
    }


    public void removeAuthStateListener(FirebaseAuth.AuthStateListener mAuthListener) {
        mAuth.removeAuthStateListener(mAuthListener);
    }

    public void signUp(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        writeNewUser(email, password, user.getUid());
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        myRef.child("users").child(userId).setValue(user);
    }
}
