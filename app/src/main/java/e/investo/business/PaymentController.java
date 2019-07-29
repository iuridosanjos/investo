package e.investo.business;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import e.investo.common.CommonConversions;
import e.investo.common.DateUtils;
import e.investo.data.LoanApplication;
import e.investo.data.LoanData;
import e.investo.data.PaymentData;
import e.investo.data.PaymentParcel;
import e.investo.data.PaymentParcelUnion;

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

    public static double getPaymentAdjustedTotalValue(LoanApplication loanApplication) {
        double value = loanApplication.RequestedValue;
        double interests = loanApplication.MonthlyInterests;
        int parcelsAmount = loanApplication.ParcelsAmount;
        return getPaymentAdjustedValue(value, interests, parcelsAmount);
    }
    public static double getPaymentAdjustedValue(double value, double interests, int parcelsAmount) {
        return value * (1 + interests * parcelsAmount);
    }

    public static PaymentData getPaymentData(@NonNull LoanApplication loanApplication, @NonNull LoanData loanData) {
        PaymentData paymentData = new PaymentData();
        paymentData.id = UUID.randomUUID().toString();
        paymentData.loanApplicationId = loanData.idApplication;
        paymentData.loanDataId = loanData.id;
        paymentData.payerUserId = loanData.idUser;
        paymentData.parcels = new ArrayList<>();

        Date firstDueDate = getFirstParcelDueDate(loanApplication);
        double loanDataAdjustedValue = getPaymentAdjustedValue(loanData.value, loanApplication.MonthlyInterests, loanApplication.ParcelsAmount);
        double parcelValue = CommonConversions.roundFloor(loanDataAdjustedValue / loanApplication.ParcelsAmount, 2);
        double lastParcelValue = CommonConversions.roundFloor(loanDataAdjustedValue - ((loanApplication.ParcelsAmount - 1) * parcelValue), 2);

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

    public static List<PaymentParcelUnion> unionMultiplePayments(int parcelsAmount, List<PaymentData> list) throws Exception {
        if (list == null || list.size() == 0)
            return null;
        else {
            for (PaymentData paymentData : list) {
                if (paymentData.parcels == null || paymentData.parcels.size() != parcelsAmount)
                    throw new Exception("Há quantidade de parcelas diferentes");
            }
        }

        List<PaymentParcelUnion> result = new ArrayList<>();

        for (int number = 0; number < parcelsAmount; number++) {

            Map<PaymentParcelKey, List<PaymentParcel>> parcelsMap = groupParcelsByDueDateAndPayday(list, number);

            for (Map.Entry<PaymentParcelKey, List<PaymentParcel>> pair : parcelsMap.entrySet()) {
                PaymentParcel unionParcel = new PaymentParcel();
                unionParcel.dueDateLong = pair.getKey().dueDate;
                unionParcel.paydayLong = pair.getKey().payDay;
                unionParcel.number = number;

                List<PaymentParcel> originalParcels = new ArrayList<>();

                unionParcel.value = 0;
                for (PaymentParcel parcel : pair.getValue()) {
                    unionParcel.value += parcel.value;
                    originalParcels.add(parcel);
                }

                PaymentParcelUnion parcelUnion = new PaymentParcelUnion();
                parcelUnion.uniqueParcel = unionParcel;
                parcelUnion.originalParcels = originalParcels;

                result.add(parcelUnion);
            }
        }

        return result;
    }
    private static Map<PaymentParcelKey, List<PaymentParcel>> groupParcelsByDueDateAndPayday(List<PaymentData> list, int parcelNumber) {
        Map<PaymentParcelKey, List<PaymentParcel>> map = new HashMap<>();

        for (PaymentData paymentData : list) {
            PaymentParcel parcel = paymentData.parcels.get(parcelNumber);

            PaymentParcelKey key = new PaymentParcelKey(parcel.dueDateLong, parcel.paydayLong);
            if (!map.containsKey(key))
                map.put(key, new ArrayList<PaymentParcel>());

            map.get(key).add(parcel);
        }

        return map;
    }

    private static class PaymentParcelKey {
        PaymentParcelKey(long dueDate, long payDay) {
            this.dueDate = dueDate;
            this.payDay = payDay;
        }

        public long dueDate;
        public long payDay;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof PaymentParcelKey) {
                PaymentParcelKey other = (PaymentParcelKey)obj;
                return dueDate == other.dueDate && payDay == other.payDay;
            }
            return false;
        }

        @Override
        public int hashCode() {
            // Deveria usar Long.hashCode, porém a API 23 não suporta
            return longToIntHashCode(dueDate) + 31 * longToIntHashCode(payDay);
        }

        private int longToIntHashCode(long value) {
            return (int)(value^(value>>>32));        }
    }
}
