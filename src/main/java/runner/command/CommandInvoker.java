package runner.command;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Invoker class for the Command pattern
 * Manages command execution, queuing, and undo operations
 */
@Slf4j
public class CommandInvoker {
    
    private final List<Command> commandQueue = new ArrayList<>();
    private final Stack<Command> executedCommands = new Stack<>();
    private final boolean enableUndo;
    
    public CommandInvoker() {
        this(false);
    }
    
    public CommandInvoker(boolean enableUndo) {
        this.enableUndo = enableUndo;
    }
    
    /**
     * Add a command to the execution queue
     * @param command The command to add
     */
    public void addCommand(Command command) {
        if (command == null) {
            log.warn("Cannot add null command to queue");
            return;
        }
        
        commandQueue.add(command);
        log.debug("Added command to queue: {} (queue size: {})", command.getName(), commandQueue.size());
    }
    
    /**
     * Execute a single command immediately
     * @param command The command to execute
     * @throws Exception if execution fails
     */
    public void executeCommand(Command command) throws Exception {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        
        if (!command.canExecute()) {
            throw new IllegalStateException("Command cannot be executed: " + command.getName());
        }
        
        log.debug("Executing command: {}", command.getName());
        
        try {
            command.execute();
            
            // Add to executed commands stack for potential undo
            if (enableUndo && command.canUndo()) {
                executedCommands.push(command);
                log.trace("Command added to undo stack: {}", command.getName());
            }
            
            log.debug("Command executed successfully: {}", command.getName());
            
        } catch (Exception e) {
            log.error("Command execution failed: {} - {}", command.getName(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Execute all queued commands in order
     * @throws Exception if any command execution fails
     */
    public void executeAll() throws Exception {
        if (commandQueue.isEmpty()) {
            log.debug("No commands in queue to execute");
            return;
        }
        
        log.info("Executing {} queued commands", commandQueue.size());
        
        List<Command> commandsToExecute = new ArrayList<>(commandQueue);
        commandQueue.clear();
        
        for (int i = 0; i < commandsToExecute.size(); i++) {
            Command command = commandsToExecute.get(i);
            
            try {
                executeCommand(command);
            } catch (Exception e) {
                log.error("Failed to execute command {} of {}: {}", i + 1, commandsToExecute.size(), e.getMessage());
                
                // Re-add remaining commands back to queue if needed
                for (int j = i + 1; j < commandsToExecute.size(); j++) {
                    commandQueue.add(commandsToExecute.get(j));
                }
                
                throw new CommandExecutionException(
                    String.format("Command execution failed at position %d of %d", i + 1, commandsToExecute.size()),
                    e, command, i);
            }
        }
        
        log.info("All {} commands executed successfully", commandsToExecute.size());
    }
    
    /**
     * Undo the last executed command
     * @throws Exception if undo fails
     * @throws UnsupportedOperationException if undo is not enabled
     */
    public void undoLast() throws Exception {
        if (!enableUndo) {
            throw new UnsupportedOperationException("Undo is not enabled for this invoker");
        }
        
        if (executedCommands.isEmpty()) {
            log.warn("No commands to undo");
            return;
        }
        
        Command lastCommand = executedCommands.pop();
        log.debug("Undoing command: {}", lastCommand.getName());
        
        try {
            lastCommand.undo();
            log.debug("Command undone successfully: {}", lastCommand.getName());
        } catch (Exception e) {
            log.error("Failed to undo command: {} - {}", lastCommand.getName(), e.getMessage());
            // Put the command back on the stack if undo failed
            executedCommands.push(lastCommand);
            throw e;
        }
    }
    
    /**
     * Clear the command queue without executing
     */
    public void clearQueue() {
        int size = commandQueue.size();
        commandQueue.clear();
        log.debug("Cleared {} commands from queue", size);
    }
    
    /**
     * Clear the executed commands history
     */
    public void clearHistory() {
        if (!enableUndo) {
            return;
        }
        
        int size = executedCommands.size();
        executedCommands.clear();
        log.debug("Cleared {} commands from history", size);
    }
    
    /**
     * Get the number of queued commands
     */
    public int getQueueSize() {
        return commandQueue.size();
    }
    
    /**
     * Get the number of executed commands (for undo)
     */
    public int getHistorySize() {
        return enableUndo ? executedCommands.size() : 0;
    }
    
    /**
     * Check if there are commands in the queue
     */
    public boolean hasQueuedCommands() {
        return !commandQueue.isEmpty();
    }
    
    /**
     * Check if there are commands that can be undone
     */
    public boolean canUndo() {
        return enableUndo && !executedCommands.isEmpty();
    }
    
    /**
     * Check if undo is enabled
     */
    public boolean isUndoEnabled() {
        return enableUndo;
    }
    
    /**
     * Get a copy of the current command queue
     */
    public List<Command> getQueuedCommands() {
        return new ArrayList<>(commandQueue);
    }
    
    /**
     * Exception thrown when command execution fails
     */
    public static class CommandExecutionException extends RuntimeException {
        private final Command failedCommand;
        private final int commandIndex;
        
        public CommandExecutionException(String message, Throwable cause, Command failedCommand, int commandIndex) {
            super(message, cause);
            this.failedCommand = failedCommand;
            this.commandIndex = commandIndex;
        }
        
        public Command getFailedCommand() { return failedCommand; }
        public int getCommandIndex() { return commandIndex; }
    }
}