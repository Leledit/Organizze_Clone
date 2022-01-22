package com.example.organizze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity{

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);



        //removendo os botoes dos slides(que vem por padrao)
        setButtonBackVisible(false);
        setButtonNextVisible(false);

        //Criando slids

        //primeiro slid
        addSlide(new FragmentSlide.Builder()
        .background(android.R.color.white)
                .fragment(R.layout.intro_01)
        .build());

        //segundo slid
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_02)
                .build());

        //terceiro slid
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_03)
                .build());

        //quarto slid
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_04)
               // .canGoForward(false)
                //.canGoBackward(false)
                .build());

        //adicioando o slid de entrada do app(cadastro/login)
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                //.canGoBackward(false)
                .build());


   }

    @Override
    protected void onStart() {
        super.onStart();

        //verificando se o usuario ja esta conectado(conta ativa)
        verificarUsuarioLogado();
    }


   public void btEntrar(View view){
      startActivity(new Intent(this,loginActivity.class));
   }

   public void btCadastrar(View view){
       startActivity(new Intent(this,cadastroActivity.class));
   }

   public void verificarUsuarioLogado(){

        auth = ConfiguracaoFirebase.getAuth();
        if(auth.getCurrentUser() != null){
            abrirTelaInicial();
        }

   }

    public void abrirTelaInicial(){
        startActivity(new Intent(this,Principal.class));
    }


}