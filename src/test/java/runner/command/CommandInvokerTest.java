package runner.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import runner.TestBase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CommandInvoker Tests")
class CommandInvokerTest extends TestBase {
    
    private CommandInvoker invoker;
    private CommandInvoker undoableInvoker;
    
    @Mock
    private Command mockCommand1;
    
    @Mock
    private Command mockCommand2;
    
    @Mock
    private Command mockUndoableCommand;
    
    @BeforeEach
    @Override
    protected void setUpTest() {
        invoker = new CommandInvoker();
        undoableInvoker = new CommandInvoker(true);
        
        when(mockCommand1.getName()).thenReturn("MockCommand1");
        when(mockCommand2.getName()).thenReturn("MockCommand2");
        when(mockUndoableCommand.getName()).thenReturn("UndoableCommand");
        
        when(mockCommand1.canExecute()).thenReturn(true);
        when(mockCommand2.canExecute()).thenReturn(true);
        when(mockUndoableCommand.canExecute()).thenReturn(true);
        when(mockUndoableCommand.canUndo()).thenReturn(true);
    }
    
    @Test
    @DisplayName("Should add commands to queue")
    void shouldAddCommandsToQueue() {
        // When
        invoker.addCommand(mockCommand1);
        invoker.addCommand(mockCommand2);
        
        // Then
        assertEquals(2, invoker.getQueueSize());
        assertTrue(invoker.hasQueuedCommands());
    }
    
    @Test
    @DisplayName("Should not add null commands")
    void shouldNotAddNullCommands() {
        // When
        invoker.addCommand(null);
        
        // Then
        assertEquals(0, invoker.getQueueSize());
        assertFalse(invoker.hasQueuedCommands());
    }
    
    @Test
    @DisplayName("Should execute single command")
    void shouldExecuteSingleCommand() throws Exception {
        // When
        invoker.executeCommand(mockCommand1);
        
        // Then
        verify(mockCommand1).execute();
    }
    
    @Test
    @DisplayName("Should not execute command that cannot execute")
    void shouldNotExecuteCommandThatCannotExecute() {
        // Given
        when(mockCommand1.canExecute()).thenReturn(false);
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> invoker.executeCommand(mockCommand1));
        verify(mockCommand1, never()).execute();
    }
    
    @Test
    @DisplayName("Should execute all queued commands")
    void shouldExecuteAllQueuedCommands() throws Exception {
        // Given
        invoker.addCommand(mockCommand1);
        invoker.addCommand(mockCommand2);
        
        // When
        invoker.executeAll();
        
        // Then
        verify(mockCommand1).execute();
        verify(mockCommand2).execute();
        assertEquals(0, invoker.getQueueSize());
    }
    
    @Test
    @DisplayName("Should handle command execution failure")
    void shouldHandleCommandExecutionFailure() throws Exception {
        // Given
        doThrow(new RuntimeException("Command failed")).when(mockCommand1).execute();
        invoker.addCommand(mockCommand1);
        invoker.addCommand(mockCommand2);
        
        // When & Then
        assertThrows(CommandInvoker.CommandExecutionException.class, () -> invoker.executeAll());
        
        // Second command should be back in queue
        assertEquals(1, invoker.getQueueSize());
        verify(mockCommand1).execute();
        verify(mockCommand2, never()).execute();
    }
    
    @Test
    @DisplayName("Should support undo operations when enabled")
    void shouldSupportUndoOperationsWhenEnabled() throws Exception {
        // Given
        undoableInvoker.executeCommand(mockUndoableCommand);
        
        // When
        undoableInvoker.undoLast();
        
        // Then
        verify(mockUndoableCommand).execute();
        verify(mockUndoableCommand).undo();
        assertEquals(0, undoableInvoker.getHistorySize());
    }
    
    @Test
    @DisplayName("Should not support undo when disabled")
    void shouldNotSupportUndoWhenDisabled() {
        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> invoker.undoLast());
        assertFalse(invoker.isUndoEnabled());
        assertFalse(invoker.canUndo());
    }
    
    @Test
    @DisplayName("Should handle undo with no commands")
    void shouldHandleUndoWithNoCommands() {
        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> undoableInvoker.undoLast());
    }
    
    @Test
    @DisplayName("Should handle undo failure")
    void shouldHandleUndoFailure() throws Exception {
        // Given
        doThrow(new RuntimeException("Undo failed")).when(mockUndoableCommand).undo();
        undoableInvoker.executeCommand(mockUndoableCommand);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> undoableInvoker.undoLast());
        
        // Command should still be in history after failed undo
        assertEquals(1, undoableInvoker.getHistorySize());
    }
    
    @Test
    @DisplayName("Should clear queue")
    void shouldClearQueue() {
        // Given
        invoker.addCommand(mockCommand1);
        invoker.addCommand(mockCommand2);
        
        // When
        invoker.clearQueue();
        
        // Then
        assertEquals(0, invoker.getQueueSize());
        assertFalse(invoker.hasQueuedCommands());
    }
    
    @Test
    @DisplayName("Should clear history when undo enabled")
    void shouldClearHistoryWhenUndoEnabled() throws Exception {
        // Given
        undoableInvoker.executeCommand(mockUndoableCommand);
        
        // When
        undoableInvoker.clearHistory();
        
        // Then
        assertEquals(0, undoableInvoker.getHistorySize());
        assertFalse(undoableInvoker.canUndo());
    }
    
    @Test
    @DisplayName("Should get queued commands copy")
    void shouldGetQueuedCommandsCopy() {
        // Given
        invoker.addCommand(mockCommand1);
        invoker.addCommand(mockCommand2);
        
        // When
        var queuedCommands = invoker.getQueuedCommands();
        
        // Then
        assertEquals(2, queuedCommands.size());
        assertTrue(queuedCommands.contains(mockCommand1));
        assertTrue(queuedCommands.contains(mockCommand2));
        
        // Modifying returned list should not affect internal queue
        queuedCommands.clear();
        assertEquals(2, invoker.getQueueSize());
    }
    
    @Test
    @DisplayName("Should only add undoable commands to history")
    void shouldOnlyAddUndoableCommandsToHistory() throws Exception {
        // Given
        when(mockCommand1.canUndo()).thenReturn(false);
        
        // When
        undoableInvoker.executeCommand(mockCommand1);
        undoableInvoker.executeCommand(mockUndoableCommand);
        
        // Then
        assertEquals(1, undoableInvoker.getHistorySize()); // Only undoable command
        assertTrue(undoableInvoker.canUndo());
    }
    
    @Test
    @DisplayName("CommandExecutionException should contain failure details")
    void commandExecutionExceptionShouldContainFailureDetails() throws Exception {
        // Given
        RuntimeException cause = new RuntimeException("Command failed");
        doThrow(cause).when(mockCommand1).execute();
        invoker.addCommand(mockCommand1);
        
        // When
        CommandInvoker.CommandExecutionException exception = assertThrows(
            CommandInvoker.CommandExecutionException.class, 
            () -> invoker.executeAll()
        );
        
        // Then
        assertEquals(mockCommand1, exception.getFailedCommand());
        assertEquals(0, exception.getCommandIndex());
        assertEquals(cause, exception.getCause());
    }
}