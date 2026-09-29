import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionRowValidatorTest {
    public static void main(String[] args) {
        acceptsValidRow();
        rejectsMissingField();
        rejectsExtraField();
        rejectsBlankId();
        rejectsInvalidBatch();
        rejectsBlankBatch();
        rejectsBatchOverflow();
        rejectsInvalidDate();
        rejectsBlankDate();
        rejectsInvalidAccount();
        rejectsBlankAccount();
        rejectsInvalidAmount();
        rejectsBlankAmount();
        acceptsNegativeAmount();
        acceptsZeroAmount();
        acceptsBlankMerchantAndMemo();
        System.out.println("PASS: all 16 row validation tests");
    }

    private static void acceptsValidRow() {
        var actual = TransactionRowValidator.validate(validFields(), 2);
        var expected = new TransactionRow("txn_1", 2, LocalDate.of(2026, 8, 3),
                AccountType.CARD, "SYSCO", "memo", new BigDecimal("12.30"));
        check(actual.equals(expected), "typed transaction with exact decimal amount");
    }

    private static void rejectsMissingField() {
        var fields = validFields();
        fields.remove(6);
        rejects(fields, "columns");
    }

    private static void rejectsExtraField() {
        var fields = validFields();
        fields.add("extra");
        rejects(fields, "columns");
    }

    private static void rejectsBlankId() {
        rejects(withField(0, " "), "id");
    }

    private static void rejectsInvalidBatch() {
        rejects(withField(1, "1.5"), "sync_batch");
    }

    private static void rejectsBlankBatch() {
        rejects(withField(1, ""), "sync_batch");
    }

    private static void rejectsBatchOverflow() {
        rejects(withField(1, "2147483648"), "sync_batch");
    }

    private static void rejectsInvalidDate() {
        rejects(withField(2, "2026-02-30"), "date");
    }

    private static void rejectsBlankDate() {
        rejects(withField(2, ""), "date");
    }

    private static void rejectsInvalidAccount() {
        rejects(withField(3, "cash"), "account");
    }

    private static void rejectsBlankAccount() {
        rejects(withField(3, ""), "account");
    }

    private static void rejectsInvalidAmount() {
        rejects(withField(6, "NaN"), "amount");
    }

    private static void rejectsBlankAmount() {
        rejects(withField(6, ""), "amount");
    }

    private static void acceptsNegativeAmount() {
        var row = TransactionRowValidator.validate(withField(6, "-12.30"), 2);
        check(row.amount().equals(new BigDecimal("-12.30")), "negative amount");
    }

    private static void acceptsZeroAmount() {
        var row = TransactionRowValidator.validate(withField(6, "0"), 2);
        check(row.amount().equals(BigDecimal.ZERO), "zero amount");
    }

    private static void acceptsBlankMerchantAndMemo() {
        var fields = withField(4, "");
        fields.set(5, "");
        var row = TransactionRowValidator.validate(fields, 2);
        check(row.merchant().isEmpty() && row.memo().isEmpty(), "optional text fields");
    }

    private static List<String> validFields() {
        return new ArrayList<>(List.of("txn_1", "2", "2026-08-03", "card", "SYSCO", "memo", "12.30"));
    }

    private static List<String> withField(int index, String value) {
        var fields = validFields();
        fields.set(index, value);
        return fields;
    }

    private static void rejects(List<String> fields, String field) {
        try {
            TransactionRowValidator.validate(fields, 7);
        } catch (IllegalArgumentException e) {
            check(e.getMessage().startsWith("Row 7:") && e.getMessage().contains(field),
                    "error identifies row and " + field);
            return;
        }
        throw new AssertionError("Expected rejection for " + field);
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
    }
}
