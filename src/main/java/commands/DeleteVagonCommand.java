// DeleteVagonCommand.java
package commands;

import services.SkladService;

public class DeleteVagonCommand implements Command {
    private SkladService service;

    public DeleteVagonCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.vydalytyVagon();
    }

    @Override
    public String getDescription() {
        return "4. Видалити вагон";
    }
}