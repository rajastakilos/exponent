# Exponent transaction categorizer

Dependency-free Java smoke test: prints the supplied CSV file unchanged to standard output.
Requires JDK 16 or newer because `TransactionRow` uses a Java record.

From the repository root, compile and run against `transactions.csv`:

```sh
javac Main.java TransactionRow.java && java Main transactions.csv
```

`Main.java` accepts exactly one file path. No CSV parsing, deduplication,
categorization, or ledger output is implemented.

`TransactionRow.java` defines a record for a parsed transaction, with fields
matching the CSV columns: `id`, `sync_batch` (represented as `syncBatch`), `date`,
`account`, `merchant`, `memo`, and `amount`. It uses `LocalDate` for dates and
`BigDecimal` for amounts. When CSV parsing is added, amounts should be constructed
directly from their text values to preserve decimal precision.
