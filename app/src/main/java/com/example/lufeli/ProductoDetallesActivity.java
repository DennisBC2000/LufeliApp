package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lufeli.Modal.Productos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductoDetallesActivity extends AppCompatActivity {


    Button btnresta, btnsuma;
    TextView tvCantidad;
    private Button agregarCarrito;
    private ImageView productoImagen;
    TextView productoPrecio, productoDescripcion, productoNombre;
    private String productoId = "", estado = "Normal", CurrentUserId;
    private FirebaseAuth auth;
    private String precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalles);

        btnsuma = findViewById(R.id.btnsuma);
        btnresta = findViewById(R.id.btnresta);
        tvCantidad = findViewById(R.id.tvcantidad);

        btnsuma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bandera = Integer.parseInt(tvCantidad.getText().toString());
                int bandera_dos = bandera + 1;
                tvCantidad.setText(""+bandera_dos);
            }
        });

        btnresta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bandera = Integer.parseInt(tvCantidad.getText().toString());
                if(bandera == 0){
                    tvCantidad.setText("0");
                }
                else{
                    int bandera_dos = bandera - 1;
                    tvCantidad.setText(""+bandera_dos);
                }
            }
        });

        productoId= getIntent().getStringExtra("pid");

        agregarCarrito=(Button)findViewById(R.id.boton_siguiente_detalles);
        productoImagen=(ImageView)findViewById(R.id.producto_imagen_detalles);
        productoPrecio=(TextView)findViewById(R.id.producto_precio_detalles);
        productoDescripcion=(TextView)findViewById(R.id.producto_descripcion_detalles);
        productoNombre=(TextView)findViewById(R.id.producto_nombre_detalles);

        ObtenerDatosProducto(productoId);
        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();

        agregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (estado.equals("Pedido") || estado.equals("Enviado")){
                    Toast.makeText(ProductoDetallesActivity.this, "Esperando a que el primer pedido finalice...", Toast.LENGTH_SHORT).show();
                }else{
                    agregarAlaLista();
                }
        }
    });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //VerificarEstadoOrden();
    }
    private void agregarAlaLista() {

        String CurrentTime, CurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat data = new SimpleDateFormat("MM-dd-yyyy");
        CurrentDate= data.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        CurrentTime= time.format(calendar.getTime());

        final DatabaseReference CarListRef = FirebaseDatabase.getInstance().getReference().child("Carrito").child(CurrentUserId);

        final HashMap<String, Object> map = new HashMap<>();
        map.put("pid", productoId);
        map.put("nombre", productoNombre.getText().toString());
        map.put("precio", precio);
        map.put("fecha", CurrentDate);
        map.put("hora", CurrentTime);
        map.put("cantidad", tvCantidad.getText().toString());
        map.put("descuento", "");

        CarListRef.child("Productos").child(productoId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    CarListRef.child("Productos").child(productoId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful()){
                                 Toast.makeText(ProductoDetallesActivity.this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();

                                 Intent intent = new Intent(ProductoDetallesActivity.this, PrincipalActivity.class);
                                 startActivity(intent);
                             }
                        }
                    });
                }
            }
        });

    }

    private void ObtenerDatosProducto(String productoId) {
        DatabaseReference ProductoRef = FirebaseDatabase.getInstance().getReference().child("Productos");
        ProductoRef.child(productoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Productos productos = snapshot.getValue(Productos.class);
                    productoNombre.setText(productos.getNombre());
                    productoDescripcion.setText(productos.getDescripcion());
                    productoPrecio.append(productos.getPrecioven());
                    precio = productos.getPrecioven();
                    Picasso.get().load(productos.getImagen()).into(productoImagen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void VerificarEstadoOrden() {
        DatabaseReference OrdenRef;
        OrdenRef = FirebaseDatabase.getInstance().getReference().child("Ordenes").child(CurrentUserId);
        OrdenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String envioStado = snapshot.child("estado").getValue().toString();
                    if (envioStado.equals("Enviado")){
                        estado="Enviado";
                    }else if (envioStado.equals("Pendiente")){
                        estado="Pendiente";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}