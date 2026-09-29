import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java Main <input-csv> [output-csv]");
            System.exit(1);
        }
        try {
            Path path = Paths.get(args[0]);
            Path output = Paths.get(args.length == 2 ? args[1] : "ledger_lines.csv");
            String contents = Files.readString(path);
            List<TransactionRow> transactions = CsvTransactionParser.parse(contents);
            List<TransactionRow> deduplicated = TransactionDeduplicator.deduplicate(transactions);
            List<LedgerLine> lines = LedgerBuilder.build(deduplicated);
            LedgerCsvWriter.write(output, lines);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Cannot generate ledger: " + e.getMessage());
            System.exit(1);
        }
    }
}
