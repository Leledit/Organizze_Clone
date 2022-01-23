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
import com.example.organizze.helper.Base64Custon;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class cadastroActivity extends AppCompatActivity {


    private EditText edtNome, edtEmail, edtSenha;
    private Button btnCadastrar;
    private FirebaseAuth autentication;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pegando os textos que foram digitados nos campos do formulario
                String nome = edtNome.getText().toString();
                String email = edtEmail.getText().toString();
                String senha = edtSenha.getText().toString();

                //validando os campos

                if(!nome.isEmpty()){
                    if(!email.isEmpty()){
                        if(!senha.isEmpty()){



                            //intanciando a classe do usuario e setando os valores
                            usuario = new Usuario();
                            usuario.setNome(nome);
                            usuario.setEmail(email);
                            usuario.setSenha(senha);

                            //agora sera feito o cadastro do usuario
                            cadastrarUsuario();
                        }else{
                            Toast.makeText(getApplicationContext(),"Prencha o campo Senha", Toast.LENGTH_LONG).show();

                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Prencha o campo Email", Toast.LENGTH_LONG).show();

                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Prencha o campo nome", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void cadastrarUsuario(){

        //recuperando a instancia do firebase da classe de configuração(criada para manter uma unica instancia no projeto)
        autentication = ConfiguracaoFirebase.getAuth();

         autentication.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){

                     String idUsuario = Base64Custon.codificarBase64(usuario.getEmail());
                     usuario.setIdUsuario(idUsuario);
                     usuario.salvar();
                     finish();
                 }else{
                    //realizando o tratamento das exceçoes

                     String excecao = "";
                     try {
                         //lançando uma exceção
                         throw task.getException();
                     } catch (FirebaseAuthWeakPasswordException e) {
                         excecao = "Digite uma senha mais forte";
                     }catch (FirebaseAuthInvalidCredentialsException e){
                         excecao = "Digite uma Email valido";
                     }catch (FirebaseAuthUserCollisionException e ){
                         excecao = "Esta conta ja foi cadastrada";
                     }catch (Exception e){
                         excecao = "Erro ao cadastrar o usuario: " + e.getMessage();
                         e.printStackTrace();
                     }

                     Toast.makeText(getApplicationContext(),excecao, Toast.LENGTH_LONG).show();
                 }
             }
         });



    }


}