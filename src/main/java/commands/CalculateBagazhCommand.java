// CalculateBagazhCommand.java
package commands;

import services.SkladService;

public class CalculateBagazhCommand implements Command {
    private SkladService service;

    public CalculateBagazhCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.pidrakhuvatyBagazh();
    }

    @Override
    public String getDescription() {
        return "7. Підрахувати багаж";
    }
}