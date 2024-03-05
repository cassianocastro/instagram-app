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
import com.example.appinstagram.helper.UsuarioFirebase;
import com.example.appinstagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/**
 *
 */
public class CadastroActivity extends AppCompatActivity
{

    private EditText campoUsuario, campoEmail, campoSenha;
    private Button buttonCadastrar;
    private ProgressBar progressBar;
    private Usuario usuario;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastro);

        this.campoUsuario    = findViewById(R.id.editCadastroNome);
        this.campoEmail      = findViewById(R.id.editCadastroEmail);
        this.campoSenha      = findViewById(R.id.editCadastroSenha);
        this.buttonCadastrar = findViewById(R.id.buttonCadastrar);
        this.progressBar     = findViewById(R.id.progressCadastro);

        this.campoUsuario   .requestFocus();
        this.progressBar    .setVisibility(View.GONE);
        this.buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String nome  = campoUsuario.getText().toString();
                String email = campoEmail  .getText().toString();
                String senha = campoSenha  .getText().toString();

                if ( nome.isEmpty() || email.isEmpty() || senha.isEmpty())
                {
                    Toast
                        .makeText(
                            getApplicationContext(),
                            "Preencha todos os campos.",
                            Toast.LENGTH_SHORT
                        )
                        .show();
                }
                else
                {
                    usuario = new Usuario();

                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);

                    cadastrar( usuario );
                }
            }
        });
    }

    public void cadastrar(Usuario usuario)
    {
        this.progressBar.setVisibility(View.VISIBLE);
        this.auth = ConfigFireBase.getFireBaseAuth();
        this.auth
            .createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
            .addOnCompleteListener(
                this,
                .createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(
                    this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            progressBar.setVisibility(View.GONE);

                            String msg = null;

                            if ( ! task.isSuccessful() )
                            {
                                try
                                {
                                    throw task.getException();
                                }
                                catch(FirebaseAuthWeakPasswordException e)
                                {
                                    msg = "Digite uma senha mais forte.";
                                }
                                catch (FirebaseAuthInvalidCredentialsException e)
                                {
                                    msg = "Digite um e-mail válido.";
                                }
                                catch (FirebaseAuthUserCollisionException e)
                                {
                                    msg = "Conta já cadastrada.";
                                }
                                catch (Exception e)
                                {
                                    msg = "Ao cadastrar usuário" + e.getMessage();
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                try
                                {
                                    String u = task.getResult().getUser().getUid();
                                    usuario.setId(u);
                                    usuario.salvar();
                                    UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                                    msg = "Cadastro realizado.";
                                    startActivity( new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            Toast
                                .makeText(
                                    getApplicationContext(),
                                    msg,
                                    Toast.LENGTH_SHORT
                                )
                                .show();
                        }
                    }
                );
                new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        progressBar.setVisibility(View.GONE);

                        String msg = null;

                        if ( ! task.isSuccessful() )
                        {
                            try
                            {
                                throw task.getException();
                            }
                            catch( FirebaseAuthWeakPasswordException e )
                            {
                                msg = "Digite uma senha mais forte.";
                            }
                            catch ( FirebaseAuthInvalidCredentialsException e )
                            {
                                msg = "Digite um e-mail válido.";
                            }
                            catch ( FirebaseAuthUserCollisionException e )
                            {
                                msg = "Conta já cadastrada.";
                            }
                            catch ( Exception e )
                            {
                                msg = "Ao cadastrar usuário" + e.getMessage();
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            try
                            {
                                String u = task.getResult().getUser().getUid();

                                usuario.setId(u);
                                usuario.salvar();

                                UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                                msg = "Cadastro realizado.";

                                startActivity( new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                            catch ( Exception e )
                            {
                                e.printStackTrace();
                            }
                        }
                        Toast
                            .makeText(
                                getApplicationContext(),
                                msg,
                                Toast.LENGTH_SHORT
                            )
                            .show();
                    }
                }
            );
    }
}