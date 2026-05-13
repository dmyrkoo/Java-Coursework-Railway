// CalculatePasazhyryCommand.java
package commands;

import services.SkladService;

public class CalculatePasazhyryCommand implements Command {
    private SkladService service;

    public CalculatePasazhyryCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.pidrakhuvatyPasazhyriv();
    }

    @Override
    public String getDescription() {
        return "6. Підрахувати пасажирів";
    }
}