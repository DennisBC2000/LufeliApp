package com.example.lufeli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animaciones
        Animation animacion1= AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animacion2= AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        TextView lufeliTextView = findViewById(R.id.lufeliTextView);
        ImageView logoImageView =  findViewById(R.id.logoImageView);

        lufeliTextView.setAnimation(animacion2);
        logoImageView.setAnimation(animacion1);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
        thread.start();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);*/
        }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}