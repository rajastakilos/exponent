import java.util.Locale;

public enum AccountType {
    CARD,
    BANK;

    public static AccountType fromCsv(String value) {
        return AccountType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}