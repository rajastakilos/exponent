import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Main <csv-file>");
            System.exit(1);
        }
        try {
            Path path = Paths.get(args[0]);
            String contents = Files.readString(path);
            List<TransactionRow> transactions = CsvTransactionParser.parse(contents);
            List<TransactionRow> deduplicated = TransactionDeduplicator.deduplicate(transactions);
            List<LedgerLine> lines = LedgerBuilder.build(deduplicated);
            LedgerCsvWriter.write(Paths.get("ledger_lines.csv"), lines);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Cannot generate ledger: " + e.getMessage());
            System.exit(1);
        }
    }
}
