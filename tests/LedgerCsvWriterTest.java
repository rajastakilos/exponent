import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class LedgerCsvWriterTest {
    private static final String HEADER = "transaction_id,date,type,category,amount,needs_review\n";

    public static void main(String[] args) throws IOException {
        writesHeaderForEmptyLedger();
        writesPurchase();
        writesAllTypesAndReviewFlags();
        preservesAmountsWithoutRounding();
        escapesCommasAndQuotes();
        escapesLineBreaks();
        writesUtf8();
        replacesExistingContents();
        System.out.println("PASS: all 8 ledger CSV writer tests");
    }

    private static void writesHeaderForEmptyLedger() throws IOException {
        check(HEADER, write(List.of()));
    }

    private static void writesPurchase() throws IOException {
        check(HEADER + "txn_1001,2026-08-03,purchase,Food & beverage,1842.17,false\n",
                write(List.of(line("txn_1001", LedgerType.PURCHASE, "Food & beverage", "1842.17", false))));
    }

    private static void writesAllTypesAndReviewFlags() throws IOException {
        check(HEADER
                + "r,2026-08-03,refund,Uncategorized,-10.00,true\n"
                + "d,2026-08-03,deposit,Card sales,-20.00,false\n"
                + "t,2026-08-03,transfer,Transfer,30.00,false\n",
                write(List.of(
                        line("r", LedgerType.REFUND, "Uncategorized", "-10.00", true),
                        line("d", LedgerType.DEPOSIT, "Card sales", "-20.00", false),
                        line("t", LedgerType.TRANSFER, "Transfer", "30.00", false))));
    }

    private static void preservesAmountsWithoutRounding() throws IOException {
        check(HEADER
                + "a,2026-08-03,purchase,Test,1.234,false\n"
                + "b,2026-08-03,purchase,Test,1000,false\n",
                write(List.of(
                        line("a", LedgerType.PURCHASE, "Test", "1.234", false),
                        line("b", LedgerType.PURCHASE, "Test", "1E+3", false))));
    }

    private static void escapesCommasAndQuotes() throws IOException {
        check(HEADER + "\"id,\"\"1\"\"\",2026-08-03,purchase,\"Food, \"\"special\"\"\",1.00,false\n",
                write(List.of(line("id,\"1\"", LedgerType.PURCHASE, "Food, \"special\"", "1.00", false))));
    }

    private static void escapesLineBreaks() throws IOException {
        check(HEADER + "\"id\r1\",2026-08-03,purchase,\"Food\nsupplies\",1.00,false\n",
                write(List.of(line("id\r1", LedgerType.PURCHASE, "Food\nsupplies", "1.00", false))));
    }

    private static void writesUtf8() throws IOException {
        check(HEADER + "café,2026-08-03,purchase,Food,1.00,false\n",
                write(List.of(line("café", LedgerType.PURCHASE, "Food", "1.00", false))));
    }

    private static void replacesExistingContents() throws IOException {
        Path path = Files.createTempFile("ledger-writer-", ".csv");
        try {
            Files.writeString(path, "Old contents that must not remain\n");
            LedgerCsvWriter.write(path, List.of());
            check(HEADER, Files.readString(path, StandardCharsets.UTF_8));
        } finally {
            Files.delete(path);
        }
    }

    private static LedgerLine line(String id, LedgerType type, String category, String amount, boolean review) {
        return new LedgerLine(id, LocalDate.of(2026, 8, 3), type, category, new BigDecimal(amount), review);
    }

    private static String write(List<LedgerLine> lines) throws IOException {
        Path path = Files.createTempFile("ledger-writer-", ".csv");
        try {
            LedgerCsvWriter.write(path, lines);
            return Files.readString(path, StandardCharsets.UTF_8);
        } finally {
            Files.delete(path);
        }
    }

    private static void check(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected:\n" + expected + "Actual:\n" + actual);
        }
    }
}
