package com.bibliotheque.controller;

import com.bibliotheque.entity.Borrow;
import com.bibliotheque.service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Enables frontend access
@RestController
@RequestMapping("/borrows")
public class BorrowController {
    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // Get all borrow records
    @GetMapping
    public ResponseEntity<List<Borrow>> getAllBorrows() {
        return ResponseEntity.ok(borrowService.getAllBorrows());
    }

    // Add a borrow record with duplicate borrowing prevention
    @PostMapping
    public ResponseEntity<?> addBorrow(@RequestBody Borrow borrow) {
        try {
            Borrow newBorrow = borrowService.addBorrow(borrow);
            return ResponseEntity.status(201).body(newBorrow);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage()); // Ensures proper error handling
        }
    }

    // Get overdue borrows
    @GetMapping("/overdue")
    public ResponseEntity<List<Borrow>> getOverdueBorrows() {
        return ResponseEntity.ok(borrowService.getOverdueBorrows());
    }

    // Apply penalty only if the book is actually overdue
    @PutMapping("/{borrowId}/penalty")
    public ResponseEntity<String> applyPenalty(@PathVariable Long borrowId) {
        if (!borrowService.existsById(borrowId)) {
            return ResponseEntity.status(404).body("Borrow record not found.");
        }
        try {
            borrowService.applyPenalty(borrowId);
            return ResponseEntity.ok("Penalty applied successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Return a book safely
    @DeleteMapping("/{borrowId}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long borrowId) {
        if (!borrowService.existsById(borrowId)) {
            return ResponseEntity.status(404).body("Borrow record not found.");
        }
        try {
            borrowService.returnBook(borrowId);
            return ResponseEntity.ok("Book returned successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
