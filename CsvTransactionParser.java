import java.util.ArrayList;
import java.util.List;

public class CsvTransactionParser {
    public static List<TransactionRow> parse(String contents) {
        CsvFileValidator.validate(contents);
        var transactions = new ArrayList<TransactionRow>();
        var fields = new ArrayList<String>();
        var field = new StringBuilder();
        boolean inQuotes = false;
        int rowNumber = 1;
        int recordStart = 0;

        for (int i = 0; i < contents.length(); i++) {
            char c = contents.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < contents.length() && contents.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (!inQuotes && c == ',') {
                fields.add(field.toString());
                field.setLength(0);
            } else if (!inQuotes && (c == '\n' || c == '\r')) {
                fields.add(field.toString());
                if (rowNumber > 1) {
                    transactions.add(TransactionRowValidator.validate(fields, rowNumber));
                }
                fields.clear();
                field.setLength(0);
                rowNumber++;
                if (c == '\r' && i + 1 < contents.length() && contents.charAt(i + 1) == '\n') i++;
                recordStart = i + 1;
            } else {
                field.append(c);
            }
        }

        // A final record need not end with a newline; preserve trailing empty fields.
        if (rowNumber > 1 && recordStart < contents.length()) {
            fields.add(field.toString());
            transactions.add(TransactionRowValidator.validate(fields, rowNumber));
        }
        return transactions;
    }
}
