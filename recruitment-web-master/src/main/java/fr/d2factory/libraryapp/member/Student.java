package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.exception.CanNotPayBookException;

public class Student extends Member {

	private boolean firstYear;

	public Student() {
	}

	public Student(float initialWallet) {
		super(initialWallet);
	}

	public Student(float initialWallet, boolean isFirstYear) {
		super(initialWallet);
		this.firstYear = isFirstYear;
	}

	@Override
	public void payBook(int numberOfDays) throws CanNotPayBookException {

		if (freeForFirstYearUnderFifteenDays(numberOfDays)) {
			return;
		}

		float moneyToPay = numberOfDays * DAILY_FEE_FOR_BORROWED_BOOK;

		float newBalance = getWallet() - moneyToPay;

		if (newBalance < 0) {
			throw new CanNotPayBookException("Member can't pay the book.");
		}
		setWallet(newBalance);
	}

	private boolean freeForFirstYearUnderFifteenDays(int numberOfDays) {
		return isFirstYear() && numberOfDays <= 15;
	}

	public boolean isFirstYear() {
		return firstYear;
	}

	public void setFirstYear(boolean firstYear) {
		this.firstYear = firstYear;
	}

	@Override
	public int getNumberOfDaysWhereConsideredLate() {
		return 30;
	}

}
