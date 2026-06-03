package ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для {@link VagonInputValidator}.
 * Перевіряє чисту бізнес-логіку валідації без JavaFX залежностей.
 */
class VagonInputValidatorTest {

    // ========== validateBase: Комфортність ==========

    @Test
    @DisplayName("komfortnist < 1 → invalid")
    void shouldRejectKomfortnistBelowOne() {
        Optional<String> error = VagonInputValidator.validateBase(0, 50);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("Комфортність"));
    }

    @Test
    @DisplayName("komfortnist > 10 → invalid")
    void shouldRejectKomfortnistAboveTen() {
        Optional<String> error = VagonInputValidator.validateBase(11, 50);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("Комфортність"));
    }

    @Test
    @DisplayName("komfortnist = 1 → valid (нижня границя)")
    void shouldAcceptKomfortnistLowerBound() {
        Optional<String> error = VagonInputValidator.validateBase(1, 50);

        assertTrue(error.isEmpty());
    }

    @Test
    @DisplayName("komfortnist = 10 → valid (верхня границя)")
    void shouldAcceptKomfortnistUpperBound() {
        Optional<String> error = VagonInputValidator.validateBase(10, 50);

        assertTrue(error.isEmpty());
    }

    // ========== validateBase: Багаж ==========

    @Test
    @DisplayName("bagazh < 0 → invalid")
    void shouldRejectNegativeBagazh() {
        Optional<String> error = VagonInputValidator.validateBase(5, -1);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("багажу"));
    }

    @Test
    @DisplayName("bagazh = 0 → valid (нуль дозволено)")
    void shouldAcceptZeroBagazh() {
        Optional<String> error = VagonInputValidator.validateBase(5, 0);

        assertTrue(error.isEmpty());
    }

    // ========== validatePasazhyrsky ==========

    @Test
    @DisplayName("pasazhyry <= 0 → invalid")
    void shouldRejectZeroPassengers() {
        Optional<String> error = VagonInputValidator.validatePasazhyrsky(0, 5, true);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("пасажирів"));
    }

    @Test
    @DisplayName("pasazhyry = -1 → invalid")
    void shouldRejectNegativePassengers() {
        Optional<String> error = VagonInputValidator.validatePasazhyrsky(-1, 5, true);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("пасажирів"));
    }

    @Test
    @DisplayName("rivenObslugovuvannya < 1 → invalid")
    void shouldRejectRivenBelowOne() {
        Optional<String> error = VagonInputValidator.validatePasazhyrsky(36, 0, true);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("обслуговування"));
    }

    @Test
    @DisplayName("rivenObslugovuvannya > 10 → invalid")
    void shouldRejectRivenAboveTen() {
        Optional<String> error = VagonInputValidator.validatePasazhyrsky(36, 11, true);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("обслуговування"));
    }

    @Test
    @DisplayName("klasKomfortu не обрано → invalid")
    void shouldRejectMissingKlasKomfortu() {
        Optional<String> error = VagonInputValidator.validatePasazhyrsky(36, 5, false);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("клас"));
    }

    @Test
    @DisplayName("Валідний пасажирський ввід → valid")
    void shouldAcceptValidPassengerInput() {
        Optional<String> error = VagonInputValidator.validatePasazhyrsky(36, 7, true);

        assertTrue(error.isEmpty());
    }

    // ========== validateSlyzhbovy ==========

    @Test
    @DisplayName("personal <= 0 → invalid")
    void shouldRejectZeroPersonal() {
        Optional<String> error = VagonInputValidator.validateSlyzhbovy(0, "Ресторан");

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("персоналу"));
    }

    @Test
    @DisplayName("personal = -1 → invalid")
    void shouldRejectNegativePersonal() {
        Optional<String> error = VagonInputValidator.validateSlyzhbovy(-1, "Ресторан");

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("персоналу"));
    }

    @Test
    @DisplayName("pryznachennya порожній → invalid")
    void shouldRejectEmptyPryznachennya() {
        Optional<String> error = VagonInputValidator.validateSlyzhbovy(3, "");

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("призначення"));
    }

    @Test
    @DisplayName("pryznachennya лише пробіли → invalid")
    void shouldRejectWhitespacePryznachennya() {
        Optional<String> error = VagonInputValidator.validateSlyzhbovy(3, "   ");

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("призначення"));
    }

    @Test
    @DisplayName("pryznachennya = null → invalid")
    void shouldRejectNullPryznachennya() {
        Optional<String> error = VagonInputValidator.validateSlyzhbovy(3, null);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("призначення"));
    }

    @Test
    @DisplayName("Валідний службовий ввід → valid")
    void shouldAcceptValidServiceInput() {
        Optional<String> error = VagonInputValidator.validateSlyzhbovy(5, "Ресторан");

        assertTrue(error.isEmpty());
    }

    // ========== Комплексні перевірки: base + type ==========

    @Test
    @DisplayName("Повна валідація пасажирського вагону — всі поля коректні")
    void shouldPassFullPassengerValidation() {
        // Base
        Optional<String> baseErr = VagonInputValidator.validateBase(8, 50);
        assertTrue(baseErr.isEmpty());

        // Passenger-specific
        Optional<String> pasErr = VagonInputValidator.validatePasazhyrsky(36, 7, true);
        assertTrue(pasErr.isEmpty());
    }

    @Test
    @DisplayName("Повна валідація службового вагону — всі поля коректні")
    void shouldPassFullServiceValidation() {
        // Base
        Optional<String> baseErr = VagonInputValidator.validateBase(6, 100);
        assertTrue(baseErr.isEmpty());

        // Service-specific
        Optional<String> sluzhErr = VagonInputValidator.validateSlyzhbovy(3, "Пошта");
        assertTrue(sluzhErr.isEmpty());
    }
}
