package repository;

import model.Vagon;

import java.util.List;

/**
 * Інтерфейс репозиторію для роботи з вагонами.
 * Визначає базові CRUD-операції для збереження та отримання вагонів.
 */
public interface VagonRepository {

    /**
     * Зберігає вагон у сховищі даних.
     *
     * @param vagon вагон для збереження
     */
    void saveVagon(Vagon vagon);

    /**
     * Повертає список усіх вагонів зі сховища.
     *
     * @return список вагонів
     */
    List<Vagon> getAllVagons();

    /**
     * Видаляє всі записи вагонів зі сховища.
     */
    void clearAll();
}
