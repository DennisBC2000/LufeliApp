package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.annotation.StyleableRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;



import com.example.lufeli.Modal.Productos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private String CurrentUserId;
    private DatabaseReference UserRef, ProductosRef;
    private String Telefono = "";
    private FloatingActionButton botonFlotante;
    private RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        /*Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            Telefono = bundle.getString("phone");
        }*/
        auth=FirebaseAuth.getInstance();
        //CurrentUserId=auth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Usuarios");
        ProductosRef= FirebaseDatabase.getInstance().getReference().child("Productos");
        recyclerMenu= findViewById(R.id.recicler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);
        botonFlotante = (FloatingActionButton)findViewById(R.id.fab);
        botonFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrincipalActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("TIENDA LUFELI");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setActionBar(toolbar);


        DrawerLayout drawerLayout =findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(
                this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=(NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

//        TextView nombreHeader = (TextView) headerView.findViewById(R.id.usuario_nombre_perfil);
        //CircleImageView imagenHeader = (CircleImageView) headerView.findViewById(R.id.usuario_imagen_perfil);

       /* UserRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("imagen")){
                    String imagen = snapshot.child("imagen").getValue().toString();
                    nombreHeader.setText(snapshot.child("nombre").getValue().toString());
                  Picasso.get().load(imagen).error(R.drawable.logouser).into(imagenHeader);
                }else if (snapshot.exists()){
                    nombreHeader.setText(snapshot.child("nombre").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }



   @Override
    protected void onStart() {
       super.onStart();
       /*FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null){
            EnviarAlLoign();
        }/*else
        {
            VerificarUsuarioExistente();
        }*/

        FirebaseRecyclerOptions<Productos> options = new FirebaseRecyclerOptions.Builder<Productos>()
                .setQuery(ProductosRef, Productos.class).build();

        FirebaseRecyclerAdapter<Productos, ProductoViewHolder> adapter = new FirebaseRecyclerAdapter<Productos, ProductoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductoViewHolder holder, int position, @NonNull Productos model) {

                holder.productoNom.setText(model.getNombre());
                //holder.productoCantidad.setText("Cantidad: "+model.getCantidad());
                holder.productoDescripcion.setText(model.getDescripcion());
                holder.productoPrecio.setText("$ "+model.getPrecioven());
                Picasso.get().load(model.getImagen()).into(holder.productoImagen);

                holder.productoImagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PrincipalActivity.this, ProductoDetallesActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.productos_item_layout, viewGroup, false);
                ProductoViewHolder holder = new ProductoViewHolder(view);
                return holder;
            }
        };

        recyclerMenu.setAdapter(adapter);
        adapter.startListening();
    }


    /*private void VerificarUsuarioExistente() {
        final String CurrentUserId = auth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(CurrentUserId)){
                   // EnviarAlSetup();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_principal_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_carrito) {
            ActivityCarrito();
        } else if (id == R.id.nav_mapa) {
            ActivityMapa();
        } else if (id == R.id.atencion_cliente) {
            ActivityAtencionCliente();
        }else if (id == R.id.facebook) {
            ActivityFacebook();
        }

        /*else if (id == R.id.nav_categorias){
            ActivityCategoria();
        }
        else if (id== R.id.nav_buscar){
            ActivityBuscar();
        }
        else if (id == R.id.nav_perfil){
            ActivityPerfil();
        }
        else if (id == R.id.nav_salir){
            ActivitySalir();
            auth.signOut();
            EnviarAlLoign();
        }*/

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ActivityFacebook() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.facebook.com/Lufeli-2598071597086410/?hc_ref=ARQD0BHLRVqGRRzw1EfNWJJOWdMKU6JkThqhuD1XNJSBnJu-qM9RLID-t6XsAJ8_d68&fref=nf&__xts__[0]=68.ARC14GMUqv3M5DAimvZGpf-mAqr42Mv3pGArRY517RPguEtBn8CMLlpaA-OwVEOVVSrK8n01Onk5Kv648wZ-g5yQQ4NnbfxD_FvqbFxzoCo7Cx7hezxinIttnveKLBUGFA9jSIHVIjNcVgRRR8UmJNfsJLeC-qizvuGkEh5eF3EYKZRn4S997R9FbzWEmsELkHd3EPS4sLHFPBHOwIrQ6G4rsvT90ao1ZU-9Alpjo6bparikMKQXRV_Qn8oa4C7OPY0szmKsBYynX2_sqETqXvxE61zqEnV2I0JBkOTtGXAmQnhiDkrZP_U&__tn__=kC-R"));
        startActivity(intent);
    }

    private void ActivityAtencionCliente() {
            int permiso = ContextCompat.checkSelfPermission(PrincipalActivity.this, Manifest.permission.CALL_PHONE);
            if (permiso != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "No tiene permisos de llamada", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(PrincipalActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 255);
            }else {
                String numero = "0983127015";
                String inicio = "tel:" + numero;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(inicio));
                startActivity(intent);
            }
        }




private void ActivitySalir() {
    }

    private void ActivityMapa() {
        Toast.makeText(this, "Nuestra Ubicación", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PrincipalActivity.this, ActivityMapa.class);
        startActivity(intent);
    }

    private void ActivityPerfil() {
        Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PrincipalActivity.this, PerfilActivity.class);
        startActivity(intent);
    }

    private void ActivityBuscar() {
        Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
    }

    private void ActivityCategoria() {
        Toast.makeText(this, "Categoria", Toast.LENGTH_SHORT).show();
    }

    private void ActivityCarrito() {
        Toast.makeText(this, "Carrito", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PrincipalActivity.this, CarritoActivity.class);
        startActivity(intent);
    }


    /*private void EnviarAlSetup() {
        Intent intent=new Intent(PrincipalActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone", Telefono);
        startActivity(intent);
        finish();
    }*/

    /*private void EnviarAlLoign() {
        Intent intent=new Intent(PrincipalActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }*/

}