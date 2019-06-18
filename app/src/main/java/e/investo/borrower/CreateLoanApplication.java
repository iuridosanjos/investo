package e.investo.borrower;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import e.investo.BaseActivity;
import e.investo.R;
import e.investo.common.CommonConstants;
import e.investo.common.CommonFormats;
import e.investo.conection.Connection;
import e.investo.data.LoanApplication;
import e.investo.data.MaskType;
import e.investo.data.MaskUtil;
import e.investo.data.SystemInfo;
import e.investo.data.User;

public class CreateLoanApplication extends BaseActivity {

    private static final int MINIMUM_VALUE_INCREMENT_REQUESTED_VALUE = 50;

    EditText editTextEstablishmentName;
    EditText editTextCNPJ;
    EditText editTextEstablishmentType;
    EditText editTextAddress;
    SeekBar seekBarRequestedValue;
    SeekBar seekBarParcelsAmount;
    TextView txtRequestedValue;
    TextView txtParcelsInfo;
    TextView txtMontlyInterests;
    TextView txtFinalValueAfterTaxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_loan_application);

        editTextEstablishmentName = (EditText) findViewById(R.id.txtEstablishmentName);
        editTextCNPJ = (EditText) findViewById(R.id.txtCNPJ);
        editTextEstablishmentType = (EditText) findViewById(R.id.txtEstablishmentType);
        editTextAddress = (EditText) findViewById(R.id.txtAddress);
        seekBarRequestedValue = (SeekBar) findViewById(R.id.seekBar);
        seekBarParcelsAmount = (SeekBar) findViewById(R.id.seekBarParcelsAmount);
        txtRequestedValue = (TextView) findViewById(R.id.txtRequestedValue);
        txtParcelsInfo = (TextView) findViewById(R.id.txtParcelsInfo);
        txtMontlyInterests = (TextView) findViewById(R.id.txtMonthlyInterests);
        txtFinalValueAfterTaxes = (TextView) findViewById(R.id.txtFinalValueAfterTaxes);

        // Aplica a máscara de CNPJ
        editTextCNPJ.addTextChangedListener(MaskUtil.insert(editTextCNPJ, MaskType.CNPJ));

        updateMonthlyInterests();
        updateFinalValueAfterTaxes();

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
        TextView txtMaxRequestedValue = (TextView) findViewById(R.id.txtMaxAmount);
        txtMaxRequestedValue.setText(CommonFormats.CURRENCY_FORMAT.format(CommonConstants.MAX_POSSIBLE_LOAN_VALUE));
        updateRequestedValue(seekBarRequestedValue.getProgress());

        seekBarParcelsAmount.setMax(CommonConstants.MAX_POSSIBLE_PARCELS_AMOUNT - 1); // -1 porque o setMin(1) não pode ser usado
        seekBarParcelsAmount.setOnSeekBarChangeListener(seekBarParcelsAmountChangeListener);
        TextView txtMaxParcelsAmount = (TextView) findViewById(R.id.txtMaxParcelsAmount);
        txtMaxParcelsAmount.setText(String.format("%sx", CommonConstants.MAX_POSSIBLE_PARCELS_AMOUNT));
        updateParcelsAmount(seekBarParcelsAmount.getProgress());
    }

    private void updateMonthlyInterests()
    {
        // TODO: realizar regra simples para modificação dos juros
        double interests = 0.0055;
        txtMontlyInterests.setText(String.format("%s a.m.", CommonFormats.PERCENTAGE_FORMAT.format(interests * 100)));
    }
    private double getMonthlyInterests()
    {
        return 0.0055;
    }

    private void updateFinalValueAfterTaxes()
    {
        txtFinalValueAfterTaxes.setText(CommonFormats.CURRENCY_FORMAT.format(getFinalValueAfterTaxes()));
    }
    private double getFinalValueAfterTaxes()
    {
        double requestedValue = getRequestedValue();
        return requestedValue * (1 + getMonthlyInterests() * getParcelsAmount());
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
        double parcelsValue = getFinalValueAfterTaxes() / parcelsCount;

        txtParcelsInfo.setText(String.format("%sx de %s", parcelsCount, CommonFormats.CURRENCY_FORMAT.format(parcelsValue)));
    }
    private int getParcelsAmount()
    {
        return seekBarParcelsAmount.getProgress() + 1; // +1 para restaurar o -1 inicial
    }

    public void submit(View view)
    {
        if (!validateFields())
            return;

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setIdAplication(UUID.randomUUID().toString());
        loanApplication.Owner = new User();
        loanApplication.Owner.id = SystemInfo.Instance.LoggedUserID;
        loanApplication.Owner.name = SystemInfo.Instance.LoggedUserName;
        loanApplication.MonthlyInterests = 0.55; // Em tese esse valor será dito apenas depois que a análise for concluída

        // Informações da empresa
        loanApplication.EstablishmentName = editTextEstablishmentName.getText().toString().trim();
        loanApplication.CNPJ = editTextCNPJ.getText().toString().trim();
        loanApplication.EstablishmentType = editTextEstablishmentType.getText().toString().trim();
        loanApplication.Address = editTextAddress.getText().toString().trim();

        // Informações do empréstimo
        loanApplication.RequestedValue = getRequestedValue();
        loanApplication.ParcelsAmount = getParcelsAmount();

        Connection.GetDatabaseReference().child("Aplicacoes").child(loanApplication.getIdAplication()).setValue(loanApplication);
        Toast.makeText(getBaseContext(), "Pedido de empréstimo criado!", Toast.LENGTH_LONG).show();
    }

    private boolean validateFields()
    {
        boolean valid =
                notIsEmpty(editTextEstablishmentName) &&
                notIsEmpty(editTextCNPJ) &&
                notIsEmpty(editTextEstablishmentType) &&
                notIsEmpty(editTextAddress);

        if (!valid)
            Toast.makeText(getBaseContext(), getString(R.string.error_unfilled_fields), Toast.LENGTH_SHORT).show();

        if (valid) {
            if (getRequestedValue() == 0) {
                Toast.makeText(getBaseContext(), getString(R.string.error_requested_value_required), Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        return valid;
    }
    private boolean notIsEmpty(EditText editText)
    {
        String str = editText.getText().toString();
        if (str == null || str.trim().length() == 0) {
            editText.setError(getString(R.string.error_field_required));
            return false;
        }

        return true;
    }

    SeekBar.OnSeekBarChangeListener seekBarRequestedValueChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            updateRequestedValue(progress);
            // Atualiza também o valor das parcelas, pois depende do valor requisitado
            updateParcelsAmount(seekBarParcelsAmount.getProgress());
            updateMonthlyInterests();
            updateFinalValueAfterTaxes();
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

            // Atualiza as informações que dependem dela
            updateMonthlyInterests();
            updateFinalValueAfterTaxes();
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
