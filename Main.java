import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Main <csv-file>");
            System.exit(1);
        }
        Files.copy(Paths.get(args[0]), System.out);
    }
}
