package commands;

import services.SkladService;

/**
 * Команда для видалення вагону зі складу потяга за ID.
 * Приймає ідентифікатор вагону через конструктор (Constructor Injection).
 */
public class DeleteVagonCommand implements Command {
    private final SkladService service;
    private final int vagonId;

    /**
     * Конструктор для GUI: видаляє вагон за конкретним ID.
     *
     * @param service сервіс управління складом
     * @param vagonId ідентифікатор вагону для видалення
     */
    public DeleteVagonCommand(SkladService service, int vagonId) {
        this.service = service;
        this.vagonId = vagonId;
    }

    @Override
    public void execute() {
        service.vydalytyVagonById(vagonId);
    }

    @Override
    public String getDescription() {
        return "4. Видалити вагон";
    }
}