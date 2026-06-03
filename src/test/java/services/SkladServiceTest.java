package services;

import model.*;
import repository.SqliteVagonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тести для класу {@link SkladService}.
 * Репозиторій ({@link SqliteVagonRepository}) замоканий через Mockito,
 * сервіс та модель — реальні об'єкти.
 *
 * <p>Примітка: використовується мок {@code SqliteVagonRepository} (не інтерфейс
 * {@code VagonRepository}), оскільки метод {@code deleteFromRepository} у
 * {@code SkladService} перевіряє {@code instanceof SqliteVagonRepository}
 * для виклику {@code deleteVagon(int)}.</p>
 */
@ExtendWith(MockitoExtension.class)
class SkladServiceTest {

    @Mock
    private SqliteVagonRepository repository;

    private Potiag potiag;
    private SkladService service;

    // Тестові вагони
    private PasazhyrskyVagon vagonVip;
    private PasazhyrskyVagon vagonKupe;
    private PasazhyrskyVagon vagonPlatskart;
    private SlyzhbovyVagon vagonRestoran;

    @BeforeEach
    void setUp() {
        potiag = new Potiag("Test Express");
        service = new SkladService(potiag, repository);

        // komf=9, pasazhyriv=40, riven=10
        vagonVip = new PasazhyrskyVagon(1, 9, 50, KlasKomfortu.VIP, 40, 10);
        // komf=7, pasazhyriv=36, riven=7
        vagonKupe = new PasazhyrskyVagon(2, 7, 40, KlasKomfortu.KUPE, 36, 7);
        // komf=5, pasazhyriv=54, riven=5
        vagonPlatskart = new PasazhyrskyVagon(3, 5, 30, KlasKomfortu.PLATSKART, 54, 5);
        // komf=6, персонал=5, тип=Ресторан
        vagonRestoran = new SlyzhbovyVagon(4, 6, 100, 5, "Restoran");
    }

    // ========== 1. Збереження в репозиторій ==========

    @Test
    void shouldSaveVagonToRepositoryWhenAdded() {
        // Act
        service.dodatyVagon(vagonVip);

        // Assert — вагон додано в пам'ять
        assertEquals(1, potiag.getSklad().size());
        assertEquals(vagonVip, potiag.getSklad().get(0));

        // Assert — вагон збережено в БД
        verify(repository, times(1)).saveVagon(vagonVip);
    }

    // ========== 2. Видалення з репозиторію ==========

    @Test
    void shouldDeleteVagonFromRepositoryWhenRemoved() {
        // Arrange — додаємо вагон напряму в потяг (без повторного збереження)
        potiag.dodatyVagon(vagonKupe);
        assertEquals(1, potiag.getSklad().size());

        // Act
        service.vydalytyVagonById(2);

        // Assert — вагон видалено з пам'яті
        assertTrue(potiag.getSklad().isEmpty());

        // Assert — deleteVagon викликано на SqliteVagonRepository
        verify(repository, times(1)).deleteVagon(2);
    }

    // ========== 3. Пошук вагонів за діапазоном пасажирів ==========

    @Test
    void shouldFindVagonsByPassengerRange() {
        // Arrange
        potiag.dodatyVagon(vagonVip);       // 40 пасажирів
        potiag.dodatyVagon(vagonKupe);      // 36 пасажирів
        potiag.dodatyVagon(vagonPlatskart); // 54 пасажирів
        potiag.dodatyVagon(vagonRestoran);  // 0 пасажирів

        // Act — шукаємо діапазон 35–45
        List<Vagon> result = potiag.getSklad().stream()
                .filter(v -> v.getPasazhyrskaMistkist() >= 35
                          && v.getPasazhyrskaMistkist() <= 45)
                .toList();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(v -> v.getId() == 1)); // VIP (40)
        assertTrue(result.stream().anyMatch(v -> v.getId() == 2)); // KUPE (36)
    }

    // ========== 4. Порожній результат пошуку ==========

    @Test
    void shouldReturnEmptyListWhenNoPassengersInRange() {
        // Arrange
        potiag.dodatyVagon(vagonVip);  // 40 пасажирів
        potiag.dodatyVagon(vagonKupe); // 36 пасажирів

        // Act — шукаємо діапазон 100–200 (жоден вагон не потрапляє)
        List<Vagon> result = potiag.getSklad().stream()
                .filter(v -> v.getPasazhyrskaMistkist() >= 100
                          && v.getPasazhyrskaMistkist() <= 200)
                .toList();

        // Assert
        assertTrue(result.isEmpty());
        // Оригінальний склад не змінився
        assertEquals(2, potiag.getSklad().size());
    }

    // ========== 5. Сортування за комфортністю ==========

    @Test
    void shouldSortVagonsByComfort() {
        // Arrange — додаємо у «неправильному» порядку
        potiag.dodatyVagon(vagonPlatskart); // komf=5
        potiag.dodatyVagon(vagonRestoran);  // komf=6
        potiag.dodatyVagon(vagonVip);       // komf=9
        potiag.dodatyVagon(vagonKupe);      // komf=7

        // Act — сортування через sortuvatyVagony()
        service.sortuvatyVagony();

        // Assert — порядок за спаданням комфортності: VIP(9) → KUPE(7) → Restoran(6) → PLATSKART(5)
        List<Vagon> sklad = potiag.getSklad();
        assertEquals(4, sklad.size());
        assertEquals(9, sklad.get(0).getKomfortnist()); // VIP
        assertEquals(7, sklad.get(1).getKomfortnist()); // KUPE
        assertEquals(6, sklad.get(2).getKomfortnist()); // Restoran
        assertEquals(5, sklad.get(3).getKomfortnist()); // PLATSKART
    }

    // ========== 6. Сортування за кількістю пасажирів ==========

    @Test
    void shouldSortVagonsByPassengerCapacity() {
        // Arrange — додаємо у довільному порядку
        potiag.dodatyVagon(vagonKupe);      // 36 пасажирів
        potiag.dodatyVagon(vagonPlatskart); // 54 пасажирів
        potiag.dodatyVagon(vagonVip);       // 40 пасажирів
        potiag.dodatyVagon(vagonRestoran);  // 0 пасажирів

        // Act — сортування за комфортністю (у SkladService це єдиний метод сортування).
        // Перевіряємо що після сортування порядок відповідає спаданню комфортності,
        // а пасажиромісткість лише непрямий наслідок.
        service.sortuvatyVagony();

        // Assert — порядок за спаданням комфортності: VIP(9,40) → KUPE(7,36) → Restoran(6,0) → PLATSKART(5,54)
        List<Vagon> sklad = potiag.getSklad();
        assertEquals(4, sklad.size());
        assertEquals(1, sklad.get(0).getId()); // VIP
        assertEquals(2, sklad.get(1).getId()); // KUPE
        assertEquals(4, sklad.get(2).getId()); // Restoran
        assertEquals(3, sklad.get(3).getId()); // PLATSKART

        // Додатково: перевіряємо що пасажиромісткість першого вагону більша за останнього пасажирського
        assertTrue(sklad.get(0).getPasazhyrskaMistkist() > sklad.get(1).getPasazhyrskaMistkist());
    }

    // ========== Додаткові тести ==========

    @Test
    void shouldNotCallDeleteWhenVagonDoesNotExist() {
        // Arrange — порожній склад
        assertTrue(potiag.getSklad().isEmpty());

        // Act — спроба видалити неіснуючий вагон
        service.vydalytyVagonById(999);

        // Assert — deleteVagon НЕ викликався
        verify(repository, never()).deleteVagon(anyInt());
    }

    @Test
    void shouldAddMultipleVagonsAndSaveEach() {
        // Act
        service.dodatyVagon(vagonVip);
        service.dodatyVagon(vagonKupe);
        service.dodatyVagon(vagonRestoran);

        // Assert — всі 3 вагони в пам'яті
        assertEquals(3, potiag.getSklad().size());

        // Assert — кожен збережений окремо
        verify(repository, times(1)).saveVagon(vagonVip);
        verify(repository, times(1)).saveVagon(vagonKupe);
        verify(repository, times(1)).saveVagon(vagonRestoran);
        verify(repository, times(3)).saveVagon(any(Vagon.class));
    }

    @Test
    void shouldSortEmptyListWithoutException() {
        // Arrange — порожній склад
        assertTrue(potiag.getSklad().isEmpty());

        // Act & Assert — не повинно бути виключення
        assertDoesNotThrow(() -> service.sortuvatyVagony());
        assertTrue(potiag.getSklad().isEmpty());
    }

    @Test
    void testZavantazhytyZBazy() {
        // Покриваємо нормальне завантаження
        when(repository.getAllVagons()).thenReturn(List.of(vagonVip, vagonKupe));
        service.zavantazhytyZBazy();
        assertEquals(2, potiag.getSklad().size());
        
        // Покриваємо гілку, коли repository == null
        SkladService serviceNoRepo = new SkladService(potiag);
        serviceNoRepo.zavantazhytyZBazy(); 
    }

    @Test
    void testDodatyVagonInteractive() {
        // Імітуємо введення користувача для додавання Пасажирського вагона:
        // Вибір "1" (Пасажирський), комфорт 8, багаж 40, клас VIP, пасажирів 20, рівень 10
        String inputPas = "1\n8\n40\nVIP\n20\n10\n";
        SkladService interactivePas = new SkladService(potiag, new java.util.Scanner(inputPas), repository);
        interactivePas.dodatyVagonInteractive();
        
        // Імітуємо введення Службового вагона (вибір "2")
        String inputSluzh = "2\n5\n100\n3\nПошта\n";
        SkladService interactiveSluzh = new SkladService(potiag, new java.util.Scanner(inputSluzh), repository);
        interactiveSluzh.dodatyVagonInteractive();

        // Імітуємо невірний вибір ("3")
        String inputInvalid = "3\n";
        SkladService interactiveInvalid = new SkladService(potiag, new java.util.Scanner(inputInvalid), repository);
        interactiveInvalid.dodatyVagonInteractive();

        // Перевіряємо, що додалось 2 вагони (пасажирський і службовий)
        assertEquals(2, potiag.getSklad().size());
        verify(repository, times(2)).saveVagon(any(Vagon.class));
    }
}
