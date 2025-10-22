package database;
/**
 * Eccezione unchecked usata per incapsulare errori di accesso dati
 * e rispettare le regole Sonar (niente doppio handling/log+rethrow).
 */
public class DataAccessException extends RuntimeException {
	public DataAccessException(String message) {
		super(message);
	}
	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
