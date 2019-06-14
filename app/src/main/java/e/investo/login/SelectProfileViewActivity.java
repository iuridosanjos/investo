package e.investo.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import e.investo.BaseActivity;
import e.investo.R;
import e.investo.lender.LoanApplicationsListActivity;

public class SelectProfileViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile_view);

    }

    public void onClickLenderProfile(View v){
        startActivity(new Intent(this, LoanApplicationsListActivity.class));
    }

}
