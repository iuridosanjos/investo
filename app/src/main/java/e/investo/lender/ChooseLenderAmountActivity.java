package e.investo.lender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import e.investo.R;
import e.investo.common.CommonFormats;

public class ChooseLenderAmountActivity extends AppCompatActivity {

    public static final String EXTRA_MAX_AMOUNT = "MaxAmount";

    TextView txtLoanAmount;
    double maxAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_lender_amount);

        maxAmount = getIntent().getDoubleExtra(EXTRA_MAX_AMOUNT, 0);
        txtLoanAmount = findViewById(R.id.txtAmount);

        TextView txtMaxLoanAmount = findViewById(R.id.txtMaxAmount);
        txtMaxLoanAmount.setText(CommonFormats.CURRENCY_FORMAT.format(maxAmount));

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar.setMax((int)(maxAmount * 100));

        int progress = seekBar.getProgress();
        updateLendAmount(progress);
    }

    private void updateLendAmount(int progress)
    {
        double value = (double)progress / 100;
        txtLoanAmount.setText(CommonFormats.CURRENCY_FORMAT.format(value));
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            updateLendAmount(progress);
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
