package services;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import utils.FileManager;

/**
 * Тести для покриття IOException блоків у SkladService
 * Використовує Mockito.mockStatic для симуляції помилок FileManager
 */
class SkladServiceIOTest {

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
    void testZberegtyUFile_IOException() {
        // Покриває: catch (IOException) в zberegtyUFile()
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        // Мокуємо статичний метод FileManager.zberegty() щоб викинути IOException
        try (MockedStatic<FileManager> mockedFileManager = Mockito.mockStatic(FileManager.class)) {
            mockedFileManager.when(() -> FileManager.zberegty(any(Potiag.class)))
                    .thenThrow(new IOException("forced IO error"));
            
            service.zberegtyUFile();
            
            // Перевіряємо, що помилка оброблена
            String output = outputStream.toString();
            assertTrue(output.contains("❌") || output.contains("Помилка"));
        }
    }

    @Test
    void testZavantazhytyZFile_IOException() {
        // Покриває: catch (IOException) в zavantazhytyZFile()
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        // Мокуємо статичний метод FileManager.zavantazhyty() щоб викинути IOException
        try (MockedStatic<FileManager> mockedFileManager = Mockito.mockStatic(FileManager.class)) {
            mockedFileManager.when(() -> FileManager.zavantazhyty())
                    .thenThrow(new IOException("file not found"));
            
            service.zavantazhytyZFile();
            
            // Перевіряємо, що помилка оброблена
            String output = outputStream.toString();
            assertTrue(output.contains("❌") || output.contains("Помилка"));
        }
    }

    @Test
    void testZberegtyUFile_success() {
        // Покриває: нормальний шлях zberegtyUFile() без помилок
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        potiag.dodatyVagon(vagon);
        
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        // Мокуємо статичний метод FileManager.zberegty() щоб успішно виконатися
        try (MockedStatic<FileManager> mockedFileManager = Mockito.mockStatic(FileManager.class)) {
            mockedFileManager.when(() -> FileManager.zberegty(any(Potiag.class)))
                    .thenAnswer(invocation -> null); // Успішне виконання
            
            service.zberegtyUFile();
            
            // Перевіряємо, що успішно виконано
            String output = outputStream.toString();
            assertTrue(output.contains("✅") || output.contains("Збережено"));
        }
    }

    @Test
    void testZavantazhytyZFile_success() {
        // Покриває: нормальний шлях zavantazhytyZFile() без помилок
        Potiag loadedPotiag = new Potiag("Loaded Potiag");
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                5, 8, 50, KlasKomfortu.VIP, 40, 9
        );
        loadedPotiag.dodatyVagon(vagon);
        
        SkladService service = new SkladService(potiag, new Scanner(System.in));
        
        // Мокуємо статичний метод FileManager.zavantazhyty() щоб повернути завантажений потіг
        try (MockedStatic<FileManager> mockedFileManager = Mockito.mockStatic(FileManager.class)) {
            mockedFileManager.when(() -> FileManager.zavantazhyty())
                    .thenReturn(loadedPotiag);
            
            service.zavantazhytyZFile();
            
            // Перевіряємо, що дані завантажено
            assertEquals(1, potiag.getSklad().size());
            String output = outputStream.toString();
            assertTrue(output.contains("✅") || output.contains("Завантажено"));
        }
    }

}

