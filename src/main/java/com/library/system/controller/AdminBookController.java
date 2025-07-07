package com.library.system.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.system.entity.Book;
import com.library.system.entity.BookTransactionsHistory;
import com.library.system.entity.MyBook;
import com.library.system.entity.User;
import com.library.system.service.BookService;
import com.library.system.service.BookTransactionHistoryService;
import com.library.system.service.MyBookListService;
import com.library.system.service.UserService;

@Controller
public class AdminBookController {

    @Autowired
    private BookService service;
    @Autowired
    private MyBookListService myBookService;
    @Autowired
    private BookTransactionHistoryService bookTransactionsHistoryService;
    @Autowired
    private UserService userService;

    // admin related methods

    @GetMapping("/admin_home")
    public String adminHome() {
        return "admin/admin_home";
    }

    @GetMapping("/admin/available_books")
    public ModelAndView getAdminAvailableBooks() {
        List<Book> list = service.getAllBook();

        list.forEach(book -> {
            if (book.getCopiesAvailable() > 0) {
                book.setAvailable(true);
            } else {
                book.setAvailable(false);
            }
        });
        return new ModelAndView("admin/admin_available_books", "book", list);
    }

    // it should be only available for admin
    @GetMapping("/admin/book_register")
    public String bookRegister() {
        return "admin/bookRegister";
    }

    // to add new book into library
    @PostMapping("/admin/save")
    public String addBook(@ModelAttribute Book b, RedirectAttributes redirectAttributes) {
        try {
            // Check if the book already exists
            Book existingBook = service.findByNameAndAuthor(b.getName(), b.getAuthor());
            if (existingBook != null) {
                // increase number of copies if book already exists
                existingBook.setCopiesAvailable((existingBook.getCopiesAvailable() + 1));
                // existingBook.setAvailable(existingBook.getCopiesAvailable() > 0);
                service.save(existingBook);
                redirectAttributes.addFlashAttribute("successMessage", "Book List updated successfully.");
            } else {
                // add new if not existed
                b.setCopiesAvailable(1);
                service.save(b);
                redirectAttributes.addFlashAttribute("successMessage", "Book Added successfully.");

            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating book: " + e.getMessage());
        }
        return "redirect:/admin/available_books"; // Redirect to the available books page
    }

    // to edit existing book details
    @RequestMapping("/admin/editBook/{id}")
    public String editBook(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Book b = service.getBookById(id);
            model.addAttribute("book", b);
            return "admin/bookEdit";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/available_books";
        }
    }

    // to delete existing book
    @RequestMapping("/admin/deleteBook/{id}")
    public String deleteBook(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {

        try {
            // check if book is issued or not
            boolean isCurrentlyIssued = myBookService.isBookCurrentlyIssued(id);
            if (isCurrentlyIssued) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot delete: Book is currently issued to a user.");
                return "redirect:/admin/available_books";
            }
            service.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/available_books";
    }

    // to check which book are loaned by whome
    @GetMapping("/admin/loaned_books")
    public String getLoanedBooks(Model model) {
        List<MyBook> loanedBooks = myBookService.getAllMyBooks(); // Fetch all loaned books
        model.addAttribute("books", loanedBooks);
        return "admin/loaned_books";
    }

    // report home page - different reports are there
    @GetMapping("/admin/report")
    public ModelAndView generateReport() {
        List<Book> loanedBooks = service.getAllLoanedBooks();
        return new ModelAndView("admin/report", "books", loanedBooks);
    }

    // after adding new table
    @GetMapping("/admin/book_transaction_history")
    public String getAllBookTransactionHistory(Model model) {
        List<BookTransactionsHistory> transactionHistory = bookTransactionsHistoryService.getAllTransactions();
        model.addAttribute("transactionHistory", transactionHistory);
        return "admin/report/book_transaction_history";
    }

    // user report
    @GetMapping("/admin/user_report")
    public String userReport(Model model) {
        // Fetch all users
        List<User> allUsers = userService.getAllUsers();

        // Calculate total users with role USER
        long totalUsers = allUsers.stream()
                .filter(user -> user.getRole().equalsIgnoreCase("USER"))
                .count();

        // Calculate total admins
        long totalAdmins = allUsers.stream()
                .filter(user -> user.getRole().equalsIgnoreCase("ADMIN"))
                .count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("allUsers", allUsers);

        return "admin/report/user_report";
    }

    // book report
    @GetMapping("/admin/book_report")
    public String bookReport(Model model) {
        // Fetch all books
        List<Book> allBooks = service.getAllBook();

        // Calculate total books registered
        int totalBooksRegistered = allBooks.size();

        // Calculate total available books
        int totalAvailableBooks = (int) allBooks.stream()
                .filter(Book::isAvailable)
                .count();

        // Calculate total loaned books
        int totalLoanedBooks = totalBooksRegistered - totalAvailableBooks;

        // Fetch most loaned books
        List<BookTransactionsHistory> allTransactions = bookTransactionsHistoryService.getAllTransactions();

        if (allTransactions.isEmpty()) {
            // If no transactions are available, set a message in the model
            model.addAttribute("noDataMessage", "No data available for loaned books.");
            model.addAttribute("totalBooksRegistered", totalBooksRegistered);
            model.addAttribute("totalAvailableBooks", totalAvailableBooks);
            model.addAttribute("totalLoanedBooks", totalLoanedBooks);
            model.addAttribute("mostLoanedBooks", List.of());
            model.addAttribute("leastLoanedBooks", List.of());
            return "admin/report/book_report";
        }

        // Group transactions by book and count the number of loans
        Map<Book, Long> loanedBookCounts = allTransactions.stream()
                .filter(tx -> tx.getBook() != null)
                .collect(Collectors.groupingBy(BookTransactionsHistory::getBook, Collectors.counting()));

        // Sort books by loan count in descending order
        List<Map.Entry<Book, Long>> sortedLoanedBooks = loanedBookCounts.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .toList();

        // Get the most loaned books
        List<Book> mostLoanedBooks = sortedLoanedBooks.stream()
                .limit(3) // Top 3 most loaned books
                .map(Map.Entry::getKey)
                .toList();

        // Get the least loaned books
        List<Book> leastLoanedBooks = sortedLoanedBooks.stream()
                .skip(Math.max(0, sortedLoanedBooks.size() - 3)) // Bottom 3 least loaned books
                .map(Map.Entry::getKey)
                .toList();

        // Add the calculated values to the model
        model.addAttribute("totalBooksRegistered", totalBooksRegistered);
        model.addAttribute("totalAvailableBooks", totalAvailableBooks);
        model.addAttribute("totalLoanedBooks", totalLoanedBooks);
        model.addAttribute("mostLoanedBooks", mostLoanedBooks);
        model.addAttribute("leastLoanedBooks", leastLoanedBooks);

        return "admin/report/book_report"; // Path to the admin report page
    }

    // to view

}
