import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TransactionRowValidator {
    // Accepts decoded CSV fields; row numbers include the header.
    public static TransactionRow validate(List<String> fields, int rowNumber) {
        if (fields.size() != 7) {
            throw invalid(rowNumber, "columns", "expected 7, got " + fields.size());
        }
        String id = fields.get(0);
        if (id.isBlank()) throw invalid(rowNumber, "id", "must not be blank");

        int batch;
        try {
            batch = Integer.parseInt(fields.get(1));
        } catch (NumberFormatException e) {
            throw invalid(rowNumber, "sync_batch", "must be a 32-bit integer");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(fields.get(2));
        } catch (DateTimeParseException e) {
            throw invalid(rowNumber, "date", "must be a valid ISO date (YYYY-MM-DD)");
        }

        AccountType account;
        try {
            account = AccountType.fromCsv(fields.get(3));
        } catch (IllegalArgumentException e) {
            throw invalid(rowNumber, "account", "must be card or bank");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(fields.get(6));
        } catch (NumberFormatException e) {
            throw invalid(rowNumber, "amount", "must be a decimal number");
        }

        return new TransactionRow(id, batch, date, account, fields.get(4), fields.get(5), amount);
    }

    private static IllegalArgumentException invalid(int row, String field, String reason) {
        return new IllegalArgumentException("Row " + row + ": " + field + " " + reason);
    }
}
