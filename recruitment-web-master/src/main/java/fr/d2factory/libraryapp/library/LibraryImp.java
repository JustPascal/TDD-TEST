package fr.d2factory.libraryapp.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.exception.CanNotPayBookException;
import fr.d2factory.libraryapp.exception.CanNotReturnBookException;
import fr.d2factory.libraryapp.exception.HasLateBooksException;
import fr.d2factory.libraryapp.member.Member;

public class LibraryImp implements Library {

	List<Member> members;

	BookRepository bookRepository;

	public LibraryImp() {
		members = new ArrayList<Member>();
		bookRepository = new BookRepository();
	}

	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {

		checkIfMemberCanBorrowBook(member);

		Book book = bookRepository.findAvailableBook(isbnCode);

		if (book == null) {
			return null;
		}

		member.addBorrowedBook(book);

		bookRepository.saveBorrowedBook(book, borrowedAt);

		return book;
	}

	private void checkIfMemberCanBorrowBook(Member member) throws HasLateBooksException {

		for (Book book : member.getBorrowedBooks()) {

			bookRepository.findBorrowedBookDate(book);

			if (getNumberOfdaysOfBorrowedBook(book) > member.getNumberOfDaysWhereConsideredLate()) {
				throw new HasLateBooksException("Member has late books.");
			}

		}
	}

	@Override
	public void returnBook(Book book, Member member) throws CanNotReturnBookException {

		long numberOfDays = getNumberOfdaysOfBorrowedBook(book);

		try {
			member.payBook((int) numberOfDays);
		} catch (CanNotPayBookException e) {
			throw new CanNotReturnBookException(e.getMessage());
		}

		member.getBorrowedBooks().remove(book);

		bookRepository.makeBorrowedBookAvailable(book);
	}

	private long getNumberOfdaysOfBorrowedBook(Book book) {
		LocalDate borrowedAt = bookRepository.findBorrowedBookDate(book);
		LocalDate today = LocalDate.now();

		long numberOfDays = ChronoUnit.DAYS.between(borrowedAt, today);
		return numberOfDays;
	}

	public void addMember(Member member) {
		if (member != null)
			members.add(member);
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public List<Member> getMembers() {
		return members;
	}

	public BookRepository getBookRepository() {
		return bookRepository;
	}

	public void setBookRepository(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

}
