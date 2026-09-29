import java.util.Locale;

public enum MerchantRule {
    SYSCO("SYSCO", "Food & beverage"),
    US_FOODS("US FOODS", "Food & beverage"),
    RESTAURANT_DEPOT("RESTAURANT DEPOT", "Food & beverage"),

    METRO_GAS_AND_ELECTRIC("METRO GAS & ELECTRIC", "Utilities"),
    CITY_WATER_DEPT("CITY WATER DEPT", "Utilities"),
    COMCAST_BUSINESS("COMCAST BUSINESS", "Utilities"),

    HOME_DEPOT("HOME DEPOT", "Repairs & maintenance"),
    ECOLAB("ECOLAB", "Cleaning & supplies"),
    WEBSTAURANT_STORE("WEBSTAURANT STORE", "Smallwares & supplies"),
    GUSTO("GUSTO", "Payroll"),
    INDEED("INDEED", "Hiring"),
    SEVEN_SHIFTS("7SHIFTS", "Software"),
    MAINLINE_PROPERTIES("MAINLINE PROPERTIES", "Rent"),

    TOAST_PAYOUT("TOAST PAYOUT", "Card sales"),
    DOORDASH_PAYOUT("DOORDASH PAYOUT", "Delivery sales"),
    UBER_EATS_PAYOUT("UBER EATS PAYOUT", "Delivery sales"),

    UNCATEGORIZED(null, "Uncategorized");

    private final String prefix;
    private final String category;

    MerchantRule(String prefix, String category) {
        this.prefix = prefix;
        this.category = category;
    }

    public String category() {
        return category;
    }

    public static MerchantRule forMerchant(String merchant) {
        String normalized = merchant.toUpperCase(Locale.ROOT);

        for (MerchantRule rule : values()) {
            if (rule.prefix != null && normalized.startsWith(rule.prefix)) {
                return rule;
            }
        }

        return UNCATEGORIZED;
    }
}