package e.investo.business;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import e.investo.common.CommonConversions;
import e.investo.common.DateUtils;
import e.investo.data.LoanApplication;
import e.investo.data.LoanData;
import e.investo.data.PaymentData;
import e.investo.data.PaymentParcel;

public class PaymentController {
    public static double getMonthlyInterests(int parcels) {
        if (parcels <= 6)
            return 0.0084;
        else if (parcels <= 18)
            return 0.0105;
        else if (parcels <= 36)
            return 0.0124;
        else
            return 0.0148;
    }

    public static PaymentData getPaymentData(@NonNull LoanApplication loanApplication, @NonNull LoanData loanData) {
        PaymentData paymentData = new PaymentData();
        paymentData.id = UUID.randomUUID().toString();
        paymentData.loanApplicationId = loanData.idApplication;
        paymentData.loanDataId = loanData.id;
        paymentData.payerUserId = loanData.idUser;
        paymentData.parcels = new ArrayList<>();

        Date firstDueDate = getFirstParcelDueDate(loanApplication);
        double parcelValue = CommonConversions.roundFloor(loanData.value / loanApplication.ParcelsAmount, 2);
        double lastParcelValue = CommonConversions.roundFloor(loanData.value - ((loanApplication.ParcelsAmount - 1) * parcelValue), 2);

        for (int number = 0; number < loanApplication.ParcelsAmount; number++)
        {
            PaymentParcel parcel = new PaymentParcel();
            parcel.number = number;
            parcel.setDueDate(addMonthTo(firstDueDate, number));
            parcel.setPayday(null);

            if (number == loanApplication.ParcelsAmount - 1) // Última parcela deve ter o valor restante
                parcel.value = lastParcelValue;
            else
                parcel.value = parcelValue;

            paymentData.parcels.add(parcel);
        }

        return paymentData;
    }
    private static Date addMonthTo(Date date, int month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);

        return calendar.getTime();
    }

    public static Date getFirstParcelDueDate(LoanApplication loanApplication)
    {
        Date expirationDate = loanApplication.getExpirationDate();
        if (expirationDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(loanApplication.getCreationDate());
            calendar.add(Calendar.DAY_OF_MONTH, 30); // Valor padrão: 30 dias da data de criação
            DateUtils.clearTime(calendar);
            expirationDate = calendar.getTime();
        }

        int dueDay = loanApplication.DueDay;
        if (dueDay <= 0)
            dueDay = 5; // Valor padrão

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expirationDate);
        calendar.set(Calendar.DAY_OF_MONTH, dueDay);
        // Limpa todos os valores de hora, pois estamos interessados apenas na data
        DateUtils.clearTime(calendar);

        // Se o dia de expiração já passou do dia do vencimento desejado, então será considerado 2 meses à frente. Caso contrário, 1 mês.
        if (DateUtils.getDay(expirationDate) >= dueDay)
            calendar.add(Calendar.MONTH, 2);
        else
            calendar.add(Calendar.MONTH, 1);

        return calendar.getTime();
    }

    public static List<PaymentParcel> unionMultiplePayments(int parcelsAmount, List<PaymentData> list) {
        if (list == null || list.size() == 0)
            return null;
        else {
            for (PaymentData paymentData : list) {
                if (paymentData.parcels == null || paymentData.parcels.size() != parcelsAmount)
                    return null; // Algum erro
            }
        }

        List<PaymentParcel> result = new ArrayList<>();

        for (int number = 0; number < parcelsAmount; number++) {

            PaymentParcel targetParcel = list.get(0).parcels.get(number);

            PaymentParcel paymentParcel = new PaymentParcel();
            paymentParcel.dueDateLong = targetParcel.dueDateLong;
            paymentParcel.paydayLong = targetParcel.paydayLong;
            paymentParcel.number = targetParcel.number;

            paymentParcel.value = 0;
            for (PaymentData paymentData : list)
                paymentParcel.value += paymentData.parcels.get(number).value;

            result.add(paymentParcel);
        }

        return result;
    }
}
