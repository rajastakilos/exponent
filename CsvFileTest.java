import java.nio.file.Files;
import java.nio.file.Path;

public class CsvFileTest {
    private static final String HEADER = "id,sync_batch,date,account,merchant,memo,amount\n";
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
        check("header-only file", result.exitCode() == 0 && result.error().isBlank());
    }

    // Shared helpers: create input, run the CLI, and report results.
    private static Path csv(String content) throws Exception {
        Path file = Files.createTempFile(directory, "input-", ".csv");
        file.toFile().deleteOnExit();
        return Files.writeString(file, content);
    }

    private static void expectRejected(String name, Path input) throws Exception {
        Result result = run(input);
        check(name, result.exitCode() != 0 && !result.error().isBlank());
    }

    private static Result run(Path input) throws Exception {
        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", System.getProperty("java.class.path"), "Main", input.toString())
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();
        String error = new String(process.getErrorStream().readAllBytes());
        return new Result(process.waitFor(), error);
    }

    private record Result(int exitCode, String error) {}

    private static void check(String name, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + name);
        if (!passed) {
            failures++;
        }
    }
}
