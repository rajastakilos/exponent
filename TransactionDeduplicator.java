import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransactionDeduplicator {
    public static List<TransactionRow> deduplicate(List<TransactionRow> rows) {
        Map<String, TransactionRow> latest = new LinkedHashMap<String, TransactionRow>();
        Map<Version, TransactionRow> seen = new HashMap<Version, TransactionRow>();

        for (TransactionRow row : rows) {
            Version version = new Version(row.id(), row.syncBatch());
            TransactionRow duplicate = seen.putIfAbsent(version, row);
            if (duplicate != null && !duplicate.equals(row)) {
                throw new IllegalArgumentException("Conflicting rows for id " + row.id()
                        + " and sync_batch " + row.syncBatch());
            }

            TransactionRow current = latest.get(row.id());
            if (current == null || row.syncBatch() > current.syncBatch()) {
                latest.put(row.id(), row);
            }
        }
        return new ArrayList<>(latest.values());
    }

    private record Version(String id, int batch) {}
}
