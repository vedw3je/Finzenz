    package com.ved.finzenz.finzenz.LoanService.repository;

    import com.ved.finzenz.finzenz.LoanService.entity.Loan;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.time.LocalDate;
    import java.util.List;

    public interface LoanRepository extends JpaRepository<Loan, Long> {

        List<Loan> findByAccountUserId(Long userId);

        List<Loan> findByAccountId(Long accountId);

        List<Loan> findByStatusAndNextDueDateLessThanEqual(
                Loan.LoanStatus status,
                LocalDate date
        );
    }
