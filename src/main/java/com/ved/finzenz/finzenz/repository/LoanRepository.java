    package com.ved.finzenz.finzenz.repository;

    import com.ved.finzenz.finzenz.entities.Loan;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.time.LocalDate;
    import java.util.List;

    @Repository
    public interface LoanRepository extends JpaRepository<Loan, Long> {
        List<Loan> findByAccountId(Long accountId);
        List<Loan> findByStatusAndNextDueDateLessThanEqual(Loan.LoanStatus status, LocalDate date);
    }
