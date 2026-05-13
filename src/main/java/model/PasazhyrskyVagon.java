// PasazhyrskyVagon.java
package model;

public class PasazhyrskyVagon extends Vagon {
    private KlasKomfortu klasKomfortu;
    private int kilkistPasazhyriv;
    private int rivenObslugovuvannya;

    public PasazhyrskyVagon(int id, int komfortnist, int bagazhKilkist,
                            KlasKomfortu klasKomfortu, int kilkistPasazhyriv,
                            int rivenObslugovuvannya) {
        super(id, komfortnist, bagazhKilkist);
        this.klasKomfortu = klasKomfortu;
        this.kilkistPasazhyriv = kilkistPasazhyriv;
        this.rivenObslugovuvannya = rivenObslugovuvannya;
    }

    @Override
    public String getType() {
        return "Pasazhyrsky";
    }

    @Override
    public int getPasazhyrskaMistkist() {
        return kilkistPasazhyriv;
    }

    @Override
    public String toFileString() {
        return String.format("PASAZHYRSKY;%d;%d;%d;%s;%d;%d",
                getId(), getKomfortnist(), getBagazhKilkist(),
                klasKomfortu.name(), kilkistPasazhyriv, rivenObslugovuvannya);
    }

    public KlasKomfortu getKlasKomfortu() { return klasKomfortu; }
    public int getKilkistPasazhyriv() { return kilkistPasazhyriv; }
    public int getRivenObslugovuvannya() { return rivenObslugovuvannya; }

    public void setKilkistPasazhyriv(int kilkistPasazhyriv) {
        this.kilkistPasazhyriv = kilkistPasazhyriv;
    }

    public void setRivenObslugovuvannya(int rivenObslugovuvannya) {
        this.rivenObslugovuvannya = rivenObslugovuvannya;
    }

    public int getRivenObslugovannya() {
        return this.rivenObslugovuvannya;
    }

    @Override
    public String toString() {
        return String.format("Vagon #%d %s (%s) [Pasazh: %d, Riven: %d, Komf: %d, Bagazh: %d]",
                getId(), getType(), klasKomfortu.name(),
                kilkistPasazhyriv, rivenObslugovuvannya,
                getKomfortnist(), getBagazhKilkist());
    }
}