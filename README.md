# Exponent transaction categorizer

Dependency-free Java smoke test: prints the supplied CSV file unchanged to standard output.
Requires a JDK (Java 8 or newer).

From the repository root, compile and run against `transactions.csv`:

```sh
javac Main.java && java Main transactions.csv
```

`Main.java` accepts exactly one file path. No CSV parsing, deduplication,
categorization, or ledger output is implemented.
