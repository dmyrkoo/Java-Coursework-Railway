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

    private void dodatyTryPasazhyrski() {
        potiag.dodatyVagon(vagonVip);
        potiag.dodatyVagon(vagonKupe);
        potiag.dodatyVagon(vagonPlatskart);
    }

    private void dodatyVsiVagony() {
        dodatyTryPasazhyrski();
        potiag.dodatyVagon(vagonRestoran);
    }

    // ========== Тести сортування за ID ==========

    @Test
    void testSortuvatyZaId_ASC() {
        potiag.dodatyVagon(vagonPlatskart); // ID=3
        potiag.dodatyVagon(vagonVip);       // ID=1
        potiag.dodatyVagon(vagonKupe);      // ID=2

        service.sortuvaty("За ID", false);

        assertEquals(1, potiag.getSklad().get(0).getId());
        assertEquals(2, potiag.getSklad().get(1).getId());
        assertEquals(3, potiag.getSklad().get(2).getId());
    }

    @Test
    void testSortuvatyZaId_DESC() {
        potiag.dodatyVagon(vagonVip);       // ID=1
        potiag.dodatyVagon(vagonPlatskart); // ID=3
        potiag.dodatyVagon(vagonKupe);      // ID=2

        service.sortuvaty("За ID", true);

        assertEquals(3, potiag.getSklad().get(0).getId());
        assertEquals(2, potiag.getSklad().get(1).getId());
        assertEquals(1, potiag.getSklad().get(2).getId());
    }

    // ========== Тести сортування за пасажирами ==========

    @Test
    void testSortuvatyZaPasazhyramy_ASC() {
        dodatyVsiVagony(); // VIP=40, KUPE=36, PLATSKART=54, Restoran=0
        service.sortuvaty("За пасажирами", false);

        assertEquals(4, potiag.getSklad().get(0).getId()); // Restoran (0)
        assertEquals(2, potiag.getSklad().get(1).getId()); // KUPE (36)
        assertEquals(1, potiag.getSklad().get(2).getId()); // VIP (40)
        assertEquals(3, potiag.getSklad().get(3).getId()); // PLATSKART (54)
    }

    @Test
    void testSortuvatyZaPasazhyramy_DESC() {
        dodatyVsiVagony();
        service.sortuvaty("За пасажирами", true);

        assertEquals(3, potiag.getSklad().get(0).getId()); // PLATSKART (54)
        assertEquals(1, potiag.getSklad().get(1).getId()); // VIP (40)
        assertEquals(2, potiag.getSklad().get(2).getId()); // KUPE (36)
        assertEquals(4, potiag.getSklad().get(3).getId()); // Restoran (0)
    }

    // ========== Тести сортування за багажем ==========

    @Test
    void testSortuvatyZaBagazhem_ASC() {
        dodatyVsiVagony(); // VIP=50, KUPE=40, PLATSKART=30, Restoran=100
        service.sortuvaty("За багажем", false);

        assertEquals(3, potiag.getSklad().get(0).getId()); // PLATSKART (30)
        assertEquals(2, potiag.getSklad().get(1).getId()); // KUPE (40)
        assertEquals(1, potiag.getSklad().get(2).getId()); // VIP (50)
        assertEquals(4, potiag.getSklad().get(3).getId()); // Restoran (100)
    }

    @Test
    void testSortuvatyZaBagazhem_DESC() {
        dodatyVsiVagony();
        service.sortuvaty("За багажем", true);

        assertEquals(4, potiag.getSklad().get(0).getId()); // Restoran (100)
        assertEquals(1, potiag.getSklad().get(1).getId()); // VIP (50)
        assertEquals(2, potiag.getSklad().get(2).getId()); // KUPE (40)
        assertEquals(3, potiag.getSklad().get(3).getId()); // PLATSKART (30)
    }

    // ========== Тести сортування за комфортністю ==========

    @Test
    void testSortuvatyZaKomfortnistyu_ASC() {
        dodatyVsiVagony(); // VIP=9, KUPE=7, PLATSKART=5, Restoran=6
        service.sortuvaty("За комфортністю", false);

        assertEquals(3, potiag.getSklad().get(0).getId()); // PLATSKART (5)
        assertEquals(4, potiag.getSklad().get(1).getId()); // Restoran (6)
        assertEquals(2, potiag.getSklad().get(2).getId()); // KUPE (7)
        assertEquals(1, potiag.getSklad().get(3).getId()); // VIP (9)
    }

    @Test
    void testSortuvatyZaKomfortnistyu_DESC() {
        dodatyVsiVagony();
        service.sortuvaty("За комфортністю", true);

        assertEquals(1, potiag.getSklad().get(0).getId()); // VIP (9)
        assertEquals(2, potiag.getSklad().get(1).getId()); // KUPE (7)
        assertEquals(4, potiag.getSklad().get(2).getId()); // Restoran (6)
        assertEquals(3, potiag.getSklad().get(3).getId()); // PLATSKART (5)
    }

    @Test
    void testSortuvatyZaKomfortom_OdnakovaKomfortnist_CompositeReversed() {
        // При однаковій комфортності порядок визначає рівень обслуговування.
        // Важливо: comparator.reversed() перевертає весь ланцюг порівнянь, включно з thenComparing.
        PasazhyrskyVagon v1 = new PasazhyrskyVagon(10, 8, 50, KlasKomfortu.VIP, 40, 9);
        PasazhyrskyVagon v2 = new PasazhyrskyVagon(11, 8, 40, KlasKomfortu.KUPE, 36, 7);

        potiag.dodatyVagon(v1); // riven 9
        potiag.dodatyVagon(v2); // riven 7

        service.sortuvaty("За комфортністю", false); // ASC
        
        // ASC: комфортність ASC -> рівень обслуговування ASC
        // Тому riven 7 (id 11) має бути перед riven 9 (id 10)
        assertEquals(11, potiag.getSklad().get(0).getId());
        assertEquals(10, potiag.getSklad().get(1).getId());

        service.sortuvaty("За комфортністю", true); // DESC
        
        // DESC: комфортність DESC -> рівень обслуговування DESC
        // Тому riven 9 (id 10) має бути перед riven 7 (id 11)
        assertEquals(10, potiag.getSklad().get(0).getId());
        assertEquals(11, potiag.getSklad().get(1).getId());
    }

    // ========== Інші тести (грайничні випадки, пошук) ==========

    @Test
    void testSortuvaty_PorozhniySklad() {
        service.sortuvaty("За комфортністю", true);
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testSortuvaty_NevidomyiKryteriy() {
        dodatyTryPasazhyrski();
        service.sortuvaty("Невідомий критерій", false);
        assertEquals(3, potiag.getSklad().size()); // Нічого не впало, розмір зберігся
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_ZnaydenoKilka() {
        dodatyTryPasazhyrski();
        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(35, 45);
        assertEquals(2, result.size());
    }

    @Test
    void testZnaytyVagonyZaPasazhyramy_NichogoNeZnaydeno() {
        dodatyTryPasazhyrski();
        List<Vagon> result = service.znaytyVagonyZaPasazhyramy(100, 200);
        assertTrue(result.isEmpty());
    }
}
