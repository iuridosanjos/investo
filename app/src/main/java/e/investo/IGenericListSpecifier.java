package e.investo;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public interface IGenericListSpecifier {

    void SetPrefixMessage(TextView textView, TextView txtPrefixSubMessage, Context context);

    void OnCreate(final Context context, ViewGroup rootContainer);

    void LoadDataAsync(final Context context);

    void SetOnLoadCompletedEventListener(OnLoadCompletedEventListener listener);

    BaseAdapter GetAdapter(Context context, List<Object> itemList);

    void OnClick(Context context, Object item);
}
