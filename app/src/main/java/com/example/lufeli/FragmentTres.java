package com.example.lufeli;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class FragmentTres extends Fragment {

    private View fragmento;
    private EditText nombre, cedula;
    private Button guardar;
    private String phone = "";
    private CircleImageView imagen;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserId;
    private static int GaleryPick = 1;
    private StorageReference UserImagenPerfil;



    public FragmentTres() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmento = inflater.inflate(R.layout.fragment_tres, container, false);

        auth= FirebaseAuth.getInstance();
        CurrentUserId=auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Admin");
        dialog = new ProgressDialog(getContext());
        UserImagenPerfil= FirebaseStorage.getInstance().getReference().child("Perfil");
        nombre=(EditText) fragmento.findViewById(R.id.perfila_nombre);
        cedula=(EditText) fragmento.findViewById(R.id.perfila_cedula);
        guardar=(Button) fragmento.findViewById(R.id.perfila_boton);
        imagen=(CircleImageView) fragmento.findViewById(R.id.perfila_imagen);

        UserRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("imagen")){
                    String nombres =  snapshot.child("nombre").getValue().toString();
                    String cedulas =  snapshot.child("cedula").getValue().toString();
                    String imagens =  snapshot.child("imagen").getValue().toString();

                    Picasso.get().load(imagens)
                            .placeholder(R.drawable.logowelcome)
                            .into(imagen);

                    nombre.setText(nombres);
                    cedula.setText(cedulas);

                }else if (snapshot.exists()){
                    String nombres =  snapshot.child("nombre").getValue().toString();
                    String cedulas =  snapshot.child("cedula").getValue().toString();

                   nombre.setText(nombres);
                   cedula.setText(cedulas);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                    }
                    //else{
                        //Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                    //}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       return fragmento;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GaleryPick && resultCode==RESULT_OK && data != null){
            Uri imageUri=data.getData();
        }
    }

    private void GuardarInformacion() {
        String nombres = nombre.getText().toString().toUpperCase();
        String cedulas = cedula.getText().toString();

        if(TextUtils.isEmpty(nombres)){
            Toast.makeText(getContext(), "Debe ingresar el nombre", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(cedulas)){
            Toast.makeText(getContext(), "Debe ingresar la cédula", Toast.LENGTH_SHORT).show();
        }else {
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
                        EnviarAlInicio();
                        dialog.dismiss();
                    }else
                    {
                        String mensaje=task.getException().toString();
                        Toast.makeText(getContext(), "Error "+mensaje, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }
        {

        }
    }

    private void EnviarAlInicio() {
        Intent intent=new Intent(getContext(), AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}