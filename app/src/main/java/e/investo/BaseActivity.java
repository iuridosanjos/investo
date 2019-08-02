package e.investo;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import e.investo.borrower.BorrowerLoanApplicationsSpecifier;
import e.investo.connection.Connection;
import e.investo.data.LoggedUserInfo;
import e.investo.data.SystemInfo;
import e.investo.lender.ListAllLoanApplicationsSpecifier;
import e.investo.lender.SelfLoanDataSpecifier;
import e.investo.login.LoginActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayUseLogoEnabled(true);

        LoggedUserInfo loggedUserInfo = SystemInfo.Instance.getLoggedUserInfo(BaseActivity.this);

        MenuItem menuItem = menu.findItem(R.id.menu_user_profile);
        if (loggedUserInfo.Name != null)
            menuItem.setTitle(loggedUserInfo.Name.substring(0, loggedUserInfo.Name.indexOf(' ')));
        if (loggedUserInfo.Photo != null)
            menuItem.setIcon(new BitmapDrawable(getResources(), loggedUserInfo.Photo));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {

            case R.id.menu_self_investiments:
                intent = new Intent(getBaseContext(), GenericListActivity.class);
                intent.putExtra(GenericListActivity.EXTRA_LIST_SPECIFIER, new SelfLoanDataSpecifier());
                startActivity(intent);
                return true;

            case R.id.menu_self_loan_applications:
                intent = new Intent(getBaseContext(), GenericListActivity.class);
                intent.putExtra(GenericListActivity.EXTRA_LIST_SPECIFIER, new BorrowerLoanApplicationsSpecifier());
                startActivity(intent);
                return true;

            case R.id.menu_list_all_loan_applications:
                intent = new Intent(getBaseContext(), GenericListActivity.class);
                intent.putExtra(GenericListActivity.EXTRA_LIST_SPECIFIER, new ListAllLoanApplicationsSpecifier());
                startActivity(intent);
                return true;

            case R.id.menu_logout:
                SystemInfo.Instance.logOut(getBaseContext());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
