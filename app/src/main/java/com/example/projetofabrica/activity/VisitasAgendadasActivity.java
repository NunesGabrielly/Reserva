package com.example.projetofabrica.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetofabrica.R;
import com.example.projetofabrica.adapter.VisitaAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VisitasAgendadasActivity extends AppCompatActivity implements VisitaAdapter.OnItemClickListener {

    private RecyclerView recyclerViewVisitas;
    private VisitaAdapter visitaAdapter;

    private DatabaseReference visitasRef;
    private ValueEventListener visitasListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitas_agendadas);
        getSupportActionBar().hide();

        recyclerViewVisitas = findViewById(R.id.recyclerViewVisitas);
        recyclerViewVisitas.setLayoutManager(new LinearLayoutManager(this));

        visitasRef = FirebaseDatabase.getInstance().getReference().child("visitas");
        visitaAdapter = new VisitaAdapter(new ArrayList<>());
        visitaAdapter.setOnItemClickListener(this);
        recyclerViewVisitas.setAdapter(visitaAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        carregarVisitasAgendadasDoFirebase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (visitasListener != null) {
            visitasRef.removeEventListener(visitasListener);
        }
    }

    private void carregarVisitasAgendadasDoFirebase() {
        visitasListener = visitasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Visita> visitas = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Visita visita = snapshot.getValue(Visita.class);
                    visitas.add(visita);
                }
                visitaAdapter.setVisitas(visitas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VisitasAgendadasActivity.this, "Falha ao carregar visitas agendadas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Visita visita) {
        // Lógica para editar a visita
        // Abra a tela de edição da visita, passando os dados da visita
        Intent intent = new Intent(this, EditarVisitaActivity.class);
        intent.putExtra("visit", visita);
        startActivity(intent);
    }

    @Override
    public void onCancelClick(Visita visita) {
        // Lógica para cancelar a visita
        // Remova a visita do Firebase
        DatabaseReference visitaRef = visitasRef.child(visita.getId());
        visitaRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(VisitasAgendadasActivity.this, "Visita cancelada com sucesso", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VisitasAgendadasActivity.this, "Falha ao cancelar visita", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
