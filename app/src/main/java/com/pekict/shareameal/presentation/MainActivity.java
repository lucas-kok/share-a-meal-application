package com.pekict.shareameal.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pekict.shareameal.R;

public class MainActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.et_email);
        passwordInput = findViewById(R.id.et_password);

        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Login
            }
        });

        TextView registerLink = findViewById(R.id.tv_register);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Registration Scene
            }
        });
    }

    public void login(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), password, Toast.LENGTH_SHORT).show();
    }
}