package com.example.projetofabrica.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetofabrica.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditarVisitaActivity extends AppCompatActivity {

    private EditText editNomeVisita;
    private Button btnSalvarEdicao;
    private Visita visita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_visita);
        getSupportActionBar().hide();

        editNomeVisita = findViewById(R.id.edit_nome);
        btnSalvarEdicao = findViewById(R.id.btnSalvarEdicao);

        visita = getIntent().getParcelableExtra("visit");

        if (visita != null) {
            editNomeVisita.setText(visita.getNome());
        }

        btnSalvarEdicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarEdicao();
            }
        });
    }

    private void salvarEdicao() {
        String novoNome = editNomeVisita.getText().toString();
        DatabaseReference visitaRef = FirebaseDatabase.getInstance().getReference().child("visitas").child(visita.getId());
        visitaRef.child("name").setValue(novoNome).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditarVisitaActivity.this, "Edição salva com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarVisitaActivity.this, "Falha ao salvar edição", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void fecharTeclado(View view){
        InputMethodManager fecharTeclado = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        fecharTeclado.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
