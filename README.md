# Exponent transaction categorizer

Requires JDK 16 or newer; no dependencies. From the repository root:

```sh
./run.sh transactions.csv
```

Optionally supply an output path as the second argument:

```sh
./run.sh transactions.csv my-ledger.csv
```

This validates the CSV, keeps the highest sync batch per transaction ID, builds
categorized ledger lines, and writes to the supplied output path (default:
`ledger_lines.csv` in the current directory), replacing an existing output file.
The output directory must already exist. IDs retain their first-seen order. Amount signs
and decimal precision are preserved. Splits and the review queue are not implemented.

Run all tests from the repository root (including the supplied CSV fixtures):

```sh
./test.sh
```

Repository layout:

```text
src/             Java application code
tests/           Small tests for each component; MainTest exercises the CLI
tests/fixtures/  Expected output for the first 15 transactions
build/           Generated classes (ignored by Git)
transactions.csv Sample input
run.sh           Compile and run the application
test.sh          Compile and run every test; stop on failure
```

Start with `src/Main.java` to follow the processing flow:
`CsvTransactionParser` (using the file and row validators) →
`TransactionDeduplicator` → `LedgerBuilder` → `LedgerCsvWriter`.
The builder combines `TransactionTypeClassifier`, `MerchantRule`,
`CategoryAssigner`, and `ReviewFlag`; `TransferRules` holds editable transfer
patterns. Records and enums describe the input and output data. Each component's
tests live in the matching `*Test.java` file. No build tool or Java packages are
needed for this small project.

Invalid input is fatal, with a row/field error where applicable. Validation and
ledger building finish before the output is opened, so invalid input leaves any
existing ledger untouched. Identical duplicate rows are allowed; conflicting rows
with the same ID and batch are rejected. CSV row numbers include the header and
count multiline quoted fields as one record. Blank merchants/memos, zero amounts,
and any batch fitting a Java integer are allowed; the prompt gives no tighter rules.

Transfer assumptions: case-insensitive memo matching, ignoring outer whitespace,
recognizes bank `ONLINE PAYMENT TO CARD`, `ONLINE TRANSFER TO SAVINGS`, and
`ONLINE TRANSFER FROM SAVINGS`, optionally followed by a numeric account suffix.
On the card it recognizes `ONLINE PAYMENT - THANK YOU`. No extra text is accepted.
`ACH PAYMENT MAINLINE PROPERTIES` remains a rent purchase. Before merging, ask
which provider fields or guaranteed memo patterns establish account ownership;
unrecognized descriptions can misclassify transfers. Extend `TransferRules` with
positive and negative tests. Also confirm the duplicate-tie and blank-field policies.

AI usage: ChatGPT helped with the initial entities; Codex helped implement and test
parsing, validation, deduplication, classification, and output. Human direction
required fatal input validation, small separately tested components, explicit Java
types, and explicit conditional logic rather than a combined review expression.
