package com.example.organizze.helper;

import android.util.Base64;

public class Base64Custon {

    //No metodo abaixo é convertido uma string para uma valor criptografado em base64
    public static String codificarBase64(String string){
       return Base64.encodeToString(string.getBytes(),Base64.DEFAULT).replaceAll("(\\n|\\r)","");
    }

    //No metodo abaixo é convetido um valor criptografado em base64 para uma string
    public static String decodificarBase64(String stringModificada){
       return new String(Base64.decode(stringModificada,Base64.DEFAULT));
    }
}
