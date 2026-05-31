package commands;

import model.Vagon;
import services.PotiagService;

import java.util.List;

public class FindVagonsQuery implements Query<List<Vagon>> {
    private final PotiagService service;
    private final int min;
    private final int max;

    public FindVagonsQuery(PotiagService service, int min, int max) {
        this.service = service;
        this.min = min;
        this.max = max;
    }

    @Override
    public List<Vagon> execute() {
        return service.znaytyVagonyZaPasazhyramy(min, max);
    }
}
