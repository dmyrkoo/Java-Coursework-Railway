package commands;

import model.Vagon;
import services.SkladService;

/**
 * Команда для додавання вагону до складу потяга.
 * Приймає готовий об'єкт {@link Vagon} через конструктор (Constructor Injection).
 */
public class AddVagonCommand implements Command {
    private final SkladService service;
    private final Vagon vagon;

    /**
     * Конструктор для GUI: додає конкретний вагон.
     *
     * @param service сервіс управління складом
     * @param vagon   вагон для додавання
     */
    public AddVagonCommand(SkladService service, Vagon vagon) {
        this.service = service;
        this.vagon = vagon;
    }


    @Override
    public void execute() {
        service.dodatyVagon(vagon);
    }

    @Override
    public String getDescription() {
        return "1. Додати вагон";
    }
}