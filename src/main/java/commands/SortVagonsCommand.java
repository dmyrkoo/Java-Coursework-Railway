package commands;

import services.PotiagService;

/**
 * Команда для сортування вагонів за рівнем комфортності.
 * Делегує виконання до {@link PotiagService} (Constructor Injection).
 */
public class SortVagonsCommand implements Command {
    private final PotiagService service;

    /**
     * Конструктор для GUI: делегує до PotiagService.
     *
     * @param service сервіс бізнес-логіки потяга
     */
    public SortVagonsCommand(PotiagService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.sortuvatyZaKomfortom();
    }

    @Override
    public String getDescription() {
        return "3. Сортувати вагони";
    }
}