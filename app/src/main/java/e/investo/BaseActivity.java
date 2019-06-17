package e.investo;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import e.investo.conection.Connection;
import e.investo.data.SystemInfo;
import e.investo.lender.LoanApplicationsListActivity;
import e.investo.lender.SelfLoanApplicationsListActivity;
import e.investo.lender.SelfLoanApplicationsSpecifier;
import e.investo.login.LoginActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayUseLogoEnabled(true);

        MenuItem menuItem = menu.findItem(R.id.menu_user_profile);
        if (SystemInfo.Instance.LoggedUserName != null)
            menuItem.setTitle(SystemInfo.Instance.LoggedUserName.substring(0, SystemInfo.Instance.LoggedUserName.indexOf(' ')));
        if (SystemInfo.Instance.LoggedUserPhoto != null)
            menuItem.setIcon(new BitmapDrawable(getResources(), SystemInfo.Instance.LoggedUserPhoto));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {

            case R.id.menu_self_investiments:
                intent = new Intent(getBaseContext(), LoanApplicationsListActivity.class);
                intent.putExtra(LoanApplicationsListActivity.EXTRA_LIST_SPECIFIER, new SelfLoanApplicationsSpecifier());
                startActivity(intent);
                return true;

            case R.id.menu_logout:
                Connection.logOut();
                intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
