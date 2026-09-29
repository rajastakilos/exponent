import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LedgerBuilderTest {
    public static void main(String[] args) {
        acceptsEmptyInput();
        buildsKnownPurchase();
        buildsUnknownPurchase();
        buildsKnownRefund();
        buildsUnknownRefund();
        buildsKnownDeposit();
        buildsUnknownDeposit();
        buildsBothCardPaymentLegs();
        buildsSavingsTransfer();
        buildsRentPurchase();
        preservesInputOrder();
        System.out.println("PASS: all 11 ledger builder tests");
    }

    private static void acceptsEmptyInput() {
        check(LedgerBuilder.build(List.of()).isEmpty(), "empty input");
    }

    private static void buildsKnownPurchase() {
        expect(row("purchase", AccountType.CARD, "sysco PHILA 8827", "", "1842.17"),
                LedgerType.PURCHASE, "Food & beverage", false);
    }

    private static void buildsUnknownPurchase() {
        expect(row("purchase", AccountType.CARD, "AMZN MKTP", "", "84.12"),
                LedgerType.PURCHASE, "Uncategorized", true);
    }

    private static void buildsKnownRefund() {
        expect(row("refund", AccountType.CARD, "HOME DEPOT #4102", "HOME DEPOT RETURN", "-59.99"),
                LedgerType.REFUND, "Repairs & maintenance", false);
    }

    private static void buildsUnknownRefund() {
        expect(row("refund", AccountType.CARD, "AMZN MKTP", "REFUND", "-41.99"),
                LedgerType.REFUND, "Uncategorized", true);
    }

    private static void buildsKnownDeposit() {
        expect(row("deposit", AccountType.BANK, "TOAST PAYOUT", "DAILY PAYOUT", "-6120.30"),
                LedgerType.DEPOSIT, "Card sales", false);
    }

    private static void buildsUnknownDeposit() {
        expect(row("deposit", AccountType.BANK, "Unknown", "", "-10.00"),
                LedgerType.DEPOSIT, "Uncategorized", true);
    }

    private static void buildsBothCardPaymentLegs() {
        expect(row("bank", AccountType.BANK, "Unknown", "ONLINE PAYMENT TO CARD 4421", "4850.00"),
                LedgerType.TRANSFER, "Transfer", false);
        expect(row("card", AccountType.CARD, "PAYMENT RECEIVED", "ONLINE PAYMENT - THANK YOU", "-4850.00"),
                LedgerType.TRANSFER, "Transfer", false);
    }

    private static void buildsSavingsTransfer() {
        expect(row("savings", AccountType.BANK, "Unknown", "ONLINE TRANSFER FROM SAVINGS 8810", "-5000.00"),
                LedgerType.TRANSFER, "Transfer", false);
    }

    private static void buildsRentPurchase() {
        expect(row("rent", AccountType.BANK, "MAINLINE PROPERTIES LLC", "ACH PAYMENT MAINLINE PROPERTIES", "6500.00"),
                LedgerType.PURCHASE, "Rent", false);
    }

    private static void preservesInputOrder() {
        TransactionRow first = row("z", AccountType.CARD, "SYSCO", "", "10.00");
        TransactionRow second = row("a", AccountType.BANK, "TOAST PAYOUT", "", "-20.00");
        List<LedgerLine> lines = LedgerBuilder.build(List.of(first, second));
        check(lines.size() == 2 && lines.get(0).transactionId().equals("z")
                && lines.get(1).transactionId().equals("a"), "input order");
    }

    private static TransactionRow row(String id, AccountType account, String merchant, String memo, String amount) {
        return new TransactionRow(id, 1, LocalDate.of(2026, 8, 3), account,
                merchant, memo, new BigDecimal(amount));
    }

    private static void expect(TransactionRow transaction, LedgerType type, String category, boolean needsReview) {
        LedgerLine expected = new LedgerLine(transaction.id(), transaction.date(), type,
                category, transaction.amount(), needsReview);
        List<LedgerLine> actual = LedgerBuilder.build(List.of(transaction));
        check(actual.equals(List.of(expected)), "ledger line for " + transaction.id() + ": " + actual);
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
    }
}
