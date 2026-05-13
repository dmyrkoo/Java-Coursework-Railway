// LoadFromFileCommand.java
package commands;

import services.SkladService;

public class LoadFromFileCommand implements Command {
    private SkladService service;

    public LoadFromFileCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.zavantazhytyZFile();
    }

    @Override
    public String getDescription() {
        return "10. Завантажити з файлу";
    }
}