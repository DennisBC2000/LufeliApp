package com.example.lufeli;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentUno extends Fragment {
    private View fragmento;
    private ImageView taza;
    private ImageView machica;
    private ImageView chocolate;

    public FragmentUno() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmento=inflater.inflate(R.layout.fragment_uno, container, false);

        taza=(ImageView) fragmento.findViewById(R.id.chcolateprod);
        machica=(ImageView) fragmento.findViewById(R.id.machicaprod);
        chocolate=(ImageView) fragmento.findViewById(R.id.tabletaprod);

        taza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","Chocolate de Taza");
                startActivity(intent);
            }
        });

        machica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","Máchica Vita Rita");
                startActivity(intent);
            }
        });

        chocolate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AgregarproductoActivity.class);
                intent.putExtra("categoria","Chocolate Puro");
                startActivity(intent);
            }
        });

        return fragmento;
    }
}