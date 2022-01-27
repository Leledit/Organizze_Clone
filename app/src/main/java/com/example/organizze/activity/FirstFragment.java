package com.example.organizze.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizze.R;
//import com.example.organizze.activity.databinding.FragmentFirstBinding;
import com.example.organizze.adapter.AdapterMovimentacao;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.databinding.FragmentFirstBinding;
import com.example.organizze.helper.Base64Custon;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private TextView textSaladacao,textSaldo;
    private FragmentFirstBinding binding;
    private MaterialCalendarView calendarView;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getAuth();
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioRef ;
    private  Double despesaTotal;
    private  Double receitaTotal;
    private  Double resumoUsuario;
    private ValueEventListener eventListenerUsuario ;
    private ValueEventListener movimentacaoEvent;


    private RecyclerView recyclerView;
    private AdapterMovimentacao Adaptermovimentacao;
    private List<Movimentacao> movimentacaoArrayList = new ArrayList<Movimentacao>();

    private String mesAnoSelecionado ;
    private Movimentacao movimentacao ;



    private DatabaseReference bancoDados = FirebaseDatabase.getInstance().getReference();


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textSaladacao = root.findViewById(R.id.textSaladacao);
        calendarView = root.findViewById(R.id.calendarView);
        textSaldo = root.findViewById(R.id.textSaldo);
        recyclerView = root.findViewById(R.id.recyclerMovimento);

        //chamando metodo que configura o calendario
        configurarCalendar();
        //Chamando o metodo Swipe
        Swipe();


        //configurando o recycleView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);



        //Configurando o adapter
        Adaptermovimentacao = new AdapterMovimentacao(movimentacaoArrayList,getContext());

        recyclerView.setAdapter(Adaptermovimentacao);
        // Toast.makeText(getContext(),"teste"+movimentacaoArrayList,Toast.LENGTH_LONG).show();



        return  root;
    }

    public void Swipe(){
        ItemTouchHelper.Callback iteCallback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                //abaixo é declarado o que ira ativar a ação
               int dragFlag = ItemTouchHelper.ACTION_STATE_IDLE;
               //abaixo é declarado a ação em si(no nosso caso,e mover o item do começo ao fim(Start),
                //e do fim para o começo(End)
               int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return  makeMovementFlags(dragFlag,swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //aqui é possivel

                excluirMovimentacao(viewHolder);
            }
        };

        //anequesar o evento iteCallback com o nosso recycleView
        new ItemTouchHelper(iteCallback).attachToRecyclerView(recyclerView);
    }


    public void excluirMovimentacao(RecyclerView.ViewHolder viewHolder){
        //criando o alerta dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        //configurando o dialog
        dialog.setTitle("Excluir Movimentação da conta");
        dialog.setMessage("Voce tem certeza que deseja realmente excluir essa conta?");
        dialog.setCancelable(false);

        //Configuando o evento da ação positiva
        dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //fazer a exclusao do item(Conta)
                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacaoArrayList.get(position);

                //recuperando o idUsuario(email)
                String idUsuario = firebaseAuth.getCurrentUser().getEmail();
                //convertendo o idUsuario para base64
                idUsuario = Base64Custon.codificarBase64(idUsuario);

                bancoDados.child("movimentacao")
                        .child(idUsuario)
                        .child(mesAnoSelecionado)
                        .child(movimentacao.getKey())
                        .removeValue();
                //Removendo o item que esta no adapeter
                Adaptermovimentacao.notifyItemRemoved(position);

                //apos excluir o item, deve ser feito a atualização do saldo
                atualizarSaldo();
            }

        });

        //Configurando o evento da ação negativa
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"Cancelado",Toast.LENGTH_LONG).show();
                Adaptermovimentacao.notifyDataSetChanged();
            }
        });
        //criando o dialog
        AlertDialog alert = dialog.create();
        //mostrando ele para o usuario
        alert.show();

    }

    public void atualizarSaldo(){

        //recuperando o idUsuario(email)
        String idUsuario = firebaseAuth.getCurrentUser().getEmail();
        //convertendo o idUsuario para base64
        idUsuario = Base64Custon.codificarBase64(idUsuario);
        usuarioRef = reference.child("usuarios").child(idUsuario);

        if(movimentacao.getTipo().equals("R")){

            receitaTotal = receitaTotal - movimentacao.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }
        if(movimentacao.getTipo().equals("D")) {
            despesaTotal = despesaTotal - movimentacao.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTotal);
        }


    }

    public void recuperarMovimentacoes() {

        //recuperando o idUsuario(email)
        String idUsuario = firebaseAuth.getCurrentUser().getEmail();
        //convertendo o idUsuario para base64
        idUsuario = Base64Custon.codificarBase64(idUsuario);


        bancoDados = bancoDados.child("movimentacao").child(idUsuario).child(mesAnoSelecionado);

        movimentacaoEvent = bancoDados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dados: snapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    //Recuperando a chave do movimento(id gerado pelo firebase)
                    movimentacao.setKey(dados.getKey());
                    movimentacaoArrayList.add(movimentacao);
                }

                Adaptermovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       /* bancoDados.child("movimentacao").child(idUsuario).child(mesAnoSelecionado).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

           //     movimentacaoArrayList.clear();
                for(DataSnapshot dados: task.getResult().getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    //Recuperando a chave do movimento(id gerado pelo firebase)
                    movimentacao.setKey(dados.getKey());
                    movimentacaoArrayList.add(movimentacao);
                }

                Adaptermovimentacao.notifyDataSetChanged();


            }


        });

        */


    }

   public void recuperarResumo(){
        //Recuperando os dados do banco

        //recuperando o idUsuario(email)
        String idUsuario = firebaseAuth.getCurrentUser().getEmail();
        //convertendo o idUsuario para base64
        idUsuario = Base64Custon.codificarBase64(idUsuario);
        usuarioRef = reference.child("usuarios").child(idUsuario);

        eventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal  ;


                //formatando o saldo atual da pessoa
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado =decimalFormat.format(resumoUsuario);
                textSaladacao.setText("Ola, " +usuario.getNome());
                textSaldo.setText( "R$:"+resultadoFormatado + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void configurarCalendar(){
        CharSequence meses[] = {"Janeiro","fevereiro","março","abril","maio","junho","julho","agosto","setembro","outubro","novembro","dezembro"};
        calendarView.setTitleMonths(meses);

        CalendarDay dataAtual = calendarView.getCurrentDate();

        mesAnoSelecionado = String.valueOf((dataAtual.getMonth()+1)+""+dataAtual.getYear());


        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mesAnoSelecionado = String.valueOf((date.getMonth()+1)+""+date.getYear());

                bancoDados.removeEventListener(movimentacaoEvent);
                recuperarMovimentacoes();

            }


        });
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStop() {
      /* usuarioRef.removeEventListener(eventListenerUsuario);
        reference.removeEventListener(eventListenerMovimentacao);*/

        bancoDados.removeEventListener(movimentacaoEvent);
        super.onStop();
    }
    @Override
    public void onStart() {
        super.onStart();
        //Receuperando os dados armazenados no banco de dados(referentes ao cliente em si)
        recuperarResumo();
        //Recuperando os dados amazendos no banco de dados(referemtes as movimetaçoes do usuario)
        recuperarMovimentacoes();



    }
}