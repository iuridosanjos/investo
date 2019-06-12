package e.investo.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import e.investo.R;
import e.investo.conection.Conection;

public class Register extends AppCompatActivity {

    private EditText editEmail, editcpfCnpj, editSenha;
    private Button btnRegistrar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inicializarCmp();
        eventoClicks();
    }

    private void eventoClicks() {
       /* btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); */
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String email = editEmail.getText().toString().trim();
               //String cpfCnpj = editcpfCnpj.getText().toString().trim();
               String senha = editSenha.getText().toString().trim();
               criarUser(email,senha);

            }
        });
    }

    private void criarUser(String email,String senha) {
        auth.createUserWithEmailAndPassword(email,senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           alert("Usuário Cadastrado com Sucesso");
                           Intent i = new Intent(Register.this, Profile.class);
                           startActivity(i);
                           finish();
                       }else{
                           alert("Erro de Cadastro");
                       }
                    }
                });
    }

    private void alert(String msg){

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void inicializarCmp() {

        editEmail = (EditText) findViewById(R.id.emailRegister);
        editcpfCnpj = (EditText) findViewById(R.id.cpf_cnpj);
        editSenha = (EditText) findViewById(R.id.senhaCadastro);
        btnRegistrar = (Button) findViewById(R.id.register);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Conection.getFirebaseAuth();
    }
}
