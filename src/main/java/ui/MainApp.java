package ui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;

import java.util.ArrayList;
import commands.AddVagonCommand;
import commands.DeleteVagonCommand;
import commands.FindVagonsQuery;
import commands.SortVagonsCommand;
import model.*;
import repository.SqliteVagonRepository;
import services.PotiagService;
import services.SkladService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Головний клас графічного інтерфейсу програми з темою AtlantaFX.
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

    /** Контейнер для візуальної схеми потяга (вагони-квадрати) */
    private HBox trainSchemaBox;

    /** Список Label-ів вагонів у схемі для підсвітки при виділенні */
    private final List<Label> trainWagonLabels = new ArrayList<>();

    /** Картки статистики (для оновлення) */
    private Label statPotiag;
    private Label statPasazhyry;
    private Label statBagazh;
    private Label statVagoniv;

    @Override
    public void start(Stage primaryStage) {
        // logger.error("ТЕСТОВЕ ПОВІДОМЛЕННЯ: Перевірка SMTP логера для курсової
        // роботи",
        // new RuntimeException("Штучний збій для перевірки Email"));
        // Встановлення теми AtlantaFX
        Application.setUserAgentStylesheet(new atlantafx.base.theme.PrimerLight().getUserAgentStylesheet());

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
        root.setPadding(new Insets(12));

        // TOP: Статистика + схема потяга
        root.setTop(createTopSection());

        // CENTER: Таблиця вагонів
        tableView = createTableView();
        root.setCenter(tableView);

        // BOTTOM: Тулбар з кнопками та пошуком
        root.setBottom(createBottomToolbar());

        Scene scene = new Scene(root, 1000, 650);
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

    // ========== Побудова TOP секції ==========

    /**
     * Створює верхню частину: картки статистики + візуальна схема потяга.
     */
    private VBox createTopSection() {
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(0, 0, 10, 0));

        // Рядок карток статистики
        HBox statsRow = createStatsRow();

        // Візуальна схема потяга
        trainSchemaBox = new HBox(4);
        trainSchemaBox.setAlignment(Pos.CENTER_LEFT);
        trainSchemaBox.setPadding(new Insets(6, 0, 4, 0));
        refreshTrainSchema();

        topBox.getChildren().addAll(statsRow, trainSchemaBox);
        return topBox;
    }

    /**
     * Створює рядок із 4 картками статистики.
     */
    private HBox createStatsRow() {
        statPotiag = createStatCard("🚆 Потяг", potiag.getNazva());
        statPasazhyry = createStatCard("👥 Пасажири", String.valueOf(potiag.getZagalnaKilkistPasazhyriv()));
        statBagazh = createStatCard("💼 Багаж", String.valueOf(potiag.getZagalnyiBagazh()));
        statVagoniv = createStatCard("🚋 Вагони", String.valueOf(potiag.getSklad().size()));

        HBox row = new HBox(10, statPotiag, statPasazhyry, statBagazh, statVagoniv);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Створює одну картку статистики (стилізований Label).
     */
    private Label createStatCard(String title, String value) {
        Label card = new Label(title + "\n" + value);
        card.setFont(Font.font("System", 13));
        card.setPadding(new Insets(10, 20, 10, 20));
        card.setStyle(
                "-fx-background-color: #f6f8fa;" +
                        "-fx-border-color: #d0d7de;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-width: 1;");
        card.setMinWidth(140);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    /**
     * Оновлює значення на картках статистики.
     */
    private void refreshStats() {
        statPotiag.setText("🚆 Потяг\n" + potiag.getNazva());
        statPasazhyry.setText("👥 Пасажири\n" + potiag.getZagalnaKilkistPasazhyriv());
        statBagazh.setText("💼 Багаж\n" + potiag.getZagalnyiBagazh());
        statVagoniv.setText("🚋 Вагонів\n" + potiag.getSklad().size());
    }

    /**
     * Перебудовує візуальну схему потяга (HBox з локомотивом та
     * квадратами-вагонами).
     */
    private void refreshTrainSchema() {
        trainSchemaBox.getChildren().clear();
        trainWagonLabels.clear();

        // Локомотив
        Label loco = new Label("🚂");
        loco.setFont(Font.font(22));
        loco.setPadding(new Insets(2, 6, 2, 0));
        trainSchemaBox.getChildren().add(loco);

        // Вагони
        for (Vagon v : potiag.getSklad()) {
            Label wagonLabel = new Label("[ " + v.getId() + " ]");
            wagonLabel.setFont(Font.font("Monospaced", 13));
            wagonLabel.setPadding(new Insets(6, 8, 6, 8));
            wagonLabel.setAlignment(Pos.CENTER);
            wagonLabel.setMinWidth(48);

            if (v instanceof PasazhyrskyVagon) {
                // Пасажирський — блакитний фон
                wagonLabel.setStyle(
                        "-fx-background-color: #dbeafe;" +
                                "-fx-text-fill: -color-fg-default;" +
                                "-fx-font-weight: normal;" +
                                "-fx-border-color: #93c5fd;" +
                                "-fx-border-radius: 4;" +
                                "-fx-background-radius: 4;" +
                                "-fx-border-width: 1;");
            } else {
                // Службовий — жовтий фон
                wagonLabel.setStyle(
                        "-fx-background-color: #fef9c3;" +
                                "-fx-text-fill: -color-fg-default;" +
                                "-fx-font-weight: normal;" +
                                "-fx-border-color: #fde047;" +
                                "-fx-border-radius: 4;" +
                                "-fx-background-radius: 4;" +
                                "-fx-border-width: 1;");
            }

            trainWagonLabels.add(wagonLabel);
            trainSchemaBox.getChildren().add(wagonLabel);
        }
    }

    // ========== Побудова CENTER секції ==========

    /**
     * Створює {@link TableView} з 7 колонками для відображення вагонів.
     * Додає слухача виділення для підсвітки відповідного Label у схемі потяга.
     */
    @SuppressWarnings("unchecked")
    private TableView<Vagon> createTableView() {
        TableView<Vagon> table = new TableView<>(vagonList);

        // 1. ID
        TableColumn<Vagon, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        idCol.setPrefWidth(50);

        // 2. Тип
        TableColumn<Vagon, String> typeCol = new TableColumn<>("Тип");
        typeCol.setCellValueFactory(data -> {
            Vagon v = data.getValue();
            if (v instanceof PasazhyrskyVagon) {
                return new ReadOnlyObjectWrapper<>("Пасажирський");
            } else if (v instanceof SlyzhbovyVagon) {
                return new ReadOnlyObjectWrapper<>("Службовий");
            }
            return new ReadOnlyObjectWrapper<>(v.getType());
        });
        typeCol.setPrefWidth(110);

        // 3. Комфортність
        TableColumn<Vagon, Integer> komfCol = new TableColumn<>("Комфортність");
        komfCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getKomfortnist()));
        komfCol.setPrefWidth(100);

        // 4. Пасажирів
        TableColumn<Vagon, String> pasCol = new TableColumn<>("Пасажирів");
        pasCol.setCellValueFactory(data -> {
            Vagon v = data.getValue();
            if (v instanceof PasazhyrskyVagon pv) {
                return new ReadOnlyObjectWrapper<>(String.valueOf(pv.getKilkistPasazhyriv()));
            }
            return new ReadOnlyObjectWrapper<>("-");
        });
        pasCol.setPrefWidth(90);

        // 4.5 Персонал
        TableColumn<Vagon, String> personalCol = new TableColumn<>("Персонал");
        personalCol.setCellValueFactory(data -> {
            Vagon v = data.getValue();
            if (v instanceof SlyzhbovyVagon sv) {
                return new ReadOnlyObjectWrapper<>(String.valueOf(sv.getPersonalKilkist()));
            }
            return new ReadOnlyObjectWrapper<>("-");
        });
        personalCol.setPrefWidth(90);

        // 5. Багаж
        TableColumn<Vagon, Integer> bagCol = new TableColumn<>("Багаж");
        bagCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getBagazhKilkist()));
        bagCol.setPrefWidth(80);

        // 6. Клас (для пасажирських)
        TableColumn<Vagon, String> klasCol = new TableColumn<>("Клас");
        klasCol.setCellValueFactory(data -> {
            Vagon v = data.getValue();
            if (v instanceof PasazhyrskyVagon pv) {
                return new ReadOnlyObjectWrapper<>(pv.getKlasKomfortu().getDisplayName());
            }
            return new ReadOnlyObjectWrapper<>("-");
        });
        klasCol.setPrefWidth(100);

        // 7. Призначення (для службових)
        TableColumn<Vagon, String> pryzCol = new TableColumn<>("Призначення");
        pryzCol.setCellValueFactory(data -> {
            Vagon v = data.getValue();
            if (v instanceof SlyzhbovyVagon sv) {
                return new ReadOnlyObjectWrapper<>(sv.getTypPryznachennya());
            }
            return new ReadOnlyObjectWrapper<>("-");
        });
        pryzCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, typeCol, komfCol, pasCol, personalCol, bagCol, klasCol, pryzCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Склад порожній — додайте вагон"));

        // Слухач виділення — підсвітка вагону у схемі потяга
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            highlightWagonInSchema(newVal);
        });

        return table;
    }

    /**
     * Підсвічує Label відповідного вагону у візуальній схемі потяга.
     * Скидає підсвітку всіх інших.
     */
    private void highlightWagonInSchema(Vagon selected) {
        List<Vagon> sklad = potiag.getSklad();
        for (int i = 0; i < trainWagonLabels.size() && i < sklad.size(); i++) {
            Label lbl = trainWagonLabels.get(i);
            Vagon v = sklad.get(i);
            boolean isSelected = (selected != null && v.getId() == selected.getId());

            if (isSelected) {
                if (v instanceof PasazhyrskyVagon) {
                    // Пасажирський — яскравий синій
                    lbl.setStyle(
                            "-fx-background-color: #007BFF;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-border-color: #0056b3;" +
                                    "-fx-border-radius: 4;" +
                                    "-fx-background-radius: 4;" +
                                    "-fx-border-width: 2.5;");
                } else {
                    // Службовий — яскравий жовтий
                    lbl.setStyle(
                            "-fx-background-color: #FFD700;" +
                                    "-fx-text-fill: black;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-border-color: #B8860B;" +
                                    "-fx-border-radius: 4;" +
                                    "-fx-background-radius: 4;" +
                                    "-fx-border-width: 2.5;");
                }
            } else {
                // Повернення до звичайного стилю
                if (v instanceof PasazhyrskyVagon) {
                    lbl.setStyle(
                            "-fx-background-color: #dbeafe;" +
                                    "-fx-text-fill: -color-fg-default;" +
                                    "-fx-font-weight: normal;" +
                                    "-fx-border-color: #93c5fd;" +
                                    "-fx-border-radius: 4;" +
                                    "-fx-background-radius: 4;" +
                                    "-fx-border-width: 1;");
                } else {
                    lbl.setStyle(
                            "-fx-background-color: #fef9c3;" +
                                    "-fx-text-fill: -color-fg-default;" +
                                    "-fx-font-weight: normal;" +
                                    "-fx-border-color: #fde047;" +
                                    "-fx-border-radius: 4;" +
                                    "-fx-background-radius: 4;" +
                                    "-fx-border-width: 1;");
                }
            }
        }
    }

    // ========== Побудова BOTTOM секції ==========

    /**
     * Створює нижній тулбар: кнопки зліва + поля пошуку справа.
     */
    private HBox createBottomToolbar() {
        // Ліва частина — основні кнопки
        Button addBtn = new Button("Додати вагон");
        addBtn.setOnAction(e -> onDodaty());

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> onVydalyty());

        ComboBox<String> sortCombo = new ComboBox<>(FXCollections.observableArrayList(
                "За комфортністю", "За пасажирами", "За багажем"));
        sortCombo.setValue("За комфортністю");

        Button sortBtn = new Button("Сортувати");
        sortBtn.setOnAction(e -> onSortuvaty(sortCombo.getValue()));

        HBox leftBox = new HBox(8, addBtn, deleteBtn, sortCombo, sortBtn);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // Права частина — пошук за місткістю
        Label labelMist = new Label("Місткість:");
        labelMist.setPadding(new Insets(0, 4, 0, 0));

        TextField minField = new TextField();
        minField.setPromptText("min");
        minField.setPrefWidth(60);

        TextField maxField = new TextField();
        maxField.setPromptText("max");
        maxField.setPrefWidth(60);

        Button findBtn = new Button("Знайти");
        findBtn.setOnAction(e -> onZnayty(minField, maxField));

        Button resetBtn = new Button("Скинути");
        resetBtn.setOnAction(e -> {
            minField.clear();
            maxField.clear();
            refreshTable();
            logger.info("Фільтр скинуто, завантажено весь склад");
        });

        HBox rightBox = new HBox(6, labelMist, minField, maxField, findBtn, resetBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        // Розділяємо ліву і праву частину
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, leftBox, spacer, rightBox);
        toolbar.setPadding(new Insets(10, 0, 0, 0));
        toolbar.setAlignment(Pos.CENTER);

        return toolbar;
    }

    // ========== Завантаження даних ==========

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

    // ========== Обробники кнопок ==========

    private void onDodaty() {
        try {
            logger.info("Натиснуто кнопку 'Додати вагон'");
            AddVagonDialog dialog = new AddVagonDialog(nextId);
            Optional<Vagon> result = dialog.showAndWait();

            if (result.isPresent()) {
                Vagon vagon = result.get();
                new AddVagonCommand(skladService, vagon).execute();
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
                showWarning("Будь ласка, оберіть вагон для видалення");
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
                        new DeleteVagonCommand(skladService, selected.getId()).execute();
                        refreshTable();
                        logger.info("Видалено вагон ID={}", selected.getId());
                    } catch (Exception ex) {
                        logger.error("Помилка видалення вагону ID={}", selected.getId(), ex);
                        showWarning("Помилка видалення: " + ex.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Помилка при видаленні вагону через UI", e);
            showWarning("Не вдалося видалити вагон: " + e.getMessage());
        }
    }

    private void onSortuvaty(String criterion) {
        try {
            new SortVagonsCommand(potiagService, criterion).execute();
            refreshTable();
            logger.info("Виконано сортування: {}", criterion);
        } catch (Exception e) {
            logger.error("Помилка сортування вагонів", e);
            showWarning("Не вдалося виконати сортування: " + e.getMessage());
        }
    }

    private void onZnayty(TextField minField, TextField maxField) {
        try {
            String minStr = minField.getText().trim();
            String maxStr = maxField.getText().trim();

            if (minStr.isEmpty() || maxStr.isEmpty()) {
                showError("Будь ласка, введіть коректні числові значення для місткості");
                return;
            }

            int min = Integer.parseInt(minStr);
            int max = Integer.parseInt(maxStr);
            FindVagonsQuery query = new FindVagonsQuery(potiagService, min, max);
            List<Vagon> result = query.execute();
            vagonList.setAll(result);
            refreshTrainSchema();
            refreshStats();
            logger.info("Пошук: знайдено {} вагонів у діапазоні {}-{}", result.size(), min, max);
        } catch (NumberFormatException ex) {
            logger.error("Некоректний ввід при пошуку вагонів", ex);
            showError("Будь ласка, введіть коректні числові значення для місткості");
        } catch (Exception e) {
            logger.error("Помилка при пошуку вагонів через UI", e);
            showError("Не вдалося виконати пошук: " + e.getMessage());
        }
    }

    // ========== Допоміжні методи ==========

    /**
     * Оновлює дані таблиці, статистику та схему потяга з поточного складу.
     */
    private void refreshTable() {
        vagonList.setAll(potiag.getSklad());
        refreshTrainSchema();
        refreshStats();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle("Увага");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Помилка");
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
