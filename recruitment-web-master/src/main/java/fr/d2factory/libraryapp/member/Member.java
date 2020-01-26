package fr.d2factory.libraryapp.member;

import java.util.ArrayList;
import java.util.List;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.exception.CanNotPayBookException;
import fr.d2factory.libraryapp.library.Library;

/**
 * A member is a person who can borrow and return books to a {@link Library} A
 * member can be either a student or a resident
 */
public abstract class Member {
	/**
	 * An initial sum of money the member has
	 */
	private float wallet;

	private List<Book> borrowedBooks;

	protected static final float DAILY_FEE_FOR_BORROWED_BOOK = 0.10f;

	public Member() {
		borrowedBooks = new ArrayList<Book>();
	}

	public Member(float initialWallet) {
		wallet = initialWallet;
		borrowedBooks = new ArrayList<Book>();
	}

	/**
	 * The member should pay their books when they are returned to the library
	 *
	 * @param numberOfDays the number of days they kept the book
	 * @throws CanNotPayBookException
	 */
	public abstract void payBook(int numberOfDays) throws CanNotPayBookException;

	public abstract int getNumberOfDaysWhereConsideredLate();

	public float getWallet() {
		return wallet;
	}

	public void setWallet(float wallet) {
		this.wallet = wallet;
	}

	public List<Book> getBorrowedBooks() {
		return borrowedBooks;
	}

	public void setBorrowedBooks(List<Book> borrowedBooks) {
		this.borrowedBooks = borrowedBooks;
	}

	public void addBorrowedBook(Book borrowedBook) {
		if (!borrowedBooks.contains(borrowedBook)) {
			borrowedBooks.add(borrowedBook);
		}
	}

}
