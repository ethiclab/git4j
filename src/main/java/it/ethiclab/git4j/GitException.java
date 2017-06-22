package it.ethiclab.git4j;

public class GitException extends RuntimeException {
    public GitException(Throwable cause) {
        super(cause);
    }
    public GitException(String message) {
        super(message);
    }
}
