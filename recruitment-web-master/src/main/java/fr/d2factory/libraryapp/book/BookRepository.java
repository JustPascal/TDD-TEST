package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {
	private Map<ISBN, Book> availableBooks;
	private Map<Book, LocalDate> borrowedBooks;

	public BookRepository() {
		availableBooks = new HashMap<>();
		borrowedBooks = new HashMap<>();
	}

	public void addBooks(List<Book> books) {
		for(Book book : books) {
			addBook(book);
		}
	}

	public void addBook(Book book) {
		if(book != null && book.getIsbn() != null) {
			availableBooks.put(book.getIsbn(), book);
		}
	}

	public Book findAvailableBook(long isbnCode) {
		ISBN isbn = new ISBN(isbnCode);
		return availableBooks.containsKey(isbn) ? availableBooks.get(isbn) : null;
	}

	public void saveBorrowedBook(Book book, LocalDate borrowedAt) {
		borrowedBooks.put(book, borrowedAt);
		availableBooks.remove(book.getIsbn());
	}

	public LocalDate findBorrowedBookDate(Book book) {
		return borrowedBooks.get(book);
	}
	
	public void makeBorrowedBookAvailable(Book book) {
		borrowedBooks.remove(book);
		availableBooks.put(book.getIsbn(), book);
	}

	public Map<ISBN, Book> getAvailableBooks() {
		return availableBooks;
	}

	public void setAvailableBooks(Map<ISBN, Book> availableBooks) {
		this.availableBooks = availableBooks;
	}

	public Map<Book, LocalDate> getBorrowedBooks() {
		return borrowedBooks;
	}

	public void setBorrowedBooks(Map<Book, LocalDate> borrowedBooks) {
		this.borrowedBooks = borrowedBooks;
	}
}
