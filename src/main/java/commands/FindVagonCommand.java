// ExitVagonCommand.java
package commands;

import services.SkladService;

public class FindVagonCommand implements Command {
    private SkladService service;

    public FindVagonCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.znaytyVagon();
    }

    @Override
    public String getDescription() {
        return "8. Знайти вагони за пасажирами";
    }
}