public class TransactionTypeClassifier {
    public static LedgerType classify(TransactionRow transaction) {
        if (TransferRules.isTransfer(transaction)) {
            return LedgerType.TRANSFER;
        }
        if (transaction.amount().signum() < 0) {
            if (transaction.account() == AccountType.CARD) {
                return LedgerType.REFUND;
            }
            return LedgerType.DEPOSIT;
        }
        return LedgerType.PURCHASE;
    }
}
