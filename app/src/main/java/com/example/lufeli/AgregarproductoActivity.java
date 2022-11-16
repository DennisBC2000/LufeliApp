package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AgregarproductoActivity extends AppCompatActivity {

    private ImageView imagenpro;
    private EditText nombrepro, descripcionpro, preciocomprapro, precioventapro;
    private TextView textox;
    private EditText cantidadpro;
    private Button agregarpro;
    private static final int Gallery_Pick = 1;
    private Uri imagenUri;
    private String productoRandomKey, downloadUri;
    private StorageReference ProductoImagenRef;
    private DatabaseReference ProductoRef;
    private ProgressDialog dialog;
    private String Categoria, Nom, Desc, PrecioCom, PrecioVen, Cant, CurrentDate, CurrentTime;
    private String pidEliminar = "";
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregarproducto);

        Categoria = getIntent().getExtras().get("categoria").toString();
        try {
            pidEliminar = getIntent().getStringExtra("pid").toString();
            Toast.makeText(AgregarproductoActivity.this, "Modificancdo producto" + pidEliminar, Toast.LENGTH_SHORT);
        }catch (Exception e){
            Toast.makeText(AgregarproductoActivity.this, "Creando producto", Toast.LENGTH_SHORT);
        }
        ProductoImagenRef= FirebaseStorage.getInstance().getReference().child("Imagenes Productos");
        ProductoRef= FirebaseDatabase.getInstance().getReference().child("Productos");

        Toast.makeText(this, Categoria, Toast.LENGTH_SHORT).show();


        imagenpro = (ImageView) findViewById(R.id.imagenpro);
        textox = (TextView) findViewById(R.id.textox);
        nombrepro = (EditText) findViewById(R.id.nombrepro);
        //preciocomprapro = (EditText) findViewById(R.id.preciocomprapro);
        precioventapro = (EditText) findViewById(R.id.precioventapro);
        descripcionpro = (EditText) findViewById(R.id.descripcionpro);
        descripcionpro = (EditText) findViewById(R.id.descripcionpro);
        cantidadpro = (EditText) findViewById(R.id.cantidadpro);
        agregarpro = (Button) findViewById(R.id.agregarpro);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        dialog= new ProgressDialog(this);

        imagenpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirGaleria();
            }
        });
        agregarpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ValidarProducto();
            }
        });
        textox.setText(Categoria+"\nAgregar producto");
    }
    private void AbrirGaleria() {
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/");
        startActivityForResult(intent,Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data != null){
            imagenUri=data.getData();
            imagenpro.setImageURI(imagenUri);
        }
    }

    private void ValidarProducto() {
        Nom = nombrepro.getText().toString();
        Desc = descripcionpro.getText().toString();
//        PrecioCom = preciocomprapro.getText().toString();
        PrecioVen = precioventapro.getText().toString();
        Cant = cantidadpro.getText().toString();


        if (imagenUri == null){
            Toast.makeText(this, "Debe ingresar la imagen del producto", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Nom)){
            Toast.makeText(this, "Debe ingresar el nombre del producto", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Desc)){
            Toast.makeText(this, "Debe ingresar la descripción del producto", Toast.LENGTH_SHORT).show();
        }
        //else if (TextUtils.isEmpty(PrecioCom)){
          //  Toast.makeText(this, "Debe ingresar el precio de compra del producto", Toast.LENGTH_SHORT).show();

        //}
         else if (TextUtils.isEmpty(PrecioVen)){
            Toast.makeText(this, "Debe ingresar el precio de venta del producto", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(Cant)){
            Toast.makeText(this, "Debe ingresar la cantidad del producto", Toast.LENGTH_SHORT).show();
        }
        else{
            GuardaInformacionProducto ();
        }


    }

    private void GuardaInformacionProducto() {

        dialog.setTitle("Guardar Producto");
        dialog.setMessage("Espere mientras guardamos el producto");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat curreDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        CurrentDate=curreDateFormat.format(calendar.getTime());
        SimpleDateFormat CurrenTimeFormat = new SimpleDateFormat("HH-mm:ss");
        CurrentTime = CurrenTimeFormat.format(calendar.getTime());
        productoRandomKey = CurrentDate + CurrentTime;

        final StorageReference filePath = ProductoImagenRef.child(imagenUri.getLastPathSegment()+productoRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imagenUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String mensaje = e.toString();
                Toast.makeText(AgregarproductoActivity.this, "Error: "+mensaje, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarproductoActivity.this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadUri = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUri = task.getResult().toString();
                            Toast.makeText(AgregarproductoActivity.this, "Imagen guardada en Firebase", Toast.LENGTH_SHORT).show();
                            GuardarEnFirebase();
                        }else{
                            Toast.makeText(AgregarproductoActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void GuardarEnFirebase() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pid", productoRandomKey);
        map.put("fecha", CurrentDate);
        map.put("hora",CurrentTime);
        map.put("descripcion",Desc);
        map.put("nombre",Nom);
        map.put("preciocom",PrecioCom);
        map.put("precioven",PrecioVen);
        map.put("Cantidad",Cant);
        map.put("imagen",downloadUri);
        map.put("categoria",Categoria);

        ProductoRef.child(productoRandomKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(AgregarproductoActivity.this, AdminActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    Toast.makeText(AgregarproductoActivity.this, "¡Solicitud Exitosa!", Toast.LENGTH_SHORT).show();
                    if (pidEliminar != ""){
                        databaseReference.child("Productos").child(pidEliminar).removeValue();
                    }
                }else{
                    dialog.dismiss();
                    String mensaje = task.getException().toString();
                    Toast.makeText(AgregarproductoActivity.this, "Error: "+mensaje, Toast.LENGTH_SHORT).show();
                }
            }
        });


}
    }