package runner.command;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite command that can execute multiple commands as a single unit
 * Implements the Composite pattern combined with Command pattern
 */
@Slf4j
public class CompositeCommand implements Command {
    
    private final List<Command> commands = new ArrayList<>();
    private final String name;
    private final String description;
    private final boolean stopOnFirstFailure;
    
    public CompositeCommand(String name) {
        this(name, name + " composite command", true);
    }
    
    public CompositeCommand(String name, String description) {
        this(name, description, true);
    }
    
    public CompositeCommand(String name, String description, boolean stopOnFirstFailure) {
        this.name = name != null ? name : "CompositeCommand";
        this.description = description != null ? description : "Composite command";
        this.stopOnFirstFailure = stopOnFirstFailure;
    }
    
    /**
     * Add a command to this composite
     * @param command The command to add
     */
    public void addCommand(Command command) {
        if (command == null) {
            log.warn("Cannot add null command to composite");
            return;
        }
        
        commands.add(command);
        log.debug("Added command to composite '{}': {} (total: {})", name, command.getName(), commands.size());
    }
    
    /**
     * Remove a command from this composite
     * @param command The command to remove
     * @return true if the command was removed, false otherwise
     */
    public boolean removeCommand(Command command) {
        boolean removed = commands.remove(command);
        if (removed) {
            log.debug("Removed command from composite '{}': {} (remaining: {})", name, command.getName(), commands.size());
        }
        return removed;
    }
    
    /**
     * Clear all commands from this composite
     */
    public void clearCommands() {
        int size = commands.size();
        commands.clear();
        log.debug("Cleared {} commands from composite '{}'", size, name);
    }
    
    @Override
    public void execute() throws Exception {
        if (commands.isEmpty()) {
            log.debug("Composite command '{}' has no commands to execute", name);
            return;
        }
        
        log.info("Executing composite command '{}' with {} commands (stopOnFirstFailure: {})", 
            name, commands.size(), stopOnFirstFailure);
        
        List<Exception> exceptions = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (int i = 0; i < commands.size(); i++) {
            Command command = commands.get(i);
            
            try {
                if (command.canExecute()) {
                    command.execute();
                    successCount++;
                    log.debug("Command {} of {} executed successfully in composite '{}': {}", 
                        i + 1, commands.size(), name, command.getName());
                } else {
                    log.warn("Skipping command {} of {} in composite '{}' (cannot execute): {}", 
                        i + 1, commands.size(), name, command.getName());
                }
                
            } catch (Exception e) {
                failureCount++;
                exceptions.add(e);
                
                log.error("Command {} of {} failed in composite '{}': {} - {}", 
                    i + 1, commands.size(), name, command.getName(), e.getMessage());
                
                if (stopOnFirstFailure) {
                    throw new CompositeCommandException(
                        String.format("Composite command '%s' failed at command %d of %d: %s", 
                            name, i + 1, commands.size(), e.getMessage()),
                        e, command, i, successCount, failureCount, exceptions);
                }
            }
        }
        
        if (!exceptions.isEmpty() && !stopOnFirstFailure) {
            throw new CompositeCommandException(
                String.format("Composite command '%s' completed with %d failures out of %d commands", 
                    name, failureCount, commands.size()),
                exceptions.get(0), null, -1, successCount, failureCount, exceptions);
        }
        
        log.info("Composite command '{}' executed successfully: {} commands completed", name, successCount);
    }
    
    @Override
    public void undo() throws Exception {
        if (!canUndo()) {
            throw new UnsupportedOperationException("Composite command '" + name + "' does not support undo");
        }
        
        log.info("Undoing composite command '{}' with {} commands", name, commands.size());
        
        // Undo commands in reverse order
        List<Exception> exceptions = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (int i = commands.size() - 1; i >= 0; i--) {
            Command command = commands.get(i);
            
            try {
                if (command.canUndo()) {
                    command.undo();
                    successCount++;
                    log.debug("Command {} undone successfully in composite '{}': {}", 
                        i + 1, name, command.getName());
                } else {
                    log.debug("Skipping undo for command {} in composite '{}' (undo not supported): {}", 
                        i + 1, name, command.getName());
                }
                
            } catch (Exception e) {
                failureCount++;
                exceptions.add(e);
                
                log.error("Failed to undo command {} in composite '{}': {} - {}", 
                    i + 1, name, command.getName(), e.getMessage());
                
                if (stopOnFirstFailure) {
                    throw new CompositeCommandException(
                        String.format("Composite command '%s' undo failed at command %d: %s", 
                            name, i + 1, e.getMessage()),
                        e, command, i, successCount, failureCount, exceptions);
                }
            }
        }
        
        if (!exceptions.isEmpty() && !stopOnFirstFailure) {
            throw new CompositeCommandException(
                String.format("Composite command '%s' undo completed with %d failures", name, failureCount),
                exceptions.get(0), null, -1, successCount, failureCount, exceptions);
        }
        
        log.info("Composite command '{}' undone successfully: {} commands", name, successCount);
    }
    
    @Override
    public boolean canUndo() {
        return commands.stream().anyMatch(Command::canUndo);
    }
    
    @Override
    public boolean canExecute() {
        return !commands.isEmpty() && commands.stream().anyMatch(Command::canExecute);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description + " (" + commands.size() + " commands)";
    }
    
    /**
     * Get an unmodifiable view of the commands in this composite
     */
    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }
    
    /**
     * Get the number of commands in this composite
     */
    public int getCommandCount() {
        return commands.size();
    }
    
    /**
     * Check if this composite has any commands
     */
    public boolean isEmpty() {
        return commands.isEmpty();
    }
    
    /**
     * Check if stop on first failure is enabled
     */
    public boolean isStopOnFirstFailure() {
        return stopOnFirstFailure;
    }
    
    /**
     * Exception thrown when composite command execution fails
     */
    public static class CompositeCommandException extends RuntimeException {
        private final Command failedCommand;
        private final int commandIndex;
        private final int successCount;
        private final int failureCount;
        private final List<Exception> allExceptions;
        
        public CompositeCommandException(String message, Throwable cause, Command failedCommand, 
                                       int commandIndex, int successCount, int failureCount, 
                                       List<Exception> allExceptions) {
            super(message, cause);
            this.failedCommand = failedCommand;
            this.commandIndex = commandIndex;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.allExceptions = new ArrayList<>(allExceptions != null ? allExceptions : List.of());
        }
        
        public Command getFailedCommand() { return failedCommand; }
        public int getCommandIndex() { return commandIndex; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public List<Exception> getAllExceptions() { return Collections.unmodifiableList(allExceptions); }
    }
}