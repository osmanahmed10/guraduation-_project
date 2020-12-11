package com.example.graduation_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    private ProgressBar loading;
    private TextInputLayout textInputEmail, textInputPassword;
    private Button buttonConfirm;

    String emailInput, passwordInput;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        updateUI();

        mAuth = FirebaseAuth.getInstance();

        textInputEmail.getEditText().addTextChangedListener(loginWatcher);
        textInputPassword.getEditText().addTextChangedListener(loginWatcher);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(login.this, home.class));
            finish();
        }
    }

    private void updateUI() {
        loading = findViewById(R.id.loading);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        buttonConfirm = findViewById(R.id.btn_confirm);
    }

    private TextWatcher loginWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            emailInput = textInputEmail.getEditText().getText().toString().trim();
            passwordInput = textInputPassword.getEditText().getText().toString().trim();

            buttonConfirm.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
        }
    };

    public void confirmInput(View v) {
        loading.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    startActivity(new Intent(login.this, home.class));
                    finish();
                } else {
                    Toast.makeText(login.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signUp(View v) {
        startActivity(new Intent(login.this, signUp.class));
        finish();
    }
}
