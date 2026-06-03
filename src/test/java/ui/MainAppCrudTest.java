package ui;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Vagon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@ExtendWith(ApplicationExtension.class)
public class MainAppCrudTest {

    @Start
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddValidPassengerVagonThroughDialog(FxRobot robot) {
        verifyThat("#vagonTable", isVisible());
        TableView<Vagon> table = robot.lookup("#vagonTable").queryAs(TableView.class);
        int initialRows = table.getItems().size();

        robot.clickOn("#addVagonButton");
        robot.targetWindow("Додати вагон");
        verifyThat("#komfortnistField", isVisible());

        robot.clickOn("#typePassengerRadio");
        robot.clickOn("#komfortnistField").write("8");
        robot.clickOn("#bagazhField").write("50");
        robot.clickOn("#pasazhyrivField").write("36");
        robot.clickOn("#rivenField").write("5");
        
        robot.clickOn("Зберегти");
        robot.targetWindow(robot.window(0));
        
        assertEquals(initialRows + 1, table.getItems().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDeleteSelectedVagonFromTable(FxRobot robot) {
        verifyThat("#vagonTable", isVisible());

        TableView<Vagon> table = robot.lookup("#vagonTable").queryAs(TableView.class);
        
        // Якщо таблиця порожня, спочатку додаємо вагон
        if (table.getItems().isEmpty()) {
            robot.clickOn("#addVagonButton");
            robot.targetWindow("Додати вагон");
            robot.clickOn("#komfortnistField").write("5");
            robot.clickOn("#bagazhField").write("20");
            robot.clickOn("#pasazhyrivField").write("10");
            robot.clickOn("#rivenField").write("3");
            robot.clickOn("Зберегти");
            robot.targetWindow(robot.window(0));
        }
        
        int initialRows = table.getItems().size();

        // Вибираємо перший рядок
        robot.clickOn(".table-row-cell");
        
        robot.clickOn("#deleteVagonButton");
        robot.targetWindow("Підтвердження");
        
        // Підтверджуємо видалення в Alert
        try {
            // ButtonType.YES за замовчуванням у JavaFX має текст "Yes" (або "Так" в українській локалі)
            // TestFX намагається знайти вузол з текстом або селектором
            robot.clickOn("Yes"); 
        } catch (Exception e) {
            try {
                robot.clickOn("Так");
            } catch (Exception ignored) {
                try {
                    robot.clickOn("OK");
                } catch (Exception ignore2) {}
            }
        }
        
        robot.targetWindow(robot.window(0));

        assertEquals(initialRows - 1, table.getItems().size());
    }
}
