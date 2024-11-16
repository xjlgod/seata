package org.apache.seata.server.console.exception;

public class ConsoleException extends RuntimeException {
    /**
     * use for globalExceptionHandlerAdvice
     *
     * @see  org.apache.seata.server.console.aop.GlobalExceptionHandlerAdvice
     */
    private String logMessage;

    public ConsoleException(Throwable cause, String logMessage) {
        super(cause);
        this.logMessage = logMessage;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}
