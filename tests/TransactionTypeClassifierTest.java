import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionTypeClassifierTest {
    public static void main(String[] args) {
        bankCardPaymentIsTransfer();
        cardPaymentOverridesRefund();
        savingsOutgoingIsTransfer();
        savingsIncomingOverridesDeposit();
        negativeCardIsRefund();
        negativeBankIsDeposit();
        positiveCardIsPurchase();
        positiveBankIsPurchase();
        zeroIsPurchase();
        rentIsPurchase();
        System.out.println("PASS: all 10 type classification tests");
    }

    private static void bankCardPaymentIsTransfer() {
        check(LedgerType.TRANSFER, AccountType.BANK, "4850.00", "ONLINE PAYMENT TO CARD 4421");
    }

    private static void cardPaymentOverridesRefund() {
        check(LedgerType.TRANSFER, AccountType.CARD, "-4850.00", "ONLINE PAYMENT - THANK YOU");
    }

    private static void savingsOutgoingIsTransfer() {
        check(LedgerType.TRANSFER, AccountType.BANK, "10000.00", "ONLINE TRANSFER TO SAVINGS 8810");
    }

    private static void savingsIncomingOverridesDeposit() {
        check(LedgerType.TRANSFER, AccountType.BANK, "-5000.00", "ONLINE TRANSFER FROM SAVINGS 8810");
    }

    private static void negativeCardIsRefund() {
        check(LedgerType.REFUND, AccountType.CARD, "-184.20", "SYSCO CREDIT SHORTED DELIVERY");
    }

    private static void negativeBankIsDeposit() {
        check(LedgerType.DEPOSIT, AccountType.BANK, "-2210.45", "DOORDASH INC PAYOUT");
    }

    private static void positiveCardIsPurchase() {
        check(LedgerType.PURCHASE, AccountType.CARD, "12.30", "");
    }

    private static void positiveBankIsPurchase() {
        check(LedgerType.PURCHASE, AccountType.BANK, "12.30", "");
    }

    private static void zeroIsPurchase() {
        check(LedgerType.PURCHASE, AccountType.CARD, "0.00", "");
        check(LedgerType.PURCHASE, AccountType.BANK, "0.00", "");
    }

    private static void rentIsPurchase() {
        check(LedgerType.PURCHASE, AccountType.BANK, "6500.00", "ACH PAYMENT MAINLINE PROPERTIES");
    }

    private static void check(LedgerType expected, AccountType account, String amount, String memo) {
        TransactionRow row = new TransactionRow("test", 1, LocalDate.of(2026, 8, 3),
                account, "Merchant", memo, new BigDecimal(amount));
        LedgerType actual = TransactionTypeClassifier.classify(row);
        if (actual != expected) {
            throw new AssertionError("Expected " + expected + ", got " + actual + " for " + memo);
        }
    }
}
