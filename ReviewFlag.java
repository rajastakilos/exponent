public class ReviewFlag {
    public static boolean needsReview(LedgerType type, MerchantRule merchantRule) {
        if (type == LedgerType.TRANSFER) {
            return false;
        }
        return merchantRule == MerchantRule.UNCATEGORIZED;
    }
}
