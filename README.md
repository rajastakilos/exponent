# Exponent transaction categorizer

Requires JDK 16 or newer; no dependencies. From the repository root:

```sh
javac Main.java && java Main transactions.csv
```

This validates the CSV, keeps the highest sync batch per transaction ID, builds
categorized ledger lines, and writes `ledger_lines.csv` in the current directory,
replacing an existing output file. IDs retain their first-seen order. Amount signs
and decimal precision are preserved. Splits and the review queue are not implemented.

Run all tests from the repository root (including the supplied CSV fixtures):

```sh
javac *.java && (for test in *Test.java; do java "${test%.java}" || exit 1; done)
```

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
