package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lufeli.Modal.Carrito;
import com.example.lufeli.Modal.Productos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button Siguiente, Enviar, EnviarDinero;
    List<Productos> lista_carrito = new ArrayList<>();
    private TextView TotalPrecio, mensaje1;
    private double PrecioTotalD = 0.0;
    private String CurrentUserId;
    private FirebaseAuth auth;
    private double total = 0;
    private String mensaje = "";
    private PendingIntent pendingIntent;
    //private final static String CHANNEL_ID = "NOTIFICACION";
    //private final static int NOTIFICATION_ID = 0;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        recyclerView=(RecyclerView) findViewById(R.id.carrito_lista);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        lista_carrito = (List<Productos>) getIntent().getSerializableExtra("Carrito");
       // Siguiente=(Button) findViewById(R.id.siguiente_proceso);
        TotalPrecio=(TextView) findViewById(R.id.precio_total);
        mensaje1=(TextView) findViewById(R.id.mensaje1);
        Enviar=(Button) findViewById(R.id.btnenviar);
        auth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        CurrentUserId=auth.getCurrentUser().getUid();

        Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent();
                //intent.setAction(Intent.ACTION_VIEW);
                mensaje = mensaje+""+TotalPrecio.getText();
                total=0;
                TotalPrecio.setText("Total: $"+total);
                //intent.setData(Uri.parse("whatsapp://send?phone=593959167853&text=Pedidos App Lufeli:\n"+mensaje));
                // Log.d("mensaje",mensaje);
                //startActivity(intent);
                //databaseReference.child("Carrito").child(CurrentUserId).removeValue();

                //createNotificationChannel();
                //createNotification();



                Intent intent = new Intent(CarritoActivity.this, PerfilActivity.class);
                intent.putExtra("mensaje", mensaje);
                startActivity(intent);


            }
        });
    }

    /*private void createNotificationChannel() {
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
    }*/


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
                        AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
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
                        builder.show();
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
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    private void VerficarEstadoOrden() {
    }
}