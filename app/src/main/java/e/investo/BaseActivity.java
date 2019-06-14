package e.investo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import e.investo.conection.Conection;
import e.investo.lender.SelfLoanApplicationsListActivity;
import e.investo.login.LoginActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // TODO: adicionar nome do usuário logado
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {

            case R.id.menu_self_investiments:
                intent = new Intent(getBaseContext(), SelfLoanApplicationsListActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_logout:
                Conection.logOut();
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
