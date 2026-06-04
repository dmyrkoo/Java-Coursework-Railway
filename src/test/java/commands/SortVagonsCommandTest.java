package commands;

import org.junit.jupiter.api.Test;
import services.PotiagService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SortVagonsCommandTest {

    @Test
    void testExecute_PassesParametersToService() {
        // Arrange
        PotiagService serviceMock = mock(PotiagService.class);
        String criterion = "За ID";
        boolean isDesc = true;
        SortVagonsCommand command = new SortVagonsCommand(serviceMock, criterion, isDesc);

        // Act
        command.execute();

        // Assert
        // Перевіряємо, що команда правильно передала критерій та прапорець isDesc
        verify(serviceMock).sortuvaty(criterion, isDesc);
    }

    @Test
    void testGetDescription_ContainsParameters() {
        // Arrange
        PotiagService serviceMock = mock(PotiagService.class);
        SortVagonsCommand command = new SortVagonsCommand(serviceMock, "За комфортністю", false);

        // Act
        String desc = command.getDescription();

        // Assert
        assertEquals("3. Сортувати вагони (За комфортністю, DESC: false)", desc);
    }
}
