package com.example.lufeli;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productoNom, productoDescripcion, productoPrecio, productoCantidad, ubicacion, fecha, hora, mensaje;
    public ImageView productoImagen;
    public ItemClickListener listener;

    public ProductoViewHolder(@NonNull View itemView) {
        super(itemView);

        productoNom = (TextView) itemView.findViewById(R.id.producto_nombre);
        productoDescripcion = (TextView) itemView.findViewById(R.id.producto_descripcion);
        productoPrecio = (TextView) itemView.findViewById(R.id.producto_precio);
        productoImagen = (ImageView) itemView.findViewById(R.id.producto_imagen);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
