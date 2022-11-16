package com.example.lufeli;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lufeli.Modal.Carrito;
import com.example.lufeli.Modal.Productos;
import com.example.lufeli.Modal.pedidos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


public class FragmentCuatro extends Fragment {

    private View fragmento;
    private DatabaseReference UserRef, ProductosRef;
    private FloatingActionButton botonFlotante;
    private RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth auth;
    private String pedido;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private String CurrentUserId;


    public FragmentCuatro() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmento = inflater.inflate(R.layout.fragment_cuatro, container, false);
        auth= FirebaseAuth.getInstance();
        CurrentUserId=auth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Usuarios");
        ProductosRef= FirebaseDatabase.getInstance().getReference().child("Pedidos");
        recyclerMenu= fragmento.findViewById(R.id.recicler_menu);
        recyclerMenu.setHasFixedSize(true);
        recyclerMenu.setPadding(0,0,0, 200);
        botonFlotante = fragmento.findViewById(R.id.fab);
        botonFlotante.setVisibility(View.INVISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerMenu.setLayoutManager(layoutManager);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        return fragmento;

    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Productos> options = new FirebaseRecyclerOptions.Builder<Productos>()
                .setQuery(ProductosRef, Productos.class).build();

        FirebaseRecyclerAdapter<Productos, ProductoViewHolder> adapter = new FirebaseRecyclerAdapter<Productos, ProductoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductoViewHolder holder, int position, @NonNull Productos model) {

                holder.productoNom.setText(model.getNombre());
                holder.productoDescripcion.setText(model.getMensaje());
                holder.productoPrecio.setText(model.getCiudad() + "\n" + model.getDireccion());

                Picasso.get().load("https://i.ibb.co/BC5hPq2/ENVIO-PROD-1.png").into(holder.productoImagen); //FIN PROCESO

                holder.productoImagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{
                                "Pedido entregado"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Opciones de pedido");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseReference.child("Pedidos").removeValue();

                                onStart();
                                Toast.makeText(getContext(),"Pedido entregado", Toast.LENGTH_SHORT).show();

                                /*if (i==0) {


                                }

                                /*if (i==1){
                                    databaseReference.child("Productos").child(model.getPid()).removeValue();
                                    onStart();
                                    Toast.makeText(getContext(),"Producto eliminado", Toast.LENGTH_SHORT).show();
                                }*/
                            }
                        });
                        builder.show();
                    }
                });
            }



            @NonNull
            @Override
            public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.productos_item_admin, viewGroup, false);
                ProductoViewHolder holder = new ProductoViewHolder(view);
                return holder;
            }
        };

        recyclerMenu.setAdapter(adapter);
        adapter.startListening();
    }
}