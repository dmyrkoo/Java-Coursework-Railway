// ExitCommand.java
package commands;

import services.SkladService;

public class ExitCommand implements Command {
    private SkladService service;

    public ExitCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.vykhid();
    }

    @Override
    public String getDescription() {
        return "0. Вихід";
    }
}