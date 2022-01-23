package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class Despesas extends AppCompatActivity {

    private TextInputEditText edtDataD,edtCategoriaD,edtDescricaoD;
    private EditText edtValorD;
    private Movimentacao movimentacao;
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getAuth();
    private double despesaTotal;
    private double despesaGerada;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        edtValorD = findViewById(R.id.edtValorR);
        edtDataD = findViewById(R.id.edtDataD);
        edtCategoriaD = findViewById(R.id.edtCategoriaD);
        edtDescricaoD = findViewById(R.id.edtDescricaoRes);


        //Prenchendo o campo data com a data atual
        edtDataD.setText(DateCuston.dataAtual());

        //recuperando o valor da despesaTotal(armazenado no banco de dados)
         recuperarDespesaTotal();
    }

    public void salvarDespesa(View view){

        if(validarCamposDespesa()){
            String dataEscolhida = edtDataD.getText().toString();
            Double valorRecuperado = Double.parseDouble(edtValorD.getText().toString());
            movimentacao = new Movimentacao();
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(edtCategoriaD.getText().toString());
            movimentacao.setDescricao(edtDescricaoD.getText().toString());
            movimentacao.setData(dataEscolhida);
            movimentacao.setTipo("D");


            //atualizando os valores referentes a movimentação do dinheiro
            despesaGerada = valorRecuperado;
            Double despesaAtualizada = despesaGerada+despesaTotal;
            atualizarDespesa(despesaAtualizada);

            //salvando os dados da nova movimentação
            movimentacao.salvar(dataEscolhida);
        }

    }

    public boolean validarCamposDespesa(){

        //recuperando os valores digitados pelo usuario
        String valor = edtValorD.getText().toString();
        String categoria = edtCategoriaD.getText().toString();
        String descricao = edtDescricaoD.getText().toString();
        String data = edtDataD.getText().toString();

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

    private void recuperarDespesaTotal(){

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
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarDespesa(Double despesa){
        //recuperando o idUsuario(email)
        String idUsuario = firebaseAuth.getCurrentUser().getEmail();

        //convertendo o idUsuario para base64
        idUsuario = Base64Custon.codificarBase64(idUsuario);

        DatabaseReference usuarioRef = reference.child("usuarios").child(idUsuario);

        //atualizando o valor da despesa no bd
        usuarioRef.child("despesaTotal").setValue(despesa);


    }
}