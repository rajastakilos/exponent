import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferRulesTest {
    public static void main(String[] args) {
        matchesBankCardPayment();
        matchesCardPaymentReceived();
        matchesSavingsOutgoing();
        matchesSavingsIncoming();
        ignoresCaseAndOuterWhitespace();
        allowsMissingAccountSuffix();
        rejectsRentPayment();
        rejectsGenericPayment();
        rejectsGenericTransfer();
        rejectsWrongAccount();
        rejectsExtraMemoText();
        System.out.println("PASS: all 11 transfer rule tests");
    }

    private static void matchesBankCardPayment() {
        check(true, AccountType.BANK, "ONLINE PAYMENT TO CARD 4421");
    }

    private static void matchesCardPaymentReceived() {
        check(true, AccountType.CARD, "ONLINE PAYMENT - THANK YOU");
    }

    private static void matchesSavingsOutgoing() {
        check(true, AccountType.BANK, "ONLINE TRANSFER TO SAVINGS 8810");
    }

    private static void matchesSavingsIncoming() {
        check(true, AccountType.BANK, "ONLINE TRANSFER FROM SAVINGS 8810");
    }

    private static void ignoresCaseAndOuterWhitespace() {
        check(true, AccountType.BANK, "  online payment to card 9999  ");
    }

    private static void allowsMissingAccountSuffix() {
        check(true, AccountType.BANK, "ONLINE TRANSFER TO SAVINGS");
    }

    private static void rejectsRentPayment() {
        check(false, AccountType.BANK, "ACH PAYMENT MAINLINE PROPERTIES");
    }

    private static void rejectsGenericPayment() {
        check(false, AccountType.BANK, "PAYMENT TO VENDOR");
    }

    private static void rejectsGenericTransfer() {
        check(false, AccountType.BANK, "TRANSFER TO VENDOR");
    }

    private static void rejectsWrongAccount() {
        check(false, AccountType.CARD, "ONLINE PAYMENT TO CARD 4421");
        check(false, AccountType.BANK, "ONLINE PAYMENT - THANK YOU");
        check(false, AccountType.CARD, "ONLINE TRANSFER TO SAVINGS 8810");
    }

    private static void rejectsExtraMemoText() {
        check(false, AccountType.BANK, "ONLINE PAYMENT TO CARD 4421 REVERSED");
    }

    private static void check(boolean expected, AccountType account, String memo) {
        TransactionRow row = new TransactionRow("test", 1, LocalDate.of(2026, 8, 3),
                account, "Unknown merchant", memo, BigDecimal.ONE);
        if (TransferRules.isTransfer(row) != expected) {
            throw new AssertionError("Unexpected transfer match for " + account + ": " + memo);
        }
    }
}
