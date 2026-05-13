package services;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Тести для методів додавання вагонів у SkladService
 * Покриває dodatyVagon(), dodatyPasazhyrskyVagon(), dodatySlyzhbovyVagon()
 * з різними сценаріями успіху та помилок
 */
class SkladServiceAddTest {

    private Potiag potiag;

    @BeforeEach
    void setUp() {
        potiag = new Potiag("Test Potiag");
    }

    @Test
    void testAddPassenger_success() {
        // Покриває: dodatyVagon() choice "1", dodatyPasazhyrskyVagon() нормальний шлях
        String input = "1\n" +           // Вибір пасажирського вагону
                      "8\n" +            // Комфортність
                      "50\n" +           // Кількість багажу
                      "VIP\n" +          // Клас комфортності
                      "40\n" +           // Кількість пасажирів
                      "9\n";             // Рівень обслуговування
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        assertEquals(1, potiag.getSklad().size());
        assertTrue(potiag.getSklad().get(0) instanceof PasazhyrskyVagon);
        PasazhyrskyVagon vagon = (PasazhyrskyVagon) potiag.getSklad().get(0);
        assertEquals(8, vagon.getKomfortnist());
        assertEquals(50, vagon.getBagazhKilkist());
        assertEquals(KlasKomfortu.VIP, vagon.getKlasKomfortu());
        assertEquals(40, vagon.getKilkistPasazhyriv());
        assertEquals(9, vagon.getRivenObslugovuvannya());
    }

    @Test
    void testAddPassenger_invalidNumberFormat() {
        // Покриває: catch (NumberFormatException) в dodatyPasazhyrskyVagon()
        String input = "1\n" +           // Вибір пасажирського вагону
                      "abc\n";           // Невалідна комфортність
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        // Вагон не додано через помилку
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testAddPassenger_invalidNumberFormat_bagazh() {
        // Покриває: catch (NumberFormatException) при невалідному багажі
        String input = "1\n" +           // Вибір пасажирського вагону
                      "8\n" +            // Валідна комфортність
                      "xyz\n";           // Невалідний багаж
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testAddPassenger_invalidNumberFormat_pasazhyriv() {
        // Покриває: catch (NumberFormatException) при невалідній кількості пасажирів
        String input = "1\n" +           // Вибір пасажирського вагону
                      "8\n" +            // Валідна комфортність
                      "50\n" +           // Валідний багаж
                      "VIP\n" +          // Валідний клас
                      "def\n";           // Невалідна кількість пасажирів
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testAddPassenger_invalidNumberFormat_riven() {
        // Покриває: catch (NumberFormatException) при невалідному рівні обслуговування
        String input = "1\n" +           // Вибір пасажирського вагону
                      "8\n" +            // Валідна комфортність
                      "50\n" +           // Валідний багаж
                      "VIP\n" +          // Валідний клас
                      "40\n" +           // Валідна кількість пасажирів
                      "ghi\n";           // Невалідний рівень
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testAddService_success() {
        // Покриває: dodatyVagon() choice "2", dodatySlyzhbovyVagon() нормальний шлях
        String input = "2\n" +           // Вибір службового вагону
                      "6\n" +            // Комфортність
                      "100\n" +          // Кількість багажу
                      "5\n" +            // Кількість персоналу
                      "Restoran\n";      // Тип призначення
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        assertEquals(1, potiag.getSklad().size());
        assertTrue(potiag.getSklad().get(0) instanceof SlyzhbovyVagon);
        SlyzhbovyVagon vagon = (SlyzhbovyVagon) potiag.getSklad().get(0);
        assertEquals(6, vagon.getKomfortnist());
        assertEquals(100, vagon.getBagazhKilkist());
        assertEquals(5, vagon.getPersonalKilkist());
        assertEquals("Restoran", vagon.getTypPryznachennya());
    }

    @Test
    void testAddService_invalidNumberFormat() {
        // Покриває: catch (NumberFormatException) в dodatySlyzhbovyVagon()
        String input = "2\n" +           // Вибір службового вагону
                      "xyz\n";           // Невалідна комфортність
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testAddService_invalidNumberFormat_bagazh() {
        // Покриває: catch (NumberFormatException) при невалідному багажі для службового вагону
        String input = "2\n" +           // Вибір службового вагону
                      "6\n" +            // Валідна комфортність
                      "abc\n";           // Невалідний багаж
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testAddService_invalidNumberFormat_personal() {
        // Покриває: catch (NumberFormatException) при невалідній кількості персоналу
        String input = "2\n" +           // Вибір службового вагону
                      "6\n" +            // Валідна комфортність
                      "100\n" +          // Валідний багаж
                      "def\n";           // Невалідна кількість персоналу
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.dodatyVagonInteractive();
        
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testAddPassenger_allKlasyKomfortu() {
        // Покриває: різні класи комфортності для пасажирських вагонів
        String[] klasy = {"VIP", "KUPE", "PLATSKART", "ZAHALNYI"};
        
        for (String klas : klasy) {
            String input = "1\n" +       // Вибір типу вагону
                          "8\n" +        // Комфортність
                          "50\n" +       // Кількість багажу
                          klas + "\n" +  // Клас комфортності
                          "40\n" +       // Кількість пасажирів
                          "9\n";         // Рівень обслуговування
            
            Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            SkladService service = new SkladService(potiag, scanner);
            service.dodatyVagonInteractive();
        }
        
        assertEquals(4, potiag.getSklad().size());
        for (Vagon v : potiag.getSklad()) {
            assertTrue(v instanceof PasazhyrskyVagon);
            assertNotNull(((PasazhyrskyVagon) v).getKlasKomfortu());
        }
    }

    @Test
    void testAddService_riziTypy() {
        // Покриває: різні типи службових вагонів
        String[] typy = {"Restoran", "Poshta", "Medychny", "Bagazhnyi"};
        
        for (String typ : typy) {
            String input = "2\n" +       // Вибір типу вагону
                          "6\n" +        // Комфортність
                          "100\n" +      // Кількість багажу
                          "5\n" +        // Кількість персоналу
                          typ + "\n";    // Тип призначення
            
            Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            SkladService service = new SkladService(potiag, scanner);
            service.dodatyVagonInteractive();
        }
        
        assertEquals(4, potiag.getSklad().size());
        for (Vagon v : potiag.getSklad()) {
            assertTrue(v instanceof SlyzhbovyVagon);
        }
    }
}

