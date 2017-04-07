package com.nowhere.login;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.BindView;

public class LoginActivity extends Activity {
    static final String EMAIL = "email";
    static final String PASSWORD = "password";

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        Toast.makeText(this, "LoginActivity.onCreate() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMAIL, _emailText.getText().toString());
        outState.putString(PASSWORD, _passwordText.getText().toString());
        Toast.makeText(this, "LoginActivity.onSaveInstanceState() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _emailText.setText(savedInstanceState.getString(EMAIL));
        _passwordText.setText(savedInstanceState.getString(PASSWORD));
        Toast.makeText(this, "LoginActivity.onRestoreInstanceState() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "LoginActivity.onStart() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "LoginActivity.onRestart() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "LoginActivity.onResume() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "LoginActivity.onPause() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "LoginActivity.onStop() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "LoginActivity.onDestroy() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.signingIn));
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        progressDialog.cancel();
        _loginButton.setEnabled(true);
        if (email.equals("ad@m.in") && password.equals("123456"))
            onLoginSuccess();
        else
            onLoginFailed();
    }

    private void onLoginFailed() {
        Toast.makeText(this, getString(R.string.signInError), Toast.LENGTH_SHORT).show();
    }

    private void onLoginSuccess() {
        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
    }

    private boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.emailError));
            valid = false;
        } else
            _emailText.setError(null);

        if (password.equals("") || password.length() < 4) {
            _passwordText.setError(getString(R.string.passwordError));
            valid = false;
        } else
            _passwordText.setError(null);

        return valid;
    }
}