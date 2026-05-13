package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

    private final ComboBox<String> typeCombo;
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

        // Кнопки ОК та Скасувати
        ButtonType okButtonType = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Скасувати", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // === Базові поля ===
        typeCombo = new ComboBox<>(FXCollections.observableArrayList("Пасажирський", "Службовий"));
        typeCombo.setValue("Пасажирський");

        komfField = new TextField();
        komfField.setPromptText("1–10");

        bagazhField = new TextField();
        bagazhField.setPromptText("Кількість одиниць");

        // === Поля для пасажирського вагону ===
        pasazhyrivField = new TextField();
        pasazhyrivField.setPromptText("Кількість");

        rivenField = new TextField();
        rivenField.setPromptText("1–10");

        klasCombo = new ComboBox<>(FXCollections.observableArrayList(KlasKomfortu.values()));
        klasCombo.setValue(KlasKomfortu.KUPE);

        pasazhyrskyBox = new VBox(5);
        pasazhyrskyBox.getChildren().addAll(
                new Label("Клас комфорту:"), klasCombo,
                new Label("Кількість пасажирів:"), pasazhyrivField,
                new Label("Рівень обслуговування:"), rivenField
        );

        // === Поля для службового вагону ===
        personalField = new TextField();
        personalField.setPromptText("Кількість");

        pryznachennyaField = new TextField();
        pryznachennyaField.setPromptText("Restoran, Poshta...");

        slyzhbovyBox = new VBox(5);
        slyzhbovyBox.getChildren().addAll(
                new Label("Кількість персоналу:"), personalField,
                new Label("Тип призначення:"), pryznachennyaField
        );

        // === Компонування ===
        GridPane baseGrid = new GridPane();
        baseGrid.setHgap(10);
        baseGrid.setVgap(8);
        baseGrid.setPadding(new Insets(10));

        baseGrid.add(new Label("Тип вагону:"), 0, 0);
        baseGrid.add(typeCombo, 1, 0);
        baseGrid.add(new Label("Комфортність:"), 0, 1);
        baseGrid.add(komfField, 1, 1);
        baseGrid.add(new Label("Кількість багажу:"), 0, 2);
        baseGrid.add(bagazhField, 1, 2);

        VBox content = new VBox(10, baseGrid, pasazhyrskyBox);
        content.setPadding(new Insets(5));
        getDialogPane().setContent(content);

        // Динамічна зміна полів при зміні типу
        typeCombo.setOnAction(e -> {
            content.getChildren().remove(pasazhyrskyBox);
            content.getChildren().remove(slyzhbovyBox);

            if ("Пасажирський".equals(typeCombo.getValue())) {
                content.getChildren().add(pasazhyrskyBox);
            } else {
                content.getChildren().add(slyzhbovyBox);
            }
        });

        // ResultConverter — створення об'єкта при натисканні ОК
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
     * Зчитує дані з полів, валідує та створює об'єкт вагону.
     *
     * @return створений {@link Vagon} або {@code null} при помилці валідації
     */
    private Vagon createVagon() {
        try {
            int komf = parseIntField(komfField, "Комфортність");
            int bagazh = parseIntField(bagazhField, "Кількість багажу");

            if ("Пасажирський".equals(typeCombo.getValue())) {
                KlasKomfortu klas = klasCombo.getValue();
                int pasazhyriv = parseIntField(pasazhyrivField, "Кількість пасажирів");
                int riven = parseIntField(rivenField, "Рівень обслуговування");

                PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                        nextId, komf, bagazh, klas, pasazhyriv, riven
                );
                logger.info("Створено пасажирський вагон ID={} ({}, пасажирів: {})",
                        nextId, klas.name(), pasazhyriv);
                return vagon;

            } else {
                int personal = parseIntField(personalField, "Кількість персоналу");
                String pryznachennya = pryznachennyaField.getText().trim();
                if (pryznachennya.isEmpty()) {
                    showValidationError("Поле 'Тип призначення' не може бути порожнім.");
                    return null;
                }

                SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                        nextId, komf, bagazh, personal, pryznachennya
                );
                logger.info("Створено службовий вагон ID={} (тип: {}, персонал: {})",
                        nextId, pryznachennya, personal);
                return vagon;
            }

        } catch (NumberFormatException e) {
            // Повідомлення вже показано у parseIntField
            logger.error("Некоректний ввід при створенні вагону", e);
            return null;
        } catch (Exception e) {
            logger.error("Несподівана помилка при створенні вагону", e);
            showValidationError("Несподівана помилка: " + e.getMessage());
            return null;
        }
    }

    /**
     * Парсить значення текстового поля як ціле число.
     *
     * @param field     текстове поле
     * @param fieldName назва поля для повідомлення про помилку
     * @return ціле число
     * @throws NumberFormatException якщо значення не є числом
     */
    private int parseIntField(TextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            showValidationError("Поле '" + fieldName + "' не може бути порожнім.");
            throw new NumberFormatException("Порожнє поле: " + fieldName);
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            showValidationError("Поле '" + fieldName + "' повинно містити ціле число.\nВведено: " + text);
            throw e;
        }
    }

    /**
     * Показує повідомлення про помилку валідації.
     */
    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Помилка введення");
        alert.setHeaderText("Некоректні дані");
        alert.showAndWait();
    }
}
