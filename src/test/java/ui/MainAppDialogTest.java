package ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@ExtendWith(ApplicationExtension.class)
public class MainAppDialogTest {

    @Start
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }

    @Test
    public void shouldOpenAddDialogFromMainWindow(FxRobot robot) {
        robot.clickOn("#addVagonButton");
        robot.targetWindow("Додати вагон");
        verifyThat("#komfortnistField", isVisible());
        robot.clickOn("Скасувати"); // Закриваємо
        robot.targetWindow(robot.window(0));
    }

    @Test
    public void shouldRejectInvalidPassengerInputInAddDialog(FxRobot robot) {
        robot.clickOn("#addVagonButton");
        robot.targetWindow("Додати вагон");
        verifyThat("#komfortnistField", isVisible());

        robot.clickOn("#typePassengerRadio");
        
        robot.clickOn("#komfortnistField").write("0");
        robot.clickOn("#bagazhField").write("-1");
        
        robot.clickOn("Зберегти");
        
        // Перевіряємо, що діалог усе ще видимий, оскільки валідація не пройшла
        verifyThat("#komfortnistField", isVisible());
        robot.clickOn("Скасувати"); // Закриваємо
        robot.targetWindow(robot.window(0));
    }

    @Test
    public void shouldSwitchBetweenPassengerAndServiceFields(FxRobot robot) {
        robot.clickOn("#addVagonButton");
        robot.targetWindow("Додати вагон");
        verifyThat("#komfortnistField", isVisible());
        
        robot.clickOn("#typeServiceRadio");
        verifyThat("#personalField", isVisible());
        
        robot.clickOn("#typePassengerRadio");
        verifyThat("#pasazhyrivField", isVisible());
        
        robot.clickOn("Скасувати");
        robot.targetWindow(robot.window(0));
    }
}
