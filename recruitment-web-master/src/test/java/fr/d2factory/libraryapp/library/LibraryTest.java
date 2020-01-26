package fr.d2factory.libraryapp.library;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.exception.CanNotReturnBookException;
import fr.d2factory.libraryapp.exception.HasLateBooksException;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;

/**
 * Do not forget to consult the README.md :)
 */
public class LibraryTest {

	private LibraryImp library;
	private BookRepository bookRepository;
	private static List<Book> books;

	@BeforeEach
	void setup() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		File booksJson = new File("src/test/resources/books.json");
		books = mapper.readValue(booksJson, new TypeReference<List<Book>>() {
		});
		library = new LibraryImp();
		bookRepository = library.getBookRepository();
		bookRepository.addBooks(books);
	}

	@Test
	@DisplayName("Members can be added to the library")
	void add_member_to_library() {

		Member student = new Student();
		library.addMember(student);

		Member resident = new Resident();
		library.addMember(resident);

		Assertions.assertTrue(library.getMembers().size() == 2);

	}

	@Test
	@DisplayName("Null Members can't be added to the library")
	void null_members_cannot_be_added_to_library() {

		library.addMember(null);
		library.addMember(null);

		Assertions.assertTrue(library.getMembers().isEmpty());

	}

	@Test
	@DisplayName("We should be able to add new books to the library")
	void we_can_add_new_books_to_library() {

		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book("One Piece", "Eichiro Oda", new ISBN(111)));
		newBooks.add(new Book("Erased", "Kei Sanbe", new ISBN(222)));

		int expectedAvailableBooksSize = bookRepository.getAvailableBooks().size() + newBooks.size();

		bookRepository.addBooks(newBooks);

		Assertions.assertEquals(expectedAvailableBooksSize, bookRepository.getAvailableBooks().size());
	}

	@Test
	@DisplayName("We should not be able to add null books to the library")
	void we_cannot_add_null_books_to_library() {

		List<Book> badBooks = new ArrayList<Book>();
		badBooks.add(null);
		badBooks.add(null);

		int expectedAvailableBooksSize = bookRepository.getAvailableBooks().size();

		bookRepository.addBooks(badBooks);

		Assertions.assertEquals(expectedAvailableBooksSize, bookRepository.getAvailableBooks().size());
	}

	@Test
	@DisplayName("We should not be able to add a book with no ISBN to the library")
	void we_cannot_add_books_without_ISBN_to_library() {

		Book book = new Book("Temple", "S. Harry", null);

		List<Book> badBooks = new ArrayList<Book>();
		badBooks.add(book);

		int expectedAvailableBooksSize = bookRepository.getAvailableBooks().size();

		bookRepository.addBooks(badBooks);

		Assertions.assertEquals(expectedAvailableBooksSize, bookRepository.getAvailableBooks().size());
	}

	@Test
	@DisplayName("A member can borrow an available book")
	void member_can_borrow_an_available_book() {

		Resident resident = new Resident();

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now();

		library.borrowBook(isbnCode, resident, borrowedAt);

		Assertions.assertTrue(resident.getBorrowedBooks().contains(book));

	}

	@Test
	@DisplayName("The borrowed book should not be available and must be among the borrowed books")
	void borrowed_book_should_not_be_available() {

		Resident resident = new Resident();

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now();

		library.borrowBook(isbnCode, resident, borrowedAt);

		Assertions.assertTrue(bookRepository.findAvailableBook(isbnCode) == null);

		Assertions.assertTrue(bookRepository.getBorrowedBooks().containsKey(book));

	}

	@Test
	@DisplayName("Member can not borrow an unavailable book")
	void member_can_not_borrow_unavailable() {

		Resident resident = new Resident();

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now();

		library.borrowBook(isbnCode, resident, borrowedAt);

		Resident secondResident = new Resident();

		library.borrowBook(isbnCode, secondResident, borrowedAt);

		Assertions.assertFalse(secondResident.getBorrowedBooks().contains(book));

	}

	@Test
	@DisplayName("Member can return borrowed book")
	void member_can_return_borrowed_book() throws CanNotReturnBookException {

		Resident resident = new Resident();

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now();

		library.borrowBook(isbnCode, resident, borrowedAt);

		library.returnBook(book, resident);

		Assertions.assertFalse(resident.getBorrowedBooks().contains(book));

	}

	@Test
	@DisplayName("Returned book should be available")
	void returned_book_should_be_available() throws CanNotReturnBookException {

		Resident resident = new Resident();

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now();

		library.borrowBook(isbnCode, resident, borrowedAt);
		library.returnBook(book, resident);

		Assertions.assertTrue(bookRepository.findAvailableBook(isbnCode) != null);

	}

	@Test
	@DisplayName("Member can't pay the book if his new balance is under Zéro")
	void member_can_not_pay_the_book() {

		Resident resident = new Resident();

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.of(2020, Month.JANUARY, 24);

		library.borrowBook(isbnCode, resident, borrowedAt);

		Exception exception = Assertions.assertThrows(CanNotReturnBookException.class, () -> {
			library.returnBook(book, resident);
		});

		Assertions.assertTrue(exception.getMessage().contains("Member can't pay the book."));

	}

	@Test
	@DisplayName("Residents are taxed 10 cents for each day they keep a book")
	void residents_are_taxed_10cents_for_each_day_they_keep_a_book() throws CanNotReturnBookException {

		Resident resident = new Resident(1f);

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now().minusDays(3);

		library.borrowBook(isbnCode, resident, borrowedAt);

		library.returnBook(book, resident);

		Assertions.assertEquals(0.70f, resident.getWallet());

	}

	@Test
	@DisplayName("Students pay 10 cents the first 30 days")
	void students_pay_10_cents_the_first_30days() throws CanNotReturnBookException {

		Student student = new Student(30f);

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now().minusDays(30);

		library.borrowBook(isbnCode, student, borrowedAt);

		library.returnBook(book, student);

		Assertions.assertEquals(27f, student.getWallet());

	}

	@Test
	@DisplayName("Students in 1st year are not taxed for the first 15 days")
	void students_in_1st_year_are_not_taxed_for_the_first_15days() throws CanNotReturnBookException {

		Student student = new Student(30f, true);

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now().minusDays(15);

		library.borrowBook(isbnCode, student, borrowedAt);

		library.returnBook(book, student);

		Assertions.assertEquals(30f, student.getWallet());

	}

	@Test
	@DisplayName("Residents pay 20 cents for each day they keep a book after the initial 60 days")
	void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days()
			throws CanNotReturnBookException {
		Resident resident = new Resident(30f);

		long isbnCode = 46578964513L;
		Book book = new Book("Harry Potter", "J.K. Rowling", new ISBN(isbnCode));

		LocalDate borrowedAt = LocalDate.now().minusDays(67);

		library.borrowBook(isbnCode, resident, borrowedAt);

		library.returnBook(book, resident);

		Assertions.assertEquals(22.6f, resident.getWallet());
	}

	@Test
	@DisplayName("Members can't borrow book if they have late books")
	void members_cannot_borrow_book_if_they_have_late_books() {

		Resident resident = new Resident(30f);

		long isbnCode = 46578964513L;

		LocalDate borrowedAt = LocalDate.now().minusDays(67);

		library.borrowBook(isbnCode, resident, borrowedAt);

		long secondIsbnCode = 968787565445L;

		Exception exception = Assertions.assertThrows(HasLateBooksException.class, () -> {
			library.borrowBook(secondIsbnCode, resident, borrowedAt);
		});

		Assertions.assertEquals(exception.getMessage(), "Member has late books.");

	}

}
