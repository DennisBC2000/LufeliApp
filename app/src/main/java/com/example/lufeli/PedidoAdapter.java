package com.example.lufeli;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lufeli.Modal.pedidos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PedidoAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView nombre, ubicacion, mensaje;

    public PedidoAdapter(@NonNull View itemView) {
        super(itemView);

        nombre = itemView.findViewById(R.id.textoNombre);
        ubicacion = itemView.findViewById(R.id.textoUbicacion);
        mensaje = itemView.findViewById(R.id.textoMensaje);
    }

    @Override
    public void onClick(View view) {

    }
}
