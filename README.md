# Exponent transaction categorizer

Dependency-free Java smoke test: validates the file header, CSV quoting, and rows,
then prints the supplied CSV file unchanged to standard output.
Requires JDK 16 or newer because `TransactionRow` uses a Java record.

From the repository root, compile and run against `transactions.csv`:

```sh
javac Main.java TransactionRow.java && java Main transactions.csv
```

`Main.java` accepts exactly one file path. `CsvFileValidator.java` checks the exact
header shown in `transactions.csv` and CSV quoting before anything is printed.
`CsvTransactionParser.java` decodes CSV fields and calls `TransactionRowValidator`
for every data row. Any invalid row is fatal: the CLI exits nonzero with an error
and prints no file contents. Row numbers count CSV records, including the header;
newlines inside quoted fields do not increment them.
Deduplication, type classification, category assignment, and review flags are
implemented separately but not yet connected to the CLI. Ledger output is pending.

`TransactionRow.java` defines a record for a parsed transaction, with fields
matching the CSV columns: `id`, `sync_batch` (represented as `syncBatch`), `date`,
`account`, `merchant`, `memo`, and `amount`. It uses `LocalDate` for dates and
`BigDecimal` for amounts. `TransactionRowValidator.java` converts seven decoded
fields into this record, constructing amounts directly from text. Invalid fields
throw an error with the row number, field, and reason; no invalid row is returned.
Blank merchant and memo values are allowed because the prompt does not require
them. Batch numbers must fit a Java integer; no minimum is specified in the prompt.
Zero and negative amounts are valid.

Run the file-level CLI tests (no test dependencies):

```sh
javac Main.java CsvFileTest.java && java CsvFileTest
```

These tests require invalid files to produce a nonzero exit status and an error
on standard error. A valid header with no transactions is accepted. They cover
file access, missing/incorrect headers, quoting, and row-validation integration.

Run the small, direct row-validator tests:

```sh
javac TransactionRowValidatorTest.java && java TransactionRowValidatorTest
```

Transfer detection assumes these memo descriptions identify the operator's own
accounts (case-insensitive, ignoring outer whitespace): bank `ONLINE PAYMENT TO
CARD`, bank `ONLINE TRANSFER TO SAVINGS` / `ONLINE TRANSFER FROM SAVINGS` (each
optionally followed by a numeric account suffix), and card `ONLINE PAYMENT - THANK
YOU`. No additional text is accepted. Ordinary `PAYMENT` or `TRANSFER` keywords
are insufficient: `ACH PAYMENT MAINLINE PROPERTIES` is a rent purchase.
Before merging, ask which provider fields or guaranteed memo patterns establish
account ownership. Unrecognized descriptions fall through to the account/sign
rules and may misclassify transfers. Add supported patterns in `TransferRules`
with positive and negative tests; the classifier need not change.

Run the transfer and type tests:

```sh
javac TransferRulesTest.java TransactionTypeClassifierTest.java && java TransferRulesTest && java TransactionTypeClassifierTest
```
