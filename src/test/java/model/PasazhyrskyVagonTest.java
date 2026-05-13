package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу PasazhyrskyVagon
 * Перевіряє функціональність пасажирських вагонів
 */
class PasazhyrskyVagonTest {

    @Test
    void testKonstruktor() {
        // Перевірка створення пасажирського вагону з усіма параметрами
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 8, 50, KlasKomfortu.VIP, 40, 9
        );

        assertEquals(1, vagon.getId());
        assertEquals(8, vagon.getKomfortnist());
        assertEquals(50, vagon.getBagazhKilkist());
        assertEquals(KlasKomfortu.VIP, vagon.getKlasKomfortu());
        assertEquals(40, vagon.getKilkistPasazhyriv());
        assertEquals(9, vagon.getRivenObslugovuvannya());
    }

    @Test
    void testGetType() {
        // Перевірка повернення типу вагону
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        assertEquals("Pasazhyrsky", vagon.getType());
    }

    @Test
    void testGetPasazhyrskaMistkist() {
        // Перевірка повернення пасажирської місткості
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 5, 30, KlasKomfortu.PLATSKART, 54, 6
        );
        assertEquals(54, vagon.getPasazhyrskaMistkist());
    }

    @Test
    void testToFileString() {
        // Перевірка форматування для збереження у файл
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                5, 7, 45, KlasKomfortu.ZAHALNYI, 60, 5
        );
        String expected = "PASAZHYRSKY;5;7;45;ZAHALNYI;60;5";
        assertEquals(expected, vagon.toFileString());
    }

    @Test
    void testToString() {
        // Перевірка рядкового представлення вагону
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                3, 9, 40, KlasKomfortu.VIP, 20, 10
        );
        String result = vagon.toString();
        assertTrue(result.contains("Vagon #3"));
        assertTrue(result.contains("Pasazhyrsky"));
        assertTrue(result.contains("VIP"));
        assertTrue(result.contains("20")); // кількість пасажирів
    }

    @Test
    void testSetKilkistPasazhyriv() {
        // Перевірка зміни кількості пасажирів
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        vagon.setKilkistPasazhyriv(40);
        assertEquals(40, vagon.getKilkistPasazhyriv());
        assertEquals(40, vagon.getPasazhyrskaMistkist());
    }

    @Test
    void testSetRivenObslugovuvannya() {
        // Перевірка зміни рівня обслуговування
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        vagon.setRivenObslugovuvannya(9);
        assertEquals(9, vagon.getRivenObslugovuvannya());
    }

    @Test
    void testSetKomfortnist() {
        // Перевірка зміни комфортності (метод з батьківського класу)
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        vagon.setKomfortnist(10);
        assertEquals(10, vagon.getKomfortnist());
    }

    @Test
    void testSetBagazhKilkist() {
        // Перевірка зміни кількості багажу (метод з батьківського класу)
        PasazhyrskyVagon vagon = new PasazhyrskyVagon(
                1, 5, 30, KlasKomfortu.KUPE, 36, 7
        );
        vagon.setBagazhKilkist(50);
        assertEquals(50, vagon.getBagazhKilkist());
    }

    @Test
    void testRizniKlasyKomfortu() {
        // Перевірка роботи з різними класами комфортності
        PasazhyrskyVagon vagonVIP = new PasazhyrskyVagon(
                1, 10, 20, KlasKomfortu.VIP, 18, 10
        );
        assertEquals(KlasKomfortu.VIP, vagonVIP.getKlasKomfortu());

        PasazhyrskyVagon vagonPlatskart = new PasazhyrskyVagon(
                2, 3, 60, KlasKomfortu.PLATSKART, 54, 4
        );
        assertEquals(KlasKomfortu.PLATSKART, vagonPlatskart.getKlasKomfortu());
    }
}
