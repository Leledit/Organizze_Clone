package com.example.organizze.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static FirebaseAuth auth;
    private static DatabaseReference reference;

    //Retorna a instancia do firebaseDatabase
    public static DatabaseReference getFirebaseDatabase(){
        if(reference == null){
            reference = FirebaseDatabase.getInstance().getReference();
        }
        return reference;
    }

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
