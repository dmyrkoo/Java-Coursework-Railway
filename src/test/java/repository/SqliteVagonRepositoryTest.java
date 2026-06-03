package repository;

import model.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Інтеграційні тести для {@link SqliteVagonRepository}.
 * Використовує in-memory SQLite БД ({@code jdbc:sqlite::memory:}),
 * щоб не чіпати production базу даних.
 *
 * <p>Конструктор {@code SqliteVagonRepository(String dbUrl)} автоматично
 * створює таблицю та вставляє seed-дані (5 вагонів). Перед кожним тестом
 * таблиця очищається через {@code clearAll()}, щоб тести не залежали
 * від seed-даних та один від одного.</p>
 */
class SqliteVagonRepositoryTest {

    private SqliteVagonRepository repository;

    @BeforeEach
    void setUp() {
        // In-memory БД: створюється та знищується автоматично
        repository = new SqliteVagonRepository("jdbc:sqlite::memory:");
        // Очищаємо seed-дані, щоб кожен тест стартував з порожньої таблиці
        repository.clearAll();
    }

    @AfterEach
    void tearDown() {
        if (repository != null) {
            repository.close();
        }
    }

    // ========== 1. Збереження пасажирського вагону ==========

    @Test
    @DisplayName("shouldSavePassengerVagon — збереження та читання пасажирського вагону")
    void shouldSavePassengerVagon() {
        // Arrange
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                101, 8, 45, KlasKomfortu.VIP, 20, 9
        );

        // Act
        repository.saveVagon(vagon);
        List<Vagon> all = repository.getAllVagons();

        // Assert
        assertEquals(1, all.size());
        assertInstanceOf(PasazhyrskyVagon.class, all.get(0));

        PasazhyrskyVagon saved = (PasazhyrskyVagon) all.get(0);
        assertEquals(101, saved.getId());
        assertEquals("Pasazhyrsky", saved.getType());
        assertEquals(8, saved.getKomfortnist());
        assertEquals(45, saved.getBagazhKilkist());
        assertEquals(KlasKomfortu.VIP, saved.getKlasKomfortu());
        assertEquals(20, saved.getKilkistPasazhyriv());
        assertEquals(9, saved.getRivenObslugovuvannya());
    }

    // ========== 2. Збереження службового вагону ==========

    @Test
    @DisplayName("shouldSaveServiceVagon — збереження та читання службового вагону")
    void shouldSaveServiceVagon() {
        // Arrange
        SlyzhbovyVagon vagon = new SlyzhbovyVagon(
                202, 6, 150, 5, "Ресторан"
        );

        // Act
        repository.saveVagon(vagon);
        List<Vagon> all = repository.getAllVagons();

        // Assert
        assertEquals(1, all.size());
        assertInstanceOf(SlyzhbovyVagon.class, all.get(0));

        SlyzhbovyVagon saved = (SlyzhbovyVagon) all.get(0);
        assertEquals(202, saved.getId());
        assertEquals("Slyzhbovy", saved.getType());
        assertEquals(6, saved.getKomfortnist());
        assertEquals(150, saved.getBagazhKilkist());
        assertEquals(5, saved.getPersonalKilkist());
        assertEquals("Ресторан", saved.getTypPryznachennya());
    }

    // ========== 3. Повернення всіх збережених вагонів ==========

    @Test
    @DisplayName("shouldReturnAllSavedVagons — повертає всі збережені вагони у правильному порядку")
    void shouldReturnAllSavedVagons() {
        // Arrange
        PasazhyrskyVagon v1 = new PasazhyrskyVagon(1, 9, 50, KlasKomfortu.VIP, 40, 10);
        PasazhyrskyVagon v2 = new PasazhyrskyVagon(2, 7, 40, KlasKomfortu.KUPE, 36, 7);
        SlyzhbovyVagon v3 = new SlyzhbovyVagon(3, 5, 200, 3, "Багажний");

        // Act
        repository.saveVagon(v1);
        repository.saveVagon(v2);
        repository.saveVagon(v3);

        List<Vagon> all = repository.getAllVagons();

        // Assert
        assertEquals(3, all.size());

        // Перевірка порядку (ORDER BY id)
        assertEquals(1, all.get(0).getId());
        assertEquals(2, all.get(1).getId());
        assertEquals(3, all.get(2).getId());

        // Перевірка типів
        assertInstanceOf(PasazhyrskyVagon.class, all.get(0));
        assertInstanceOf(PasazhyrskyVagon.class, all.get(1));
        assertInstanceOf(SlyzhbovyVagon.class, all.get(2));
    }

    // ========== 4. Видалення вагону за ID ==========

    @Test
    @DisplayName("shouldDeleteVagonById — видаляє лише вказаний вагон")
    void shouldDeleteVagonById() {
        // Arrange
        PasazhyrskyVagon v1 = new PasazhyrskyVagon(10, 8, 50, KlasKomfortu.VIP, 20, 10);
        PasazhyrskyVagon v2 = new PasazhyrskyVagon(20, 6, 40, KlasKomfortu.KUPE, 36, 7);
        SlyzhbovyVagon v3 = new SlyzhbovyVagon(30, 4, 100, 2, "Пошта");

        repository.saveVagon(v1);
        repository.saveVagon(v2);
        repository.saveVagon(v3);
        assertEquals(3, repository.getAllVagons().size());

        // Act — видаляємо середній вагон
        repository.deleteVagon(20);

        // Assert
        List<Vagon> remaining = repository.getAllVagons();
        assertEquals(2, remaining.size());
        assertEquals(10, remaining.get(0).getId());
        assertEquals(30, remaining.get(1).getId());

        // Переконуємось, що видалений вагон відсутній
        assertTrue(remaining.stream().noneMatch(v -> v.getId() == 20));
    }

    // ========== 5. Коректність маппінгу полів ==========

    @Test
    @DisplayName("shouldMapPassengerAndServiceFieldsCorrectly — всі поля зберігаються/читаються без втрат")
    void shouldMapPassengerAndServiceFieldsCorrectly() {
        // Arrange — вагони з різними enum-значеннями
        PasazhyrskyVagon pasVip = new PasazhyrskyVagon(1, 10, 60, KlasKomfortu.VIP, 18, 10);
        PasazhyrskyVagon pasKupe = new PasazhyrskyVagon(2, 7, 45, KlasKomfortu.KUPE, 36, 7);
        PasazhyrskyVagon pasPlatskart = new PasazhyrskyVagon(3, 4, 30, KlasKomfortu.PLATSKART, 54, 3);
        PasazhyrskyVagon pasZahalnyi = new PasazhyrskyVagon(4, 2, 20, KlasKomfortu.ZAHALNYI, 80, 1);
        SlyzhbovyVagon sluzhRestoran = new SlyzhbovyVagon(5, 7, 150, 5, "Ресторан");
        SlyzhbovyVagon sluzhPoshta = new SlyzhbovyVagon(6, 3, 500, 2, "Пошта");

        // Act
        repository.saveVagon(pasVip);
        repository.saveVagon(pasKupe);
        repository.saveVagon(pasPlatskart);
        repository.saveVagon(pasZahalnyi);
        repository.saveVagon(sluzhRestoran);
        repository.saveVagon(sluzhPoshta);

        List<Vagon> all = repository.getAllVagons();

        // Assert — розмір
        assertEquals(6, all.size());

        // --- Пасажирські вагони ---

        // VIP
        PasazhyrskyVagon loadedVip = (PasazhyrskyVagon) all.get(0);
        assertEquals(KlasKomfortu.VIP, loadedVip.getKlasKomfortu());
        assertEquals(18, loadedVip.getKilkistPasazhyriv());
        assertEquals(10, loadedVip.getRivenObslugovuvannya());
        assertEquals(10, loadedVip.getKomfortnist());
        assertEquals(60, loadedVip.getBagazhKilkist());

        // KUPE
        PasazhyrskyVagon loadedKupe = (PasazhyrskyVagon) all.get(1);
        assertEquals(KlasKomfortu.KUPE, loadedKupe.getKlasKomfortu());
        assertEquals(36, loadedKupe.getKilkistPasazhyriv());

        // PLATSKART
        PasazhyrskyVagon loadedPlatskart = (PasazhyrskyVagon) all.get(2);
        assertEquals(KlasKomfortu.PLATSKART, loadedPlatskart.getKlasKomfortu());
        assertEquals(54, loadedPlatskart.getKilkistPasazhyriv());

        // ZAHALNYI
        PasazhyrskyVagon loadedZahalnyi = (PasazhyrskyVagon) all.get(3);
        assertEquals(KlasKomfortu.ZAHALNYI, loadedZahalnyi.getKlasKomfortu());
        assertEquals(80, loadedZahalnyi.getKilkistPasazhyriv());
        assertEquals(1, loadedZahalnyi.getRivenObslugovuvannya());

        // --- Службові вагони ---

        // Ресторан
        SlyzhbovyVagon loadedRestoran = (SlyzhbovyVagon) all.get(4);
        assertEquals(5, loadedRestoran.getPersonalKilkist());
        assertEquals("Ресторан", loadedRestoran.getTypPryznachennya());
        assertEquals(7, loadedRestoran.getKomfortnist());
        assertEquals(150, loadedRestoran.getBagazhKilkist());
        assertEquals(0, loadedRestoran.getPasazhyrskaMistkist()); // Службовий — 0 пасажирів

        // Пошта
        SlyzhbovyVagon loadedPoshta = (SlyzhbovyVagon) all.get(5);
        assertEquals(2, loadedPoshta.getPersonalKilkist());
        assertEquals("Пошта", loadedPoshta.getTypPryznachennya());
        assertEquals(500, loadedPoshta.getBagazhKilkist());
    }

    // ========== Додаткові тести ==========

    @Test
    @DisplayName("clearAll — повністю очищає таблицю")
    void shouldClearAllVagons() {
        // Arrange
        repository.saveVagon(new PasazhyrskyVagon(1, 5, 30, KlasKomfortu.KUPE, 36, 5));
        repository.saveVagon(new SlyzhbovyVagon(2, 3, 100, 2, "Пошта"));
        assertEquals(2, repository.getAllVagons().size());

        // Act
        repository.clearAll();

        // Assert
        assertTrue(repository.getAllVagons().isEmpty());
    }

    @Test
    @DisplayName("saveVagon з однаковим ID — перезаписує запис (INSERT OR REPLACE)")
    void shouldReplaceVagonWithSameId() {
        // Arrange
        PasazhyrskyVagon original = new PasazhyrskyVagon(1, 5, 30, KlasKomfortu.KUPE, 36, 5);
        repository.saveVagon(original);

        // Act — зберігаємо вагон з тим же ID, але іншими даними
        PasazhyrskyVagon updated = new PasazhyrskyVagon(1, 9, 50, KlasKomfortu.VIP, 20, 10);
        repository.saveVagon(updated);

        // Assert — має бути 1 запис з оновленими даними
        List<Vagon> all = repository.getAllVagons();
        assertEquals(1, all.size());

        PasazhyrskyVagon saved = (PasazhyrskyVagon) all.get(0);
        assertEquals(9, saved.getKomfortnist());
        assertEquals(KlasKomfortu.VIP, saved.getKlasKomfortu());
        assertEquals(20, saved.getKilkistPasazhyriv());
    }

    @Test
    @DisplayName("deleteVagon з неіснуючим ID — не кидає виключення")
    void shouldNotThrowWhenDeletingNonExistentId() {
        // Act & Assert — не повинно бути виключення
        assertDoesNotThrow(() -> repository.deleteVagon(999));
        assertTrue(repository.getAllVagons().isEmpty());
    }

    @Test
    @DisplayName("getAllVagons з порожньої таблиці — повертає порожній список")
    void shouldReturnEmptyListFromEmptyTable() {
        List<Vagon> all = repository.getAllVagons();

        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenDbUrlIsInvalid - покриває помилку конструктора")
    void shouldThrowExceptionWhenDbUrlIsInvalid() {
        assertThrows(RuntimeException.class, () -> new SqliteVagonRepository("jdbc:sqlite:/invalid_path/test.db"));
    }

    @Test
    @DisplayName("shouldCoverCatchBlocksAfterConnectionClosed - покриває всі catch(SQLException)")
    void shouldCoverCatchBlocksAfterConnectionClosed() {
        SqliteVagonRepository repo = new SqliteVagonRepository("jdbc:sqlite::memory:");
        repo.close();

        assertThrows(RuntimeException.class, () -> repo.saveVagon(new PasazhyrskyVagon(1, 1, 1, KlasKomfortu.VIP, 1, 1)));
        assertThrows(RuntimeException.class, () -> repo.deleteVagon(1));
        assertThrows(RuntimeException.class, () -> repo.getAllVagons());
        assertThrows(RuntimeException.class, () -> repo.clearAll());
    }
}
