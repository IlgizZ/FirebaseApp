package ru.innopolis.zamaleev.firebaseapp.presentation.view;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.innopolis.zamaleev.firebaseapp.R;
import ru.innopolis.zamaleev.firebaseapp.interactor.SignInInteractor;

public class SingInActivity extends AppCompatActivity {

    private static final String ON_AUTH = "OnAuth";

    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText email, password;
    private TextInputLayout emailLayout, passwordLayout;

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

        signInInteractor = new SignInInteractor(mAuthListener, this);

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        emailLayout = (TextInputLayout) findViewById(R.id.sign_in_layout_email);
        passwordLayout = (TextInputLayout) findViewById(R.id.sign_in_layout_password);

        findViewById(R.id.btn_sign_in).setOnClickListener(v -> submit());
    }

    private void submit(){
        signInInteractor.signIn(email, password, emailLayout, passwordLayout);
    }

}
