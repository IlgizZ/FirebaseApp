package ru.innopolis.zamaleev.firebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ON_AUTH = "OnAuth";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(EmailPasswordActivity.this, "user already have signed in", Toast.LENGTH_SHORT).show();
                    Log.d(ON_AUTH, "onAuthStateChanged:signed_in:" + user.getUid());

                    Intent intent = new Intent(EmailPasswordActivity.this, EventActivity.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Toast.makeText(EmailPasswordActivity.this, "user signed out", Toast.LENGTH_SHORT).show();
                    Log.d(ON_AUTH, "onAuthStateChanged:signed_out");
                }
            }
        };

        ETemail = (EditText) findViewById(R.id.et_email);
        ETpassword = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_sign_up).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_sign_in) {
            signIn(ETemail.getText().toString(), ETpassword.getText().toString());
        } else if (view.getId() == R.id.btn_sign_up) {
            signUp(ETemail.getText().toString(), ETpassword.getText().toString());
        }

    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailPasswordActivity.this, "Aвторизация успешна", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(EmailPasswordActivity.this, "Aвторизация провалена", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailPasswordActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(EmailPasswordActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
