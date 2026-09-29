public class MerchantRuleTest {
    public static void main(String[] args) {
        matchesIgnoringCase();
        matchesStoreSuffix();
        requiresPrefix();
        unknownMerchantFallsBack();
        blankMerchantFallsBack();
        System.out.println("PASS: all 5 merchant matching tests");
    }

    private static void matchesIgnoringCase() {
        check(MerchantRule.SYSCO, "sYsCo");
    }

    private static void matchesStoreSuffix() {
        check(MerchantRule.HOME_DEPOT, "HOME DEPOT #4102");
    }

    private static void requiresPrefix() {
        check(MerchantRule.UNCATEGORIZED, "PAYMENT TO SYSCO");
    }

    private static void unknownMerchantFallsBack() {
        check(MerchantRule.UNCATEGORIZED, "AMZN MKTP US*2K4X9");
    }

    private static void blankMerchantFallsBack() {
        check(MerchantRule.UNCATEGORIZED, "");
    }

    private static void check(MerchantRule expected, String merchant) {
        MerchantRule actual = MerchantRule.forMerchant(merchant);
        if (actual != expected) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }
}
