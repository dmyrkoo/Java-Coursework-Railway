// FileManager.java
package utils;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileManager {
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
    private static final String DEFAULT_FILE = "potiag_data.txt";

    // Приватний конструктор для запобігання створення екземплярів утилітного класу
    private FileManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void zberegty(Potiag potiag, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("NAZVA:" + potiag.getNazva());
            writer.newLine();

            for (Vagon v : potiag.getSklad()) {
                writer.write(v.toFileString());
                writer.newLine();
            }
            logger.info("Дані успішно збережено у файл: {}", filename);
        } catch (IOException e) {
            logger.error("Критична помилка при збереженні файлу: " + filename, e);
            throw e;
        }
    }

    public static void zberegty(Potiag potiag) throws IOException {
        zberegty(potiag, DEFAULT_FILE);
    }

    public static Potiag zavantazhyty(String filename) throws IOException {
        logger.info("Спроба завантаження даних з файлу: {}", filename);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            logger.error("Помилка при читанні файлу: " + filename, e);
            throw e;
        }

        if (lines.isEmpty()) {
            logger.error("Файл порожній: " + filename);
            throw new IOException("Файл порожній");
        }

        String nazva = "Potiag";
        if (lines.get(0).startsWith("NAZVA:")) {
            nazva = lines.get(0).substring(6);
            lines.remove(0);
        }

        Potiag potiag = new Potiag(nazva);

        for (String line : lines) {
            Vagon v = parseVagon(line);
            if (v != null) {
                potiag.dodatyVagon(v);
            }
        }

        logger.info("Успішно завантажено потяг з файлу: {}. Кількість вагонів: {}", filename, potiag.getSklad().size());
        return potiag;
    }

    public static Potiag zavantazhyty() throws IOException {
        return zavantazhyty(DEFAULT_FILE);
    }

    private static Vagon parseVagon(String line) {
        String[] parts = line.split(";");
        if (parts.length < 2) return null;

        try {
            String type = parts[0];

            if (type.equals("PASAZHYRSKY") && parts.length >= 7) {
                int id = Integer.parseInt(parts[1]);
                int komf = Integer.parseInt(parts[2]);
                int bagazh = Integer.parseInt(parts[3]);
                KlasKomfortu klas = KlasKomfortu.fromString(parts[4]);
                int pasazhyriv = Integer.parseInt(parts[5]);
                int riven = Integer.parseInt(parts[6]);

                return new PasazhyrskyVagon(id, komf, bagazh, klas, pasazhyriv, riven);

            } else if (type.equals("SLYZHBOVY") && parts.length >= 6) {
                int id = Integer.parseInt(parts[1]);
                int komf = Integer.parseInt(parts[2]);
                int bagazh = Integer.parseInt(parts[3]);
                int personal = Integer.parseInt(parts[4]);
                String pryznachennya = parts[5];

                return new SlyzhbovyVagon(id, komf, bagazh, personal, pryznachennya);
            }
        } catch (NumberFormatException e) {
            logger.warn("Помилка парсингу рядка файлу: {}", line, e);
        } catch (Exception e) {
            logger.error("Несподівана помилка при парсингу рядка: {}", line, e);
        }

        return null;
    }

    public static boolean fileExists() {
        return fileExists(DEFAULT_FILE);
    }

    // Перевантажений метод для тестування з конкретним файлом
    public static boolean fileExists(String filename) {
        return new File(filename).exists();
    }
}