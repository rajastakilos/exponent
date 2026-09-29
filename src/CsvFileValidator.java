public class CsvFileValidator {
    private static final String HEADER = "id,sync_batch,date,account,merchant,memo,amount";

    public static void validate(String contents) {
        validateHeader(contents);
        new QuoteValidator(contents).validate();
    }

    private static void validateHeader(String contents) {
        String header = contents.lines().findFirst().orElse("");
        if (!HEADER.equals(header)) {
            throw new IllegalArgumentException("Expected header: " + HEADER);
        }
    }

    // One instance per file keeps quote state local to this validation call.
    private static class QuoteValidator {
        private final String contents;
        private int position;
        private boolean inQuotes;
        private boolean fieldStart = true;
        private boolean quoteClosed;

        private QuoteValidator(String contents) {
            this.contents = contents;
        }

        private void validate() {
            while (position < contents.length()) {
                validateCharacter(contents.charAt(position));
                position++;
            }
            if (inQuotes) {
                throw new IllegalArgumentException("Unterminated quoted field");
            }
        }

        private void validateCharacter(char character) {
            if (inQuotes) {
                validateInsideQuotes(character);
            } else if (isSeparator(character)) {
                startField();
            } else if (character == '"' && fieldStart) {
                openQuote();
            } else {
                validateUnquotedCharacter(character);
            }
        }

        private void validateInsideQuotes(char character) {
            if (character != '"') {
                return;
            }
            if (nextCharacterIsQuote()) {
                position++; // An escaped quote does not close the field.
                return;
            }
            inQuotes = false;
            quoteClosed = true;
        }

        private boolean nextCharacterIsQuote() {
            return position + 1 < contents.length() && contents.charAt(position + 1) == '"';
        }

        private boolean isSeparator(char character) {
            return character == ',' || character == '\n' || character == '\r';
        }

        private void startField() {
            fieldStart = true;
            quoteClosed = false;
        }

        private void openQuote() {
            inQuotes = true;
            fieldStart = false;
        }

        private void validateUnquotedCharacter(char character) {
            if (character == '"' || quoteClosed) {
                throw new IllegalArgumentException("Malformed CSV quoting");
            }
            fieldStart = false;
        }
    }
}
