package ui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

import model.*;
import repository.SqliteVagonRepository;
import services.PotiagService;
import services.SkladService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Головний клас графічного інтерфейсу програми.
 * Запуск: {@code mvn javafx:run}
 */
public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    private Potiag potiag;
    private PotiagService potiagService;
    private SkladService skladService;
    private SqliteVagonRepository repository;
    private ObservableList<Vagon> vagonList;
    private TableView<Vagon> tableView;
    private int nextId = 1;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Ініціалізація моделі та сервісів
            potiag = new Potiag("Lviv-Kyiv Express");
            repository = new SqliteVagonRepository();
            potiagService = new PotiagService(potiag);
            skladService = new SkladService(potiag, repository);
            logger.info("Сервіси ініціалізовано для потяга '{}'", potiag.getNazva());

            // Завантаження даних з БД
            zavantazhytyDani();

            // Визначення наступного ID на основі завантажених даних
            for (Vagon v : potiag.getSklad()) {
                if (v.getId() >= nextId) {
                    nextId = v.getId() + 1;
                }
            }

        } catch (Exception e) {
            logger.error("Критична помилка ініціалізації додатку", e);
            showErrorAndExit("Не вдалося запустити додаток: " + e.getMessage());
            return;
        }

        // Побудова інтерфейсу
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Заголовок
        Label titleLabel = new Label("Управління рухомим складом потяга");
        titleLabel.setFont(Font.font("System", 18));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);

        // Таблиця вагонів
        tableView = createTableView();
        root.setCenter(tableView);

        // Кнопки
        HBox buttonBar = createButtonBar();
        root.setBottom(buttonBar);

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Потяг — " + potiag.getNazva());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            try {
                repository.close();
                logger.info("Додаток закрито, з'єднання з БД закрито");
            } catch (Exception ex) {
                logger.error("Помилка при закритті з'єднання з БД", ex);
            }
        });
        primaryStage.show();
    }

    /**
     * Завантажує вагони з бази даних у модель та заповнює таблицю.
     */
    private void zavantazhytyDani() {
        try {
            var vagons = repository.getAllVagons();
            for (Vagon v : vagons) {
                potiag.dodatyVagon(v);
            }
            vagonList = FXCollections.observableArrayList(potiag.getSklad());
            logger.info("Завантажено {} вагонів з БД для UI", vagonList.size());
        } catch (Exception e) {
            logger.error("Помилка завантаження даних з бази даних", e);
            vagonList = FXCollections.observableArrayList();
            showWarning("Не вдалося завантажити дані з БД.\nПрограма запущена з порожнім складом.");
        }
    }

    /**
     * Створює {@link TableView} з колонками для відображення вагонів.
     */
    @SuppressWarnings("unchecked")
    private TableView<Vagon> createTableView() {
        TableView<Vagon> table = new TableView<>(vagonList);

        // ID
        TableColumn<Vagon, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        idCol.setPrefWidth(50);

        // Тип
        TableColumn<Vagon, String> typeCol = new TableColumn<>("Тип");
        typeCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getType()));
        typeCol.setPrefWidth(120);

        // Комфортність
        TableColumn<Vagon, Integer> komfCol = new TableColumn<>("Комфортність");
        komfCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getKomfortnist()));
        komfCol.setPrefWidth(120);

        // Кількість пасажирів
        TableColumn<Vagon, Integer> pasCol = new TableColumn<>("Пасажирів");
        pasCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPasazhyrskaMistkist()));
        pasCol.setPrefWidth(110);

        // Багаж
        TableColumn<Vagon, Integer> bagCol = new TableColumn<>("Багаж");
        bagCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getBagazhKilkist()));
        bagCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, typeCol, komfCol, pasCol, bagCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Склад порожній — додайте вагон"));

        return table;
    }

    /**
     * Створює панель кнопок у нижній частині вікна.
     */
    private HBox createButtonBar() {
        Button addBtn = new Button("Додати вагон");
        addBtn.setOnAction(e -> onDodaty());

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> onVydalyty());

        Button sortBtn = new Button("Сортувати");
        sortBtn.setOnAction(e -> onSortuvaty());

        Button findBtn = new Button("Знайти");
        findBtn.setOnAction(e -> onZnayty());

        HBox hbox = new HBox(10, addBtn, deleteBtn, sortBtn, findBtn);
        hbox.setPadding(new Insets(10, 0, 0, 0));
        hbox.setAlignment(Pos.CENTER);

        return hbox;
    }

    // ========== Обробники кнопок ==========

    private void onDodaty() {
        try {
            logger.info("Натиснуто кнопку 'Додати вагон'");
            AddVagonDialog dialog = new AddVagonDialog(nextId);
            Optional<Vagon> result = dialog.showAndWait();

            if (result.isPresent()) {
                Vagon vagon = result.get();
                skladService.dodatyVagon(vagon);
                nextId++;
                refreshTable();
                logger.info("Додано вагон ID={} ({}) через UI", vagon.getId(), vagon.getType());
            }
        } catch (Exception e) {
            logger.error("Помилка при додаванні вагону через UI", e);
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не вдалося додати вагон: " + e.getMessage());
            alert.setTitle("Помилка");
            alert.setHeaderText("Помилка збереження");
            alert.showAndWait();
        }
    }

    private void onVydalyty() {
        try {
            Vagon selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showWarning("Оберіть вагон для видалення.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Видалити вагон #" + selected.getId() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Підтвердження");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        potiag.vydalytyVagon(selected.getId());
                        repository.deleteVagon(selected.getId());
                        refreshTable();
                        logger.info("Видалено вагон ID={}", selected.getId());
                    } catch (Exception ex) {
                        logger.error("Помилка видалення вагону ID={} з БД", selected.getId(), ex);
                        showWarning("Помилка видалення з бази даних: " + ex.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Помилка при видаленні вагону через UI", e);
            showWarning("Не вдалося видалити вагон: " + e.getMessage());
        }
    }

    private void onSortuvaty() {
        try {
            potiagService.sortuvatyZaKomfortom();
            refreshTable();
            logger.info("Виконано сортування за комфортністю");
        } catch (Exception e) {
            logger.error("Помилка сортування вагонів", e);
            showWarning("Не вдалося виконати сортування: " + e.getMessage());
        }
    }

    private void onZnayty() {
        try {
            TextInputDialog minDialog = new TextInputDialog("0");
            minDialog.setTitle("Пошук вагонів");
            minDialog.setHeaderText("Знайти вагони за кількістю пасажирів");
            minDialog.setContentText("Мін. пасажирів:");

            minDialog.showAndWait().ifPresent(minStr -> {
                TextInputDialog maxDialog = new TextInputDialog("100");
                maxDialog.setTitle("Пошук вагонів");
                maxDialog.setHeaderText(null);
                maxDialog.setContentText("Макс. пасажирів:");

                maxDialog.showAndWait().ifPresent(maxStr -> {
                    try {
                        int min = Integer.parseInt(minStr.trim());
                        int max = Integer.parseInt(maxStr.trim());
                        var result = potiagService.znaytyVagonyZaPasazhyramy(min, max);
                        vagonList.setAll(result);
                        showInfo("Знайдено " + result.size() + " вагонів у діапазоні " + min + "–" + max +
                                ".\nНатисніть 'Сортувати' для повернення до повного списку.");
                        logger.info("Пошук: знайдено {} вагонів у діапазоні {}-{}", result.size(), min, max);
                    } catch (NumberFormatException ex) {
                        logger.error("Некоректний ввід при пошуку вагонів: min='{}', max='{}'", minStr, maxStr, ex);
                        showWarning("Введіть коректні числа.");
                    }
                });
            });
        } catch (Exception e) {
            logger.error("Помилка при пошуку вагонів через UI", e);
            showWarning("Не вдалося виконати пошук: " + e.getMessage());
        }
    }

    // ========== Допоміжні методи ==========

    /**
     * Оновлює дані таблиці з поточного складу потяга.
     */
    private void refreshTable() {
        vagonList.setAll(potiag.getSklad());
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Інформація");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle("Увага");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Показує критичну помилку та завершує додаток.
     */
    private void showErrorAndExit(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Критична помилка");
        alert.setHeaderText("Додаток не може продовжити роботу");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
