public class CategoryAssignerTest {
    public static void main(String[] args) {
        transferOverridesKnownMerchant();
        transferOverridesUnknownMerchant();
        purchaseUsesMerchantCategory();
        refundUsesMerchantCategory();
        depositUsesMerchantCategory();
        unknownMerchantIsUncategorized();
        System.out.println("PASS: all 6 category tests");
    }

    private static void transferOverridesKnownMerchant() {
        check("Transfer", CategoryAssigner.category(LedgerType.TRANSFER, MerchantRule.SYSCO));
    }

    private static void transferOverridesUnknownMerchant() {
        check("Transfer", CategoryAssigner.category(LedgerType.TRANSFER, MerchantRule.UNCATEGORIZED));
    }

    private static void purchaseUsesMerchantCategory() {
        check("Food & beverage", CategoryAssigner.category(LedgerType.PURCHASE, MerchantRule.SYSCO));
    }

    private static void refundUsesMerchantCategory() {
        check("Repairs & maintenance", CategoryAssigner.category(LedgerType.REFUND, MerchantRule.HOME_DEPOT));
    }

    private static void depositUsesMerchantCategory() {
        check("Card sales", CategoryAssigner.category(LedgerType.DEPOSIT, MerchantRule.TOAST_PAYOUT));
    }

    private static void unknownMerchantIsUncategorized() {
        for (LedgerType type : new LedgerType[] {LedgerType.PURCHASE, LedgerType.REFUND, LedgerType.DEPOSIT}) {
            check("Uncategorized", CategoryAssigner.category(type, MerchantRule.UNCATEGORIZED));
        }
    }

    private static void check(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }
}
