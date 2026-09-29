public class CsvFileValidator {
    private static final String HEADER = "id,sync_batch,date,account,merchant,memo,amount";

    public static void validate(String contents) {
        String header = contents.lines().findFirst().orElse("");
        if (!HEADER.equals(header)) {
            throw new IllegalArgumentException("Expected header: " + HEADER);
        }
        validateQuoting(contents);
    }

    private static void validateQuoting(String contents) {
        boolean inQuotes = false;
        boolean fieldStart = true;
        boolean quoteClosed = false;

        for (int i = 0; i < contents.length(); i++) {
            char c = contents.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < contents.length() && contents.charAt(i + 1) == '"') {
                        i++; // Two quotes inside a quoted field represent a literal quote.
                    } else {
                        inQuotes = false;
                        quoteClosed = true;
                    }
                }
            } else if (c == ',' || c == '\n' || c == '\r') {
                fieldStart = true;
                quoteClosed = false;
            } else if (c == '"' && fieldStart) {
                inQuotes = true;
                fieldStart = false;
            } else {
                if (c == '"' || quoteClosed) {
                    throw new IllegalArgumentException("Malformed CSV quoting");
                }
                fieldStart = false;
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("Unterminated quoted field");
        }
    }
}
