import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentValidator {
    private static final Logger log = LoggerFactory.getLogger(DocumentValidator.class);

    // Custom exception to distinguish expected validation failures from system errors
    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    public ValidationResult validate(Document doc) throws ValidationException {
        try {
            if (doc == null) {
                throw new ValidationException("Document is null");
            }
            
            String content = doc.extractContent();
            if (content == null || content.isEmpty()) {
                throw new ValidationException("Empty content");
            }
            
            return runValidationRules(content);

        } catch (ValidationException e) {
            // FIX issue 1: Log expected failures at WARN/INFO level without stack traces to keep logs clean
            log.warn("Validation failed: {}", e.getMessage());
            // FIX issue 2: Throw the exception instead of returning null to let the caller handle the flow
            throw e;
        } catch (Exception e) {
            // Unexpected system errors should still log the full stack trace for debugging
            log.error("Unexpected error during validation", e);
            throw new RuntimeException("Internal validation error", e);
        }
    }

    public void validateBatch(List<Document> docs) {
        for (Document doc : docs) {
            try {
                ValidationResult r = validate(doc);
                // FIX issue 3: Added null check because validate() could return null in original code; 
                // but with the new throw logic, we handle the result safely.
                if (r != null && r.isValid()) {
                    saveResult(r);
                }
            } catch (ValidationException e) {
                // Expected failures in a batch: Log and continue to next document
                log.info("Skipping document in batch due to validation failure: {}", e.getMessage());
            } catch (Exception e) {
                // FIX issue 4: Never swallow exceptions completely. Log the error so it's visible to support.
                log.error("Critical error processing document in batch", e);
            }
        }
    }
}