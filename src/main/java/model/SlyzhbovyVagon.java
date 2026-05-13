// SluzhbovyVagon.java
package model;

public class SlyzhbovyVagon extends Vagon {
    private int personalKilkist;
    private String typPryznachennya;

    public SlyzhbovyVagon(int id, int komfortnist, int bagazhKilkist,
                          int personalKilkist, String typPryznachennya) {
        super(id, komfortnist, bagazhKilkist);
        this.personalKilkist = personalKilkist;
        this.typPryznachennya = typPryznachennya;
    }

    @Override
    public String getType() {
        return "Slyzhbovy";
    }

    @Override
    public int getPasazhyrskaMistkist() {
        return 0;
    }

    @Override
    public String toFileString() {
        return String.format("SLYZHBOVY;%d;%d;%d;%d;%s",
                getId(), getKomfortnist(), getBagazhKilkist(),
                personalKilkist, typPryznachennya);
    }

    public int getPersonalKilkist() { return personalKilkist; }
    public String getTypPryznachennya() { return typPryznachennya; }

    public void setPersonalKilkist(int personalKilkist) {
        this.personalKilkist = personalKilkist;
    }

    public void setTypPryznachennya(String typPryznachennya) {
        this.typPryznachennya = typPryznachennya;
    }

    @Override
    public String toString() {
        return String.format("Vagon #%d %s (%s) [Personal: %d, Komf: %d, Bagazh: %d]",
                getId(), getType(), typPryznachennya,
                personalKilkist, getKomfortnist(), getBagazhKilkist());
    }
}