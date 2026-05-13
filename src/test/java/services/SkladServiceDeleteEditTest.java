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
 * Тести для методів видалення та редагування вагонів у SkladService
 * Покриває vydalytyVagon(), redaguvatyVagon() з різними сценаріями
 */
class SkladServiceDeleteEditTest {

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
    void testDelete_found() {
        // Покриває: позитивний шлях у vydalytyVagon() - успішне видалення
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "1\n";            // ID вагону для видалення
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.vydalytyVagon();
        
        assertTrue(potiag.getSklad().isEmpty());
        String output = outputStream.toString();
        assertTrue(output.contains("✅") || output.contains("видалено"));
    }

    @Test
    void testDelete_notFound() {
        // Покриває: else-блок у vydalytyVagon() - вагон не знайдено
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "999\n";          // Неіснуючий ID
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.vydalytyVagon();
        
        assertEquals(1, potiag.getSklad().size());
        String output = outputStream.toString();
        assertTrue(output.contains("не знайдено") || output.contains("❌"));
    }

    @Test
    void testDelete_invalidInput() {
        // Покриває: catch (NumberFormatException) у vydalytyVagon()
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "abc\n";          // Невалідний ID
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.vydalytyVagon();
        
        assertEquals(1, potiag.getSklad().size());
        String output = outputStream.toString();
        assertTrue(output.contains("❌") || output.contains("Некоректний"));
    }

    @Test
    void testDelete_emptySklad() {
        // Покриває: if (potiag.getSklad().isEmpty()) return у vydalytyVagon()
        String input = "1\n";
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.vydalytyVagon();
        
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testEdit_found_updateFields() {
        // Покриває: redaguvatyVagon() нормальний шлях, instanceof branch (pattern matching)
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "1\n" +           // ID вагону
                      "9\n" +            // Нова комфортність
                      "60\n" +           // Новий багаж
                      "45\n" +           // Нова кількість пасажирів
                      "10\n";            // Новий рівень обслуговування
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.redaguvatyVagon();
        
        PasazhyrskyVagon updated = (PasazhyrskyVagon) potiag.getSklad().get(0);
        assertEquals(9, updated.getKomfortnist());
        assertEquals(60, updated.getBagazhKilkist());
        assertEquals(45, updated.getKilkistPasazhyriv());
        assertEquals(10, updated.getRivenObslugovuvannya());
    }

    @Test
    void testEdit_skipFields() {
        // Покриває: skip-логіку в redaguvatyVagon() - пропуск полів через Enter
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "1\n" +           // ID вагону
                      "\n" +             // Пропустити комфортність
                      "\n" +             // Пропустити багаж
                      "\n" +             // Пропустити пасажирів
                      "\n";              // Пропустити рівень
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.redaguvatyVagon();
        
        // Перевіряємо, що дані не змінилися
        PasazhyrskyVagon updated = (PasazhyrskyVagon) potiag.getSklad().get(0);
        assertEquals(8, updated.getKomfortnist());
        assertEquals(50, updated.getBagazhKilkist());
        assertEquals(40, updated.getKilkistPasazhyriv());
        assertEquals(9, updated.getRivenObslugovuvannya());
    }

    @Test
    void testEdit_partialUpdate() {
        // Покриває: часткове оновлення полів в redaguvatyVagon()
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "1\n" +           // ID вагону
                      "9\n" +            // Нова комфортність
                      "\n" +             // Пропустити багаж
                      "45\n" +           // Нова кількість пасажирів
                      "\n";              // Пропустити рівень
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.redaguvatyVagon();
        
        PasazhyrskyVagon updated = (PasazhyrskyVagon) potiag.getSklad().get(0);
        assertEquals(9, updated.getKomfortnist());      // Оновлено
        assertEquals(50, updated.getBagazhKilkist());   // Не змінено
        assertEquals(45, updated.getKilkistPasazhyriv()); // Оновлено
        assertEquals(9, updated.getRivenObslugovuvannya()); // Не змінено
    }

    @Test
    void testEdit_slyzhbovyVagon() {
        // Покриває: редагування службового вагону (не instanceof PasazhyrskyVagon)
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                1, 6, 100, 5, "Restoran"
        );
        potiag.dodatyVagon(vagon);
        
        String input = "1\n" +           // ID вагону
                      "7\n" +            // Нова комфортність
                      "120\n" +          // Новий багаж
                      "\n" +             // Пропустити (для пасажирських полів не буде)
                      "\n";              // Пропустити
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.redaguvatyVagon();
        
        SlyzhbovyVagon updated = (SlyzhbovyVagon) potiag.getSklad().get(0);
        assertEquals(7, updated.getKomfortnist());
        assertEquals(120, updated.getBagazhKilkist());
    }

    @Test
    void testEdit_notFound() {
        // Покриває: return при неіснуючому ID в redaguvatyVagon()
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "999\n";          // Неіснуючий ID
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.redaguvatyVagon();
        
        // Перевіряємо, що дані не змінилися
        assertEquals(1, potiag.getSklad().size());
        PasazhyrskyVagon v = (PasazhyrskyVagon) potiag.getSklad().get(0);
        assertEquals(8, v.getKomfortnist());
        String output = outputStream.toString();
        assertTrue(output.contains("не знайдено") || output.contains("❌"));
    }

    @Test
    void testEdit_invalidInput() {
        // Покриває: catch (NumberFormatException) у redaguvatyVagon()
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        String input = "1\n" +           // ID вагону
                      "abc\n";          // Невалідна комфортність
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.redaguvatyVagon();
        
        // Перевіряємо, що дані не змінилися через помилку
        PasazhyrskyVagon v = (PasazhyrskyVagon) potiag.getSklad().get(0);
        assertEquals(8, v.getKomfortnist());
        String output = outputStream.toString();
        assertTrue(output.contains("❌") || output.contains("Помилка"));
    }

    @Test
    void testEdit_emptySklad() {
        // Покриває: if (potiag.getSklad().isEmpty()) return у redaguvatyVagon()
        String input = "1\n";
        
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SkladService service = new SkladService(potiag, scanner);
        
        service.redaguvatyVagon();
        
        assertTrue(potiag.getSklad().isEmpty());
    }
}

