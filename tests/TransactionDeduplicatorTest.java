import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionDeduplicatorTest {
    public static void main(String[] args) {
        acceptsEmptyList();
        keepsUniqueIds();
        keepsEntireHighestBatchRow();
        keepsHigherBatchWhenItComesFirst();
        preservesFirstSeenIdOrder();
        allowsIdenticalDuplicates();
        rejectsConflictingTies();
        rejectsConflictingOlderTies();
        System.out.println("PASS: all 8 deduplication tests");
    }

    private static void acceptsEmptyList() {
        check(TransactionDeduplicator.deduplicate(List.of()).isEmpty(), "empty input");
    }

    private static void keepsUniqueIds() {
        List<TransactionRow> rows = List.of(row("a", 1), row("b", 1));
        check(TransactionDeduplicator.deduplicate(rows).equals(rows), "unique IDs");
    }

    private static void keepsEntireHighestBatchRow() {
        TransactionRow latest = new TransactionRow("a", 3, LocalDate.of(2026, 8, 4),
                AccountType.BANK, "Updated merchant", "Posted", new BigDecimal("15.25"));
        List<TransactionRow> rows = List.of(row("a", 1), latest, row("a", 2));
        check(TransactionDeduplicator.deduplicate(rows).equals(List.of(latest)), "entire latest row");
    }

    private static void keepsHigherBatchWhenItComesFirst() {
        TransactionRow latest = row("a", 2);
        check(TransactionDeduplicator.deduplicate(List.of(latest, row("a", 1)))
                .equals(List.of(latest)), "highest batch first");
    }

    private static void preservesFirstSeenIdOrder() {
        List<TransactionRow> rows = List.of(row("b", 1), row("a", 1), row("b", 2));
        check(TransactionDeduplicator.deduplicate(rows).equals(List.of(row("b", 2), row("a", 1))),
                "first-seen ID order");
    }

    private static void allowsIdenticalDuplicates() {
        check(TransactionDeduplicator.deduplicate(List.of(row("a", 1), row("a", 1)))
                .equals(List.of(row("a", 1))), "identical duplicates");
    }

    private static void rejectsConflictingTies() {
        rejects(List.of(row("a", 1), conflictingRow()));
    }

    private static void rejectsConflictingOlderTies() {
        rejects(List.of(row("a", 2), row("a", 1), conflictingRow()));
    }

    private static TransactionRow row(String id, int batch) {
        return new TransactionRow(id, batch, LocalDate.of(2026, 8, 3),
                AccountType.CARD, "SYSCO", "Pending", new BigDecimal("10.00"));
    }

    private static TransactionRow conflictingRow() {
        return new TransactionRow("a", 1, LocalDate.of(2026, 8, 3),
                AccountType.CARD, "SYSCO", "Different memo", new BigDecimal("10.00"));
    }

    private static void rejects(List<TransactionRow> rows) {
        try {
            TransactionDeduplicator.deduplicate(rows);
        } catch (IllegalArgumentException e) {
            check(e.getMessage().contains("id a") && e.getMessage().contains("sync_batch 1"),
                    "conflict identifies ID and batch");
            return;
        }
        throw new AssertionError("Expected conflicting tie to be rejected");
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
    }
}
