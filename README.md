# Exponent transaction categorizer

Requires JDK 16 or newer (`java` and `javac` on your PATH) and a POSIX shell;
no dependencies. From the repository root, compile and run:

```sh
./run.sh transactions.csv
```

Optionally supply an output path as the second argument:

```sh
./run.sh transactions.csv my-ledger.csv
```

`run.sh` compiles `src/*.java` into `build/classes/` before running `Main`.
Input and output paths are relative to your current directory; quote paths that
contain spaces. You do not need to compile individual Java files manually.

This validates the CSV, keeps the highest sync batch per transaction ID, builds
categorized ledger lines, and writes to the supplied output path (default:
`ledger_lines.csv` in the current directory), replacing an existing output file.
The output directory must already exist. IDs retain their first-seen order. Amount signs
and decimal precision are preserved. Splits and the review queue are not implemented.

Run all tests from the repository root (including the supplied CSV fixtures):

```sh
./test.sh
```

`test.sh` compiles `src/*.java` and `tests/*.java` into `build/test-classes/`,
then runs every `tests/*Test.java` class. `tests/MainTest.java` runs the CLI in
temporary directories and compares the supplied feed's output against
`tests/fixtures/expected_first_15.csv`. Keep `transactions.csv` at the repository
root so that this test can find it.

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
tests live in the matching `tests/*Test.java` file. No build tool or Java packages are
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


Developer's Note:
1. How to run it: 
Simply run the following command and specify input and output. 
`./run.sh transactions.csv your-output.csv`

2. Anything in the story you found unclear or wrong, and what you'd ask before merging
I thought most of the story was clear. The key thing I looked out for was business logic that was a bit fuzzy.

a. The big thing here (mentioned above) is matching transfer types. I created a set of business rules for now,
but it's clear to me that we'd need something way more robust. Before we'd merged I'd want this piece to be 
clarified with product so that we aren't making major errors.
b. I made this assuming a single input, but we'd need to think through taking in multiple files, files that overlap
with transactions. There is no state, so everything that is processed has no way of knowing what came before it. 
c. We don't handle currency or money here. I'd need to handle that.

3. where you used AI and where you overrode it
a. My strategy was to spend about 30 minutes just reading this to internalize the business rules. I worked with
ChatGpt to discuss and understand. I basically handcoded the entities with ChatGPT. Once I had that in place, 
I moved to Codex. I didn't want to simply feed it a prompt. I did show it the input you sent me so that it was working 
from a shared understanding. From there I wanted to handle (1) Csv validation, (2) row validation, (3) Ledger writing
I broke ledger writing down into review flagging, categories, and transfers. I then wired it all together, refactored,
and updated the README.md. I always worked with tests. TDD is important with AI.
b. I mostly overrode AI on scope of approach. I wanted to take this in bite-sized pieces. Human context is essential
for workign with AI. I don't believe in vibe-coding. In the event there is an issue, we can always go back to a bite-sized commit and change things. 