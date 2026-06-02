// PotiagService.java
package services;

import model.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Сервіс для бізнес-логіки потягу
 * Містить методи сортування та пошуку вагонів
 */
public class PotiagService {
    private static final Logger logger = LoggerFactory.getLogger(PotiagService.class);

    private final Potiag potiag;

    public PotiagService(Potiag potiag) {
        this.potiag = potiag;
        logger.info("PotiagService створено для потяга: {}", potiag.getNazva());
    }

    /**
     * Сортує вагони за вказаним критерієм.
     *
     * @param criterion критерій сортування ("За комфортністю", "За пасажирами", "За багажем")
     */
    public void sortuvaty(String criterion) {
        List<Vagon> sklad = potiag.getSklad();

        if (sklad.isEmpty()) {
            logger.info("Сортування неможливе: склад порожній");
            return;
        }

        switch (criterion) {
            case "За ID":
                sklad.sort(Comparator.comparingInt(Vagon::getId));
                break;
            case "За комфортністю":
                sklad.sort(Comparator
                        .comparingInt(Vagon::getKomfortnist).reversed()
                        .thenComparing((v1, v2) -> {
                            if (v1 instanceof PasazhyrskyVagon && v2 instanceof PasazhyrskyVagon) {
                                return Integer.compare(
                                        ((PasazhyrskyVagon) v2).getRivenObslugovuvannya(),
                                        ((PasazhyrskyVagon) v1).getRivenObslugovuvannya()
                                );
                            }
                            return 0;
                        })
                );
                break;
            case "За пасажирами":
                sklad.sort(Comparator.comparingInt(Vagon::getPasazhyrskaMistkist).reversed());
                break;
            case "За багажем":
                sklad.sort(Comparator.comparingInt(Vagon::getBagazhKilkist).reversed());
                break;
            default:
                logger.warn("Невідомий критерій сортування: {}", criterion);
                return;
        }

        logger.info("Виконано сортування вагонів за критерієм: {}", criterion);
    }

    /**
     * Знаходить вагони за діапазоном кількості пасажирів
     *
     * @param min мінімальна кількість пасажирів
     * @param max максимальна кількість пасажирів
     * @return список вагонів, що відповідають критерію
     */
    public List<Vagon> znaytyVagonyZaPasazhyramy(int min, int max) {
        logger.info("Пошук вагонів за діапазоном пасажирів: {}-{}", min, max);

        List<Vagon> znaideni = potiag.getSklad().stream()
                .filter(v -> v.getPasazhyrskaMistkist() >= min && v.getPasazhyrskaMistkist() <= max)
                .collect(Collectors.toList());

        logger.info("Знайдено {} вагонів у діапазоні {}-{}", znaideni.size(), min, max);
        return znaideni;
    }

    public Potiag getPotiag() {
        return potiag;
    }
}
