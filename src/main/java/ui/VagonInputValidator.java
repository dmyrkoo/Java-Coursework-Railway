package ui;

import java.util.Optional;

/**
 * Валідатор введених даних для форми додавання вагону.
 * Містить чисту бізнес-логіку перевірки значень без залежностей від JavaFX.
 * Використовується у {@link AddVagonDialog} для делегування валідації.
 */
public class VagonInputValidator {

    private VagonInputValidator() {
        // Утилітний клас — не інстанціюється
    }

    /**
     * Валідує базові поля, спільні для обох типів вагонів.
     *
     * @param komfortnist   значення комфортності
     * @param bagazhKilkist кількість багажу
     * @return {@link Optional#empty()} якщо валідація пройшла, або повідомлення про помилку
     */
    public static Optional<String> validateBase(int komfortnist, int bagazhKilkist) {
        if (komfortnist < 1 || komfortnist > 10) {
            return Optional.of("Комфортність повинна бути від 1 до 10.");
        }
        if (bagazhKilkist < 0) {
            return Optional.of("Кількість багажу не може бути меншою за 0.");
        }
        return Optional.empty();
    }

    /**
     * Валідує поля пасажирського вагону.
     *
     * @param pasazhyriv          кількість пасажирів
     * @param rivenObslugovuvannya рівень обслуговування
     * @param klasKomfortuSelected чи обрано клас комфорту (не null)
     * @return {@link Optional#empty()} якщо валідація пройшла, або повідомлення про помилку
     */
    public static Optional<String> validatePasazhyrsky(int pasazhyriv, int rivenObslugovuvannya,
                                                        boolean klasKomfortuSelected) {
        if (!klasKomfortuSelected) {
            return Optional.of("Оберіть клас комфорту.");
        }
        if (pasazhyriv <= 0) {
            return Optional.of("Кількість пасажирів повинна бути більшою за 0.");
        }
        if (rivenObslugovuvannya < 1 || rivenObslugovuvannya > 10) {
            return Optional.of("Рівень обслуговування повинен бути від 1 до 10.");
        }
        return Optional.empty();
    }

    /**
     * Валідує поля службового вагону.
     *
     * @param personalKilkist   кількість персоналу
     * @param typPryznachennya  тип призначення (текст)
     * @return {@link Optional#empty()} якщо валідація пройшла, або повідомлення про помилку
     */
    public static Optional<String> validateSlyzhbovy(int personalKilkist, String typPryznachennya) {
        if (personalKilkist <= 0) {
            return Optional.of("Кількість персоналу повинна бути більшою за 0.");
        }
        if (typPryznachennya == null || typPryznachennya.trim().isEmpty()) {
            return Optional.of("Поле 'Тип призначення' не може бути порожнім.");
        }
        return Optional.empty();
    }
}
