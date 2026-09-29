import java.util.ArrayList;
import java.util.List;

public class CsvTransactionParser {
    public static List<TransactionRow> parse(String contents) {
        CsvFileValidator.validate(contents);
        return new RecordReader(contents).readTransactions();
    }

    // The file validator has already checked quoting; this reader decodes fields.
    private static class RecordReader {
        private final String contents;
        private final List<TransactionRow> transactions = new ArrayList<>();
        private final List<String> fields = new ArrayList<>();
        private final StringBuilder field = new StringBuilder();
        private int position;
        private int rowNumber = 1;
        private int recordStart;
        private boolean inQuotes;

        private RecordReader(String contents) {
            this.contents = contents;
        }

        private List<TransactionRow> readTransactions() {
            while (position < contents.length()) {
                readCharacter(contents.charAt(position));
                position++;
            }
            finishLastRecord();
            return transactions;
        }

        private void readCharacter(char character) {
            if (character == '"') {
                readQuote();
            } else if (inQuotes) {
                field.append(character);
            } else if (character == ',') {
                finishField();
            } else if (character == '\n' || character == '\r') {
                readRecordEnding(character);
            } else {
                field.append(character);
            }
        }

        private void readQuote() {
            if (inQuotes && nextCharacterIs('"')) {
                field.append('"');
                position++;
                return;
            }
            inQuotes = !inQuotes;
        }

        private boolean nextCharacterIs(char character) {
            return position + 1 < contents.length() && contents.charAt(position + 1) == character;
        }

        private void finishField() {
            fields.add(field.toString());
            field.setLength(0);
        }

        private void finishRecord() {
            finishField();
            if (rowNumber > 1) {
                transactions.add(TransactionRowValidator.validate(fields, rowNumber));
            }
            fields.clear();
            rowNumber++;
        }

        private void readRecordEnding(char character) {
            finishRecord();
            if (character == '\r' && nextCharacterIs('\n')) {
                position++; // Treat CRLF as a single record ending.
            }
            recordStart = position + 1;
        }

        private void finishLastRecord() {
            // No final newline is required, and a trailing empty field still counts.
            if (rowNumber > 1 && recordStart < contents.length()) {
                finishRecord();
            }
        }
    }
}
