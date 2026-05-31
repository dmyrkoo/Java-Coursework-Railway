package commands;

import services.PotiagService;

/**
 * Команда для сортування вагонів за вибраним критерієм.
 * Делегує виконання до {@link PotiagService} (Constructor Injection).
 */
public class SortVagonsCommand implements Command {
    private final PotiagService service;
    private final String criterion;

    /**
     * Конструктор для GUI: делегує до PotiagService з критерієм.
     *
     * @param service сервіс бізнес-логіки потяга
     * @param criterion критерій сортування
     */
    public SortVagonsCommand(PotiagService service, String criterion) {
        this.service = service;
        this.criterion = criterion;
    }

    @Override
    public void execute() {
        service.sortuvaty(criterion);
    }

    @Override
    public String getDescription() {
        return "3. Сортувати вагони (" + criterion + ")";
    }
}