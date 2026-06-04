package commands;

import services.PotiagService;

/**
 * Команда для сортування вагонів за вибраним критерієм.
 * Делегує виконання до {@link PotiagService} (Constructor Injection).
 */
public class SortVagonsCommand implements Command {
    private final PotiagService service;
    private final String criterion;
    private final boolean isDesc;

    /**
     * Конструктор для GUI: делегує до PotiagService з критерієм.
     *
     * @param service сервіс бізнес-логіки потяга
     * @param criterion критерій сортування
     * @param isDesc напрямок сортування (true = DESC)
     */
    public SortVagonsCommand(PotiagService service, String criterion, boolean isDesc) {
        this.service = service;
        this.criterion = criterion;
        this.isDesc = isDesc;
    }

    @Override
    public void execute() {
        service.sortuvaty(criterion, isDesc);
    }

    @Override
    public String getDescription() {
        return "3. Сортувати вагони (" + criterion + ", DESC: " + isDesc + ")";
    }
}