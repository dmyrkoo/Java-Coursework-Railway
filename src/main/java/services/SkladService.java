// SkladService.java
package services;

import model.*;
import repository.SqliteVagonRepository;
import repository.VagonRepository;
import utils.FileManager;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Сервіс для управління складом потягу.
 * Синхронізує операції з оперативною пам'яттю та базою даних.
 */
public class SkladService {

    private static final Logger logger = LoggerFactory.getLogger(SkladService.class);

    private final Potiag potiag;
    private final Scanner scanner;
    private final VagonRepository repository;
    private int nextId = 1;

    /**
     * Конструктор з повною ін'єкцією залежностей.
     *
     * @param potiag     потяг для управління
     * @param scanner    джерело вводу
     * @param repository репозиторій для збереження в БД
     */
    public SkladService(Potiag potiag, Scanner scanner, VagonRepository repository) {
        this.potiag = potiag;
        this.scanner = scanner;
        this.repository = repository;
        logger.info("SkladService створено для потяга: {}", potiag.getNazva());
        logger.info("SkladService created for train: " + potiag.getNazva());
    }

    /**
     * Конструктор з репозиторієм та стандартним System.in.
     */
    public SkladService(Potiag potiag, VagonRepository repository) {
        this(potiag, new Scanner(System.in), repository);
    }

    /**
     * Конструктор без репозиторію (зворотна сумісність для тестів).
     */
    public SkladService(Potiag potiag, Scanner scanner) {
        this(potiag, scanner, null);
    }

    /**
     * Конструктор за замовчуванням (System.in, без репозиторію).
     */
    public SkladService(Potiag potiag) {
        this(potiag, new Scanner(System.in), null);
    }

    /**
     * Завантажує вагони з бази даних в оперативну пам'ять потягу.
     * Якщо репозиторій не підключено, нічого не відбувається.
     */
    public void zavantazhytyZBazy() {
        if (repository == null) {
            logger.warn("Репозиторій не підключено, пропускаю завантаження з БД");
            return;
        }

        List<Vagon> vagons = repository.getAllVagons();
        for (Vagon v : vagons) {
            potiag.dodatyVagon(v);
            if (v.getId() >= nextId) {
                nextId = v.getId() + 1;
            }
        }

        System.out.println("✅ Завантажено " + vagons.size() + " вагонів з бази даних");
        logger.info("Завантажено {} вагонів з БД у потяг '{}'", vagons.size(), potiag.getNazva());
    }

    /**
     * Додає готовий вагон у потяг та зберігає в БД.
     * Призначений для виклику з UI або програмного коду.
     *
     * @param vagon вагон для додавання
     */
    public void dodatyVagon(Vagon vagon) {
        potiag.dodatyVagon(vagon);
        saveToRepository(vagon);
        logger.info("Додано вагон ID={} ({}) через програмний виклик", vagon.getId(), vagon.getType());
    }

    /**
     * Інтерактивне додавання вагону через консоль (Scanner).
     */
    public void dodatyVagonInteractive() {
        System.out.println("\n=== Додавання вагону ===");
        System.out.println("1 - Пасажирський");
        System.out.println("2 - Службовий");
        System.out.print("Ваш вибір: ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            logger.info("Користувач обрав додати пасажирський вагон");
            logger.info("User chose to add passenger wagon");
            dodatyPasazhyrskyVagon();
        } else if (choice.equals("2")) {
            logger.info("Користувач обрав додати службовий вагон");
            logger.info("User chose to add service wagon");
            dodatySlyzhbovyVagon();
        } else {
            System.out.println("❌ Невірний вибір!");
            logger.warn("Невірний вибір при додаванні вагону: {}", choice);
            logger.info("Invalid choice when adding wagon: " + choice);
        }
    }

    private void dodatyPasazhyrskyVagon() {
        try {
            System.out.print("Комфортність (1-10): ");
            int komf = Integer.parseInt(scanner.nextLine());

            System.out.print("Кількість багажу: ");
            int bagazh = Integer.parseInt(scanner.nextLine());

            System.out.println("Клас (VIP/KUPE/PLATSKART/ZAHALNYI): ");
            String klasStr = scanner.nextLine().toUpperCase();
            KlasKomfortu klas = KlasKomfortu.fromString(klasStr);

            System.out.print("Кількість пасажирів: ");
            int pasazhyriv = Integer.parseInt(scanner.nextLine());

            System.out.print("Рівень обслуговування (1-10): ");
            int riven = Integer.parseInt(scanner.nextLine());

            PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                    nextId++, komf, bagazh, klas, pasazhyriv, riven
            );

            potiag.dodatyVagon(vagon);
            saveToRepository(vagon);
            System.out.println("✅ Додано: " + vagon);
            logger.info("Додано пасажирський вагон ID {} (комфорт: {}, пасажирів: {})", vagon.getId(), komf, pasazhyriv);
            logger.info("Added passenger wagon ID " + vagon.getId());

        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка введення!");
            logger.warn("Некоректні дані при додаванні пасажирського вагону", e);
            logger.info("Invalid input when adding passenger wagon: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Помилка!");
            logger.error("Несподівана помилка при додаванні пасажирського вагону", e);
            logger.error("Unexpected error when adding passenger wagon", e);
        }
    }

    private void dodatySlyzhbovyVagon() {
        try {
            System.out.print("Комфортність (1-10): ");
            int komf = Integer.parseInt(scanner.nextLine());

            System.out.print("Кількість багажу: ");
            int bagazh = Integer.parseInt(scanner.nextLine());

            System.out.print("Кількість персоналу: ");
            int personal = Integer.parseInt(scanner.nextLine());

            System.out.print("Тип (Restoran/Poshta/...): ");
            String typ = scanner.nextLine();

            SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                    nextId++, komf, bagazh, personal, typ
            );

            potiag.dodatyVagon(vagon);
            saveToRepository(vagon);
            System.out.println("✅ Додано: " + vagon);
            logger.info("Додано службовий вагон ID {} (тип: {}, персонал: {})", vagon.getId(), typ, personal);
            logger.info("Added service wagon ID " + vagon.getId());

        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка введення!");
            logger.warn("Некоректні дані при додаванні службового вагону", e);
            logger.info("Invalid input when adding service wagon: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Помилка!");
            logger.error("Несподівана помилка при додаванні службового вагону", e);
            logger.error("Unexpected error when adding service wagon", e);
        }
    }

    /**
     * Видаляє вагон за ID (програмний виклик, без консольного вводу).
     * Призначений для виклику з UI через Command Pattern.
     *
     * @param id ідентифікатор вагону для видалення
     */
    public void vydalytyVagonById(int id) {
        if (potiag.vydalytyVagon(id)) {
            deleteFromRepository(id);
            logger.info("Видалено вагон ID={} через програмний виклик", id);
        } else {
            logger.warn("Спроба видалити неіснуючий вагон ID={}", id);
        }
    }

    public void vydalytyVagon() {
        pokazatySklad();
        if (potiag.getSklad().isEmpty()) {
            return;
        }

        System.out.print("\nВведіть ID для видалення: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (potiag.vydalytyVagon(id)) {
                deleteFromRepository(id);
                System.out.println("✅ Вагон #" + id + " видалено");
                logger.info("Видалено вагон ID {}", id);
                logger.info("Deleted wagon ID " + id);
            } else {
                System.out.println("❌ Вагон не знайдено");
                logger.info("Спроба видалити неіснуючий вагон ID {}", id);
                logger.info("Attempted to delete non-existing wagon ID " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Некоректний ID!");
            logger.warn("Некоректний ID при видаленні вагону", e);
            logger.info("Invalid ID when deleting wagon: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Помилка при видаленні вагону");
            logger.error("Помилка при видаленні вагону", e);
            logger.error("Error when deleting wagon", e);
        }
    }

    public void redaguvatyVagon() {
        pokazatySklad();
        if (potiag.getSklad().isEmpty()) {
            return;
        }

        System.out.print("\nВведіть ID для редагування: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Vagon vagon = potiag.znaytyVagonById(id);

            if (vagon == null) {
                System.out.println("❌ Вагон не знайдено");
                logger.info("Спроба редагувати неіснуючий вагон ID {}", id);
                logger.info("Attempted to edit non-existing wagon ID " + id);
                return;
            }

            System.out.println("Редагування: " + vagon);

            System.out.print("Нова комфортність (Enter - skip): ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                vagon.setKomfortnist(Integer.parseInt(input));
            }

            System.out.print("Новий багаж (Enter - skip): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                vagon.setBagazhKilkist(Integer.parseInt(input));
            }

            if (vagon instanceof PasazhyrskyVagon pv) {
                // використовую pattern variable (Java 16+)
                System.out.print("Нова к-ть пасажирів (Enter - skip): ");
                input = scanner.nextLine();
                if (!input.isEmpty()) {
                    pv.setKilkistPasazhyriv(Integer.parseInt(input));
                }

                System.out.print("Новий рівень (Enter - skip): ");
                input = scanner.nextLine();
                if (!input.isEmpty()) {
                    pv.setRivenObslugovuvannya(Integer.parseInt(input));
                }
            }

            saveToRepository(vagon);
            System.out.println("✅ Оновлено: " + vagon);
            logger.info("Оновлено вагон ID {}", id);
            logger.info("Updated wagon ID " + id);

        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка!");
            logger.warn("Некоректні дані при редагуванні вагону", e);
            logger.info("Invalid input when editing wagon: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Помилка!");
            logger.error("Несподівана помилка при редагуванні вагону", e);
            logger.error("Unexpected error when editing wagon", e);
        }
    }

    public void sortuvatyVagony() {
        List<Vagon> sklad = potiag.getSklad();

        if (sklad.isEmpty()) {
            System.out.println("❌ Склад порожній!");
            return;
        }

        sklad.sort(Comparator
                .comparingInt(Vagon::getKomfortnist).reversed()
                .thenComparing((v1, v2) -> {
                    if (v1 instanceof PasazhyrskyVagon && v2 instanceof PasazhyrskyVagon) {
                        return Integer.compare(
                                ((PasazhyrskyVagon)v2).getRivenObslugovuvannya(),
                                ((PasazhyrskyVagon)v1).getRivenObslugovuvannya()
                        );
                    }
                    return 0;
                })
        );

        System.out.println("✅ Відсортовано за рівнем комфортності");
        logger.info("Виконано сортування вагонів за комфортністю");
        logger.info("Sorted wagons by comfort");
        pokazatySklad();
    }

    public void pokazatySklad() {
        System.out.println("\n=== " + potiag + " ===");

        if (potiag.getSklad().isEmpty()) {
            System.out.println("Склад порожній");
            return;
        }

        for (Vagon v : potiag.getSklad()) {
            System.out.println(v);
        }
    }

    public void pidrakhuvatyPasazhyriv() {
        int total = potiag.getZagalnaKilkistPasazhyriv();
        System.out.println("✅ Загальна к-ть пасажирів: " + total);
        logger.info("Запит загальної кількості пасажирів: {}", total);
        logger.info("Queried total passengers: " + total);
    }

    public void pidrakhuvatyBagazh() {
        int total = potiag.getZagalnyiBagazh();
        System.out.println("✅ Загальний багаж: " + total);
        logger.info("Запит загальної кількості багажу: {}", total);
        logger.info("Queried total baggage: " + total);
    }

    public void znaytyVagon() {
        try {
            System.out.print("Мін. к-ть пасажирів: ");
            int min = Integer.parseInt(scanner.nextLine());

            System.out.print("Макс. к-ть пасажирів: ");
            int max = Integer.parseInt(scanner.nextLine());

            logger.info("Пошук вагонів за діапазоном пасажирів: {}-{}", min, max);
            logger.info("Searching wagons by passenger range: " + min + "-" + max);

            List<Vagon> znaideni = potiag.getSklad().stream()
                    .filter(v -> v.getPasazhyrskaMistkist() >= min && v.getPasazhyrskaMistkist() <= max)
                    .toList();

            System.out.println("\n=== Знайдено: " + znaideni.size() + " ===");
            for (Vagon v : znaideni) {
                System.out.println(v);
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка!");
            logger.warn("Некоректні дані при пошуку вагонів", e);
            logger.info("Invalid input when searching wagons: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Помилка!");
            logger.error("Несподівана помилка при пошуку вагонів", e);
            logger.error("Unexpected error when searching wagons", e);
        }
    }

    public void zberegtyUFile() {
        try {
            FileManager.zberegty(potiag);
            System.out.println("✅ Збережено у potiag_data.txt");
            logger.info("Дані потяга збережено у файл: potiag_data.txt");
            logger.info("Saved train to file: potiag_data.txt");
        } catch (IOException e) {
            System.out.println("❌ Помилка: " + e.getMessage());
            logger.error("Помилка при збереженні потяга у файл", e);
            logger.error("Error saving train to file", e);
        }
    }

    public void zavantazhytyZFile() {
        try {
            logger.info("Спроба завантажити потяг з файлу");
            logger.info("Attempting to load train from file");
            Potiag loaded = FileManager.zavantazhyty();
            potiag.ochystyty();
            for (Vagon v : loaded.getSklad()) {
                potiag.dodatyVagon(v);
                if (v.getId() >= nextId) {
                    nextId = v.getId() + 1;
                }
            }
            System.out.println("✅ Завантажено");
            logger.info("Успішно завантажено потяг з файлу. Кількість вагонів: {}", potiag.getSklad().size());
            logger.info("Successfully loaded train from file. Count: " + potiag.getSklad().size());
            pokazatySklad();
        } catch (IOException e) {
            System.out.println("❌ Помилка: " + e.getMessage());
            logger.error("Помилка при завантаженні потяга з файлу", e);
            logger.error("Error loading train from file", e);
        }
    }

    public void vykhid() {
        System.out.println("До побачення!");
        logger.info("Користувач вийшов із програми");
        logger.info("User exited application");
    }

    public Potiag getPotiag() {
        return potiag;
    }

    // ========== Допоміжні методи для роботи з репозиторієм ==========

    /**
     * Зберігає вагон у репозиторій, якщо він підключений.
     */
    private void saveToRepository(Vagon vagon) {
        if (repository != null) {
            try {
                repository.saveVagon(vagon);
                logger.info("Вагон ID={} синхронізовано з БД", vagon.getId());
            } catch (Exception e) {
                logger.error("Помилка синхронізації вагону ID={} з БД", vagon.getId(), e);
                System.out.println("⚠️ Вагон додано в пам'ять, але не збережено в БД");
            }
        }
    }

    /**
     * Видаляє вагон з репозиторію, якщо він підключений.
     */
    private void deleteFromRepository(int id) {
        if (repository instanceof SqliteVagonRepository sqliteRepo) {
            try {
                sqliteRepo.deleteVagon(id);
                logger.info("Вагон ID={} видалено з БД", id);
            } catch (Exception e) {
                logger.error("Помилка видалення вагону ID={} з БД", id, e);
                System.out.println("⚠️ Вагон видалено з пам'яті, але не з БД");
            }
        }
    }
}
