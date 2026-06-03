package ui;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Vagon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@ExtendWith(ApplicationExtension.class)
public class MainAppInteractionTest {

    @Start
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSelectRowAndHighlightTrainScheme(FxRobot robot) {
        verifyThat("#vagonTable", isVisible());

        TableView<Vagon> table = robot.lookup("#vagonTable").queryAs(TableView.class);

        // Переконуємось, що маємо хоча б один вагон
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

        Vagon firstVagon = table.getItems().get(0);
        int vagonId = firstVagon.getId();

        // Клікаємо на перший рядок таблиці
        robot.clickOn(".table-row-cell");

        // Очікуваний ID вагону в схемі потяга
        String wagonNodeId = "#wagonRect-" + vagonId;
        verifyThat(wagonNodeId, isVisible());

        Label wagonLabel = robot.lookup(wagonNodeId).queryAs(Label.class);

        // Перевіряємо наявність стилю підсвітки (у нашому випадку "bold")
        assertTrue(wagonLabel.getStyle().contains("bold"), 
            "Вузол вагону у візуальній схемі потяга має бути підсвічений (bold)");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSortTableBySelectedCriterion(FxRobot robot) {
        verifyThat("#vagonTable", isVisible());
        TableView<Vagon> table = robot.lookup("#vagonTable").queryAs(TableView.class);
        
        if (table.getItems().size() < 2) {
            robot.clickOn("#addVagonButton");
            robot.targetWindow("Додати вагон");
            robot.clickOn("#komfortnistField").write("9");
            robot.clickOn("#bagazhField").write("10");
            robot.clickOn("#pasazhyrivField").write("10");
            robot.clickOn("#rivenField").write("3");
            robot.clickOn("Зберегти");
            robot.targetWindow(robot.window(0));
            
            robot.clickOn("#addVagonButton");
            robot.targetWindow("Додати вагон");
            robot.clickOn("#komfortnistField").write("2");
            robot.clickOn("#bagazhField").write("50");
            robot.clickOn("#pasazhyrivField").write("20");
            robot.clickOn("#rivenField").write("1");
            robot.clickOn("Зберегти");
            robot.targetWindow(robot.window(0));
        }
        
        robot.clickOn("#sortCombo");
        robot.clickOn("За комфортністю");
        robot.clickOn("#sortBtn");
        
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        
        Vagon firstVagon = table.getItems().get(0);
        Vagon secondVagon = table.getItems().get(1);
        
        assertTrue(firstVagon.getKomfortnist() >= secondVagon.getKomfortnist());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldFindVagonsByPassengerRange(FxRobot robot) {
        verifyThat("#vagonTable", isVisible());
        TableView<Vagon> table = robot.lookup("#vagonTable").queryAs(TableView.class);

        robot.clickOn("#minField").write("10");
        robot.clickOn("#maxField").write("30");
        robot.clickOn("#findBtn");
        
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        
        for (Vagon v : table.getItems()) {
            assertTrue(v.getPasazhyrskaMistkist() >= 10 && v.getPasazhyrskaMistkist() <= 30);
        }
    }

    @Test
    public void shouldShowAlertWhenSearchInputIsInvalid(FxRobot robot) {
        verifyThat("#minField", isVisible());
        
        robot.clickOn("#minField").write("invalid");
        robot.clickOn("#findBtn");
        
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        
        // Alert dialog has class .dialog-pane
        verifyThat(".dialog-pane", isVisible());
        robot.clickOn("OK");
    }

    @Test
    public void shouldUpdateStatusBarAfterAction(FxRobot robot) {
        verifyThat("#statusLeftLabel", isVisible());
        
        robot.clickOn("#sortCombo");
        robot.clickOn("За багажем");
        robot.clickOn("#sortBtn");
        
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
        
        Label statusLabel = robot.lookup("#statusLeftLabel").queryAs(Label.class);
        assertTrue(statusLabel.getText().contains("За багажем"));
    }
}
