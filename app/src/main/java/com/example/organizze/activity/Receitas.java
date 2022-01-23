package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custon;
import com.example.organizze.helper.DateCuston;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Receitas extends AppCompatActivity {


    private TextView edtValorR ;
    private TextInputEditText edtDataR,edtCategoriaR,edtDescricaoR;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getAuth();
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private Double receitasTotais;
    private Movimentacao movimentacao;
    private Double receitasGeradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        edtValorR = findViewById(R.id.edtValorR);
        edtDataR = findViewById(R.id.edtDataR);
        edtCategoriaR = findViewById(R.id.edtCategoriaR);
        edtDescricaoR = findViewById(R.id.edtDescricaoR);


        //Prenchendo o campo data com a data atual
        edtDataR.setText(DateCuston.dataAtual());

        //recuperando o valor da receitasTotal(armazenado no banco de dados)
       recuperarReceitasTotal();


    }

    private void recuperarReceitasTotal(){
        //recuperando o idUsuario(email)
        String idUsuario = firebaseAuth.getCurrentUser().getEmail();

        //convertendo o idUsuario para base64
        idUsuario = Base64Custon.codificarBase64(idUsuario);

        DatabaseReference usuarioRef = reference.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //buscando os valores que estao armazenados no bd(referentes ao usuario) e alocando numa instancia da classe Usuario
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitasTotais = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public boolean validarCamposDespesa(){

        //recuperando os valores digitados pelo usuario
        String valor = edtValorR.getText().toString();
        String categoria = edtCategoriaR.getText().toString();
        String descricao = edtDescricaoR.getText().toString();
        String data = edtDataR.getText().toString();

        if(!valor.isEmpty()) {
            if(!categoria.isEmpty()) {
                if(!descricao.isEmpty()) {
                    if(!data.isEmpty()) {
                        return  true;
                    }else{
                        Toast.makeText(getApplicationContext(),"Prencha o campo data", Toast.LENGTH_LONG).show();
                        return  false;
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Prencha o campo descricao", Toast.LENGTH_LONG).show();
                    return  false;
                }
            }else{
                Toast.makeText(getApplicationContext(),"Prencha o campo categoria", Toast.LENGTH_LONG).show();
                return  false;
            }
        }else{
            Toast.makeText(getApplicationContext(),"Prencha o campo valor", Toast.LENGTH_LONG).show();
            return  false;
        }

    }

    public void salvarReceitas(View view){

        if(validarCamposDespesa()){
            String dataEscolhida = edtDataR.getText().toString();
            Double valorRecuperado = Double.parseDouble(edtValorR.getText().toString());
            movimentacao = new Movimentacao();
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(edtCategoriaR.getText().toString());
            movimentacao.setDescricao(edtDescricaoR.getText().toString());
            movimentacao.setData(dataEscolhida);
            movimentacao.setTipo("R");


            //atualizando os valores referentes a movimentação do dinheiro
            receitasGeradas = valorRecuperado;
            Double despesaAtualizada = receitasGeradas+receitasTotais;
            atualizarReceitas(despesaAtualizada);

            //salvando os dados da nova movimentação
            movimentacao.salvar(dataEscolhida);
        }

    }

    public void atualizarReceitas(Double despesa){
        //recuperando o idUsuario(email)
        String idUsuario = firebaseAuth.getCurrentUser().getEmail();

        //convertendo o idUsuario para base64
        idUsuario = Base64Custon.codificarBase64(idUsuario);

        DatabaseReference usuarioRef = reference.child("usuarios").child(idUsuario);

        //atualizando o valor da despesa no bd
        usuarioRef.child("receitaTotal").setValue(despesa);


    }
}