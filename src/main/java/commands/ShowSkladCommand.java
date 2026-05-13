// ShowSkladCommand.java
package commands;

import services.SkladService;

public class ShowSkladCommand implements Command {
    private SkladService service;

    public ShowSkladCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.pokazatySklad();
    }

    @Override
    public String getDescription() {
        return "2. Показати склад потяга";
    }
}