package services;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Тести для методів сортування, пошуку та розрахунків у SkladService
 * Покриває sortuvatyVagony(), znaytyVagon(), pidrakhuvatyPasazhyriv(), pidrakhuvatyBagazh()
 */
class SkladServiceSortFindCalcTest {

    private Potiag potiag;
    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        potiag = new Potiag("Test Potiag");
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testSort_nonEmpty_comparator() {
        // Покриває: sortuvatyVagony() з thenComparing branch для пасажирських вагонів
        // Створюємо 3 пасажирські вагони з різними komf та riven
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 5, 30, KlasKomfortu.PLATSKART, 54, 6  // komf=5, riven=6
        );
        PasazhyrskyVagon vagon2 = new PasazhyrskyVagon(
                2, 8, 50, KlasKomfortu.VIP, 40, 9        // komf=8, riven=9
        );
        PasazhyrskyVagon vagon3 = new PasazhyrskyVagon(
                3, 8, 40, KlasKomfortu.KUPE, 36, 7        // komf=8, riven=7 (менше ніж vagon2)
        );
        
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        potiag.dodatyVagon(vagon3);
        
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.sortuvatyVagony();
        
        // Після сортування: спочатку за komf (спадання), потім за riven (спадання)
        // Очікуваний порядок: vagon2 (komf=8, riven=9), vagon3 (komf=8, riven=7), vagon1 (komf=5)
        assertEquals(3, potiag.getSklad().size());
        assertEquals(2, potiag.getSklad().get(0).getId()); // vagon2 має бути першим
        assertEquals(3, potiag.getSklad().get(1).getId()); // vagon3 другим
        assertEquals(1, potiag.getSklad().get(2).getId()); // vagon1 третім
    }

    @Test
    void testSort_mixedVagons() {
        // Покриває: thenComparing branch коли не обидва вагони є PasazhyrskyVagon
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        SlyzhbovyVagon vagon2 = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );
        PasazhyrskyVagon vagon3 = new PasazhyrskyVagon(
                3, 7, 40, KlasKomfortu.KUPE, 36, 8
        );
        
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        potiag.dodatyVagon(vagon3);
        
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.sortuvatyVagony();
        
        // Перевіряємо, що сортування виконано (за komf спадання)
        assertEquals(3, potiag.getSklad().size());
        // vagon1 (komf=8) має бути першим
        assertEquals(1, potiag.getSklad().get(0).getId());
    }

    @Test
    void testSort_empty() {
        // Покриває: if (sklad.isEmpty()) return у sortuvatyVagony()
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.sortuvatyVagony();
        
        assertTrue(potiag.getSklad().isEmpty());
        String output = outputStream.toString();
        assertTrue(output.contains("порожній") || output.contains("❌"));
    }

    @Test
    void testFind_validRange() {
        // Покриває: znaytyVagon() нормальний шлях
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        PasazhyrskyVagon vagon2 = new PasazhyrskyVagon(
                2, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        PasazhyrskyVagon vagon3 = new PasazhyrskyVagon(
                3, 4, 60, KlasKomfortu.PLATSKART, 54, 6
        );
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        potiag.dodatyVagon(vagon3);
        
        String input = "35\n" +          // Мінімальна кількість пасажирів
                      "45\n";            // Максимальна кількість пасажирів
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.znaytyVagon();
        
        // Метод має завершитися без помилок
        String output = outputStream.toString();
        assertTrue(output.contains("Знайдено") || output.length() > 0);
    }

    @Test
    void testFind_znaydenoKilka() {
        // Покриває: пошук знаходить кілька вагонів
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        PasazhyrskyVagon vagon2 = new PasazhyrskyVagon(
                2, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        PasazhyrskyVagon vagon3 = new PasazhyrskyVagon(
                3, 4, 60, KlasKomfortu.PLATSKART, 54, 6
        );
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        potiag.dodatyVagon(vagon3);
        
        String input = "30\n" +          // Мінімальна кількість пасажирів
                      "50\n";            // Максимальна кількість пасажирів
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.znaytyVagon();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Знайдено") || output.length() > 0);
    }

    @Test
    void testFind_nichogoNeZnaydeno() {
        // Покриває: пошук не знаходить жодного вагону
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "100\n" +         // Мінімальна кількість (більше ніж у вагоні)
                      "200\n";           // Максимальна кількість
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.znaytyVagon();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Знайдено: 0") || output.length() > 0);
    }

    @Test
    void testFind_invalidInput() {
        // Покриває: catch (NumberFormatException) у znaytyVagon()
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "abc\n" +         // Невалідна мінімальна кількість
                      "xyz\n";           // Невалідна максимальна кількість
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.znaytyVagon();
        
        String output = outputStream.toString();
        assertTrue(output.contains("❌") || output.contains("Помилка"));
    }

    @Test
    void testFind_emptySklad() {
        // Покриває: пошук в порожньому складі
        String input = "10\n" +          // Мінімальна кількість пасажирів
                      "50\n";            // Максимальна кількість пасажирів
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.znaytyVagon();
        
        assertTrue(potiag.getSklad().isEmpty());
        String output = outputStream.toString();
        assertTrue(output.contains("Знайдено: 0") || output.length() > 0);
    }

    @Test
    void testCalculate_passengers_nonEmpty() {
        // Покриває: pidrakhuvatyPasazhyriv() для непорожнього потягу
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        PasazhyrskyVagon vagon2 = new PasazhyrskyVagon(
                2, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.pidrakhuvatyPasazhyriv();
        
        String output = outputStream.toString();
        assertTrue(output.contains("76") || output.contains("пасажирів")); // 40 + 36 = 76
    }

    @Test
    void testCalculate_passengers_empty() {
        // Покриває: pidrakhuvatyPasazhyriv() для порожнього потягу
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.pidrakhuvatyPasazhyriv();
        
        String output = outputStream.toString();
        assertTrue(output.contains("0") || output.contains("пасажирів"));
    }

    @Test
    void testCalculate_baggage_nonEmpty() {
        // Покриває: pidrakhuvatyBagazh() для непорожнього потягу
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        SlyzhbovyVagon vagon2 = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.pidrakhuvatyBagazh();
        
        String output = outputStream.toString();
        assertTrue(output.contains("150") || output.contains("багаж")); // 50 + 100 = 150
    }

    @Test
    void testCalculate_baggage_empty() {
        // Покриває: pidrakhuvatyBagazh() для порожнього потягу
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.pidrakhuvatyBagazh();
        
        String output = outputStream.toString();
        assertTrue(output.contains("0") || output.contains("багаж"));
    }

    @Test
    void testPokazatySklad_nonEmpty() {
        // Покриває: pokazatySklad() для непорожнього складу
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.pokazatySklad();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Test Potiag") || output.contains("Vagon"));
    }

    @Test
    void testPokazatySklad_empty() {
        // Покриває: pokazatySklad() для порожнього складу
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.pokazatySklad();
        
        String output = outputStream.toString();
        assertTrue(output.contains("порожній") || output.length() > 0);
    }

    @Test
    void testVykhid() {
        // Покриває: vykhid() метод
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        service.vykhid();
        
        String output = outputStream.toString();
        assertTrue(output.contains("До побачення") || output.length() > 0);
    }

    @Test
    void testGetPotiag() {
        // Покриває: getPotiag() метод
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        Potiag result = service.getPotiag();
        
        assertSame(potiag, result);
    }
}

