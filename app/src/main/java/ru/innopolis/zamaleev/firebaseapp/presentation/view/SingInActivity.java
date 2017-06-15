package ru.innopolis.zamaleev.firebaseapp.presentation.view;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.interactor.SignInInteractor;

public class SingInActivity extends AppCompatActivity {

    private static final String ON_AUTH = "OnAuth";

    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;

    private SignInInteractor signInInteractor;

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            signInInteractor.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuthListener = firebaseAuth -> {

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Toast.makeText(SingInActivity.this, "user already have signed in", Toast.LENGTH_SHORT).show();
                Log.d(ON_AUTH, "onAuthStateChanged:signed_in:" + user.getUid());

                Intent intent = new Intent(SingInActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                // User is signed out
                Toast.makeText(SingInActivity.this, "user signed out", Toast.LENGTH_SHORT).show();
                Log.d(ON_AUTH, "onAuthStateChanged:signed_out");
            }

        };

        signInInteractor = new SignInInteractor(mAuthListener);

        ETemail = (EditText) findViewById(R.id.et_email);
        ETpassword = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_sign_in).setOnClickListener(v -> {
            signInInteractor.signIn(ETemail.getText().toString(), ETpassword.getText().toString(), task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SingInActivity.this, "Aвторизация успешна", Toast.LENGTH_SHORT).show();
                    //TODO
                } else
                    Toast.makeText(SingInActivity.this, "Aвторизация провалена", Toast.LENGTH_SHORT).show();
                    //TODO
            });
        });
//        findViewById(R.id.btn_sign_up).setOnClickListener(v -> {
//            signUp(ETemail.getText().toString(), ETpassword.getText().toString());
//        });
    }


//    public void signIn(String email, String password) {
//        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
//
//                if (task.isSuccessful()) {
//                    Toast.makeText(SingInActivity.this, "Aвторизация успешна", Toast.LENGTH_SHORT).show();
//                } else
//                    Toast.makeText(SingInActivity.this, "Aвторизация провалена", Toast.LENGTH_SHORT).show();
//
//
//        });
//    }

//    public void signUp(String email, String password) {
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(SingInActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
//                } else
//                    Toast.makeText(SingInActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
