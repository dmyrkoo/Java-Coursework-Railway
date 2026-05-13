// Potiag.java
package model;

import java.util.ArrayList;
import java.util.List;

public class Potiag {
    private String nazva;
    private List<Vagon> sklad = new ArrayList<>();

    public Potiag(String nazva) {
        this.nazva = nazva;
    }

    public String getNazva() { return nazva; }
    public List<Vagon> getSklad() { return sklad; }

    public void dodatyVagon(Vagon vagon) {
        sklad.add(vagon);
    }

    public boolean vydalytyVagon(int id) {
        return sklad.removeIf(v -> v.getId() == id);
    }

    public Vagon znaytyVagonById(int id) {
        for (Vagon v : sklad) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    public int getZagalnaKilkistPasazhyriv() {
        int total = 0;
        for (Vagon v : sklad) {
            total += v.getPasazhyrskaMistkist();
        }
        return total;
    }

    public int getZagalnyiBagazh() {
        int total = 0;
        for (Vagon v : sklad) {
            total += v.getBagazhKilkist();
        }
        return total;
    }

    public void ochystyty() {
        sklad.clear();
    }

    @Override
    public String toString() {
        return String.format("Potiag '%s' [Vagoniv: %d, Pasazhyriv: %d, Bagazh: %d]",
                nazva, sklad.size(), getZagalnaKilkistPasazhyriv(), getZagalnyiBagazh());
    }
}