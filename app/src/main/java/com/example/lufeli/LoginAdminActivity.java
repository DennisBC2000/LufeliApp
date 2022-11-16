package com.example.lufeli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LoginAdminActivity extends AppCompatActivity {

    private EditText numero, codigo;
    private Button enviarnumero, enviarcodigo;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String VerificacionID;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private String phoneNumber;
    private Button ayuda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        numero=(EditText)findViewById(R.id.numeroadmin);
        codigo=(EditText)findViewById(R.id.codigoadmin);
        enviarnumero=(Button) findViewById(R.id.enviarnumeroadmin);
        enviarcodigo=(Button) findViewById(R.id.enviarcodigoadmin);
        ayuda=(Button) findViewById(R.id.contacto);

        auth= FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW);
                intent.setData(Uri.parse("mailto:lufelisoporte@gmail.com?subject=Restablecer%20contraseña%20aplicación%20Lufeli"));
                startActivity(intent);
            }
        });

        enviarnumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = numero.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(LoginAdminActivity.this, "¡Primero debes ingresar tu número!", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setTitle("Validando número");
                    dialog.setMessage("Por favor espere mientras validamos su número");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber("+593"+ phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(LoginAdminActivity.this)
                            .setCallbacks(callbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options); //ENVIA EL NÚMERO CELULAR
                }
            }
        });
        enviarcodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numero.setVisibility(View.GONE);
                enviarnumero.setVisibility(View.GONE);
                String VerificacionCode = codigo.getText().toString();
                if(TextUtils.isEmpty(VerificacionCode)){
                    Toast.makeText(LoginAdminActivity.this, "Ingrese su codigo personal", Toast.LENGTH_SHORT).show();
                }else
                {
                    dialog.setTitle("Verificando");
                    dialog.setMessage("Tenga un poco de paciencia");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificacionID, VerificacionCode);
                    IngresadoConExito(credential);
                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                IngresadoConExito(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                Toast.makeText(LoginAdminActivity.this, "Error: \nUsuario no autorizado", Toast.LENGTH_SHORT).show();
                numero.setVisibility(View.VISIBLE);
                enviarnumero.setVisibility(View.VISIBLE);
                codigo.setVisibility(View.GONE);
                enviarcodigo.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                VerificacionID = s;
                resendingToken=token;
                dialog.dismiss();
                Toast.makeText(LoginAdminActivity.this, "Ingrese su código personal", Toast.LENGTH_SHORT).show();
                numero.setVisibility(View.GONE);
                enviarnumero.setVisibility(View.GONE);
                codigo.setVisibility(View.VISIBLE);
                enviarcodigo.setVisibility(View.VISIBLE);
            }
        };

    }

    private void IngresadoConExito(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(LoginAdminActivity.this, "Ingresado con éxito", Toast.LENGTH_SHORT).show();
                    EnviaralaPrincipal();
                }else
                {
                    String err = task.getException().toString();
                    Toast.makeText(LoginAdminActivity.this, "Error: "+err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null)
        {
            EnviaralaPrincipal();
        }*/
    }

    private void EnviaralaPrincipal() {
        Intent intent = new Intent(LoginAdminActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
        finish();
    }

}