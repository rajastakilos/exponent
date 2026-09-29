public class CategoryAssigner {
    public static String category(LedgerType type, MerchantRule merchantRule) {
        if (type == LedgerType.TRANSFER) {
            return "Transfer";
        }
        return merchantRule.category();
    }
}
