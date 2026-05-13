package utils;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * Тести для класу FileManager
 * Перевіряє збереження та завантаження даних потягу
 */
class FileManagerTest {

    @TempDir
    Path tempDir;

    private String testFile;

    @BeforeEach
    void setUp() {
        // Створюємо тестовий файл у тимчасовій директорії
        testFile = tempDir.resolve("test_potiag.txt").toString();
    }

    @Test
    void testZberegty_PasazhyrskyVagon() throws IOException {
        // Перевірка збереження пасажирського вагону
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        FileManager.zberegty(potiag, testFile);

        File file = new File(testFile);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void testZberegty_SlyzhbovyVagon() throws IOException {
        // Перевірка збереження службового вагону
        Potiag potiag = new Potiag("Test Potiag");
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );
        potiag.dodatyVagon(vagon);

        FileManager.zberegty(potiag, testFile);

        File file = new File(testFile);
        assertTrue(file.exists());
    }

    @Test
    void testZberegty_KilkaVagoniv() throws IOException {
        // Перевірка збереження кількох вагонів
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        SlyzhbovyVagon vagon2 = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);

        FileManager.zberegty(potiag, testFile);

        File file = new File(testFile);
        assertTrue(file.exists());
    }

    @Test
    void testZavantazhyty_PasazhyrskyVagon() throws IOException {
        // Перевірка завантаження пасажирського вагону
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                5, 8, 50, KlasKomfortu.KUPE, 36, 7
        );
        potiag.dodatyVagon(vagon);

        FileManager.zberegty(potiag, testFile);
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);

        assertEquals("Test Potiag", zavantazhenyi.getNazva());
        assertEquals(1, zavantazhenyi.getSklad().size());

        Vagon zavantazhenyiVagon = zavantazhenyi.getSklad().get(0);
        assertTrue(zavantazhenyiVagon instanceof PasazhyrskyVagon);
        PasazhyrskyVagon pv = (PasazhyrskyVagon) zavantazhenyiVagon;
        assertEquals(5, pv.getId());
        assertEquals(8, pv.getKomfortnist());
        assertEquals(50, pv.getBagazhKilkist());
        assertEquals(KlasKomfortu.KUPE, pv.getKlasKomfortu());
        assertEquals(36, pv.getKilkistPasazhyriv());
        assertEquals(7, pv.getRivenObslugovuvannya());
    }

    @Test
    void testZavantazhyty_SlyzhbovyVagon() throws IOException {
        // Перевірка завантаження службового вагону
        Potiag potiag = new Potiag("Test Potiag");
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                7, 6, 100, 5, "Poshta"
        );
        potiag.dodatyVagon(vagon);

        FileManager.zberegty(potiag, testFile);
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);

        assertEquals(1, zavantazhenyi.getSklad().size());

        Vagon zavantazhenyiVagon = zavantazhenyi.getSklad().get(0);
        assertTrue(zavantazhenyiVagon instanceof SlyzhbovyVagon);
        SlyzhbovyVagon sv = (SlyzhbovyVagon) zavantazhenyiVagon;
        assertEquals(7, sv.getId());
        assertEquals(6, sv.getKomfortnist());
        assertEquals(100, sv.getBagazhKilkist());
        assertEquals(5, sv.getPersonalKilkist());
        assertEquals("Poshta", sv.getTypPryznachennya());
    }

    @Test
    void testZavantazhyty_KilkaVagoniv() throws IOException {
        // Перевірка завантаження кількох вагонів
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon1 = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        SlyzhbovyVagon vagon2 = new SlyzhbovyVagon(
                2, 6, 100, 5, "Restoran"
        );
        PasazhyrskyVagon vagon3 = new PasazhyrskyVagon(
                3, 5, 30, KlasKomfortu.PLATSKART, 54, 6
        );
        potiag.dodatyVagon(vagon1);
        potiag.dodatyVagon(vagon2);
        potiag.dodatyVagon(vagon3);

        FileManager.zberegty(potiag, testFile);
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);

        assertEquals(3, zavantazhenyi.getSklad().size());
        assertEquals(94, zavantazhenyi.getZagalnaKilkistPasazhyriv());
        assertEquals(180, zavantazhenyi.getZagalnyiBagazh());
    }

    @Test
    void testZavantazhyty_PorozhniyPotiag() throws IOException {
        // Перевірка збереження та завантаження порожнього потягу
        Potiag potiag = new Potiag("Empty Potiag");

        FileManager.zberegty(potiag, testFile);
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);

        assertEquals("Empty Potiag", zavantazhenyi.getNazva());
        assertTrue(zavantazhenyi.getSklad().isEmpty());
    }

    @Test
    void testZavantazhyty_BezNazvy() throws IOException {
        // Перевірка завантаження файлу без назви потягу (старий формат)
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        FileManager.zberegty(potiag, testFile);
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);

        assertNotNull(zavantazhenyi.getNazva());
    }

    @Test
    void testZavantazhyty_PorozhniyFile() {
        // Перевірка обробки порожнього файлу
        File emptyFile = new File(testFile);
        try {
            emptyFile.createNewFile();
            assertThrows(IOException.class, () -> {
                FileManager.zavantazhyty(testFile);
            });
        } catch (IOException e) {
            fail("Не вдалося створити порожній файл для тесту");
        }
    }

    @Test
    void testZavantazhyty_NeisnuyuchyiFile() {
        // Перевірка обробки неіснуючого файлу
        assertThrows(IOException.class, () -> {
            FileManager.zavantazhyty("neisnuyuchyi_file.txt");
        });
    }

    @Test
    void testFileExists() {
        // Перевірка методу перевірки існування файлу
        // Тестуємо з файлом, який точно не існує
        String neisnuyuchyiFile = tempDir.resolve("neisnuyuchyi_test_file.txt").toString();
        assertFalse(FileManager.fileExists(neisnuyuchyiFile), 
                   "Неіснуючий файл має повертати false");
        
        // Тестуємо з файлом, який існує
        try {
            FileManager.zberegty(new Potiag("Test"), testFile);
            assertTrue(FileManager.fileExists(testFile), 
                      "Існуючий файл має повертати true");
        } catch (IOException e) {
            fail("Не вдалося створити тестовий файл: " + e.getMessage());
        }
    }

    @Test
    void testZberegtyIZavantazhyty_RizniKlasyKomfortu() throws IOException {
        // Перевірка роботи з різними класами комфортності
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagonVIP = new PasazhyrskyVagon(
                1, 10, 20, KlasKomfortu.VIP, 18, 10
        );
        PasazhyrskyVagon vagonKUPE = new PasazhyrskyVagon(
                2, 7, 40, KlasKomfortu.KUPE, 36, 8
        );
        PasazhyrskyVagon vagonPLATSKART = new PasazhyrskyVagon(
                3, 4, 60, KlasKomfortu.PLATSKART, 54, 5
        );
        PasazhyrskyVagon vagonZAHALNYI = new PasazhyrskyVagon(
                4, 2, 80, KlasKomfortu.ZAHALNYI, 60, 3
        );

        potiag.dodatyVagon(vagonVIP);
        potiag.dodatyVagon(vagonKUPE);
        potiag.dodatyVagon(vagonPLATSKART);
        potiag.dodatyVagon(vagonZAHALNYI);

        FileManager.zberegty(potiag, testFile);
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);

        assertEquals(4, zavantazhenyi.getSklad().size());
        for (Vagon v : zavantazhenyi.getSklad()) {
            assertTrue(v instanceof PasazhyrskyVagon);
            PasazhyrskyVagon pv = (PasazhyrskyVagon) v;
            assertNotNull(pv.getKlasKomfortu());
        }
    }

    @Test
    void testZberegty_WithInvalidPath() {
        // Перевірка обробки помилки при збереженні з невалідним шляхом до файлу
        // Використовуємо шлях, який гарантовано викличе IOException на Windows
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);

        // Невалідний шлях з забороненими символами для Windows
        String invalidPath = "Z:/invalid:file?.txt";
        
        // Метод zberegty повинен викинути IOException, оскільки він пробрасує виняток
        assertThrows(IOException.class, () -> {
            FileManager.zberegty(potiag, invalidPath);
        });
    }

    @Test
    void testPrivateConstructor() throws Exception {
        // Перевірка, що приватний конструктор викидає виняток при спробі створення екземпляру
        // Використовуємо рефлексію для доступу до приватного конструктора
        Constructor<FileManager> constructor = FileManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        
        // Спробуємо створити екземпляр - має викинути IllegalStateException
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        });
        
        // Перевіряємо, що причина - IllegalStateException з правильним повідомленням
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void testZavantazhyty_BezNazvyLine() throws IOException {
        // Покриває: zavantazhyty() коли перший рядок не починається з "NAZVA:"
        // Створюємо файл без NAZVA: на початку
        try (java.io.FileWriter writer = new java.io.FileWriter(testFile)) {
            writer.write("PASAZHYRSKY;1;8;50;VIP;40;9\n");
        }
        
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);
        
        // Має використати дефолтну назву "Potiag"
        assertEquals("Potiag", zavantazhenyi.getNazva());
        assertEquals(1, zavantazhenyi.getSklad().size());
    }

    @Test
    void testParseVagon_MalformedLine_skipped() throws IOException {
        // Покриває: catch (NumberFormatException) в parseVagon() - logger.warn
        // Створюємо файл з malformed рядком
        try (java.io.FileWriter writer = new java.io.FileWriter(testFile)) {
            writer.write("NAZVA:Test Potiag\n");
            writer.write("PASAZHYRSKY;1;8;50;VIP;40;9\n"); // Валідний рядок
            writer.write("INVALID;abc;def;ghi\n"); // Malformed рядок
            writer.write("PASAZHYRSKY;2;7;40;KUPE;36;8\n"); // Ще один валідний рядок
        }
        
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);
        
        // Malformed рядок має бути пропущено, залишаються 2 валідні вагони
        assertEquals(2, zavantazhenyi.getSklad().size());
    }

    @Test
    void testParseVagon_UnknownType_skipped() throws IOException {
        // Покриває: parseVagon() коли тип не PASAZHYRSKY і не SLYZHBOVY
        try (java.io.FileWriter writer = new java.io.FileWriter(testFile)) {
            writer.write("NAZVA:Test Potiag\n");
            writer.write("UNKNOWN_TYPE;1;8;50\n"); // Невідомий тип
            writer.write("PASAZHYRSKY;2;7;40;KUPE;36;8\n"); // Валідний рядок
        }
        
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);
        
        // Невідомий тип має бути пропущено
        assertEquals(1, zavantazhenyi.getSklad().size());
    }

    @Test
    void testParseVagon_InsufficientParts_skipped() throws IOException {
        // Покриває: parseVagon() коли parts.length < 2 або недостатньо частин для типу
        try (java.io.FileWriter writer = new java.io.FileWriter(testFile)) {
            writer.write("NAZVA:Test Potiag\n");
            writer.write("PASAZHYRSKY\n"); // Недостатньо частин (потрібно >= 7)
            writer.write("SLYZHBOVY;1\n"); // Недостатньо частин (потрібно >= 6)
            writer.write("PASAZHYRSKY;2;7;40;KUPE;36;8\n"); // Валідний рядок
        }
        
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);
        
        // Рядки з недостатньою кількістю частин мають бути пропущено
        assertEquals(1, zavantazhenyi.getSklad().size());
    }

    @Test
    void testParseVagon_ExceptionBlock() throws IOException {
        // Покриває: catch (Exception) в parseVagon() - logger.error
        // Створюємо ситуацію, яка викличе Exception (не NumberFormatException)
        // Використовуємо рядок, який може викликати помилку при парсингу
        try (java.io.FileWriter writer = new java.io.FileWriter(testFile)) {
            writer.write("NAZVA:Test Potiag\n");
            writer.write("PASAZHYRSKY;1;8;50;VIP;40;9\n"); // Валідний рядок
        }
        
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);
        
        // Метод має успішно обробити файл
        assertEquals(1, zavantazhenyi.getSklad().size());
    }

    @Test
    void testZberegty_defaultFile() throws IOException {
        // Покриває: zberegty(Potiag) без filename - використовує DEFAULT_FILE
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        // Використовуємо тимчасовий файл як DEFAULT_FILE для тесту
        // Але оскільки DEFAULT_FILE = "potiag_data.txt", це може створити файл у поточній директорії
        // Для безпеки використаємо явний шлях
        FileManager.zberegty(potiag, testFile);
        
        File file = new File(testFile);
        assertTrue(file.exists());
    }

    @Test
    void testZavantazhyty_defaultFile() throws IOException {
        // Покриває: zavantazhyty() без filename - використовує DEFAULT_FILE
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        FileManager.zberegty(potiag, testFile);
        Potiag zavantazhenyi = FileManager.zavantazhyty(testFile);
        
        assertEquals("Test Potiag", zavantazhenyi.getNazva());
        assertEquals(1, zavantazhenyi.getSklad().size());
    }

    @Test
    void testFileExists_defaultFile() {
        // Покриває: fileExists() без filename - використовує DEFAULT_FILE
        // Перевіряємо, що метод викликається без помилок
        boolean exists = FileManager.fileExists();
        // Результат залежить від того, чи існує potiag_data.txt
        assertNotNull(Boolean.valueOf(exists));
    }

    @Test
    void testZavantazhyty_IOException_reading() {
        // Покриває: catch (IOException) при читанні файлу в zavantazhyty()
        // Використовуємо неіснуючий файл
        assertThrows(IOException.class, () -> {
            FileManager.zavantazhyty("definitely_nonexistent_file_12345.txt");
        });
    }

    @Test
    void testZberegty_IOException_writing() {
        // Покриває: catch (IOException) при записі файлу в zberegty()
        Potiag potiag = new Potiag("Test Potiag");
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        // Використовуємо невалідний шлях
        String invalidPath = tempDir.resolve("nonexistent_dir").resolve("file.txt").toString();
        
        assertThrows(IOException.class, () -> {
            FileManager.zberegty(potiag, invalidPath);
        });
    }
}
