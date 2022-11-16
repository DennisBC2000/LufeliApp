package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lufeli.Modal.Carrito;
import com.example.lufeli.Modal.Productos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private EditText nombre, direccion, telefono, ciudad;
    private Button guardar;
    private String phone = "";
    private CircleImageView imagen;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserId;
    private static int GaleryPick = 1;
    private StorageReference UserImagenPerfil;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button Siguiente, Enviar;
    List<Productos> lista_carrito = new ArrayList<>();
    private TextView TotalPrecio, mensaje1;
    private double PrecioTotalD = 0.0;
    //private String CurrentUserId;
    //private FirebaseAuth auth;
    private double total = 0;
    private String mensaje = "";
    private PendingIntent pendingIntent;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICATION_ID = 0;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        lista_carrito = (List<Productos>) getIntent().getSerializableExtra("Carrito");
        auth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        CurrentUserId=auth.getCurrentUser().getUid();
        mensaje = getIntent().getStringExtra("mensaje");

        nombre=(EditText) findViewById(R.id.perfil_nombre);
        ciudad=(EditText) findViewById(R.id.perfil_ciudad);
        direccion=(EditText) findViewById(R.id.perfil_direccion);
        //telefono=(EditText) findViewById(R.id.perfil_telefono);
        guardar=(Button) findViewById(R.id.perfil_boton);
        imagen=(CircleImageView) findViewById(R.id.perfil_imagen);



        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nombre.getText().toString().equals("") && !ciudad.getText().toString().equals("") && !direccion.getText().toString().equals("")){

                    String CurrentTime, CurrentDate;
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat data = new SimpleDateFormat("MM-dd-yyyy");
                    CurrentDate= data.format(calendar.getTime());

                    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
                    CurrentTime= time.format(calendar.getTime());

                    final DatabaseReference CarListRef = FirebaseDatabase.getInstance().getReference().child("Pedidos").child(CurrentUserId);

                    final HashMap<String, Object> map = new HashMap<>();
                    map.put("nombre", nombre.getText().toString());
                    map.put("direccion", direccion.getText().toString());
                    map.put("ciudad", ciudad.getText().toString());
                    map.put("fecha", CurrentDate);
                    map.put("hora", CurrentTime);
                    map.put("mensaje", getIntent().getStringExtra("mensaje"));

                    createNotificationChannel();
                    createNotification();

                    CarListRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                CarListRef.child("Pedidos").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(PerfilActivity.this, "Muchas Gracias!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });

                    Intent intent = new Intent();
                    //Intent intent2= new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("whatsapp://send?phone=593979127100&text=Pedidos App Lufeli:\nHola,\nMi nombre es: "+ nombre.getText()+"\nMi dirección es: "+ ciudad.getText() +", "+ direccion.getText() +"\nAyudeme por favor con:\n"+mensaje));
                    Intent intent2 = new Intent(PerfilActivity.this, PrincipalActivity.class);
                    startActivity(intent2);
                    startActivity(intent);
                    //databaseReference.child("Carrito").child(CurrentUserId).removeValue();

                   /* intent = new Intent(PerfilActivity.this, PrincipalActivity.class);
                    intent.putExtra("mensaje", mensaje);
                    startActivity(intent);*/

                }
                else{
                    Toast.makeText(PerfilActivity.this, "Llene todos los datos por favor!", Toast.LENGTH_LONG).show();
                }


            }


        }); //FIN PROCESO



        /*
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,GaleryPick);
            }
        });
*/
        /*UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    if(snapshot.hasChild("imagen")){
                        String imagestr = snapshot.child("imagen").getValue().toString();
                        Picasso.get().load(imagestr).placeholder(R.drawable.logouser).into(imagen);
                    }else{
                        Toast.makeText(PerfilActivity.this, "Perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    } //ONCREATE

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pedidos Lufeli";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logowelcome);
        builder.setContentTitle("¡Gracias por su pedido!");
        builder.setContentText("Confirme su compra en WhatsApp.");
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GaleryPick && resultCode==RESULT_OK && data != null){
            Uri imageUri=data.getData();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        total = 0;

        VerficarEstadoOrden();

        final DatabaseReference CartListRef = FirebaseDatabase.getInstance().getReference().child("Carrito").child(CurrentUserId);

        FirebaseRecyclerOptions<Carrito> options = new FirebaseRecyclerOptions.Builder<Carrito>().setQuery(CartListRef.child("Productos"),Carrito.class).build();

        FirebaseRecyclerAdapter<Carrito, CarritoViewHolder> adapter = new FirebaseRecyclerAdapter<Carrito, CarritoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarritoViewHolder holder, int position, @NonNull Carrito model) {

                holder.carProductoNombre.setText(model.getNombre());
                holder.carProductoCantidad.setText("Cantidad: "+model.getCantidad());
                holder.carProductoPrecio.setText("Precio: $" +model.getPrecio());


                total = (Double.parseDouble(model.getPrecio())*Double.parseDouble(model.getCantidad()))+total;

                TotalPrecio.setText("Total: $"+total);

                mensaje = "•"+model.getCantidad()+" "+model.getNombre()+"\n"+mensaje;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{
                                "Editar",
                                "Eliminar"
                        };
                       /* AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                        builder.setTitle("Opciones del Producto");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0){
                                    Intent intent = new Intent(CarritoActivity.this, ProductoDetallesActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i==1){
                                    databaseReference.child("Carrito").child(CurrentUserId).child("Productos").child(model.getPid()).removeValue();
                                    total=0;
                                    TotalPrecio.setText("Total: $"+total);
                                    onStart();
                                    Toast.makeText(CarritoActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.show();*/
                    }
                });
            }
            @NonNull
            @Override
            public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item_layout, parent, false);
                CarritoViewHolder holder = new CarritoViewHolder(view);
                return holder;
            }
        };
        //recyclerView.setAdapter(adapter);
        //adapter.startListening();
    }


    private void VerficarEstadoOrden() {
    }
    private void GuardarInformacion() {

        /*String nombres = nombre.getText().toString().toUpperCase();
        String direccions = direccion.getText().toString();
        String ciudads = ciudad.getText().toString();
        String phones = telefono.getText().toString();

        if(TextUtils.isEmpty(nombres)){
            Toast.makeText(this, "Debe ingresar el nombre", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(direccions)){
            Toast.makeText(this, "Debe ingresar la direccion", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(ciudads)){
            Toast.makeText(this, "Debe ingresar la ciudad", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phones)){
            Toast.makeText(this, "Debe ingresar su numero", Toast.LENGTH_SHORT).show();
        }else {
            dialog.setTitle("Guardando");
            dialog.setMessage("Por favor espere");
            dialog.setCanceledOnTouchOutside(true);
            HashMap map= new HashMap();
            map.put("nombre",nombres);
            map.put("direccion",direccions);
            map.put("ciudad",ciudads);
            map.put("telefono",phones);
           // map.put("uid",CurrentUserId);
            /*UserRef.child(CurrentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        EnviarAlInicio();
                        dialog.dismiss();
                    }else
                    {
                        String mensaje=task.getException().toString();
                        Toast.makeText(PerfilActivity.this, "Error "+mensaje, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });*/
        }
        {

        }
    //}

    private void EnviarAlInicio() {
        Intent intent=new Intent(PerfilActivity.this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}