package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class loginActivity extends AppCompatActivity {

    private EditText edtEmail, edtSenha ;
    private Button btnLogin;
    private Usuario usuario;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail_log);
        edtSenha = findViewById(R.id.edtSenha_log);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //buscando os valores digitados nos campos
                String email = edtEmail.getText().toString();
                String senha = edtSenha.getText().toString();

                //validando os campos vindo do formulario

                if(!email.isEmpty()){
                    if(!senha.isEmpty()){

                        //intanciando a classe do usuario e setando os valores
                        usuario = new Usuario();
                        usuario.setEmail(email);
                        usuario.setSenha(senha);

                        //agora sera feito o cadastro do usuario
                        logandoUsuario();
                    }else{
                        Toast.makeText(getApplicationContext(),"Prencha o campo Senha", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Prencha o campo Email", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void logandoUsuario(){

        //intanciando a classe do firebase autentication
        firebaseAuth = ConfiguracaoFirebase.getAuth();
        //realizando o login do usuario
        firebaseAuth.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaInicial();

                }else{
                    //lançando exceçoes para o login
                    String excecao = "";
                    try {
                        //lançando uma exceção
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha nao correspodem a um usuario cadastrado";
                    }catch (FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuario nao cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar o usuario: " + e.getMessage();
                        e.printStackTrace();
                    }

                     Toast.makeText(getApplicationContext(),excecao, Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    public void abrirTelaInicial(){
        startActivity(new Intent(this,Principal.class));
        finish();
    }
}