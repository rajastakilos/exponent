import java.util.List;
import java.util.regex.Pattern;

public class TransferRules {
    // Add supported provider descriptions here, with positive and negative tests.
    private static final List<Rule> RULES = List.of(
            rule(AccountType.BANK, "ONLINE PAYMENT TO CARD(?: [0-9]+)?"),
            rule(AccountType.CARD, "ONLINE PAYMENT - THANK YOU"),
            rule(AccountType.BANK, "ONLINE TRANSFER TO SAVINGS(?: [0-9]+)?"),
            rule(AccountType.BANK, "ONLINE TRANSFER FROM SAVINGS(?: [0-9]+)?")
    );

    public static boolean isTransfer(TransactionRow transaction) {
        for (Rule rule : RULES) {
            if (rule.account() == transaction.account()
                    && rule.memo().matcher(transaction.memo().trim()).matches()) {
                return true;
            }
        }
        return false;
    }

    private static Rule rule(AccountType account, String memoPattern) {
        return new Rule(account, Pattern.compile(memoPattern, Pattern.CASE_INSENSITIVE));
    }

    private record Rule(AccountType account, Pattern memo) {}
}
