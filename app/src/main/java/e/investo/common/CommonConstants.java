package e.investo.common;

public class CommonConstants {
    public static String LOG_TAG = "investo";

    // Valor máximo que pode ser pedido por empréstimo P2P
    public static final double REQUESTED_VALUE_MAX_VALUE = 15000;

    // Incremento do valor requisitado
    public static final int REQUESTED_VALUE_INCREMENT_VALUE = 100;

    // Número máximo de parcelas
    public static final int PARCELS_AMOUNT_MAX_VALUE = 36;

    // Dia do vencimento máximo
    public static final int DUE_DAY_MAX_VALUE = 25;
    // Dia do vencimento máximo
    public static final int DUE_DAY_MIN_VALUE = 5;
    // Incremento para o dia de vencimento
    public static final int DUE_DAY_INCREMENT_VALUE = 5;

    public static final int EXPIRATION_DAYS_MIN_VALUE = 15;
    public static final int EXPIRATION_DAYS_MAX_VALUE = 60;
    public static final int EXPIRATION_DAYS_INCREMENT_VALUE = 15;

    // Dados de parcelas de pagamento
    public static final int DUE_DATE_MISSING_DAYS_ALERT = 5;
}
