package e.investo;

import android.content.Context;
import android.content.res.Resources;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import e.investo.data.LoanApplication;

public interface ILoanApplicationListSpecifier {

    void SetPrefixMessage(TextView textView, Context context);

    void BeginGetLoanApplications(final Context context);

    void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener);

    BaseAdapter GetAdapter(Context context, List<LoanApplication> loanApplicationList);

    void OnClick(Context context, LoanApplication loanApplication);
}
