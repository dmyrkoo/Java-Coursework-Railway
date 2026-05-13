// KlasKomfortu.java
package model;

public enum KlasKomfortu {
    VIP,
    KUPE,
    PLATSKART,
    ZAHALNYI;

    public static KlasKomfortu fromString(String str) {
        try {
            return KlasKomfortu.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ZAHALNYI;
        }
    }
}