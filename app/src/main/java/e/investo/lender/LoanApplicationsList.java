package e.investo.lender;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import e.investo.R;
import e.investo.data.LoanApplication;
import e.investo.lender.adapter.LoanApplicationAdapter;

public class LoanApplicationsList extends AppCompatActivity {

    private LoanApplication[] mLoans;
    private ListView mListView;

    private static final String[] DUMMY_LOAN_APPLICATIONS = new String[]{
            "Java Café@Rua Embargador do Sol, Boa Viagem@5000",
            "Itália em casa (Delivery)@Av. Conde da Lua, Graças@1700"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_applications_list);
    }

    @Override
    public void onStart() {
        super.onStart();

        mListView = findViewById(R.id.listLoans);

        new SyncDataTask().execute();
    }

    private void loadData() {
        mLoans = new LoanApplication[DUMMY_LOAN_APPLICATIONS.length];

        for (int i = 0; i < DUMMY_LOAN_APPLICATIONS.length; i++)
        {
            LoanApplication loan = new LoanApplication();
            loan.Id = i+1;

            String[] split = DUMMY_LOAN_APPLICATIONS[i].split("@");
            loan.EstablishmentName = split[0];
            loan.Address = split[1];
            loan.RequestedValue = Double.parseDouble(split[2]);

            mLoans[i] = loan;
        }
    }

    private void setupListView() {
        TextView textLoading = (TextView)findViewById(R.id.txtLoading);

        // Se não tiver nada pra listar, atualiza a tela para deixar isso claro
        if (mLoans == null || mLoans.length == 0)
        {
            textLoading.setText(getString(R.string.no_loan_applications_found));
        }
        else
        {
            textLoading.setVisibility(View.INVISIBLE);

            LoanApplicationAdapter mHotelsAdapter = new LoanApplicationAdapter(getBaseContext(), mLoans);

            mListView.setAdapter(mHotelsAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LoanApplication loan = (LoanApplication) adapterView.getItemAtPosition(i);

                    Intent it = new Intent(getBaseContext(), LoanApplicationDetail.class);
                    it.putExtra(LoanApplicationDetail.EXTRA_LOAN_APPLICATION_ITEM, loan);

                    startActivity(it);
                }
            });
        }
    }

    private class SyncDataTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Object doInBackground(Object... params) {
            //load data in backgroud
            loadData();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            setupListView();
        }
    }
}
