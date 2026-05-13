// Vagon.java
package model;

public abstract class Vagon {
    private int id;
    private int komfortnist;
    private int bagazhKilkist;

    public Vagon(int id, int komfortnist, int bagazhKilkist) {
        this.id = id;
        this.komfortnist = komfortnist;
        this.bagazhKilkist = bagazhKilkist;
    }

    public abstract String getType();
    public abstract int getPasazhyrskaMistkist();
    public abstract String toFileString();

    public int getId() { return id; }
    public int getKomfortnist() { return komfortnist; }
    public int getBagazhKilkist() { return bagazhKilkist; }

    public void setKomfortnist(int komfortnist) {
        this.komfortnist = komfortnist;
    }

    public void setBagazhKilkist(int bagazhKilkist) {
        this.bagazhKilkist = bagazhKilkist;
    }

    @Override
    public String toString() {
        return String.format("Vagon #%d [Komf: %d, Bagazh: %d]",
                id, komfortnist, bagazhKilkist);
    }
}