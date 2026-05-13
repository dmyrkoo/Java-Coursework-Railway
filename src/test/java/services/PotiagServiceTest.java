package services;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу {@link PotiagService}.
 * Перевіряє функціональність сортування та пошуку вагонів.
 */
class PotiagServiceTest {

    private Potiag potiag;
    private PotiagService service;

    private PasazhyrskyVagon vagonVip;
    private PasazhyrskyVagon vagonKupe;
    private PasazhyrskyVagon vagonPlatskart;
    private SlyzhbovyVagon vagonRestoran;

    @BeforeEach
    void setUp() {
        potiag = new Potiag("Test Potiag");
        service = new PotiagService(potiag);

        // komf=9, pasazhyriv=40, riven=10
        vagonVip = new PasazhyrskyVagon(1, 9, 50, KlasKomfortu.VIP, 40, 10);
        // komf=7, pasazhyriv=36, riven=7
        vagonKupe = new PasazhyrskyVagon(2, 7, 40, KlasKomfortu.KUPE, 36, 7);
        // komf=5, pasazhyriv=54, riven=5
        vagonPlatskart = new PasazhyrskyVagon(3, 5, 30, KlasKomfortu.PLATSKART, 54, 5);
        // komf=6, pasazhyriv=0 (службовий)
        vagonRestoran = new SlyzhbovyVagon(4, 6, 100, 5, "Restoran");
    }

    // ========== Допоміжні методи ==========

    /**
     * Додає три пасажирські вагони (VIP, KUPE, PLATSKART) до потяга.
     */
    private void dodatyTryPasazhyrski() {
        potiag.dodatyVagon(vagonVip);
        potiag.dodatyVagon(vagonKupe);
        potiag.dodatyVagon(vagonPlatskart);
    }

    /**
     * Додає всі чотири тестові вагони (VIP, KUPE, PLATSKART, Restoran) до потяга.
     */
    private void dodatyVsiVagony() {
        dodatyTryPasazhyrski();
        potiag.dodatyVagon(vagonRestoran);
    }

    // ========== Тести для sortuvatyZaKomfortom ==========

    @Test
    void testSortuvatyZaKomfortom_SortuvannyaZaSpadannyam() {
        // Додаємо у "неправильному" порядку
        potiag.dodatyVagon(vagonPlatskart); // komf=5
        potiag.dodatyVagon(vagonVip);       // komf=9
        potiag.dodatyVagon(vagonKupe);      // komf=7

        service.sortuvatyZaKomfortom();

        // Очікуваний порядок: VIP(9) -> KUPE(7) -> PLATSKART(5)
        assertEquals(3, potiag.getSklad().size());
        assertEquals(1, potiag.getSklad().get(0).getId());
        assertEquals(2, potiag.getSklad().get(1).getId());
        assertEquals(3, potiag.getSklad().get(2).getId());
    }

    @Test
    void testSortuvatyZaKomfortom_OdnakovaKomfortnist() {
        // Два вагони з однаковою комфортністю, різний рівень обслуговування
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(10, 8, 50, KlasKomfortu.VIP, 40, 9);
        PasazhyrskyVagon vagon2 = new PasazhyrskyVagon(11, 8, 40, KlasKomfortu.KUPE, 36, 7);

        potiag.dodatyVagon(vagon2); // нижчий riven додаємо першим
        potiag.dodatyVagon(vagon1);

        service.sortuvatyZaKomfortom();

        // При однаковій комфортності: vagon1 (riven=9) перед vagon2 (riven=7)
        assertEquals(10, potiag.getSklad().get(0).getId());
        assertEquals(11, potiag.getSklad().get(1).getId());
    }

    @Test
    void testSortuvatyZaKomfortom_ZmishaniVagony() {
        potiag.dodatyVagon(vagonRestoran);  // komf=6
        potiag.dodatyVagon(vagonVip);       // komf=9
        potiag.dodatyVagon(vagonPlatskart); // komf=5

        service.sortuvatyZaKomfortom();

        // Очікуваний порядок: VIP(9) -> Restoran(6) -> PLATSKART(5)
        assertEquals(1, potiag.getSklad().get(0).getId());
        assertEquals(4, potiag.getSklad().get(1).getId());
        assertEquals(3, potiag.getSklad().get(2).getId());
    }

    @Test
    void testSortuvatyZaKomfortom_PorozhniySklad() {
        // Порожній склад — метод не повинен викликати помилку
        service.sortuvatyZaKomfortom();

        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testSortuvatyZaKomfortom_OdynVagon() {
        potiag.dodatyVagon(vagonVip);

        service.sortuvatyZaKomfortom();

        assertEquals(1, potiag.getSklad().size());
        assertEquals(1, potiag.getSklad().get(0).getId());
    }

    // ========== Тести для znaytyVagonyZaPasazhyramy ==========

    @Test
    void testZnaytyVagonyZaPasazhyramy_ZnaydenoKilka() {
        dodatyTryPasazhyrski();

        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(35, 45);

        // VIP(40) та KUPE(36) потрапляють у діапазон
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(v -> v.getId() == 1));
        assertTrue(result.stream().anyMatch(v -> v.getId() == 2));
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_ZnaydenoOdyn() {
        dodatyTryPasazhyrski();

        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(50, 60);

        // Тільки PLATSKART(54) потрапляє у діапазон
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getId());
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_NichogoNeZnaydeno() {
        potiag.dodatyVagon(vagonVip);
        potiag.dodatyVagon(vagonKupe);

        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(100, 200);

        assertTrue(result.isEmpty());
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_SlyzhboviNeVklyucheni() {
        potiag.dodatyVagon(vagonVip);
        potiag.dodatyVagon(vagonRestoran);

        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(1, 50);

        // Службовий вагон (0 пасажирів) не входить у діапазон 1–50
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_GranychniZnachennya() {
        potiag.dodatyVagon(vagonVip);  // 40
        potiag.dodatyVagon(vagonKupe); // 36

        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(36, 40);

        // Обидва потрапляють у діапазон включно
        assertEquals(2, result.size());
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_PorozhniySklad() {
        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(10, 50);

        assertTrue(result.isEmpty());
    }

    // ========== Граничні випадки (edge cases) ==========

    @Test
    void testSortuvatyZaKomfortom_PorozhniySklad_BezPobichnyhEfektiv() {
        // Перевірка що порожній потяг не змінює стан після сортування
        assertTrue(potiag.getSklad().isEmpty());

        service.sortuvatyZaKomfortom();

        assertTrue(potiag.getSklad().isEmpty());
        assertEquals(0, potiag.getZagalnaKilkistPasazhyriv());
        assertEquals(0, potiag.getZagalnyiBagazh());
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_ZhodenNeVidpovidaye() {
        dodatyVsiVagony();

        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(70, 100);

        // Жоден вагон не має 70–100 пасажирів
        assertTrue(result.isEmpty());
        // Оригінальний склад не змінився
        assertEquals(4, potiag.getSklad().size());
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_VsiVagonyVDiapazoni() {
        dodatyVsiVagony();

        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(0, 100);

        // Всі 4 вагони потрапляють у діапазон 0–100 (включно зі службовим)
        assertEquals(4, result.size());
        assertTrue(result.stream().anyMatch(v -> v.getId() == 1));
        assertTrue(result.stream().anyMatch(v -> v.getId() == 2));
        assertTrue(result.stream().anyMatch(v -> v.getId() == 3));
        assertTrue(result.stream().anyMatch(v -> v.getId() == 4));
    }
}
