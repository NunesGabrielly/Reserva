package com.example.projetofabrica.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetofabrica.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarVisita extends AppCompatActivity {

    private EditText editDataVisita, editHoraVisita;
    private Button btnSalvarEdicao;
    private AutoCompleteTextView editNomeVisita;
    private Visita visita;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_visita);
        getSupportActionBar().hide();

        editDataVisita = findViewById(R.id.edit_data);
        editHoraVisita = findViewById(R.id.edit_hora);
        btnSalvarEdicao = findViewById(R.id.btnSalvarEdicao);

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.edit_nome);
        String[] countries = getResources().getStringArray(R.array.instituicoes_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                countries);
        textView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        Task<DocumentSnapshot> snapshot = FirebaseFirestore.getInstance().collection("Visitas").document(bundle.getString("agendaId")).get();
        snapshot.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                visita = new Visita(task.getResult().getId(), task.getResult().getString("nome"), task.getResult().getString("data"), task.getResult().getString("hora"));
                if (visita != null) {
                    editNomeVisita.setText(visita.getNome());
                    editDataVisita.setText(visita.getData());
                    editHoraVisita.setText(visita.getHora());
                }
            }
        });

        btnSalvarEdicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarEdicao();
                Intent intent =  new Intent(EditarVisita.this, FormAgenda.class);
                startActivity(intent);
            }
        });
    }

    private void salvarEdicao() {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        VisitaDTO newVisita = new VisitaDTO(userID, editNomeVisita.getText().toString(), editDataVisita.getText().toString(), editHoraVisita.getText().toString());
        FirebaseFirestore.getInstance().collection("Visitas").document(visita.getId()).set(newVisita).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EditarVisita.this, "Edição salva com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarVisita.this, "Falha ao salvar edição", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void fecharTeclado(View view){
        InputMethodManager fecharTeclado = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        fecharTeclado.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
