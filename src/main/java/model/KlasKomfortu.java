package model;

public enum KlasKomfortu {
    VIP("ВІП"),
    KUPE("Купе"),
    PLATSKART("Плацкарт"),
    ZAHALNYI("Загальний");

    private final String displayName;

    KlasKomfortu(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static KlasKomfortu fromString(String str) {
        if (str == null) return ZAHALNYI;
        String normalized = str.trim();
        for (KlasKomfortu klas : values()) {
            if (klas.name().equalsIgnoreCase(normalized) || klas.getDisplayName().equalsIgnoreCase(normalized)) {
                return klas;
            }
        }
        return ZAHALNYI;
    }
}