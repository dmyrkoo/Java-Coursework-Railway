package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Діалог для додавання нового вагону.
 * Повертає створений {@link Vagon} або {@code null}, якщо користувач скасував.
 */
public class AddVagonDialog extends Dialog<Vagon> {

    private static final Logger logger = LoggerFactory.getLogger(AddVagonDialog.class);

    private final RadioButton rbPasazhyrsky;
    private final RadioButton rbSlyzhbovy;
    private final ToggleGroup typeGroup;

    private final TextField komfField;
    private final TextField bagazhField;

    // Поля для пасажирського вагону
    private final TextField pasazhyrivField;
    private final TextField rivenField;
    private final ComboBox<KlasKomfortu> klasCombo;

    // Поля для службового вагону
    private final TextField personalField;
    private final TextField pryznachennyaField;

    // Контейнери для динамічних полів
    private final VBox pasazhyrskyBox;
    private final VBox slyzhbovyBox;

    private final int nextId;

    /**
     * Створює діалог додавання вагону.
     *
     * @param nextId ідентифікатор для нового вагону
     */
    public AddVagonDialog(int nextId) {
        this.nextId = nextId;

        setTitle("Додати вагон");
        setHeaderText("Введіть параметри нового вагону (ID: " + nextId + ")");

        // Кнопки Зберегти та Скасувати
        ButtonType okButtonType = new ButtonType("Зберегти", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        final Button btOk = (Button) getDialogPane().lookupButton(okButtonType);
        btOk.getStyleClass().add(atlantafx.base.theme.Styles.ACCENT);
        btOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateInput()) {
                event.consume();
            }
        });

        // === Базові поля ===
        rbPasazhyrsky = new RadioButton("Пасажирський");
        rbPasazhyrsky.setId("typePassengerRadio");
        rbSlyzhbovy = new RadioButton("Службовий");
        rbSlyzhbovy.setId("typeServiceRadio");
        typeGroup = new ToggleGroup();
        rbPasazhyrsky.setToggleGroup(typeGroup);
        rbSlyzhbovy.setToggleGroup(typeGroup);
        rbPasazhyrsky.setSelected(true);

        HBox typeBox = new HBox(10, rbPasazhyrsky, rbSlyzhbovy);

        komfField = new TextField();
        komfField.setId("komfortnistField");
        komfField.setPromptText("1–10");
        komfField.setPrefHeight(32);

        bagazhField = new TextField();
        bagazhField.setId("bagazhField");
        bagazhField.setPromptText("Наприклад: 50");
        bagazhField.setPrefHeight(32);

        // === Поля для пасажирського вагону ===
        pasazhyrivField = new TextField();
        pasazhyrivField.setId("pasazhyrivField");
        pasazhyrivField.setPromptText("Наприклад: 36");
        pasazhyrivField.setPrefHeight(32);

        rivenField = new TextField();
        rivenField.setId("rivenField");
        rivenField.setPromptText("1–10");
        rivenField.setPrefHeight(32);

        klasCombo = new ComboBox<>(FXCollections.observableArrayList(KlasKomfortu.values()));
        klasCombo.setId("klasCombo");
        klasCombo.setValue(KlasKomfortu.KUPE);
        klasCombo.setPrefHeight(32);

        pasazhyrskyBox = new VBox(10);
        pasazhyrskyBox.getChildren().addAll(
                new Label("Клас комфорту:"), klasCombo,
                new Label("Кількість пасажирів:"), pasazhyrivField,
                new Label("Рівень обслуговування:"), rivenField);

        // === Поля для службового вагону ===
        personalField = new TextField();
        personalField.setId("personalField");
        personalField.setPromptText("Наприклад: 2");
        personalField.setPrefHeight(32);

        pryznachennyaField = new TextField();
        pryznachennyaField.setId("pryznachennyaField");
        pryznachennyaField.setPromptText("Ресторан, Пошта...");
        pryznachennyaField.setPrefHeight(32);

        slyzhbovyBox = new VBox(10);
        slyzhbovyBox.getChildren().addAll(
                new Label("Кількість персоналу:"), personalField,
                new Label("Тип призначення:"), pryznachennyaField);

        // === Компонування ===
        GridPane baseGrid = new GridPane();
        baseGrid.setHgap(15);
        baseGrid.setVgap(15);
        baseGrid.setPadding(new Insets(20));

        baseGrid.add(new Label("Тип вагону:"), 0, 0);
        baseGrid.add(typeBox, 1, 0);
        baseGrid.add(new Label("Комфортність:"), 0, 1);
        baseGrid.add(komfField, 1, 1);
        baseGrid.add(new Label("Кількість багажу:"), 0, 2);
        baseGrid.add(bagazhField, 1, 2);

        VBox content = new VBox(10, baseGrid, pasazhyrskyBox);
        content.setPadding(new Insets(5));
        getDialogPane().setContent(content);

        // Динамічна зміна полів при зміні типу
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            content.getChildren().remove(pasazhyrskyBox);
            content.getChildren().remove(slyzhbovyBox);

            if (rbPasazhyrsky.isSelected()) {
                content.getChildren().add(pasazhyrskyBox);
                personalField.clear();
                pryznachennyaField.clear();
            } else {
                content.getChildren().add(slyzhbovyBox);
                pasazhyrivField.clear();
                rivenField.clear();
                klasCombo.setValue(KlasKomfortu.KUPE);
            }
            getDialogPane().getScene().getWindow().sizeToScene();
        });

        // ResultConverter — створення об'єкта при натисканні Зберегти
        setResultConverter(buttonType -> {
            if (buttonType == okButtonType) {
                return createVagon();
            }
            return null;
        });

        // Фокус на перше поле
        komfField.requestFocus();
    }

    /**
     * Валідує поля перед закриттям діалогу.
     * Делегує перевірку значень до {@link VagonInputValidator}.
     */
    private boolean validateInput() {
        try {
            int komf = parseIntField(komfField, "Комфортність");
            int bagazh = parseIntField(bagazhField, "Кількість багажу");

            // Валідація базових полів
            var baseError = VagonInputValidator.validateBase(komf, bagazh);
            if (baseError.isPresent()) {
                showValidationError(baseError.get());
                return false;
            }

            if (rbPasazhyrsky.isSelected()) {
                if (klasCombo.getValue() == null) {
                    showValidationError("Оберіть клас комфорту.");
                    return false;
                }
                int pas = parseIntField(pasazhyrivField, "Кількість пасажирів");
                int riven = parseIntField(rivenField, "Рівень обслуговування");

                var pasError = VagonInputValidator.validatePasazhyrsky(pas, riven, klasCombo.getValue() != null);
                if (pasError.isPresent()) {
                    showValidationError(pasError.get());
                    return false;
                }
            } else {
                int pers = parseIntField(personalField, "Кількість персоналу");
                String pryznachennya = pryznachennyaField.getText().trim();

                var sluzhError = VagonInputValidator.validateSlyzhbovy(pers, pryznachennya);
                if (sluzhError.isPresent()) {
                    showValidationError(sluzhError.get());
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Зчитує дані з полів та створює об'єкт вагону.
     * (Викликається тільки якщо validateInput() повернув true)
     */
    private Vagon createVagon() {
        int komf = Integer.parseInt(komfField.getText().trim());
        int bagazh = Integer.parseInt(bagazhField.getText().trim());

        if (rbPasazhyrsky.isSelected()) {
            KlasKomfortu klas = klasCombo.getValue();
            int pasazhyriv = Integer.parseInt(pasazhyrivField.getText().trim());
            int riven = Integer.parseInt(rivenField.getText().trim());

            PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                    nextId, komf, bagazh, klas, pasazhyriv, riven);
            logger.info("Створено пасажирський вагон ID={} ({}, пасажирів: {})",
                    nextId, klas.name(), pasazhyriv);
            return vagon;
        } else {
            int personal = Integer.parseInt(personalField.getText().trim());
            String pryznachennya = pryznachennyaField.getText().trim();

            SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                    nextId, komf, bagazh, personal, pryznachennya);
            logger.info("Створено службовий вагон ID={} (тип: {}, персонал: {})",
                    nextId, pryznachennya, personal);
            return vagon;
        }
    }

    private int parseIntField(TextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            showValidationError("Поле '" + fieldName + "' не може бути порожнім.");
            throw new NumberFormatException("Empty field");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            showValidationError("Поле '" + fieldName + "' повинно містити ціле число.\nВведено: " + text);
            throw e;
        }
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Помилка валідації");
        alert.setHeaderText("Некоректні дані");
        alert.showAndWait();
    }
}
