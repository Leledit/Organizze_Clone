package com.example.organizze.config;

import com.google.firebase.auth.FirebaseAuth;

public class ConfiguracaoFirebase {

    private static FirebaseAuth auth;


    //retorna a instancia do firebaseAuth
    public static FirebaseAuth getAuth(){

       if(auth == null) {
           auth = FirebaseAuth.getInstance();
       }
       return auth;
    }
    //é bom criar essa classe pois com ela, temos apenas uma unica instancia da classe FirebaseAuth. a chave disso é uso
    // do static

}
