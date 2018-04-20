package ru.innopolis.zamaleev.firebaseapp.util;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import ru.innopolis.zamaleev.firebaseapp.R;

/**
 * Created by Ilgiz on 6/15/2017.
 */

public class Validator {
    private Activity context;
    private Animation animShake;
    private Vibrator vib;

    public Validator(Activity context) {
        this.context = context;
        animShake = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.shake);
        vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public boolean checkToEmpty(EditText et, TextInputLayout til, int error, int required) {
        boolean condition = et.getText().toString().trim().isEmpty();
        return checkCondition(et, til, error, condition, required);
    }

    public boolean isValidEmail(EditText et, TextInputLayout til, int error, int required) {
        String email = et.getText().toString();
        boolean condition = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return checkCondition(et, til, error, condition, required);
    }

    public boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void setError(EditText et, TextInputLayout til, int error, int required) {
        til.setErrorEnabled(true);
        til.setError(context.getString(error));

        if (required > 0)
            et.setError(context.getString(required));

        et.setAnimation(animShake);
        et.startAnimation(animShake);
        requestFocus(et);
        vib.vibrate(120);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean checkCondition(EditText et, TextInputLayout til, int error, boolean condition, int required) {
        if (condition) {
            setError(et, til, error, required);
            return false;
        }

        til.setErrorEnabled(false);
        return true;
    }

}
