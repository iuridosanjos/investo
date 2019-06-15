package e.investo.borrower;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import e.investo.BaseActivity;
import e.investo.R;
import e.investo.common.CommonConstants;
import e.investo.common.CommonFormats;
import e.investo.data.DataMocks;
import e.investo.data.LoanApplication;
import e.investo.data.SystemInfo;
import e.investo.data.User;

public class CreateLoanApplication extends BaseActivity {

    private static final int MINIMUM_VALUE_INCREMENT_REQUESTED_VALUE = 50;

    EditText editTextEstablishmentName;
    EditText editTextEstablishmentType;
    EditText editTextAddress;
    SeekBar seekBarRequestedValue;
    SeekBar seekBarParcelsAmount;
    TextView txtRequestedValue;
    TextView txtParcelsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_loan_application);

        editTextEstablishmentName = (EditText) findViewById(R.id.txtEstablishmentName);
        editTextEstablishmentType = (EditText) findViewById(R.id.txtEstablishmentType);
        editTextAddress = (EditText) findViewById(R.id.txtAddress);
        seekBarRequestedValue = (SeekBar) findViewById(R.id.seekBar);
        seekBarParcelsAmount = (SeekBar) findViewById(R.id.seekBarParcelsAmount);
        txtRequestedValue = (TextView) findViewById(R.id.txtRequestedValue);
        txtParcelsInfo = (TextView) findViewById(R.id.txtParcelsInfo);
        TextView txtMaxRequestedValue = (TextView) findViewById(R.id.txtMaxAmount);
        TextView txtMaxParcelsAmount = (TextView) findViewById(R.id.txtMaxParcelsAmount);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submit(v);
            }
        });

        seekBarRequestedValue.setMax((int)CommonConstants.MAX_POSSIBLE_LOAN_VALUE / MINIMUM_VALUE_INCREMENT_REQUESTED_VALUE);
        seekBarRequestedValue.setOnSeekBarChangeListener(seekBarRequestedValueChangeListener);
        txtMaxRequestedValue.setText(CommonFormats.CURRENCY_FORMAT.format(CommonConstants.MAX_POSSIBLE_LOAN_VALUE));
        updateRequestedValue(seekBarRequestedValue.getProgress());

        seekBarParcelsAmount.setMax(CommonConstants.MAX_POSSIBLE_PARCELS_AMOUNT - 1); // -1 porque o setMin(1) não pode ser usado
        seekBarParcelsAmount.setOnSeekBarChangeListener(seekBarParcelsAmountChangeListener);
        txtMaxParcelsAmount.setText(String.format("%sx", CommonConstants.MAX_POSSIBLE_PARCELS_AMOUNT));
        updateParcelsAmount(seekBarParcelsAmount.getProgress());
    }

    private void updateRequestedValue(int progress)
    {
        double value = (double)progress * MINIMUM_VALUE_INCREMENT_REQUESTED_VALUE;
        txtRequestedValue.setText(CommonFormats.CURRENCY_FORMAT.format(value));
    }
    private double getRequestedValue()
    {
        return (double)seekBarRequestedValue.getProgress() * MINIMUM_VALUE_INCREMENT_REQUESTED_VALUE;
    }

    private void updateParcelsAmount(int progress)
    {
        // Formato: 12x de R% 120,00
        int parcelsCount = progress + 1; // +1 para restaurar o -1 inicial
        double parcelsValue = getRequestedValue() / parcelsCount;

        txtParcelsInfo.setText(String.format("%sx de %s", parcelsCount, CommonFormats.CURRENCY_FORMAT.format(parcelsValue)));
    }
    private int getParcelsAmount()
    {
        return seekBarParcelsAmount.getProgress() + 1; // +1 para restaurar o -1 inicial
    }

    public void submit(View view)
    {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.Id = DataMocks.LOAN_APPLICATIONS.get(DataMocks.LOAN_APPLICATIONS.size() - 1).Id + 1;
        loanApplication.Owner = new User();
        loanApplication.Owner.Id = SystemInfo.Instance.LoggedUserID;
        loanApplication.Owner.Name = SystemInfo.Instance.LoggedUserName;
        loanApplication.MonthlyInterests = 0.55; // Em tese esse valor será dito apenas depois que a análise for concluída

        // Informações da empresa
        loanApplication.EstablishmentName = editTextEstablishmentName.getText().toString().trim();
        loanApplication.EstablishmentType = editTextEstablishmentType.getText().toString().trim();
        loanApplication.Address = editTextAddress.getText().toString().trim();

        // Informações do empréstimo
        loanApplication.RequestedValue = getRequestedValue();
        loanApplication.ParcelsAmount = getParcelsAmount();

        Toast.makeText(getBaseContext(), "Pedido de empréstimo criado!", Toast.LENGTH_LONG).show();
        DataMocks.LOAN_APPLICATIONS.add(loanApplication);
    }

    SeekBar.OnSeekBarChangeListener seekBarRequestedValueChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            updateRequestedValue(progress);
            // Atualiza também o valor das parcelas, pois depende do valor requisitado
            updateParcelsAmount(seekBarParcelsAmount.getProgress());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarParcelsAmountChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            updateParcelsAmount(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };
}
