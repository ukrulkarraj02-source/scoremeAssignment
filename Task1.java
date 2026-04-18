public List<LoanAccount> getOverdueLoans(List<LoanAccount> accounts) {
    // FIX: Initialize result to an empty list to avoid NullPointerException
    List<LoanAccount> result = new ArrayList<>();

    // FIX: Add null check for the input collection
    if (accounts == null) {
        return result;
    }

    for (LoanAccount account : accounts) {
        // FIX: Check if dueDate is null (restructured accounts) before calling .before()
        if (account.getDueDate() != null && account.getDueDate().before(new Date())) {
            
            // FIX: Ensure balance is strictly greater than zero to be considered overdue
            if (account.getOutstandingBalance() > 0) {
                result.add(account);
            }
        }
    }
    
    return result;
}