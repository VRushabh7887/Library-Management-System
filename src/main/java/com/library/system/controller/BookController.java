package com.library.system.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.system.entity.Book;
import com.library.system.entity.BookTransactionsHistory;
import com.library.system.entity.MyBook;
import com.library.system.service.BookService;
import com.library.system.service.BookTransactionHistoryService;
import com.library.system.service.MyBookListService;

@Controller
public class BookController {

    @Autowired
    private BookService service;
    @Autowired
    private MyBookListService myBookService;
    @Autowired
    private BookTransactionHistoryService bookTransactionHistoryService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    // user related methods
    @GetMapping("/user_home")
    public String adminHome(Model model, Authentication authentication) {
        String username = authentication.getName();

        model.addAttribute("userName", username);
        return "user/user_home";
    }

    @GetMapping("/user/available_books")
    public ModelAndView getAllBook() {
        List<Book> list = service.getAllBook();
        // if we want to fetch only availbale books then use below code
        // .stream()
        // .filter(Book::isAvailable)
        // .toList();
        return new ModelAndView("user/bookList", "book", list);
    }

    @GetMapping("/user/my_books")
    public String getMyBooks(Model model, Authentication authentication) {
        String username = authentication.getName(); // Get the logged-in user's username

        // Fetch all books loaned to the user
        List<MyBook> myBooks = myBookService.getAllMyBooks().stream()
                // .filter(book -> username.equals(book.getLoanedTo())) // Filter books loaned
                // to the user
                .filter(book -> username.equalsIgnoreCase(book.getLoanedTo()) && book.getReturnDate() == null)
                .toList();
        model.addAttribute("book", myBooks);

        return "user/myBooks";
    }

    @PostMapping("/user/loanBook/{id}")
    public String loanBook(@PathVariable("id") int id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // Redirect to login page if not authenticated
        }

        try {
            Book book = service.getBookById(id); // Fetch the book by ID
            if (book != null && book.getCopiesAvailable() > 0) { // Check if the book is available
                // Save the book to the user's issued books (MyBooks table)
                MyBook myBook = new MyBook();
                myBook.setBook(book);
                // myBook.setId(book.getId());
                myBook.setName(book.getName());
                myBook.setAuthor(book.getAuthor());
                myBook.setPrice(book.getPrice());
                myBook.setLoanedTo(authentication.getName());
                myBook.setLoanDate(LocalDate.now()); // Set the logged-in user's username
                myBookService.saveMyBook(myBook);

                // decrease the number of available copies
                book.setCopiesAvailable(book.getCopiesAvailable() - 1);
                book.setAvailable(book.getCopiesAvailable() > 0);
                // Update the book's availability and loan details
                book.setLoaned(true);
                // book.setAvailable(false);
                book.setLoanDate(LocalDate.now());
                book.setLoanedTo(authentication.getName());
                service.save(book);

                redirectAttributes.addFlashAttribute("successMessage", "Book issued successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Book is not available for loan.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error issuing book: " + e.getMessage());
        }

        return "redirect:/user/available_books";
    }

    // after adding new table

    // checking new way to return book - 09/06
    // using myBook records unique id
    @PostMapping("/user/returnBook/{myBookId}")
    public String returnBook(@PathVariable("myBookId") int myBookId, Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();
        MyBook myBook = myBookService.getMyBookById(myBookId);

        if (myBook != null && username.equalsIgnoreCase(myBook.getLoanedTo()) && myBook.getReturnDate() == null) {
            Book book = myBook.getBook();

            // Create a new transaction history record
            BookTransactionsHistory history = new BookTransactionsHistory();
            history.setBook(book);
            history.setBookName(book.getName());
            history.setBookAuthor(book.getAuthor());
            history.setLoanedTo(myBook.getLoanedTo());
            history.setLoanDate(myBook.getLoanDate());
            history.setReturnDate(LocalDate.now());
            bookTransactionHistoryService.saveTransaction(history);

            // Delete the active loan record
            myBookService.deleteMyBook(myBook);

            // Increase available copies after return of book
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
            book.setAvailable(book.getCopiesAvailable() > 0);
            book.setLoaned(false);
            book.setLoanedTo(null);
            book.setLoanDate(null);
            service.save(book);

            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No active book record found for this book.");
        }
        return "redirect:/user/my_books";
    }

}
