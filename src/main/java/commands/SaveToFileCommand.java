// SaveToFileCommand.java
package commands;

import services.SkladService;

public class SaveToFileCommand implements Command {
    private SkladService service;

    public SaveToFileCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.zberegtyUFile();
    }

    @Override
    public String getDescription() {
        return "9. Зберегти у файл";
    }
}