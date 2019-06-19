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

import e.investo.conection.Connection;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import e.investo.R;
import e.investo.data.MaskType;
import e.investo.data.MaskUtil;
import e.investo.data.SystemInfo;
import e.investo.data.User;

public class RegisterActivity extends AppCompatActivity {

    static int REQUESCODE = 1;
    private EditText editEmail, editNome, editCpf, editSenha, editConfSenha;
    private ProgressBar loadingprogress;
    private ImageView imguserPhoto;
    private Button btnRegistrar;
    private FirebaseAuth auth;
    Uri prickedImg;
    static int PReqCode = 1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

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

                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestPorPermission();
                } else {
                    openGallery();
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingprogress.setVisibility(View.VISIBLE);
                btnRegistrar.setVisibility(View.INVISIBLE);

                String email = editEmail.getText().toString().trim();
                String nome = editNome.getText().toString().trim();
                String cpf = editCpf.getText().toString().trim();
                String senha = editSenha.getText().toString().trim();
                String confirmaSenha = editConfSenha.getText().toString().trim();


                if (email.isEmpty() || cpf.isEmpty() || nome.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
                    showMessage("Por favor Verifique todos os campos");
                    btnRegistrar.setVisibility(View.VISIBLE);
                    loadingprogress.setVisibility(View.INVISIBLE);
                } else {
                    criarUser(nome, email, senha, confirmaSenha);

                    User user = new User();
                    user.setId(UUID.randomUUID().toString());
                    user.setName(nome);
                    user.setCpfUser(cpf);
                    Connection.GetDatabaseReference().child("Usuario").child(auth.getCurrentUser().getUid()).setValue(user);
                }
            }
        });
    }

    private void showMessage(String verify) {
        Toast.makeText(getApplicationContext(), verify, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {

        Intent galleryItent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryItent.setType("image/*");
        startActivityForResult(galleryItent, REQUESCODE);


    }

    private void checkAndRequestPorPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this, "Por favor Aceite a Permissão", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery();
        }
    }

    private void criarUser(final String nome, String email, String senha, String confirmaSenha) {


        auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // updateUserInfo(nome, prickedImg, auth.getCurrentUser());

                            SystemInfo.Instance.Update(Connection.getFirebaseUser(), getContentResolver());

                            //Intent i = new Intent(RegisterActivity.this, Profile.class);
                            Intent i = new Intent(RegisterActivity.this, SelectProfileViewActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            alert("Erro de Cadastro" + task.getException().getMessage());
                            btnRegistrar.setVisibility(View.VISIBLE);
                            loadingprogress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void updateUserInfo(final String nome, final Uri prickedImg, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imgFilePath = mStorage.child(prickedImg.getLastPathSegment());
        imgFilePath.putFile(prickedImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nome)
                                .setPhotoUri(prickedImg)
                                .build();

                        currentUser.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        alert("Usuário Cadastrado com Sucesso");
                                    }
                                });

                    }
                });
            }
        });
    }

    private void alert(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            prickedImg = data.getData();
            imguserPhoto.setImageURI(prickedImg);

        }
    }

    private void inicializarCmp() {

        editEmail = (EditText) findViewById(R.id.emailRegister);
        //
        editNome = (EditText) findViewById(R.id.nome);
        editCpf = (EditText) findViewById(R.id.cpf);
        editCpf.addTextChangedListener(MaskUtil.insert(editCpf, MaskType.CPF));
        editSenha = (EditText) findViewById(R.id.senhaCadastro);
        editConfSenha = (EditText) findViewById(R.id.confSenha);
        btnRegistrar = (Button) findViewById(R.id.register);
        imguserPhoto = (ImageView) findViewById(R.id.userphoto);
        loadingprogress = (ProgressBar) findViewById(R.id.loadingprogress);

        loadingprogress.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
    }
}
