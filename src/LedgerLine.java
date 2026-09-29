import java.math.BigDecimal;
import java.time.LocalDate;

public record LedgerLine(
    String transactionId,
    LocalDate date,
    LedgerType type,
    String category,
    BigDecimal amount,
    boolean needsReview
) {}
