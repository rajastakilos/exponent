public class ReviewFlagTest {
    public static void main(String[] args) {
        unknownTransferDoesNotNeedReview();
        knownTransferDoesNotNeedReview();
        unknownPurchaseNeedsReview();
        knownPurchaseDoesNotNeedReview();
        unknownRefundNeedsReview();
        knownRefundDoesNotNeedReview();
        unknownDepositNeedsReview();
        knownDepositDoesNotNeedReview();
        System.out.println("PASS: all 8 review flag tests");
    }

    private static void unknownTransferDoesNotNeedReview() {
        check(!ReviewFlag.needsReview(LedgerType.TRANSFER, MerchantRule.UNCATEGORIZED),
                "transfer overrides unknown merchant");
    }

    private static void knownTransferDoesNotNeedReview() {
        check(!ReviewFlag.needsReview(LedgerType.TRANSFER, MerchantRule.SYSCO),
                "known transfer");
    }

    private static void unknownPurchaseNeedsReview() {
        check(ReviewFlag.needsReview(LedgerType.PURCHASE, MerchantRule.UNCATEGORIZED),
                "unknown purchase");
    }

    private static void knownPurchaseDoesNotNeedReview() {
        check(!ReviewFlag.needsReview(LedgerType.PURCHASE, MerchantRule.SYSCO),
                "known purchase");
    }

    private static void unknownRefundNeedsReview() {
        check(ReviewFlag.needsReview(LedgerType.REFUND, MerchantRule.UNCATEGORIZED),
                "unknown refund");
    }

    private static void knownRefundDoesNotNeedReview() {
        check(!ReviewFlag.needsReview(LedgerType.REFUND, MerchantRule.HOME_DEPOT),
                "known refund");
    }

    private static void unknownDepositNeedsReview() {
        check(ReviewFlag.needsReview(LedgerType.DEPOSIT, MerchantRule.UNCATEGORIZED),
                "unknown deposit");
    }

    private static void knownDepositDoesNotNeedReview() {
        check(!ReviewFlag.needsReview(LedgerType.DEPOSIT, MerchantRule.TOAST_PAYOUT),
                "known deposit");
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
    }
}
