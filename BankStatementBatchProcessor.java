import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger; // Import added

public class BankStatementBatchProcessor {
    // FIX: Use AtomicInteger instead of primitive int to ensure thread-safe increments
    private final AtomicInteger processedCount = new AtomicInteger(0);

    public void process(List<StatementRecord> records) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (StatementRecord record : records) {
            executor.submit(() -> {
                processRecord(record);
                // FIX: incrementAndGet() is an atomic operation
                processedCount.incrementAndGet(); 
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getProcessedCount() {
        // FIX: Use .get() to retrieve the current value
        return processedCount.get();
    }

    // Standard placeholder for the method mentioned in constraints
    private void processRecord(StatementRecord record) {
        // Business logic here
    }
}