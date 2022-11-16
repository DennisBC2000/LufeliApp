package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.Principal;

public class HomeActivity extends AppCompatActivity {

    private Button buttonUser, buttonAdmin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonUser=(Button)findViewById(R.id.buttonUser);
        buttonAdmin=(Button)findViewById(R.id.buttonAdmin);

        mAuth = FirebaseAuth.getInstance();

        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(HomeActivity.this, PrincipalActivity.class);
                //startActivity(intent);
                loginAnonymous();


            }

            private void loginAnonymous() {
                    mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(HomeActivity.this, "¡BIENVENIDO!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(HomeActivity.this, PrincipalActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(HomeActivity.this, "Error al loguearse", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LoginAdminActivity.class);
                startActivity(intent);
            }
        });
    }
}