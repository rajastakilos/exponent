import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRow(
    String id,
    int syncBatch,
    LocalDate date,
    AccountType account,
    String merchant,
    String memo,
    BigDecimal amount
) {} 
