import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class CsvFileTest {
    private static final String HEADER = "id,sync_batch,date,account,merchant,memo,amount\n";
    private static final String LEDGER_HEADER = "transaction_id,date,type,category,amount,needs_review\n";
    private static int failures;
    private static Path directory;

    public static void main(String[] args) throws Exception {
        directory = Files.createTempDirectory("csv-file-tests-");
        directory.toFile().deleteOnExit();

        rejectsMissingFile();
        rejectsDirectory();
        rejectsEmptyFile();
        rejectsIncorrectHeader();
        rejectsUnterminatedQuote();
        acceptsHeaderOnly();
        rejectsInvalidRowWithoutOutput();
        acceptsQuotedFields();
        rejectsMissingAmount();
        rejectsEmptyQuotedRecord();
        deduplicatesBeforeBuilding();
        rejectsConflictingDuplicates();
        matchesExpectedFeedOutput();
        writesNamedOutput();
        writesAbsoluteOutput();
        rejectsInvalidInputWithNamedOutput();

        if (failures > 0) {
            throw new AssertionError(failures + " file-level test(s) failed");
        }
    }

    private static void rejectsMissingFile() throws Exception {
        expectRejected("missing file", directory.resolve("missing.csv"));
    }

    private static void rejectsDirectory() throws Exception {
        expectRejected("directory", directory);
    }

    private static void rejectsEmptyFile() throws Exception {
        expectRejected("empty file", csv(""));
    }

    private static void rejectsIncorrectHeader() throws Exception {
        expectRejected("incorrect header", csv("id,batch,date,account,merchant,memo,amount\n"));
    }

    private static void rejectsUnterminatedQuote() throws Exception {
        expectRejected("unterminated quote", csv(HEADER + "txn_1,1,2026-08-03,card,\"SYSCO\n"));
    }

    private static void acceptsHeaderOnly() throws Exception {
        Result result = run(csv(HEADER));
        check("header-only file", result.exitCode() == 0 && result.error().isBlank()
                && result.output().equals(LEDGER_HEADER));
    }

    private static void rejectsEmptyQuotedRecord() throws Exception {
        Result result = run(csv(HEADER + "\"\""));
        check("empty quoted record", result.exitCode() != 0
                && result.error().contains("Row 2: columns") && result.output().isEmpty());
    }

    private static void rejectsInvalidRowWithoutOutput() throws Exception {
        Result result = run(csv(HEADER
                + "txn_1,1,2026-08-03,card,SYSCO,memo,12.30\n"
                + "txn_2,1,2026-08-03,card,SYSCO,memo,wrong\n"));
        check("invalid row is fatal with no partial output", result.exitCode() != 0
                && result.error().contains("Row 3: amount") && result.output().isEmpty());
    }

    private static void acceptsQuotedFields() throws Exception {
        String contents = HEADER.replace("\n", "\r\n")
                + "txn_1,1,2026-08-03,card,\"Shop, Inc\",\"a \"\"quote\"\"\nand newline\",12.30";
        Result result = run(csv(contents));
        check("quoted fields and no final newline", result.exitCode() == 0
                && result.error().isBlank() && result.output().equals(LEDGER_HEADER
                + "txn_1,2026-08-03,purchase,Uncategorized,12.30,true\n"));
    }

    private static void rejectsMissingAmount() throws Exception {
        Result result = run(csv(HEADER + "txn_1,1,2026-08-03,card,SYSCO,memo,"));
        check("trailing empty amount", result.exitCode() != 0
                && result.error().contains("Row 2: amount") && result.output().isEmpty());
    }

    // Shared helpers: create input, run the CLI, and report results.
    private static void deduplicatesBeforeBuilding() throws Exception {
        Result result = run(csv(HEADER
                + "txn_1,1,2026-08-03,card,Unknown,pending,1.00\n"
                + "txn_1,2,2026-08-03,card,SYSCO,posted,-12.30\n"));
        check("highest batch determines complete ledger line", result.exitCode() == 0
                && result.output().equals(LEDGER_HEADER
                + "txn_1,2026-08-03,refund,Food & beverage,-12.30,false\n"));
    }

    private static void rejectsConflictingDuplicates() throws Exception {
        Result result = run(csv(HEADER
                + "txn_1,1,2026-08-03,card,SYSCO,memo,1.00\n"
                + "txn_1,1,2026-08-03,card,SYSCO,memo,2.00\n"));
        check("conflicting duplicates are fatal", result.exitCode() != 0
                && result.error().contains("Conflicting rows") && result.output().isEmpty());
    }

    private static void matchesExpectedFeedOutput() throws Exception {
        Result result = run(Path.of("transactions.csv").toAbsolutePath());
        List<String> actual = result.output().lines().toList();
        List<String> expected = Files.readAllLines(Path.of("expected_first_15.csv"));
        check("feed produces 56 lines and matches expected first 15", result.exitCode() == 0
                && result.error().isBlank() && actual.size() == 57
                && actual.subList(0, expected.size()).equals(expected));
    }

    private static Path csv(String content) throws Exception {
        Path file = Files.createTempFile(directory, "input-", ".csv");
        file.toFile().deleteOnExit();
        return Files.writeString(file, content);
    }

    private static void writesNamedOutput() throws Exception {
        Result result = run(csv(HEADER + "txn_1,1,2026-08-03,card,SYSCO,memo,12.30\n"), "my ledger.csv");
        check("named output with spaces", result.exitCode() == 0 && result.error().isBlank()
                && result.output().equals(LEDGER_HEADER
                + "txn_1,2026-08-03,purchase,Food & beverage,12.30,false\n"));
    }

    private static void writesAbsoluteOutput() throws Exception {
        Result result = run(csv(HEADER), directory.resolve("absolute-ledger.csv").toAbsolutePath().toString());
        check("absolute output path", result.exitCode() == 0 && result.error().isBlank()
                && result.output().equals(LEDGER_HEADER));
    }

    private static void rejectsInvalidInputWithNamedOutput() throws Exception {
        Result result = run(csv(HEADER + "bad row\n"), "custom.csv");
        check("invalid input with named output", result.exitCode() != 0
                && result.error().contains("Row 2: columns") && result.output().isEmpty());
    }

    private static void expectRejected(String name, Path input) throws Exception {
        Result result = run(input);
        check(name, result.exitCode() != 0 && !result.error().isBlank() && result.output().isEmpty());
    }

    private static Result run(Path input) throws Exception {
        return run(input, null);
    }

    private static Result run(Path input, String outputName) throws Exception {
        Path workingDirectory = Files.createTempDirectory(directory, "run-");
        workingDirectory.toFile().deleteOnExit();
        Path output = workingDirectory.resolve(outputName == null ? "ledger_lines.csv" : outputName);
        output.toFile().deleteOnExit();
        String[] classpath = System.getProperty("java.class.path").split(File.pathSeparator);
        for (int i = 0; i < classpath.length; i++) {
            classpath[i] = Path.of(classpath[i]).toAbsolutePath().toString();
        }
        List<String> command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", String.join(File.pathSeparator, classpath), "Main", input.toAbsolutePath().toString()));
        if (outputName != null) {
            command.add(outputName);
        }
        Process process = new ProcessBuilder(command)
                .directory(workingDirectory.toFile())
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();
        String error = new String(process.getErrorStream().readAllBytes());
        int exitCode = process.waitFor();
        if (outputName != null && Files.exists(workingDirectory.resolve("ledger_lines.csv"))) {
            throw new AssertionError("Custom output must not also create the default file");
        }
        return new Result(exitCode, error, Files.exists(output) ? Files.readString(output) : "");
    }

    private record Result(int exitCode, String error, String output) {}

    private static void check(String name, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + name);
        if (!passed) {
            failures++;
        }
    }
}
