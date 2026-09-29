# Exponent transaction categorizer

Dependency-free Java smoke test: validates the file header and CSV quoting,
then prints the supplied CSV file unchanged to standard output.
Requires JDK 16 or newer because `TransactionRow` uses a Java record.

From the repository root, compile and run against `transactions.csv`:

```sh
javac Main.java TransactionRow.java && java Main transactions.csv
```

`Main.java` accepts exactly one file path. `CsvFileValidator.java` checks the exact
header shown in `transactions.csv` and CSV quoting before anything is printed.
No row-level validation, transaction parsing, deduplication, categorization, or
ledger output is implemented.

`TransactionRow.java` defines a record for a parsed transaction, with fields
matching the CSV columns: `id`, `sync_batch` (represented as `syncBatch`), `date`,
`account`, `merchant`, `memo`, and `amount`. It uses `LocalDate` for dates and
`BigDecimal` for amounts. When CSV parsing is added, amounts should be constructed
directly from their text values to preserve decimal precision.

Run the file-level CLI tests (no test dependencies):

```sh
javac Main.java CsvFileTest.java && java CsvFileTest
```

These tests require invalid files to produce a nonzero exit status and an error
on standard error. A valid header with no transactions is accepted. They cover
file access, missing/incorrect headers, and unterminated quoting, not row-level
validation.
