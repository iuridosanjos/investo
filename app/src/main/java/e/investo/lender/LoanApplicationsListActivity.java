package e.investo.lender;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import e.investo.BaseActivity;
import e.investo.R;
import e.investo.data.DataMocks;
import e.investo.data.LoanApplication;
import e.investo.data.User;
import e.investo.lender.adapter.LoanApplicationAdapter;

public class LoanApplicationsListActivity extends BaseActivity {

    private LoanApplication[] mLoans;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_applications_list);
    }

    @Override
    public void onStart() {
        super.onStart();

        mListView = findViewById(R.id.listLoans);

        new AsyncDataTask().execute();
    }

    private void loadData() {
        mLoans = new LoanApplication[DataMocks.LOAN_APPLICATIONS.size()];

        for (int i = 0; i < DataMocks.LOAN_APPLICATIONS.size(); i++)
            mLoans[i] = DataMocks.LOAN_APPLICATIONS.get(i);
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

            LoanApplicationAdapter mLoanAdapter = new LoanApplicationAdapter(getBaseContext(), mLoans);

            mListView.setAdapter(mLoanAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LoanApplication loan = (LoanApplication) adapterView.getItemAtPosition(i);

                    Intent it = new Intent(getBaseContext(), LoanApplicationDetailActivity.class);
                    it.putExtra(LoanApplicationDetailActivity.EXTRA_LOAN_APPLICATION_ITEM, loan);

                    startActivity(it);
                }
            });
        }
    }

    private class AsyncDataTask extends AsyncTask<Object, Object, Object> {

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
