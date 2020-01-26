package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.exception.CanNotPayBookException;

public class Resident extends Member {

	public Resident() {
	}

	public Resident(float initialWallet) {
		super(initialWallet);
	}

	@Override
	public void payBook(int numberOfDays) throws CanNotPayBookException {

		float moneyToPay = calculateReturnBookPrice(numberOfDays);

		float newBalance = getWallet() - moneyToPay;

		if (newBalance < 0) {
			throw new CanNotPayBookException("Member can't pay the book.");
		}
		setWallet(newBalance);
	}

	private float calculateReturnBookPrice(int numberOfDays) {

		float moneyToPay;

		final float FEE_WHEN_CONSIDERED_LATE = 0.20f;

		final int NUMBER_OF_DAYS_WHERE_CONSIDERED_LATE = getNumberOfDaysWhereConsideredLate();

		if (numberOfDays > NUMBER_OF_DAYS_WHERE_CONSIDERED_LATE) {
			moneyToPay = NUMBER_OF_DAYS_WHERE_CONSIDERED_LATE * DAILY_FEE_FOR_BORROWED_BOOK;
			moneyToPay = moneyToPay + (numberOfDays - NUMBER_OF_DAYS_WHERE_CONSIDERED_LATE) * FEE_WHEN_CONSIDERED_LATE;
		} else {
			moneyToPay = numberOfDays * DAILY_FEE_FOR_BORROWED_BOOK;
		}
		return moneyToPay;
	}

	@Override
	public int getNumberOfDaysWhereConsideredLate() {
		return 60;
	}
}
