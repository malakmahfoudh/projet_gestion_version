package com.bibliotheque.service;

import com.bibliotheque.entity.Borrow;
import com.bibliotheque.repository.BorrowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowService {
    private final BorrowRepository borrowRepository;
    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);

    public BorrowService(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    // Get all borrow records
    public List<Borrow> getAllBorrows() {
        return borrowRepository.findAll();
    }

    // Add a borrow record with validation to prevent duplicate borrowing
    public Borrow addBorrow(Borrow borrow) {
        Long studentId = borrow.getStudent().getId();
        Long bookId = borrow.getBook().getId();

        boolean alreadyBorrowed = borrowRepository.existsByStudentIdAndBookIdAndReturnDateIsNull(studentId, bookId);
        logger.info("Validation check for student {} and book {}: Query result = {}", studentId, bookId, alreadyBorrowed);

        if (alreadyBorrowed) {
            logger.warn("Duplicate borrow prevented for student {} and book {}", studentId, bookId);
            throw new RuntimeException("Student has already borrowed this book and hasn't returned it.");
        }

        borrow.setOverduePenalty(false);
        return borrowRepository.save(borrow);
    }


    // Get overdue borrows
    public List<Borrow> getOverdueBorrows() {
        return borrowRepository.findByReturnDateBefore(LocalDate.now());
    }

    // Apply penalty only if the book is overdue
    public void applyPenalty(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (borrow.getReturnDate() != null && borrow.getReturnDate().isBefore(LocalDate.now())) {
            borrow.setOverduePenalty(true);
            borrowRepository.save(borrow);
            logger.info("Penalty applied for overdue book with Borrow ID: {}", borrowId);
        } else {
            throw new RuntimeException("Book is not overdue, no penalty applied.");
        }
    }

    // Return a book safely with proper `returnDate` update
    public Borrow returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        borrow.setReturnDate(LocalDate.now()); // Ensure return date is recorded
        borrowRepository.save(borrow);

        logger.info("Book returned successfully for Borrow ID: {}", borrowId);
        return borrow;
    }

    // Check if a borrow record exists (used in controller)
    public boolean existsById(Long borrowId) {
        return borrowRepository.existsById(borrowId);
    }
}
