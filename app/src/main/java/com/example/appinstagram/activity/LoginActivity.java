package com.example.appinstagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appinstagram.R;
import com.example.appinstagram.helper.ConfigFireBase;
import com.example.appinstagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {
    private EditText campoEmail, campoSenha;
    private Button buttonEntrar;
    private ProgressBar progressBar;

    private Usuario usuario;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userIsLogged();

        this.campoEmail      = findViewById(R.id.editLoginEmail);
        this.campoSenha      = findViewById(R.id.editLoginSenha);
        this.buttonEntrar    = findViewById(R.id.buttonEntrar);
        this.progressBar     = findViewById(R.id.progressLogin);

        this.campoEmail  .requestFocus();
        this.progressBar .setVisibility(View.GONE);
        this.buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if ( email.isEmpty() || senha.isEmpty())
                    Toast.makeText(
                            getApplicationContext(),
                            "Preencha todos os campos.",
                            Toast.LENGTH_SHORT
                    ).show();
                else{
                    usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    validarLoginDeste( usuario );
                }
            }
        });
    }

    public void userIsLogged(){
        this.auth = ConfigFireBase.getFireBaseAuth();
        if ( this.auth.getCurrentUser() != null ){
            startActivity( new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void abrirCadastro(View view) {
        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }

    public void validarLoginDeste( Usuario usuario ){
        this.progressBar.setVisibility(View.VISIBLE);
        this.auth = ConfigFireBase.getFireBaseAuth();
        this.auth
                .signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if ( ! task.isSuccessful() ){
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Erro ao fazer Login.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }else{
                                    startActivity( new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }
                        }

                );
    }
}