package e.investo.lender;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import e.investo.BaseActivity;
import e.investo.R;
import e.investo.data.DataMocks;
import e.investo.data.LoanApplication;
import e.investo.lender.adapter.LoanApplicationAdapter;
import e.investo.lender.adapter.SelfLoanApplicationAdapter;

public class SelfLoanApplicationsListActivity extends BaseActivity {

    private LoanApplication[] mLoans;
    private ListView mListView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<LoanApplication> ltLoanApplications = new ArrayList<LoanApplication>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_loan_applications_list);
        inicializarFirabase();
    }

    @Override
    public void onStart() {
        super.onStart();

        mListView = findViewById(R.id.listLoans);

        new AsyncDataTask().execute();
    }
/*
    private void loadData() {
        mLoans = new LoanApplication[DataMocks.LOGGED_USER_LOAN_APPLICATIONS.size()];

        for (int i = 0; i < DataMocks.LOGGED_USER_LOAN_APPLICATIONS.size(); i++)
            mLoans[i] = DataMocks.LOGGED_USER_LOAN_APPLICATIONS.get(i);
    }
*/

    private void inicializarFirabase() {

        firebaseDatabase = firebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private void setupListView() {


        databaseReference.child("Investimentos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView textLoading = (TextView)findViewById(R.id.txtLoading);
                textLoading.setVisibility(View.INVISIBLE);

                for(DataSnapshot objSnapshot: dataSnapshot.getChildren()){
                    LoanApplication loanApplication = objSnapshot.getValue(LoanApplication.class);
                    ltLoanApplications.add(loanApplication);
                }

                mLoans = new LoanApplication[ltLoanApplications.size()];
                if (mLoans == null || mLoans.length == 0)
                {
                    textLoading.setText(getString(R.string.no_loan_applications_found));
                }
                else
                {
                    for (int i = 0; i < ltLoanApplications.size(); i++)
                        mLoans[i] = ltLoanApplications.get(i);
                    SelfLoanApplicationAdapter mLoanAdapter = new SelfLoanApplicationAdapter(getBaseContext(), mLoans);

                    mListView.setAdapter(mLoanAdapter);

            /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LoanApplication loan = (LoanApplication) adapterView.getItemAtPosition(i);

                    Intent it = new Intent(getBaseContext(), LoanApplicationDetailActivity.class);
                    it.putExtra(LoanApplicationDetailActivity.EXTRA_LOAN_APPLICATION_ITEM, loan);

                    startActivity(it);
                }
            });*/
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Se não tiver nada pra listar, atualiza a tela para deixar isso claro

    }

    private class AsyncDataTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Object doInBackground(Object... params) {
            //load data in backgroud
           // loadData();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            setupListView();
        }
    }
}
