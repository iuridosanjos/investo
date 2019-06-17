package e.investo.lender;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import e.investo.ILoanApplicationListSpecifier;
import e.investo.OnLoadCompletedEventListener;
import e.investo.R;
import e.investo.data.DataMocks;
import e.investo.data.LoanApplication;
import e.investo.data.User;
import e.investo.lender.adapter.LoanApplicationAdapter;

public class LoanApplicationsListActivity extends BaseActivity {

    public static final String EXTRA_LIST_SPECIFIER = "ListSpecifier";

    private LoanApplication[] mLoans;
    private ListView mListView;
    TextView txtLoading;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<LoanApplication> ltLoanApplications = new ArrayList<LoanApplication>();
    ILoanApplicationListSpecifier mListSpecifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_applications_list);

        mListSpecifier = (ILoanApplicationListSpecifier) getIntent().getSerializableExtra(EXTRA_LIST_SPECIFIER);

       // mListSpecifier.SetPrefixMessage((TextView) findViewById(R.id.listLoans_prefix_message), getBaseContext());

        mListSpecifier.SetOnLoadCompletedEventListener(new OnLoadCompletedEventListener() {
            @Override
            public void OnLoadCompleted(Object result) {
                setupListView((List<LoanApplication>)result);
            }
        });

        mListView = findViewById(R.id.listLoans);
        txtLoading = (TextView) findViewById(R.id.txtLoading);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LoanApplication loan = (LoanApplication) adapterView.getItemAtPosition(i);
                mListSpecifier.OnClick(getBaseContext(), loan);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        mListSpecifier.BeginGetLoanApplications(getBaseContext());
    }

    private void setupListView(List<LoanApplication> loanApplications) {

        if (loanApplications == null || loanApplications.size() == 0) {
            txtLoading.setText(getString(R.string.no_loan_applications_found));
        } else {

            BaseAdapter adapter = mListSpecifier.GetAdapter(getBaseContext(), loanApplications);
            mListView.setAdapter(adapter);

            txtLoading.setVisibility(View.GONE);
        }
    }
}
