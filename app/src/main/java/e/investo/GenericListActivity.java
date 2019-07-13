package e.investo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import e.investo.data.LoanApplication;

public class GenericListActivity extends BaseActivity {

    public static final String EXTRA_LIST_SPECIFIER = "ListSpecifier";

    private ListView mListView;
    TextView txtLoading;
    IGenericListSpecifier mListSpecifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_applications_list);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.hide();

        mListSpecifier = (IGenericListSpecifier) getIntent().getSerializableExtra(EXTRA_LIST_SPECIFIER);

        mListSpecifier.OnCreate(getBaseContext(), (ViewGroup) findViewById(R.id.list_root_container));

        mListSpecifier.SetPrefixMessage((TextView) findViewById(R.id.list_prefix_message), getBaseContext());

        mListSpecifier.SetOnLoadCompletedEventListener(new OnLoadCompletedEventListener() {
            @Override
            public void OnLoadCompleted(Object result) {
                setupListView((List<Object>) result);
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

        txtLoading.setText(getString(R.string.loading));
        txtLoading.setVisibility(View.VISIBLE);
        mListSpecifier.LoadDataAsync(getBaseContext());
    }

    private void setupListView(List<Object> itemList) {

        if (itemList == null || itemList.size() == 0) {
            txtLoading.setText(getString(R.string.no_loan_applications_found));
            mListView.setVisibility(View.GONE);
            txtLoading.setVisibility(View.VISIBLE);
        } else {

            BaseAdapter adapter = mListSpecifier.GetAdapter(getBaseContext(), itemList);
            mListView.setAdapter(adapter);

            txtLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }
}
