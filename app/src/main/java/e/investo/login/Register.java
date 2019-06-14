package e.investo.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;

import e.investo.R;
import e.investo.conection.Conection;

public class Register extends AppCompatActivity {

    static  int REQUESCODE = 1;
    private EditText editEmail, editNome, editSenha, editConfSenha;
    private ImageView imguserPhoto;
    private Button btnRegistrar;
    private FirebaseAuth auth;
    Uri prickedImg;
    static  int PReqCode = 1;

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

       imguserPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if(Build.VERSION.SDK_INT >=22){

                   checkAndRequestPorPermission();
               }else{
                    openGallery();
               }
           }
       });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String email = editEmail.getText().toString().trim();
               String nome = editNome.getText().toString().trim();
               //String cpfCnpj = editcpfCnpj.getText().toString().trim();
               String senha = editSenha.getText().toString().trim();
               String confirmaSenha = editConfSenha.getText().toString().trim();
               criarUser(nome, email, senha, confirmaSenha);

            }
        });
    }

    private void openGallery() {

        Intent galleryItent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryItent.setType("image/*");
        startActivityForResult(galleryItent,REQUESCODE);



    }

    private void checkAndRequestPorPermission() {

        if(ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(Register.this,"Por favor Aceite a Permissão", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(Register.this,
                                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    PReqCode);
            }
        }
        else{
            openGallery();
        }
    }

    private void criarUser(String nome, String email, String senha, String confirmaSenha) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
            prickedImg = data.getData();
            imguserPhoto.setImageURI(prickedImg);

        }
    }

    private void inicializarCmp() {

        editEmail = (EditText) findViewById(R.id.emailRegister);
        //editcpfCnpj = (EditText) findViewById(R.id.cpf_cnpj);
        editNome = (EditText) findViewById(R.id.nome);
        editSenha = (EditText) findViewById(R.id.senhaCadastro);
        editConfSenha = (EditText) findViewById(R.id.confSenha);
        btnRegistrar = (Button) findViewById(R.id.register);
        imguserPhoto = (ImageView) findViewById(R.id.userphoto);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Conection.getFirebaseAuth();
    }
}
