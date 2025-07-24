package runner.command;

/**
 * Command interface for the Command pattern
 * Encapsulates a request as an object, allowing parameterization and queuing of requests
 */
public interface Command {
    
    /**
     * Execute the command
     * @throws Exception if execution fails
     */
    void execute() throws Exception;
    
    /**
     * Undo the command if possible
     * @throws Exception if undo fails
     * @throws UnsupportedOperationException if undo is not supported
     */
    default void undo() throws Exception {
        throw new UnsupportedOperationException("Undo operation not supported for " + getClass().getSimpleName());
    }
    
    /**
     * Check if this command supports undo operation
     * @return true if undo is supported, false otherwise
     */
    default boolean canUndo() {
        return false;
    }
    
    /**
     * Get a description of what this command does
     * @return Command description
     */
    String getDescription();
    
    /**
     * Get the command name for logging and debugging
     * @return Command name
     */
    default String getName() {
        return getClass().getSimpleName();
    }
    
    /**
     * Check if this command can be executed in the current state
     * @return true if the command can be executed, false otherwise
     */
    default boolean canExecute() {
        return true;
    }
}