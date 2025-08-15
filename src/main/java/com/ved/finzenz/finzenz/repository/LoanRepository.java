    package com.ved.finzenz.finzenz.repository;

    import com.ved.finzenz.finzenz.entities.Loan;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.time.LocalDate;
    import java.util.List;

    @Repository
    public interface LoanRepository extends JpaRepository<Loan, Long> {
        @Query(
                value = "SELECT l.* FROM loans l " +
                        "JOIN accounts a ON  l.account_id = a.id " +
                        "WHERE a.user_id = :userId",
                nativeQuery = true

        )
        List<Loan> findByUserId(@Param("userId") Long userId);


        List<Loan> findByAccountId(Long accountId);
        List<Loan> findByStatusAndNextDueDateLessThanEqual(Loan.LoanStatus status, LocalDate date);
    }
