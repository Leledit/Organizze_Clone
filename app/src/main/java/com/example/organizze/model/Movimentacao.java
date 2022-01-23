package com.example.organizze.model;

import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custon;
import com.example.organizze.helper.DateCuston;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private double valor ;


    public Movimentacao() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void salvar(String dataEscolhida){

        //obtendo o mes e ano atual(para poder fazer o lançamento no banco de dados)
        String data = DateCuston.mesAnoDataEscolhida(this.data);

        //obtendo o email do usuario
        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getAuth();

        //Convertendo o email do usuario para o formate base64
        String aidUsuario = Base64Custon.codificarBase64(firebaseAuth.getCurrentUser().getEmail());

        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        reference.child("movimentacao")
                 .child(aidUsuario)
                 .child(data)
                 .push()
                 .setValue(this);
    }
}
