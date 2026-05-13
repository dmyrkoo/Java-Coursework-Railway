package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для enum KlasKomfortu
 * Перевіряє коректність роботи з класами комфортності
 */
class KlasKomfortuTest {

    @Test
    void testFromString_VIP() {
        // Перевірка коректного парсингу VIP класу
        KlasKomfortu klas = KlasKomfortu.fromString("VIP");
        assertEquals(KlasKomfortu.VIP, klas);
    }

    @Test
    void testFromString_KUPE() {
        // Перевірка коректного парсингу KUPE класу
        KlasKomfortu klas = KlasKomfortu.fromString("KUPE");
        assertEquals(KlasKomfortu.KUPE, klas);
    }

    @Test
    void testFromString_PLATSKART() {
        // Перевірка коректного парсингу PLATSKART класу
        KlasKomfortu klas = KlasKomfortu.fromString("PLATSKART");
        assertEquals(KlasKomfortu.PLATSKART, klas);
    }

    @Test
    void testFromString_ZAHALNYI() {
        // Перевірка коректного парсингу ZAHALNYI класу
        KlasKomfortu klas = KlasKomfortu.fromString("ZAHALNYI");
        assertEquals(KlasKomfortu.ZAHALNYI, klas);
    }

    @Test
    void testFromString_LowerCase() {
        // Перевірка конвертації з малих літер
        KlasKomfortu klas = KlasKomfortu.fromString("vip");
        assertEquals(KlasKomfortu.VIP, klas);
    }

    @Test
    void testFromString_MixedCase() {
        // Перевірка конвертації зі змішаного регістру
        KlasKomfortu klas = KlasKomfortu.fromString("KuPe");
        assertEquals(KlasKomfortu.KUPE, klas);
    }

    @Test
    void testFromString_WithSpaces() {
        // Перевірка обробки пробілів
        KlasKomfortu klas = KlasKomfortu.fromString("  VIP  ");
        assertEquals(KlasKomfortu.VIP, klas);
    }

    @Test
    void testFromString_InvalidValue() {
        // Перевірка обробки невалідного значення (має повертати ZAHALNYI за замовчуванням)
        KlasKomfortu klas = KlasKomfortu.fromString("NEVYSNOVLENYI");
        assertEquals(KlasKomfortu.ZAHALNYI, klas);
    }

    @Test
    void testFromString_EmptyString() {
        // Перевірка обробки порожнього рядка
        KlasKomfortu klas = KlasKomfortu.fromString("");
        assertEquals(KlasKomfortu.ZAHALNYI, klas);
    }

    @Test
    void testValues() {
        // Перевірка наявності всіх значень enum
        KlasKomfortu[] values = KlasKomfortu.values();
        assertEquals(4, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(KlasKomfortu.VIP));
        assertTrue(java.util.Arrays.asList(values).contains(KlasKomfortu.KUPE));
        assertTrue(java.util.Arrays.asList(values).contains(KlasKomfortu.PLATSKART));
        assertTrue(java.util.Arrays.asList(values).contains(KlasKomfortu.ZAHALNYI));
    }
}
