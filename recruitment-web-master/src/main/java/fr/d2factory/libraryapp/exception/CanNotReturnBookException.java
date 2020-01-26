package fr.d2factory.libraryapp.exception;

public class CanNotReturnBookException extends Exception {
	
	private static final long serialVersionUID = -3929554000868729978L;

	public CanNotReturnBookException(String message) {
        super(message);
    }
}
