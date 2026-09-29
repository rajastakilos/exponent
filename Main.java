import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Main <csv-file>");
            System.exit(1);
        }
        try {
            var path = Paths.get(args[0]);
            String contents = Files.readString(path);
            CsvTransactionParser.parse(contents);
            System.out.print(contents);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Cannot read CSV: " + e.getMessage());
            System.exit(1);
        }
    }
}
