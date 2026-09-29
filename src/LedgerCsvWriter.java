import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class LedgerCsvWriter {
    public static void write(Path path, List<LedgerLine> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("transaction_id,date,type,category,amount,needs_review\n");
            for (LedgerLine line : lines) {
                writer.write(String.join(",",
                        escape(line.transactionId()),
                        line.date().toString(),
                        line.type().name().toLowerCase(Locale.ROOT),
                        escape(line.category()),
                        line.amount().toPlainString(),
                        Boolean.toString(line.needsReview())));
                writer.write("\n");
            }
        }
    }

    private static String escape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
