// DeleteVagonCommand.java
package commands;

import services.SkladService;

public class EditVagonCommand implements Command {
    private SkladService service;

    public EditVagonCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.redaguvatyVagon();
    }

    @Override
    public String getDescription() {
        return "5. Редагувати вагон";
    }
}