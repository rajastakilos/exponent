import java.util.ArrayList;
import java.util.List;

public class LedgerBuilder {
    // The caller supplies validated, deduplicated transactions in output order.
    public static List<LedgerLine> build(List<TransactionRow> transactions) {
        List<LedgerLine> lines = new ArrayList<>();
        for (TransactionRow transaction : transactions) {
            LedgerType type = TransactionTypeClassifier.classify(transaction);
            MerchantRule merchantRule = MerchantRule.forMerchant(transaction.merchant());
            String category = CategoryAssigner.category(type, merchantRule);
            boolean needsReview = ReviewFlag.needsReview(type, merchantRule);
            lines.add(new LedgerLine(transaction.id(), transaction.date(), type,
                    category, transaction.amount(), needsReview));
        }
        return lines;
    }
}
