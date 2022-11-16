package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private String CurrentUserId;
    private DatabaseReference UserRef;
    private String Telefono = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        bottomNavigationView=findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) listener);
        auth=FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            Telefono = bundle.getString("phone");
        }
        auth= FirebaseAuth.getInstance();
        CurrentUserId=auth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Admin");
    }


   private void Fragmentos(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null){
            EnviarAlLoign();
        }else
        {
            //VerificarUsuarioExistente();
        }
    }

    /*private void VerificarUsuarioExistente() {
        final String CurrentUserId = auth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(CurrentUserId)){
                    EnviarAlSetup();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    /*private void EnviarAlSetup() {
        Intent intent=new Intent(AdminActivity.this, SetupAdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone", Telefono);
        startActivity(intent);
        finish();
    }/*

    /*private void EnviarAlLoign() {
        Intent intent=new Intent(AdminActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }*/
    private NavigationBarView.OnItemSelectedListener listener = new NavigationBarView.OnItemSelectedListener ()  {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        if(id == R.id.fragmentUno){
            Fragmentos(new FragmentUno());
        }
        if(id == R.id.fragmentDos){
            Fragmentos(new FragmentDos());
        }
        if(id == R.id.fragmentCuatro){
            Fragmentos(new FragmentCuatro());

        }
        /*if(id == R.id.fragmentTres){
            Fragmentos(new FragmentTres());
        }
        if(id == R.id.fragmentCuatro){
            Fragmentos(new FragmentCuatro());
        }*/
        else if (id == R.id.cerrarSesion){
            ActivitySalir();
            auth.signOut();
            EnviarAlLoign();
        }
        return true;
    }
     };

    private void ActivitySalir() {
    }
        private void EnviarAlLoign() {
        Intent intent=new Intent(AdminActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}

