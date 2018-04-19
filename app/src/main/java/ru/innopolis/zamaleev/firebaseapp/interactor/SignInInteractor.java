package ru.innopolis.zamaleev.firebaseapp.interactor;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.data.entity.User;
import ru.innopolis.zamaleev.firebaseapp.presentation.view.SingInActivity;
import ru.innopolis.zamaleev.firebaseapp.util.Validator;

/**
 * Created by Ilgiz on 6/14/2017.
 */

public class SignInInteractor {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private Validator validator;
    private OnCompleteListener<AuthResult> listener;

    public SignInInteractor(FirebaseAuth.AuthStateListener mAuthListener, Activity activity) {
        this.mAuth = FirebaseAuth.getInstance();
        this.myRef = FirebaseDatabase.getInstance().getReference("users");
        this.mAuth.addAuthStateListener(mAuthListener);
        this.validator = new Validator(activity);

        this.listener = task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(activity, "Authorization failed!", Toast.LENGTH_SHORT).show();
        };

    }

    public void signIn(EditText emailET, EditText passwordET, TextInputLayout emailLayout, TextInputLayout passwordLayout) {
        if (!validator.checkToEmpty(emailET, emailLayout, R.string.err_msg_email, R.string.err_msg_required)){
            return;
        }

        if (!validator.checkToEmpty(passwordET, passwordLayout, R.string.err_msg_password, -1)){
            return;
        }

        final String email = emailET.getText().toString().trim();
        final String password = passwordET.getText().toString();

        if (!validator.isValidEmail(email)) {
            myRef.orderByChild("username").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataSnapshot.getChildren().forEach(user -> {
                        String emailFromDB = (String) user.child("email").getValue();
                        mAuth.signInWithEmailAndPassword(emailFromDB, password).addOnCompleteListener(listener);
                        return;
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    validator.setError(emailET, emailLayout, R.string.err_msg_no_such_email_user, -1);
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
        writeNewUser(email, password, user.getUid(), null);
    }



    private void writeNewUser(String userId, String name, String email, String img) {
//        User user = new User(name, email, img);
//        myRef.child("users").child(userId).setValue(user);
    }
}
