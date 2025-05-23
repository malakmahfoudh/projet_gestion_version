package com.bibliotheque.repository;

import com.bibliotheque.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    // Fetch overdue borrows
    List<Borrow> findByReturnDateBefore(LocalDate currentDate);

    // Prevent duplicate borrowing
    @Query("SELECT COUNT(b) > 0 FROM Borrow b WHERE b.student.id = :studentId AND b.book.id = :bookId")
    boolean existsByStudentIdAndBookIdAndReturnDateIsNull(@Param("studentId") Long studentId, @Param("bookId") Long bookId);


}
