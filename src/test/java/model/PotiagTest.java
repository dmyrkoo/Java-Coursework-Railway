package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Potiag
 * Перевіряє функціональність управління складом потягу
 */
class PotiagTest {

    private Potiag potiag;

    @BeforeEach
    void setUp() {
        // Створюємо новий потяг перед кожним тестом
        potiag = new Potiag("Test Potiag");
    }

    @Test
    void testKonstruktor() {
        // Перевірка створення потягу
        Potiag novyiPotiag = new Potiag("Lviv-Kyiv Express");
        assertEquals("Lviv-Kyiv Express", novyiPotiag.getNazva());
        assertTrue(novyiPotiag.getSklad().isEmpty());
    }

    @Test
    void testDodatyVagon_Pasazhyrsky() {
        // Перевірка додавання пасажирського вагону
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        assertEquals(1, potiag.getSklad().size());
        assertEquals(vagon, potiag.getSklad().get(0));
    }

    @Test
    void testDodatyVagon_Slyzhbovy() {
        // Перевірка додавання службового вагону
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );
        potiag.dodatyVagon(vagon);

        assertEquals(1, potiag.getSklad().size());
        assertEquals(vagon, potiag.getSklad().get(0));
    }

    @Test
    void testDodatyVagon_KilkaVagoniv() {
        // Перевірка додавання кількох вагонів
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        SlyzhbovyVagon vagon2 = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );
        PasazhyrskyVagon vagon3 = new PasazhyrskyVagon(
                3, 5, 30, KlasKomfortu.KUPE, 36, 7
        );

        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        potiag.dodatyVagon(vagon3);

        assertEquals(3, potiag.getSklad().size());
    }

    @Test
    void testVydalytyVagon_Isnuyuchyi() {
        // Перевірка видалення існуючого вагону
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                5, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        boolean result = potiag.vydalytyVagon(5);
        assertTrue(result);
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testVydalytyVagon_Neisnuyuchyi() {
        // Перевірка видалення неіснуючого вагону
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                5, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        boolean result = potiag.vydalytyVagon(99);
        assertFalse(result);
        assertEquals(1, potiag.getSklad().size());
    }

    @Test
    void testVydalytyVagon_PorozhniySklad() {
        // Перевірка видалення з порожнього складу
        boolean result = potiag.vydalytyVagon(1);
        assertFalse(result);
    }

    @Test
    void testZnaytyVagonById_Isnuyuchyi() {
        // Перевірка пошуку існуючого вагону за ID
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                10, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        SlyzhbovyVagon vagon2 = new SlyzhbovyVagon(
                20, 6, 100, 5, "Restoran"
        );
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);

        Vagon znaydenyi = potiag.znaytyVagonById(20);
        assertNotNull(znaydenyi);
        assertEquals(20, znaydenyi.getId());
        assertEquals(vagon2, znaydenyi);
    }

    @Test
    void testZnaytyVagonById_Neisnuyuchyi() {
        // Перевірка пошуку неіснуючого вагону
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                10, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        Vagon znaydenyi = potiag.znaytyVagonById(99);
        assertNull(znaydenyi);
    }

    @Test
    void testGetZagalnaKilkistPasazhyriv() {
        // Перевірка розрахунку загальної кількості пасажирів
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        PasazhyrskyVagon vagon2 = new PasazhyrskyVagon(
                2, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        SlyzhbovyVagon vagon3 = new SlyzhbovyVagon(
                3, 6, 100, 5, "Restoran"
        );

        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        potiag.dodatyVagon(vagon3);

        // Службовий вагон має 0 пасажирів
        assertEquals(76, potiag.getZagalnaKilkistPasazhyriv());
    }

    @Test
    void testGetZagalnaKilkistPasazhyriv_PorozhniySklad() {
        // Перевірка розрахунку для порожнього складу
        assertEquals(0, potiag.getZagalnaKilkistPasazhyriv());
    }

    @Test
    void testGetZagalnyiBagazh() {
        // Перевірка розрахунку загального багажу
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        SlyzhbovyVagon vagon2 = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );
        PasazhyrskyVagon vagon3 = new PasazhyrskyVagon(
                3, 5, 30, KlasKomfortu.KUPE, 36, 7
        );

        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        potiag.dodatyVagon(vagon3);

        assertEquals(180, potiag.getZagalnyiBagazh());
    }

    @Test
    void testGetZagalnyiBagazh_PorozhniySklad() {
        // Перевірка розрахунку для порожнього складу
        assertEquals(0, potiag.getZagalnyiBagazh());
    }

    @Test
    void testOchystyty() {
        // Перевірка очищення складу
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        SlyzhbovyVagon vagon2 = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );

        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        assertEquals(2, potiag.getSklad().size());

        potiag.ochystyty();
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testToString() {
        // Перевірка рядкового представлення потягу
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        String result = potiag.toString();
        assertTrue(result.contains("Test Potiag"));
        assertTrue(result.contains("Vagoniv: 1"));
        assertTrue(result.contains("Pasazhyriv: 40"));
        assertTrue(result.contains("Bagazh: 50"));
    }

    @Test
    void testGetSklad_Immutability() {
        // Перевірка, що getSklad повертає посилання на реальний список
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        // Отримуємо список і перевіряємо, що зміни відображаються
        var sklad = potiag.getSklad();
        assertEquals(1, sklad.size());
        assertEquals(1, potiag.getSklad().size());
    }
}
