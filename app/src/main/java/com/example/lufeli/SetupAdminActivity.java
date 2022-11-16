package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupAdminActivity extends AppCompatActivity {

    private EditText nombre, direccion, telefono, ciudad, cedula;
    private Button guardar;
    private String phone = "";
    private CircleImageView imagen;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserId;
    private static int GaleryPick = 1;
    private StorageReference UserImagenPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_admin);

        auth= FirebaseAuth.getInstance();
        CurrentUserId=auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Admin");
        dialog = new ProgressDialog(this);
        UserImagenPerfil= FirebaseStorage.getInstance().getReference().child("Perfil");
        nombre=(EditText) findViewById(R.id.asetup_nombre);
        cedula=(EditText) findViewById(R.id.asetup_cedula);
        guardar=(Button) findViewById(R.id.asetup_boton);
        imagen=(CircleImageView) findViewById(R.id.asetup_imagen);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            phone = bundle.getString("phone");
        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuardarInformacion();
            }

        });

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,GaleryPick);
            }
        });

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    if(snapshot.hasChild("imagen")){
                        String imagestr = snapshot.child("imagen").getValue().toString();
                        Picasso.get().load(imagestr).placeholder(R.drawable.logouser).into(imagen);
                    }else{
                        Toast.makeText(SetupAdminActivity.this, "Perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } //ONCREATE

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GaleryPick && resultCode==RESULT_OK && data != null){
            Uri imageUri=data.getData();
        }
    }

    private void GuardarInformacion() {
        String nombres = nombre.getText().toString().toUpperCase();
        String cedulas = cedula.getText().toString();

        try {
            if(TextUtils.isEmpty(nombres) && TextUtils.isEmpty(cedulas)){
                Toast.makeText(this, "Debe ingresar los datos", Toast.LENGTH_SHORT).show();
            }
            else{
                {
                    dialog.setTitle("Guardando");
                    dialog.setMessage("Por favor espere");
                    dialog.setCanceledOnTouchOutside(true);
                    HashMap map= new HashMap();
                    map.put("nombre",nombres);
                    map.put("cedula",cedulas);
                    map.put("uid",CurrentUserId);
                    UserRef.child(CurrentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                //EnviarAlInicio();
                                dialog.dismiss();
                            }else
                            {
                                String mensaje=task.getException().toString();
                                Toast.makeText(SetupAdminActivity.this, "Error "+mensaje, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }

            }

        }catch (Exception e){
            Toast.makeText(SetupAdminActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void EnviarAlInicio() {
        Intent intent=new Intent(SetupAdminActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    }